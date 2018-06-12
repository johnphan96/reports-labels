package dina.api;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;

import spark.Response;


public class ApiResponseCode extends HttpStatus {
	private int intCode;
	private String message;
	private HttpServletResponse response;
	
	public ApiResponseCode( Response res) {
		response = res.raw();
	}

	public ApiResponseCode(Response res, int c, String message) {
		response = res.raw();
		this.setCode(c, message);
	}

	public ApiResponseCode(Response res, Code co) {
		response = res.raw();
		this.setCode(co);
	}
	
	public int getCode() {
		return intCode;
	}
	
	public void setCode(int c, String message) {
		this.intCode = c;
		if(message==null)
			this.message = HttpStatus.getMessage(c);
		
		response.setStatus(c);
		try {
			response.sendError(c);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setCode(Code co) {
		this.intCode = co.getCode();
		this.response.setStatus(co.getCode());
		try {
			response.sendError(co.getCode());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.message = co.getMessage();
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