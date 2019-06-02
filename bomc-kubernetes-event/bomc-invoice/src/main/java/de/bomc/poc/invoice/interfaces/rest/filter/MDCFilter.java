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

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import org.jboss.logging.MDC;

/**
 * <pre>
 *  This filter adds the {@link MDC} properties X-EGOV-REQUEST-ID, X-EGOV-BASE-URI, host.
 * </pre>
 * 
 * Append this to log pattern to append the properties to each log entry:
 * <code>R:[%X{X-EGOV-REQUEST-ID}] U:[%X{X-EGOV-BASE-URI}]</code>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @since 19.07.2016
 */
//Smaller numbers are first in the chain.
@Priority(value = Priorities.AUTHORIZATION + 300)
public class MDCFilter implements  ClientRequestFilter {

	private static final Logger LOGGER = Logger.getLogger(MDCFilter.class.getName());
	private static final String LOG_PREFIX = "MDCFilter#";
	public static final String HEADER_REQUEST_ID_ATTR = "X-BOMC-REQUEST-ID";

	/**
	 * Filter method called before a request has been dispatched to a resource.
	 */
	@Override
	public void filter(final ClientRequestContext requestContext) throws IOException {
		LOGGER.log(Level.INFO, LOG_PREFIX + "filter");
		
		final Object requestIdFromMDC = MDC.get(HEADER_REQUEST_ID_ATTR);
		
		if(requestIdFromMDC != null) {
			//
			// Get requestId from MDC context and add to header.
			if (requestContext.getHeaders().containsKey(HEADER_REQUEST_ID_ATTR)) {
				requestContext.getHeaders().remove(HEADER_REQUEST_ID_ATTR);
			}
			
			requestContext.getHeaders().add(HEADER_REQUEST_ID_ATTR, requestIdFromMDC.toString());
		} else {
			//
			// No requestId is available, create a new one.
			final String newRequestId = UUID.randomUUID().toString();
			LOGGER.log(Level.INFO, LOG_PREFIX + "filter - no requestId in MDC available, create a new one [requestId=" + newRequestId + "]");
			
			requestContext.getHeaders().add(HEADER_REQUEST_ID_ATTR, newRequestId);
		}
	}
}
