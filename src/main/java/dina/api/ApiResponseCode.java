package dina.api;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;


import spark.Response;


public class ApiResponseCode{
	private int intCode;
	private String message;
	private HttpServletResponse response;
	private Map<Integer,String> HttpStatusCode;
	
	public ApiResponseCode( Response res) {
		
		HttpStatusCode.put(200, "OK");
		HttpStatusCode.put(400, "Bad Request");
		HttpStatusCode.put(403, "Forbidden");
		HttpStatusCode.put(404, "Not Found");
		HttpStatusCode.put(405, "Method Not Allowed");
		HttpStatusCode.put(500, "Internal Error");
		
		response = res.raw();
		
	}

	public ApiResponseCode(Response res, int c, String message) {
		response = res.raw();
		this.setCode(c, message);
	}

	public ApiResponseCode(Response res, int co) {
		response = res.raw();
		this.intCode = co;
	}
	
	public int getCode() {
		return intCode;
	}
	
	public void setCode(int c, String message) {
		this.intCode = c;
		if(message==null)
			this.message = HttpStatusCode.get(c);
		
		response.setStatus(c);
		try {
			response.sendError(c);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setCode(int code) {
		this.intCode = code;
		this.response.setStatus(code);
		try {
			response.sendError(code);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.message = HttpStatusCode.get(code);
		if(this.message==null)
			this.message = "Unkown Error";
	}
	
	public String getMessage() {
		return message;
	}
	
	public HttpServletResponse getResponse() {
		return response;
	}
	
	/*public void setMessage(String message) {
		this.message = message;
	}*/
}