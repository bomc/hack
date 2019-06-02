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

import ${package}.application.util.JaxRsActivator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * This endpoint returns metrics in worst cases (time, average and count) for all deployed endpoints.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
@Path(JaxRsActivator.PERFORMANCE_ENDPOINT_PATH)
@Produces({PerformanceRestEndpoint.MEDIA_TYPE_JSON_V1})
@Api(value = JaxRsActivator.PERFORMANCE_ENDPOINT_PATH, description = "REST Methods to access performance metrics")
public interface PerformanceRestEndpoint {

    /**
     * A {@code String} constant representing json v1
     * "{@value ${symbol_pound}MEDIA_TYPE_JSON_V1}" media type .
     */
    String MEDIA_TYPE_JSON_V1 = "application/vnd.performance-v1+json";

    @ApiOperation(value = "Worst by average.", response = String.class)
    @ApiResponses({@ApiResponse(code = 404, message = "Endpoint not found."), @ApiResponse(code = 200, message = "The performance metric by worst average as response object that wraps the javax.json.JsonObject as a string.")})
    @GET
    @Path("/worst-by-average")
    Response getWorstByAverage();

    @ApiOperation(value = "Worst by count.", response = String.class)
    @ApiResponses({@ApiResponse(code = 404, message = "Endpoint not found."), @ApiResponse(code = 200, message = "The performance metric by worst count as response object that wraps the javax.json.JsonObject as a string.")})
    @GET
    @Path("/worst-by-count")
    Response getWorstByCount();

    @ApiOperation(value = "Worst by time.", response = String.class)
    @ApiResponses({@ApiResponse(code = 404, message = "Endpoint not found."), @ApiResponse(code = 200, message = "The performance metric by worst time as response object that wraps the javax.json.JsonObject as a string.")})
    @GET
    @Path("/worst-by-time")
    Response getWorstByTime();
}
