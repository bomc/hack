/**
 * Project: MY_POC
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.rest.filter.accept;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;

/**
 * A request filter for setting the version via MediaType as header parameter.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
public class AcceptHeaderRequestFilter implements ClientRequestFilter {

	private static final Logger LOGGER = Logger.getLogger(AcceptHeaderRequestFilter.class);
	
	private String acceptVersionHeader;
	
	/**
	 * Creates a new instance of <code>AcceptHeaderRequestFilter</code>.
	 * 
	 * @param acceptVersionHeader contains the version as MediaType value.
	 * 
	 * @throws IllegalArgumentException if given parameter is empty or null.
	 */
	public AcceptHeaderRequestFilter(final String acceptVersionHeader) {
		if(acceptVersionHeader != null && !acceptVersionHeader.isEmpty()) {
			this.acceptVersionHeader = acceptVersionHeader;
		} else {
			throw new IllegalArgumentException("acceptVersionHeader must not be null!");
		}
	}
	
	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		LOGGER.debug("AcceptHeaderRequestFilter#filter [path=" + requestContext.getUri().getPath() + "]");
		
		// Add header to define the api version.
		requestContext.getHeaders().get("Accept").clear();
		requestContext.getHeaders().add("Accept", this.acceptVersionHeader);
		
		// Log all headers.
		MultivaluedMap<String, Object> headers = requestContext.getHeaders();
		Iterator<?> it = headers.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<?, ?> pair = (Map.Entry<?, ?>)it.next();
			
			LOGGER.debug("AcceptHeaderRequestFilter#filter [header=" + pair.getKey() + ", value=" + pair.getValue() + "]");
		}
	}
}
