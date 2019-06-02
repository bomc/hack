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
package de.bomc.poc.rest.filter.uid;

import org.apache.log4j.Logger;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.UUID;

/**
 * A filter to add the header attribute requestId.
 * This filter checks every request if a requestId as a header parameter is set.
 * If the parameter is set the parameter will be adopted, otherwise a new requestId will be generated as {@link UUID}.
 * The parameter HEADER_REQUEST_ID_ATTR is defined in {@link MDCFilter}.
 *
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
public class UIDHeaderRequestFilter implements ClientRequestFilter {

	private static final Logger LOGGER = Logger.getLogger(UIDHeaderRequestFilter.class);
	private String requestId = null;

	public UIDHeaderRequestFilter() {

	}

	public UIDHeaderRequestFilter(final String requestId) {
		this.requestId = requestId;
	}

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		LOGGER.debug("UIDHeaderRequestFilter#filter [requestContext=" + requestContext.toString() + "]");

		final MultivaluedMap<String, Object> headers = requestContext.getHeaders();

		if (requestId == null || requestId.isEmpty()) {
			headers.add(MDCFilter.HEADER_REQUEST_ID_ATTR, UUID.randomUUID().toString());
		} else {
			headers.add(MDCFilter.HEADER_REQUEST_ID_ATTR, requestId);
		}
	}
}

