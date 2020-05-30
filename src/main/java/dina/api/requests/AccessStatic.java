package dina.api.requests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletResponse;

import dina.LabelCreator.Options.Options;
import dina.api.ApiResponseCode;
import spark.Request;
import spark.Response;


public class AccessStatic {
 	
	public String f;
	private Options op;
	private Request req;
	private Response res;
	
	public AccessStatic(Options options, Request request, Response response) {
		op = options;
		req = request;
		res = response;
	}

	public HttpServletResponse result() throws IOException {
		
		   String file = req.url();
		   file = file.replace(op.baseURL, "").replaceFirst("static", "");
		   
		   if(op.debug)
			   System.out.println("static file request: "+file);
		   
		   if(file==null)
			   return new ApiResponseCode(res, 400).getResponse();
		   if(file.isEmpty())
			   return new ApiResponseCode(res, 400).getResponse();
		   
		   Path path = Paths.get("src/main/resources/public/"+file);
		  
		   if(!path.toFile().exists())
			   return new ApiResponseCode(res, 404).getResponse();
		   else
			   if(op.debug)
				   System.out.println("static file exists!");
		  
	       	byte[] bytes = Files.readAllBytes(path);         
	        HttpServletResponse raw = res.raw();
	
	        //raw.addHeader("Content-Type", "application/octet-stream");
	        raw.getOutputStream().write(bytes);
	        raw.getOutputStream().flush();
	        raw.getOutputStream().close();
	        
	        return res.raw();      
	}
}
