package dina.api.requests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

	private List<String> codesForCleanUp = new ArrayList<>();
	
	public CreatePDF(Options options, Request request, Response response) {
		op = options;
		req = request;
		res = response;
	}

	public HttpServletResponse result() throws IOException {
		   
			if(req.queryParams("template")!=null)
				op.templateFile = op.templateDir+"/"+req.queryParams("template");
		
		   LabelCreator labels = new LabelCreator(op, req.queryParams("data"));
		   labels.baseURL = "http://"+req.host()+req.pathInfo();
     	   labels.createPDF();
     	   
     	   if(op.debug)
     		   System.out.println("Reading PDF: "+Paths.get(op.outputFile));
     	   
     	   byte[] bytes = Files.readAllBytes(Paths.get(op.outputFile));         
           HttpServletResponse raw = res.raw();

            raw.getOutputStream().write(bytes);
            raw.getOutputStream().flush();
            raw.getOutputStream().close();
            
            return res.raw();
	}
	
	public List<String> getCleanUp(){
		
		return codesForCleanUp;
	}
}
