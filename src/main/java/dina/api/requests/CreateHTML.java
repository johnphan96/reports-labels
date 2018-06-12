package dina.api.requests;

import java.io.IOException;
import dina.LabelCreator.LabelCreator;
import dina.LabelCreator.Options.Options;
import spark.Request;
import spark.Response;

public class CreateHTML {

	public String template;
	private Options op;
	private Request req;
	private Response res;
	
	public CreateHTML(Options options, Request request, Response response) {
		op = options;
		req = request;
		res = response;
	}

	public String result() throws IOException {
		   
			if(req.queryParams("template")!=null)
			   op.templateFile = "templates/"+req.queryParams("template");
		
		   LabelCreator labels = new LabelCreator(op);
		   labels.baseURL = "http://"+req.host()+req.pathInfo();
		   labels.jsonData = req.queryParams("data");
		   return labels.parseTemplate();
	}
}
