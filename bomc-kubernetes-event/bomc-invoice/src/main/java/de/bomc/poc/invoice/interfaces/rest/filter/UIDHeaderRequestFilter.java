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
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A filter (used by a REST client) to add the header attribute requestId. This
 * filter checks every request if a requestId as a header parameter is set. If
 * the parameter is set the parameter will be adopted, otherwise a new requestId
 * will be generated as {@link UUID}. The parameter HEADER_REQUEST_ID_ATTR is
 * defined in {@link MDCFilter}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 17.04.2019
 */
//Smaller numbers are first in the chain.
@Priority(value = Priorities.AUTHORIZATION + 200)
public class UIDHeaderRequestFilter implements ClientRequestFilter {

	private static final Logger LOGGER = Logger.getLogger(UIDHeaderRequestFilter.class.getName());
	private static final String LOG_PREFIX = "UIDHeaderRequestFilter#";
	private String requestId;

	/**
	 * Creates a new instance of <code>UIDHeaderRequestFilter</code> default#co.
	 */
	public UIDHeaderRequestFilter() {
		//
		LOGGER.log(Level.FINE, LOG_PREFIX + "co");
	}

	/**
	 * Creates a new instance of <code>UIDHeaderRequestFilter</code>.
	 * 
	 * @param requestId the given requestId, comes with client request. Can be null
	 *                  - a new one will be generaded
	 */
	public UIDHeaderRequestFilter(final String requestId) {
		LOGGER.log(Level.FINE, LOG_PREFIX + "co [requestId=" + requestId + "]");

		this.requestId = requestId;
	}

	/**
	 * Filters the header for a requestId. If a requestId is available, the given
	 * requestId is added to the header otherwise a new requestId is generated and
	 * added to the header.
	 * 
	 * @param requestContext the request context.
	 */
	@Override
	public void filter(final ClientRequestContext requestContext) {
		LOGGER.log(Level.FINE, LOG_PREFIX + "filter [requestId=" + this.requestId + "]");

		final MultivaluedMap<String, Object> headers = requestContext.getHeaders();

		if (this.requestId == null || this.requestId.isEmpty()) {
			// Set new requestId to instance variable to prevent a new setting for further
			// invocation.
			requestId = UUID.randomUUID().toString();
			
			LOGGER.log(Level.INFO, LOG_PREFIX + "request id was not set, generated a new one: " + requestId);
			
			headers.add(MDCFilter.HEADER_REQUEST_ID_ATTR, requestId);
		} else {
			headers.add(MDCFilter.HEADER_REQUEST_ID_ATTR, this.requestId);
		}
	}
}
