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

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;

import de.bomc.poc.exception.cdi.interceptor.ExceptionHandlerInterceptor;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.order.application.basis.log.qualifier.AuditLogQualifier;
import de.bomc.poc.order.application.basis.performance.qualifier.PerformanceTrackingQualifier;
import de.bomc.poc.order.application.order.OrderController;
import de.bomc.poc.order.application.order.dto.ItemDTO;
import de.bomc.poc.order.application.order.dto.OrderDTO;
import de.bomc.poc.order.domain.shared.DomainObjectUtils;
import de.bomc.poc.order.infrastructure.rest.cache.qualifier.CacheControlConfigQualifier;

/**
 * This endpoint handles needs for customer relationships.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 03.02.2019
 */
@AuditLogQualifier
@PerformanceTrackingQualifier
@Interceptors({ ExceptionHandlerInterceptor.class })
public class OrderRestEndpointImpl implements OrderRestEndpoint {

    private static final String LOG_PREFIX = "OrderRestEndpointImpl#";
    // private static final String ZONE_ID_EUROPE_ZURICH = "Europe/Zurich";
    @Inject
    @LoggerQualifier
    private Logger logger;
    @Context
    private Request request;
    @Inject
    @CacheControlConfigQualifier
    private CacheControl cacheControl;
    @EJB
    private OrderController orderControllerEJB;

    /**
     * <code>curl -X GET "http://localhost:8180/bomc-order/rest/customer/latest-modified-date" -H "accept: application/vnd.customer-v1+json" 
     * -H "X-BOMC_USER_ID: admin"</code>
     */
    @Override
    public Response getLatestModifiedDate(final String userId) {
        ResponseBuilder responseBuilder = null;

        final LocalDateTime lastModifiedLocalDateTime = this.orderControllerEJB.findLatestModifiedDateTime(userId);

        if (lastModifiedLocalDateTime != null) {

            final Date convertedRFC1132Date = DomainObjectUtils.convertLocalDateTimeToDate(lastModifiedLocalDateTime);
            responseBuilder = request.evaluatePreconditions(convertedRFC1132Date);

            if (responseBuilder == null) {
                // The precondition are met, this means there are modified
                // resources.
                this.logger.info(LOG_PREFIX + "getLatestModifiedDate - resources are modified.");

                // Read modified order from db.
                final List<OrderDTO> orderDTOList = this.orderControllerEJB
                        .findByAllOlderThanGivenDate(lastModifiedLocalDateTime, userId);

                // Build response.
                final GenericEntity<List<OrderDTO>> entity = new GenericEntity<List<OrderDTO>>(orderDTOList) {
                };

                responseBuilder = Response.ok().entity(entity).cacheControl(cacheControl).lastModified(convertedRFC1132Date);
            } else {
                // No modified resources. Return the automatically generated
                // response.
                this.logger.info(LOG_PREFIX + "getLatestModifiedDate - No modified resources - HTTP 304 status.");

                responseBuilder = Response.notModified();
            }
        } else {
            //
            // lastModifiedLocalDateTime is null, no orders available.
            responseBuilder = Response.notModified();
        }
        
        return responseBuilder.build();
        
    }

    /**
     * <code>curl -X POST "http://localhost:8180/bomc-order/rest/order/create" -H "accept: application/vnd.order-v1+json" 
     * -H "X-BOMC_USER_ID: admin" -H "Content-Type: application/vnd.order-v1+json" -d 
     * "{ \"shippingAddress\": { \"street\": \"myShippingStreet\", \"zip\": \"myShippingZip\", \"city\": \"myShippingCity\" }, 
     * \"billingAddress\": { \"street\": \"myBillingStreet\", \"zip\": \"myBillingZip\", \"city\": \"myBillingCity\" }, 
     * \"customer\": { \"name\": \"admin\", \"firstname\": \"admin\", \"username\": \"admin@admin.org\" }, \"orderLineDTOSet\": 
     * [ { \"quantity\": 42, \"item\": { \"name\": \"Apple Watch\", \"price\": 0 } } ], \"orderId\": 0}"</code>
     */
    @Override
    public Response createOrder(final OrderDTO orderDTO, String userId) {

        final Long orderId = this.orderControllerEJB.createOrder(orderDTO, userId);
        final JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("orderId", orderId);

        return Response.ok(jsonObjectBuilder.build()).type(OrderRestEndpoint.MEDIA_TYPE_JSON_V1).build();
    }

