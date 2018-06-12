package dina.api.requests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus.Code;

import dina.LabelCreator.LabelCreator;
import dina.LabelCreator.Options.Options;
import dina.api.ApiResponseCode;
import spark.Request;
import spark.Response;


public class AccessTmp {
 	
	public String f;
	private Options op;
	private Request req;
	private Response res;
	
	public AccessTmp(Options options, Request request, Response response) {
		op = options;
		req = request;
		res = response;
	}

	public HttpServletResponse result() throws IOException {
		   
		   LabelCreator labels = new LabelCreator(op);
		   labels.baseURL = "http://"+req.host()+req.pathInfo();
		
		   if(req.queryParams("f")==null)
			   return new ApiResponseCode(res, 400, null).getResponse();
		   if(req.queryParams("f").isEmpty())
			   return new ApiResponseCode(res, 400, null).getResponse();
		   
		   Path path = Paths.get(op.tmpDir+"/"+req.queryParams("f"));
		   
		   if(!path.toFile().exists())
			   return new ApiResponseCode(res, Code.NOT_FOUND).getResponse();
		  
	       	byte[] bytes = Files.readAllBytes(path);         
	        HttpServletResponse raw = res.raw();
	
	        raw.addHeader("Content-Type", "application/octet-stream");
	        raw.getOutputStream().write(bytes);
	        raw.getOutputStream().flush();
	        raw.getOutputStream().close();
	        
	        return res.raw();      
	}
}
