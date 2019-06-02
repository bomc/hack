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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.core.Dispatcher;

import ${package}.application.util.JaxRsActivator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * A resource that displays a list of available endpoints.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
@Path(JaxRsActivator.OVERVIEW_ENDPOINT_PATH)
@Api(value = "/overview", description = "Show all deployed endpoints of this service.")
public interface ApiOverviewRestEndpoint {

    /**
     * A {@code String} constant representing json v1
     * "{@value ${symbol_pound}MEDIA_TYPE_JSON_V1}" media type .
     */
    String MEDIA_TYPE_JSON_V1 = "application/vnd.overview-v1+json";

    /**
     * <pre>
     *  http://127.0.0.1:8180/${artifactId}/rest/overview/endpoints
     *  curl -v -H "Accept: application/vnd.overview-v1+json" -H "Content-Type: application/vnd.overview-v1+json" -H "X-BOMC-REQUEST-ID: SET-BY-CURL-123" -H "X-BOMC-AUTHORIZATION: BOMC_USER" -X GET
     * "127.0.0.1:8180/${artifactId}/rest/overview/endpoints"
     * </pre>
     */
    @ApiOperation(value = "List all available endpoints.", response = String.class)
    @ApiResponses({@ApiResponse(code = 404, message = "Endpoint not found."), @ApiResponse(code = 200, message = "Response object as string that wraps the javax.json.JsonObject.")})
    @GET
    @Path("/endpoints")
    @Produces(ApiOverviewRestEndpoint.MEDIA_TYPE_JSON_V1)
    Response getAvailableEndpoints(@Context final Dispatcher dispatcher);
}
