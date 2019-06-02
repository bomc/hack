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
package de.bomc.poc.order.interfaces.rest.v1.order;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import de.bomc.poc.order.application.order.dto.OrderDTO;
import de.bomc.poc.order.application.util.JaxRsActivator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Handles the ordering use case.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 03.02.2019
 */
@Path(JaxRsActivator.ORDER_ENDPOINT_PATH)
@Produces({ OrderRestEndpoint.MEDIA_TYPE_JSON_V1 })
@Consumes({ OrderRestEndpoint.MEDIA_TYPE_JSON_V1 })
@Api(value = "/order", description = "Handles actions for the order actions.")
public interface OrderRestEndpoint {

    /**
     * A {@code String} constant representing json v1
     * "{@value #MEDIA_TYPE_JSON_V1}" media type .
     */
    String MEDIA_TYPE_JSON_V1 = "application/vnd.order-v1+json";

    /**
     * <pre>
     *  TODO Get from swagger gui invocation.
     * </pre>
     * 
     */
    @ApiOperation(value = "Find the latest modified date for created order.", response = String.class)
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
     */
    @ApiOperation(value = "Create a order by the given data.", response = Long.class)
    @ApiResponses({ @ApiResponse(code = 404, message = "Endpoint not found."),
            @ApiResponse(code = 200, message = "Returns the technical id or null if user is not created in db.") })
    @POST
    @Path("/create")
    Response createOrder(@ApiParam(value = "The user data.") final OrderDTO orderDTO,
            @ApiParam(value = "The executed user (used for auditing).", required = true) @HeaderParam("X-BOMC_USER_ID") String userId);

    /**
     * <pre>
     *  TODO Get from swagger ui invocation.
     * </pre>
     * 
     */
    @ApiOperation(value = "Find all items in db.", response = List.class)
    @ApiResponses({ @ApiResponse(code = 404, message = "Endpoint not found."),
            @ApiResponse(code = 200, message = "Returns a list of ItemDTOs.") })
    @GET
    @Path("/find/item")
    Response findAllItems(
            @ApiParam(value = "The executed user (used for auditing).", required = true) @HeaderParam("X-BOMC_USER_ID") String userId);

    /**
     * <pre>
     *  TODO Get from swagger ui invocation.
     * </pre>
     * 
     */
    @ApiOperation(value = "Find all orders in db.", response = List.class)
    @ApiResponses({ @ApiResponse(code = 404, message = "Endpoint not found."),
            @ApiResponse(code = 200, message = "Returns a list of OrderDTOs.") })
    @GET
    @Path("/find/order")
    Response findAllOrder(
            @ApiParam(value = "The executed user (used for auditing).", required = true) @HeaderParam("X-BOMC_USER_ID") String userId);

    /**
     * <pre>
     *  TODO Get from swagger ui invocation.
     * </pre>
     * 
     */
    @ApiOperation(value = "Find order by given id.", response = OrderDTO.class)
    @ApiResponses({ @ApiResponse(code = 404, message = "Endpoint not found."),
            @ApiResponse(code = 200, message = "Returns a OrderDTO by the given.") })
    @Path("/find/{orderId}/order")
    @GET
    Response findOrderById(@ApiParam(value = "The technical order id.") @PathParam("orderId") Long orderId,
            @ApiParam(value = "The executed user (used for auditing).", required = true) @HeaderParam("X-BOMC_USER_ID") String userId);

    /**
     * <pre>
     *  TODO Get from swagger ui invocation.
     * </pre>
     * 
     */
    @ApiOperation(value = "Add a orderline to the order.")
    @ApiResponses({ @ApiResponse(code = 404, message = "Endpoint not found."),
            @ApiResponse(code = 204, message = "Orderline is successful created.") })
    @POST
    @Path("/add/orderline")
    Response addLine(@ApiParam(value = "The given orderline data.") OrderDTO orderDTO,
            @ApiParam(value = "The executed user (used for auditing).", required = true) @HeaderParam("X-BOMC_USER_ID") String userId);

    /**
     * <pre>
     *  TODO Get from swagger ui invocation.
     * </pre>
     * 
     */
    @ApiOperation(value = "Delete order by id.")
    @ApiResponses({ @ApiResponse(code = 404, message = "Endpoint not found."),
            @ApiResponse(code = 204, message = "Order is successful deleted.") })
    @DELETE
    @Path("/delete/{orderId}/order")
    Response deleteOrderById(@ApiParam(value = "The technical order id.") @PathParam("orderId") Long orderId,
            @ApiParam(value = "The executed user (used for auditing).", required = true) @HeaderParam("X-BOMC_USER_ID") String userId);
}
