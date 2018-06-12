package dina.api.requests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletResponse;

import dina.LabelCreator.LabelCreator;
import dina.LabelCreator.Options.Options;
import spark.Request;
import spark.Response;

public class CreatePDF {

	public String template;
	private Options op;
	private Request req;
	private Response res;
	
	public CreatePDF(Options options, Request request, Response response) {
		op = options;
		req = request;
		res = response;
	}

	public HttpServletResponse result() throws IOException {
		   
			if(req.queryParams("template")!=null)
				op.templateFile = "templates/"+req.queryParams("template");
		
		   LabelCreator labels = new LabelCreator(op);
		   labels.baseURL = "http://"+req.host()+req.pathInfo();
		   labels.jsonData = req.queryParams("data");
     	   labels.createPDF();
     	   
     	   byte[] bytes = Files.readAllBytes(Paths.get(op.outputFile));         
           HttpServletResponse raw = res.raw();

            raw.getOutputStream().write(bytes);
            raw.getOutputStream().flush();
            raw.getOutputStream().close();
            
            return res.raw();
	}
}
