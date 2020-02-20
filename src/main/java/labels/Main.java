package labels;

import static spark.Spark.*;
import static spark.Spark.staticFiles;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import spark.embeddedserver.EmbeddedServers;
import spark.embeddedserver.jetty.EmbeddedJettyServer;
import spark.embeddedserver.jetty.JettyHandler;
import spark.http.matching.MatcherFilter;
import spark.route.Routes;
import spark.staticfiles.StaticFilesConfiguration;
import dina.LabelCreator.Options.Options;
import dina.TemplateCreator.TemplateCreator;
import dina.api.SwaggerParser;
import dina.api.requests.AccessTmp;
import dina.api.requests.CreateHTML;
import dina.api.requests.CreatePDF;
import dina.api.routes.AccessTmpRoute;
import dina.api.routes.CreateHTMLRoute;
import dina.api.routes.CreatePDFRoute;

// Swagger integration insprired by https://github.com/srlk/spark-swagger 
@SwaggerDefinition(host = "localhost", //
basePath = "labels/v1.0", //
info = @Info(description = "DINA Labels API", //
version = "v1.0", //
title = "DINA Labels Module", //
contact = @Contact(name = "Falko Gloeckler", url = "", email = "falko.gloeckler@mfn-berlin.de") ) , //
schemes = { SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS }, //
consumes = { "application/json" }, //
produces = { "application/json", "application/pdf", "text/html" }, //
tags = { @Tag(name = "swagger"),  @Tag(name = "DINA Web") })

public class Main {
	
	public static final String APP_PACKAGE = "dina.api";
	private static final String API_VERSION = "v1.0";
	
