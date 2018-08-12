package dina.api.routes;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import dina.LabelCreator.Options.Options;
import dina.api.ApiResponseCode;
import dina.api.requests.CreateHTML;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import spark.Request;
import spark.Response;
import spark.Route;

@Api
@Path("/html")
@Produces("text/html")
public class CreateHTMLRoute implements Route {

	@POST
	@ApiOperation(value = "Creates HTML", nickname="CreateHTML")
	@ApiImplicitParams({ //
			@ApiImplicitParam(required = true, dataType="string", name="auth", paramType = "header"), //
			@ApiImplicitParam(required = true, dataType = "dina.api.requests.CreateHTML", paramType = "body") //
	}) //
	@ApiResponses(value = { //
			@ApiResponse(code = 200, message = "Success", response=CreateHTML.class), //
			@ApiResponse(code = 400, message = "Invalid input data", response=ApiResponseCode.class), //
			@ApiResponse(code = 401, message = "Unauthorized", response=ApiResponseCode.class), //
			@ApiResponse(code = 404, message = "Not found", response=ApiResponseCode.class) //
	})

	public CreateHTML handle(@ApiParam(hidden=true) Request request, @ApiParam(hidden=true)Response response) throws Exception {
		return new CreateHTML(null, request, response);
	}
	
	public static void route(@ApiParam(hidden=true) Options op) throws Exception {
		
	spark.Spark.post("/html", (req, res) -> {
			
			CreateHTML c = new CreateHTML(op, req, res);
			String re = c.result();
			
			return re;
     });
		
		spark.Spark.get("/html", (req, res) -> {
			// CreateHTML c = new CreateHTML(op, req, res);
	         //  return c.result();
			   return "Please use POST method.";
	     });
	}
	
}