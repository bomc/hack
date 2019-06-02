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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

/**
 * Sets the header 'If-Modified-Since'.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 16.04.2019
 */
//Smaller numbers are first in the chain.
@Priority(value = Priorities.AUTHORIZATION + 200)
public class RestClientHeaderIfModifiedSinceFilter implements ClientRequestFilter {

	private static final String LOG_PREFIX = "RestClientHeaderIfModifiedSinceFilter#";
	private static final Logger LOGGER = Logger.getLogger(RestClientHeaderIfModifiedSinceFilter.class.getSimpleName());
	private static final String IF_MODIFIED_SINCE = "X-If-Modified-Since";
	private final String lastModifiedDate;

	public RestClientHeaderIfModifiedSinceFilter(final String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;

	}

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		LOGGER.log(Level.INFO, LOG_PREFIX + "filter [lastModifiedDate=" + this.lastModifiedDate + "]");

		if (requestContext.getHeaders().containsKey(IF_MODIFIED_SINCE)) {
			requestContext.getHeaders().remove(IF_MODIFIED_SINCE);
		}
		
		requestContext.getHeaders().add(IF_MODIFIED_SINCE, lastModifiedDate);
	}

}
