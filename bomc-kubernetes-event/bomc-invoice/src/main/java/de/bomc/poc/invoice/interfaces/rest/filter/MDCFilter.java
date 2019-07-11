/**
 * Project: bomc-invoice
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
package de.bomc.poc.invoice.interfaces.rest.filter;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.MDC;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <pre>
 *  This filter adds the {@link MDC} properties x-bomc-request-id.
 * </pre>
 * 
 * Append this to log pattern to append the properties to each log entry:
 * <code>R:[%X{x-bomc-request-id}]</code>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @since 19.07.2016
 */
@Provider
//Smaller numbers are first in the chain.
@Priority(value = Priorities.AUTHORIZATION + 300)
public class MDCFilter implements ContainerRequestFilter, ContainerResponseFilter {

	private static final Logger LOGGER = Logger.getLogger(MDCFilter.class.getName());
	private static final String LOG_PREFIX = "MDCFilter#";
	public static final String HEADER_REQUEST_ID_ATTR = "x-bomc-request-id";
	// TODO
	public static final String HEADER_USER_ID_ATTR = "x-bomc-user-id";

	/**
	 * Filter method called after a response has been provided for a request (either
	 * by a {@link ContainerRequestFilter request filter} or by a matched resource
	 * method.
	 */
	@Override
	public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
			throws IOException {
		LOGGER.log(Level.INFO,
				LOG_PREFIX + "filter [requestContext.absolutePath=" + requestContext.getUriInfo().getAbsolutePath()
						+ ", responseContext.status=" + responseContext.getStatus() + "]");
		
		if (MDC.get(HEADER_REQUEST_ID_ATTR) != null) {
			LOGGER.log(Level.INFO, LOG_PREFIX + "filter - response, set requestId to header [requestId="
					+ MDC.get(HEADER_REQUEST_ID_ATTR) + "]");
			// Set the requestId to the response. Thus the request is set to the next
			// endpoint invocation.
			responseContext.getHeaders().add(HEADER_REQUEST_ID_ATTR, MDC.get(HEADER_REQUEST_ID_ATTR));
		}
		//
		// Remove all MDC's.
		MDC.remove(HEADER_REQUEST_ID_ATTR);
	}

	/**
	 * Filter method called before a request has been dispatched to a resource.
	 */
	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {

		final String requestId;

		if (MDC.get(HEADER_REQUEST_ID_ATTR) != null) {
			LOGGER.log(Level.INFO,
					LOG_PREFIX + "filter - MDC request id already set: " + MDC.get(HEADER_REQUEST_ID_ATTR));
			
			return;
		}

		if (requestContext.getHeaders().containsKey(HEADER_REQUEST_ID_ATTR)) {
			//
			// Add request id to the MDC.
			final List<String> headerList = requestContext.getHeaders().get(HEADER_REQUEST_ID_ATTR);

			if (headerList.size() == 1) {
				//
				// A request id is available.
				requestId = headerList.iterator().next();
				MDC.put(HEADER_REQUEST_ID_ATTR, requestId);
			} else {
				//
				// The list size has unexpected size, expected is 1.
				LOGGER.log(Level.WARNING, LOG_PREFIX + "filter - the header '" + HEADER_REQUEST_ID_ATTR
						+ "' has unexpected size, must be 1. Generate a new one. [size=" + headerList.size() + "]");
				
				MDC.put(HEADER_REQUEST_ID_ATTR, UUID.randomUUID().toString());
			}
		} else {
			//
			// The request id is not set, so a new will be added.
			final String newId = UUID.randomUUID().toString();
			
			LOGGER.log(Level.INFO,
					LOG_PREFIX + "filter - requestId was not set in the received request. [requestId=" + newId + "]");
			
			MDC.put(HEADER_REQUEST_ID_ATTR, newId);
		}

		// Add further diagnostic data.
//            final String
//                requestBaseUri =
//                requestContext.getUriInfo()
//                              .getBaseUri()
//                              .toString();
//            final String[] splitRequestBaseUri = requestBaseUri.split("//");
//            MDC.put(MDC_REQUEST_BASE_URI, ">>" + splitRequestBaseUri[1]);

	}
}
