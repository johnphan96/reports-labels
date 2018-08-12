package dina.api.routes;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import dina.LabelCreator.Options.Options;
import dina.api.ApiResponseCode;
import dina.api.requests.CreatePDF;
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
@Path("/pdf")
@Produces("application/pdf")
public class CreatePDFRoute implements Route {

	@POST
	@ApiOperation(value = "Creates a new PDF", nickname="CreatePDF")
	@ApiImplicitParams({ //
			@ApiImplicitParam(required = true, dataType="string", name="auth", paramType = "header"), //
			@ApiImplicitParam(required = true, dataType = "dina.api.requests.CreatePDF", paramType = "body") //
	}) //
	@ApiResponses(value = { //
			@ApiResponse(code = 200, message = "Success", response=CreatePDF.class), //
			@ApiResponse(code = 400, message = "Invalid input data", response=ApiResponseCode.class), //
			@ApiResponse(code = 401, message = "Unauthorized", response=ApiResponseCode.class), //
			@ApiResponse(code = 404, message = "Not found", response=ApiResponseCode.class) //
	})

	public CreatePDF handle(@ApiParam(hidden=true) Request request, @ApiParam(hidden=true)Response response) throws Exception {
		return new CreatePDF(null, request, response);
	}
	
	public static void route(@ApiParam(hidden=true) Options op) throws Exception {
		
		spark.Spark.post("/pdf", (req, res) -> {
    		
		   CreatePDF c = new CreatePDF(op, req, res);
           return c.result();
     });
		
		spark.Spark.get("/pdf", (req, res) -> {
			 CreatePDF c = new CreatePDF(op, req, res);
	        //   return c.result();
			 return "Please use POST method.";
	     });
	}
	
}