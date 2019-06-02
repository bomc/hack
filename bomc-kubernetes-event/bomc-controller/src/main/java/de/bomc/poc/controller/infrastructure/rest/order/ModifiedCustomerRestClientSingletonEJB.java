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
package de.bomc.poc.controller.infrastructure.rest.order;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.rest.logger.client.ResteasyClientLogger;

/**
 * A rest client for reading a order to the order service.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
@Startup
@Singleton
public class ModifiedCustomerRestClientSingletonEJB {
    
    private static final String LOG_PREFIX = "ModifiedCustomerRestClientSingletonEJB#";
    private static final String BOMC_ORDER_SVC_PORT_80_TCP_ADDR = "BOMC_ORDER_SVC_PORT_80_TCP_ADDR";
    private static final String BOMC_ORDER_SVC_PORT_80_TCP_PORT = "BOMC_ORDER_SVC_PORT_80_TCP_PORT";
    @Inject
    @LoggerQualifier
    private Logger logger;
    // The address of order service (ip + port)
    private String path;
    
    @PostConstruct
    public void init() {
        this.logger.debug(LOG_PREFIX + "init");
        
        // Run command to determine the ip + port of order-svc, to read out the environment variables.
        // 'kubectl exec bomc-order-77c89d78cd-j9t7l -n=bomc-app -- printenv'.
        final String orderServiceAddress = System.getenv(BOMC_ORDER_SVC_PORT_80_TCP_ADDR);
        final String orderServicePort = System.getenv(BOMC_ORDER_SVC_PORT_80_TCP_PORT);
        
        this.path = "http://" + orderServiceAddress + ":" + orderServicePort
                + "/bomc-order/rest/customer/latest-modified-date";
        
        this.logger.debug(LOG_PREFIX + "init [path=" + path + "]");
    }
    
    @Schedule(second="*/3", minute="*",hour="*", persistent=false)
    public void checkModifiedCustomer() {
        this.logger.debug(LOG_PREFIX + "checkModifiedCustomer");
        
        Response response = null;
        
        try {
            // Using the RESTEasy libraries, initiate a client request.
            final ResteasyClient client = new ResteasyClientBuilder().build();
            client.register(new ResteasyClientLogger(this.logger, true));
            
            // Set url as target.
            final ResteasyWebTarget target = client.target(UriBuilder.fromPath(this.path));
            
            // Be sure to set the mediatype of the request and get the response.
            response = target.request("application/vnd.customer-v1+json").get();
            
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final JsonObject jsonObject = response.readEntity(JsonObject.class);
                
                this.logger.info(LOG_PREFIX + "checkModifiedCustomer [" + jsonObject + "]");
            } else {
                this.logger
                        .error(LOG_PREFIX + "checkModifiedCustomer - failed! [status=" + response.getStatus() + "]");
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}
