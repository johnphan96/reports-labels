package dina.api.routes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.eclipse.jetty.http.HttpStatus;

import dina.LabelCreator.Options.Options;
import dina.api.ApiResponseCode;
import dina.api.requests.AccessTmp;
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
@Path("/*/tmp")
@Produces("application/octet-stream")
public class AccessTmpRoute implements Route {

	@GET 
	@ApiOperation(value = "Gives temporary access to included files (e.g. images of QR-Codes)", nickname="AccessTmp")
	@ApiImplicitParams({ //
			@ApiImplicitParam(required = true, dataType="string", name="f", paramType = "query"), //
	}) //
	@ApiResponses(value = { //
			@ApiResponse(code = 200, message = "Success", response=AccessTmp.class), //
			@ApiResponse(code = 400, message = "Invalid input data", response=String.class), //
			//@ApiResponse(code = 401, message = "Unauthorized", response=ApiResponseCode.Code.class), //
			@ApiResponse(code = 404, message = "Not found", response=String.class) //
	})

	public AccessTmp handle(@ApiParam(hidden=true) Request request, @ApiParam(hidden=true)Response response) throws Exception {
		return new AccessTmp(null, request, response);
	}
	
	public static void route(@ApiParam(hidden=true) Options op) throws Exception {
		
		spark.Spark.get("/*/tmp", (req, res) -> {
    		
		  AccessTmp c = new AccessTmp(op, req, res);
          return c.result();
     });
	}
	
}