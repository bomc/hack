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
package de.bomc.poc.invoice.interfaces.rest.client;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.bomc.poc.invoice.interfaces.rest.filter.MDCFilter;
import de.bomc.poc.invoice.interfaces.rest.filter.RestClientLogger;

/**
 * Handles the ordering use case.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 03.02.2019
 */
@RegisterRestClient
@RegisterProviders({ @RegisterProvider(MDCFilter.class), @RegisterProvider(RestClientLogger.class)/*,
		@RegisterProvider(RestClientHeaderIfModifiedSinceFilter.class)*/ })
@Path(OrderRestEndpoint.ORDER_ENDPOINT_PATH)
@Produces({ OrderRestEndpoint.MEDIA_TYPE_JSON_V1 })
@Consumes({ OrderRestEndpoint.MEDIA_TYPE_JSON_V1 })
public interface OrderRestEndpoint {

	/**
	 * A {@code String} constant representing json v1 "{@value #MEDIA_TYPE_JSON_V1}"
	 * media type.
	 */
	String MEDIA_TYPE_JSON_V1 = "application/vnd.order-v1+json";
	String ORDER_ENDPOINT_PATH = "/order";

	/**
	 * <pre>
	 *  TODO Get from swagger gui invocation.
	 * </pre>
	 * 
	 */
	@GET
	@Path("/latest-modified-date")
	Response getLatestModifiedDate(@HeaderParam("X-BOMC-USER-ID") String userId);
}
