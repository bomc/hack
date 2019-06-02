#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: ${symbol_dollar}Author: bomc ${symbol_dollar}
 *
 *  date: ${symbol_dollar}Date: ${symbol_dollar}
 *
 *  revision: ${symbol_dollar}Revision: ${symbol_dollar}
 *
 * </pre>
 */
package ${package}.interfaces.rest.v1.basis;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ${package}.application.util.JaxRsActivator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * A resource that displays the last, maximum 100, exception.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
@Path(JaxRsActivator.EXCEPTION_ENDPOINT_PATH)
@Api(value = "/exceptions", description = "Displays the last exceptions of the last 30 days.")
public interface ExceptionLogEndpoint {

    /**
     * A {@code String} constant representing json v1
     * "{@value ${symbol_pound}MEDIA_TYPE_JSON_V1}" media type .
     */
    String MEDIA_TYPE_JSON_V1 = "application/vnd.exception-v1+json";

    /**
     * <pre>
     *  http://127.0.0.1:8180/${artifactId}/rest/exceptions/stored
     *  curl -v -H "Accept: application/vnd.exception-v1+json" -H "Content-Type: application/vnd.exception-v1+json" -H "X-BOMC-REQUEST-ID: SET-BY-CURL-123" -H "X-BOMC-AUTHORIZATION: BOMC_USER" -X GET "127.0.0.1:8180/${artifactId}/rest/exception/stored"
     * </pre>
     */
    @ApiOperation(value = "List all stored exceptions (max. 100).", response = String.class)
    @ApiResponses({ @ApiResponse(code = 404, message = "Endpoint not found."),
        @ApiResponse(code = 200, message = "Response object as string that wraps the javax.json.JsonObject. If no entries in db are available, a empty string is returned.") })
    @GET
    @Path("/stored")
    @Produces({ExceptionLogEndpoint.MEDIA_TYPE_JSON_V1})
    Response getStoredExceptions();
}