package labels;

import static spark.Spark.*;

import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import dina.LabelCreator.LabelCreator;
import dina.LabelCreator.Options.Options;
import dina.api.SwaggerParser;
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
    	
    	Options op = new Options();
    	op.setOptions(args);
    	//LabelCreator labels = new LabelCreator(op);
    			
    	port(80);
    	
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
		        
		        get("/template", (req, res) -> {
				   
		           if(req.queryParams("template")!=null)
						op.templateFile = "templates/"+req.queryParams("template");
					
		     	   byte[] bytes = Files.readAllBytes(Paths.get(op.templateFile));         
		            HttpServletResponse raw = res.raw();
		
		            raw.getOutputStream().write(bytes);
		            raw.getOutputStream().flush();
		            raw.getOutputStream().close();
		            
		            return res.raw();
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
}