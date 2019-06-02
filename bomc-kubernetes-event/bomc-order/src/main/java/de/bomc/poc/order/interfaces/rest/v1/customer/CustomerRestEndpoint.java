/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.order.interfaces.rest.v1.customer;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import de.bomc.poc.order.application.customer.dto.CustomerDTO;
import de.bomc.poc.order.application.util.JaxRsActivator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Handles actions for the {@link CustomerEntity}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 03.02.2019
 */
@Path(JaxRsActivator.CUSTOMER_ENDPOINT_PATH)
@Produces({ CustomerRestEndpoint.MEDIA_TYPE_JSON_V1 })
@Consumes({ CustomerRestEndpoint.MEDIA_TYPE_JSON_V1 })
@Api(value = "/customer", description = "Handles actions for the CustomerEntity.")
public interface CustomerRestEndpoint {

    /**
     * A {@code String} constant representing json v1
     * "{@value #MEDIA_TYPE_JSON_V1}" media type .
     */
    String MEDIA_TYPE_JSON_V1 = "application/vnd.customer-v1+json";

    /**
     * <pre>
     *  TODO Get from swagger gui invocation.
     * </pre>
     * 
     * @throws ParseException
     */
    @ApiOperation(value = "Find the latest modified date.", response = String.class)
    @ApiResponses({ @ApiResponse(code = 404, message = "Endpoint not found."),
            @ApiResponse(code = 200, message = "The latest modified date as response object that javax.json.JsonObject as a string.") })
    @GET
    @Path("/latest-modified-date")
    Response getLatestModifiedDate(
            @ApiParam(value = "The executed user (used for auditing).", required = true) @HeaderParam("X-BOMC_USER_ID") String userId);

    /**
     * <pre>
     *  TODO Get from swagger ui invocation.
     * </pre>
     * 
     * @throws ParseException
     */
    @ApiOperation(value = "Create a user by the given parameter.", response = String.class)
    @ApiResponses({ 
        @ApiResponse(code = 404, message = "Endpoint not found."),
        @ApiResponse(code = 200, message = "Returns the technical id or null if user is not created in db.") })
    @POST
    @Path("/create")
    Response createCustomer(
            @ApiParam(value = "The user data.") final CustomerDTO customerDTO,
            @ApiParam(value = "The executed user (used for auditing).", required = true) @HeaderParam("X-BOMC_USER_ID") String userId);
    
    /**
     * <pre>
     *  TODO Get from swagger ui invocation.
     * </pre>
     * 
     * @throws ParseException
     */
    @ApiOperation(value = "Find customer by username.", response = String.class)
    @ApiResponses({ 
        @ApiResponse(code = 404, message = "Endpoint not found."),
        @ApiResponse(code = 200, message = "Returns customer by given username.") })
    @GET
    @Path("/find/{username}")
    Response findCustomerByUsername(
            @ApiParam(value = "The given username.") @PathParam("username") final String username,
            @ApiParam(value = "The executed user (used for auditing).", required = true) @HeaderParam("X-BOMC_USER_ID") String userId);
}