    public static void main(String[] args) {
    	
    	// config embedded Jetty server
    	EmbeddedServers.add(EmbeddedServers.Identifiers.JETTY, (Routes routeMatcher, StaticFilesConfiguration staticFilesConfiguration, boolean hasMultipleHandler) -> {
            JettyHandler handler = setupHandler(routeMatcher, staticFilesConfiguration, hasMultipleHandler);
            customJettyServerFactory serv = new customJettyServerFactory();
            return new EmbeddedJettyServer(serv, handler);
        });
    	
    	port(80);
    	
    	Options op = new Options();
    	op.setOptions(args);
    	//LabelCreator labels = new LabelCreator(op);
    	
    	staticFiles.location("/public");  // Static (web accessible) files (e.g. CSS files etc.) can be placed in src/main/resources/public 
    	
    	// any empty action will be redirected to the API documentation
    	redirect.any("/", "/labels/"+API_VERSION+"/");
    	redirect.any("/labels", "/labels/"+API_VERSION+"/");
    	redirect.any("/labels/", "/labels/"+API_VERSION+"/");	
    	redirect.any("/labels/"+API_VERSION, "/labels/"+API_VERSION+"/");	
    	
    	try {
			// Build swagger json description
			final String swaggerJson = SwaggerParser.getSwaggerJson(APP_PACKAGE);
			get("/labels/"+API_VERSION+"/", (req, res) -> {   // TODO:  redirect the Swagger JSON to a Swagger UI instance instead of returning it  e.g.    redirect.any("/test", "http://petstore.swagger.io");
				res.header("Content-Type", "application/json");
				return swaggerJson;//.replace(oldChar, newChar);
			});

		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
    	
    	
    	
    	// Routes other than default Swagger documentation
    	path("/labels", () -> {
    		path("/"+API_VERSION, () -> {
	    		
    			post("/", (req, res) -> {
    				
    				if(req.queryParams("template")==null || req.queryParams("template").isEmpty())
    				{	
    					//res.redirect("/labels/"+API_VERSION+"/template/choose", 303);
    					TemplateCreator tmpl = new TemplateCreator(op);
			        	tmpl.baseURL = "http://"+req.host();
			        	tmpl.target = tmpl.baseURL+ req.pathInfo();
			        	tmpl.origReq = req;
			        	
			        	return tmpl.chooseTemplateForm();
    				}
    				else {
    					String format = req.queryParams("format");
    					String data = req.queryParams("data");
    					op.baseURL = "http://"+req.host() + req.pathInfo();
    					
    					System.out.println(op.baseURL);
    					
    					if(format==null || format.isEmpty())
    						format="html";
    					
	    				if(format.equalsIgnoreCase("html"))
	    					try {
	    						 CreateHTML c = new CreateHTML(op, req, res);
	    				         String re = c.result();
	    				         return re;
	    					} catch (Exception e) {
	    						// TODO Auto-generated catch block
	    						e.printStackTrace();
	    					}
	    				 
	    				if(format.equalsIgnoreCase("pdf"))
	    					try {
	    						CreatePDF c = new CreatePDF(op, req, res);
	    				        return c.result();
	    					} catch (Exception e) {
	    						// TODO Auto-generated catch block
	    						e.printStackTrace();
	    					}
    				}
    				return "3rror";
    			});
    			
    			get("/tmp", (req, res) -> {
    	    		
    				  AccessTmp c = new AccessTmp(op, req, res);
    		          return c.result();
    		     });
    			
    			try {
					CreatePDFRoute.route(op);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
    			try {
					CreateHTMLRoute.route(op);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		       
    			try {
					AccessTmpRoute.route(op);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
		        // TODO: Metadata about existing templates
		        // @route   /templates/ 		=> get a list of all existing templates
		        // @route   /template/{id} 		=> get a specific template with unique list of included placeholders and path to html file
		        
    			path("/template", () -> {
			        get("/show", (req, res) -> {
					   
			        	res.header("Content-Type", "text/html");
			        	
			           if(req.queryParams("template")!=null)
							op.templateFile = op.templateDir+"/"+req.queryParams("template");
			           
			           String html = "";
			           File file = new File(op.templateFile);
					   FileInputStream fis;
						
					    if(file.exists()) {
							fis = new FileInputStream(file);
						    byte[] data = new byte[(int) file.length()];
							fis.read(data);
						    fis.close();
						    html = new String(data, "UTF-8");
					    };
					    res.body(html);
					    
					    return res.body();
			        });
			        
			        get("/show/all", (req, res) -> {
			        	
			        	res.header("Content-Type", "application/json");
			        	res.body(new TemplateCreator(op).getTemplates().toString());
			        	return res.body();
			        });
			       
			       
    			});
		        
		        get("/check", (req, res)-> {
		        	
		        	 byte[] bytes = Files.readAllBytes(Paths.get("templates/submit.html"));         
			            HttpServletResponse raw = res.raw();
			
			            raw.getOutputStream().write(bytes);
			            raw.getOutputStream().flush();
			            raw.getOutputStream().close();
			            
			            return res.raw();
		        	
		        });
		        
		        // TODO: make template editable (e.g. with (static) implementation of WYSIWYG Aloha Editor (http://alohoeditor.org))
		        // get("/template/edit", (req, res) -> {
		        //		@param Sting template:  name of the template
		        // }
		        
		        // TODO: save template (e.g. by sending the (full) DOM of the template edited by Aloha Editor (http://alohoeditor.org))
		        // get("/template/save", (req, res) -> {
		        //		@param String template:  name of the (new) template
		        //		@param Boolean overwrite: what to do if template name already exists 
		        // }
		        
		        
		        
		       /* get("/json", (req, res) -> {
		            
		            byte[] bytes = Files.readAllBytes(Paths.get("templates/data.json"));         
		            HttpServletResponse raw = res.raw();
		
		            raw.addHeader("Content-Type", "application/json");
		            raw.getOutputStream().write(bytes);
		            raw.getOutputStream().flush();
		            raw.getOutputStream().close();
		
		            return res.raw();
		            
		        	 });
		        */
	    	});
    	});
    }
    
    private static JettyHandler setupHandler(Routes routeMatcher, StaticFilesConfiguration staticFilesConfiguration, boolean hasMultipleHandler) {
        MatcherFilter matcherFilter = new MatcherFilter(routeMatcher, staticFilesConfiguration, false, hasMultipleHandler);
        matcherFilter.init(null);
 
        return new JettyHandler(matcherFilter);
    }
}