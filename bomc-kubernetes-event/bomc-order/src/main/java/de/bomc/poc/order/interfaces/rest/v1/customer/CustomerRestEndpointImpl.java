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

import java.time.LocalDateTime;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;

import de.bomc.poc.exception.cdi.interceptor.ExceptionHandlerInterceptor;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.order.application.basis.log.qualifier.AuditLogQualifier;
import de.bomc.poc.order.application.basis.performance.qualifier.PerformanceTrackingQualifier;
import de.bomc.poc.order.application.customer.CustomerController;
import de.bomc.poc.order.application.customer.dto.CustomerDTO;
import de.bomc.poc.order.infrastructure.rest.cache.qualifier.CacheControlConfigQualifier;

/**
 * This endpoint handles needs for customer relationships.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 03.02.2019
 */
@AuditLogQualifier
@PerformanceTrackingQualifier
@Interceptors({ExceptionHandlerInterceptor.class})
public class CustomerRestEndpointImpl implements CustomerRestEndpoint {

    private static final String LOG_PREFIX = "OrderRestEndpointImpl#";
    private static final String LATEST_MODIFIED_DATE_KEY_NAME = "last-modified-date";
    @Inject
    @LoggerQualifier
    private Logger logger;
    @Inject
    @CacheControlConfigQualifier(maxAge = 2000)
    private CacheControl cacheControl;
    @Context
    private Request request;
    @EJB
    private CustomerController customerControllerEJB;
    
    /**
     * <code>curl -X GET "http://localhost:8180/bomc-order/rest/customer/latest-modified-date" -H "accept: application/vnd.customer-v1+json" 
     * -H "X-BOMC_USER_ID: admin"</code>
     */
    @Override
    public Response getLatestModifiedDate(final String userId) {

        final JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        final LocalDateTime lastModifiedLocalDateTime = this.customerControllerEJB.findLatestModifiedDateTime(userId);
        
        ResponseBuilder responseBuilder = null;
        
        if (lastModifiedLocalDateTime != null) {
            this.logger.info(LOG_PREFIX + "getLatestModifiedDate [lastModifiedLocalDate=" + lastModifiedLocalDateTime + "]");
            
            // Create the eTag.
            final EntityTag eTag = new EntityTag(Integer.toString(lastModifiedLocalDateTime.hashCode()));
            
            // Format LocalDateTime to java.util.Date.
            responseBuilder = request.evaluatePreconditions(eTag);
            
            if (responseBuilder == null) {
                // The precondition are met, this means there are modified resources.
                this.logger.info(LOG_PREFIX + "getLatestModifiedDate - resources are modified.");
                
                jsonObjectBuilder.add(LATEST_MODIFIED_DATE_KEY_NAME, lastModifiedLocalDateTime.toString());
                responseBuilder = Response.ok().entity(jsonObjectBuilder.build()).cacheControl(cacheControl).tag(eTag);
            } else {
                // No modified resources. Return the automatically generated response.
                this.logger.info(LOG_PREFIX + "getLatestModifiedDate - No modified resources - HTTP 304 status.");
                
                responseBuilder = Response.notModified();
            }
        }

        return responseBuilder.build();
    }

    /**
     * <code>curl -X POST "http://localhost:8180/bomc-order/rest/customer/create" -H "accept: application/vnd.customer-v1+json" 
     * -H "X-BOMC_USER_ID: michael" -H "Content-Type: application/vnd.customer-v1+json" -d "{ \"name\": \"michael\", \"firstname\": \"börner\", 
     * \"username\": \"bomc@bomc.org\"}"</code>
     */
    @Override
    public Response createCustomer(final CustomerDTO customerDTO, final String userId) {
        
        final Long id = this.customerControllerEJB.createCustomer(customerDTO, userId);
        
        // Build json response.
        final JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("id", id);
        
        final Response response = Response.ok().entity(jsonObjectBuilder.build()).build();
                
        return response;
    }

    @Override
    public Response findCustomerByUsername(final String username, final String userId) {

        final CustomerDTO customerDTO = this.customerControllerEJB.findCustomerByUsername(username, userId);
        
        // Build json response.
        final JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("firstname", customerDTO.getFirstname());
        jsonObjectBuilder.add("name", customerDTO.getName());
        jsonObjectBuilder.add("username", customerDTO.getUsername());
        
        final Response response = Response.ok().entity(jsonObjectBuilder.build()).build();
        
        return response;
    }
}