    /**
     * <code>curl -X GET "http://localhost:8180/bomc-order/rest/order/find/item" -H "accept: application/vnd.order-v1+json" 
     * -H "X-BOMC_USER_ID: admin"</code>
     */
    @Override
    public Response findAllItems(final String userId) {

        final List<ItemDTO> itemDTOList = this.orderControllerEJB.findAllItems(userId);
        final GenericEntity<List<ItemDTO>> entity = new GenericEntity<List<ItemDTO>>(itemDTOList) {
        };

        return Response.ok(entity).type(OrderRestEndpoint.MEDIA_TYPE_JSON_V1).build();
    }

    /**
     * <code>curl -X GET "http://localhost:8180/bomc-order/rest/order/find/order" -H "accept: application/vnd.order-v1+json" 
     * -H "X-BOMC_USER_ID: admin"</code>
     */
    @Override
    public Response findAllOrder(final String userId) {

        final List<OrderDTO> orderDTOList = this.orderControllerEJB.findAllOrder(userId);
        final GenericEntity<List<OrderDTO>> entity = new GenericEntity<List<OrderDTO>>(orderDTOList) {
        };

        return Response.ok(entity).type(OrderRestEndpoint.MEDIA_TYPE_JSON_V1).build();
    }

    /**
     * <code>curl -X GET "http://localhost:8180/bomc-order/rest/order/find/2/order" -H "accept: application/vnd.order-v1+json" 
     * -H "X-BOMC_USER_ID: admin"</code>
     */
    @Override
    public Response findOrderById(final Long orderId, final String userId) {

        final OrderDTO orderDTO = this.orderControllerEJB.findOrderById(orderId, userId);

        return Response.ok(orderDTO).type(OrderRestEndpoint.MEDIA_TYPE_JSON_V1).build();
    }

    /**
     * <code>curl -X POST "http://localhost:8180/bomc-order/rest/order/add/orderline" -H "accept: application/vnd.order-v1+json" 
     * -H "X-BOMC_USER_ID: admin" -H "Content-Type: application/vnd.order-v1+json" -d "{ \"shippingAddress\": { \"street\": \"string\", \"zip\": 
     * \"string\", \"city\": \"string\" }, \"billingAddress\": { \"street\": \"string\", \"zip\": \"string\", \"city\": \"string\" }, 
     * \"customer\": { \"name\": \"string\", \"firstname\": \"string\", \"username\": \"admin@admin.org\" }, \"orderLineDTOSet\": 
     * [ { \"quantity\": 1, \"item\": { \"name\": \"iPhone\", \"price\": 0 } } ], \"orderId\": 1}"</code>
     */
    @Override
    public Response addLine(final OrderDTO orderDTO, final String userId) {

        this.orderControllerEJB.addLine(orderDTO, userId);

        return Response.noContent().type(OrderRestEndpoint.MEDIA_TYPE_JSON_V1).build();
    }

    /**
     * <code>curl -X DELETE "http://localhost:8180/bomc-order/rest/order/delete/1/order" -H "accept: application/vnd.order-v1+json" 
     * -H "X-BOMC_USER_ID: admin"</code>
     */
    @Override
    public Response deleteOrderById(final Long orderId, final String userId) {

        this.orderControllerEJB.deleteOrderById(orderId, userId);

        return Response.noContent().type(OrderRestEndpoint.MEDIA_TYPE_JSON_V1).build();
    }
}
