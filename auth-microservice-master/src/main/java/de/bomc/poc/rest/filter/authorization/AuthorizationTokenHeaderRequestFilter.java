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
 * Copyright (c): BOMC, 2015
 */
package de.bomc.poc.rest.filter.authorization;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;

/**
 * A request filter for setting the authorization token as header parameter.
 *
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
public class AuthorizationTokenHeaderRequestFilter implements ClientRequestFilter {
	private static final Logger LOGGER = Logger.getLogger(AuthorizationTokenHeaderRequestFilter.class);
	public static final String AUTHORIZATION_PROPERTY = "X-BOMC-AUTHORIZATION";
	private String authorizationTokenHeader;

	/**
	 * Creates a new instance of
	 * <code>AuthorizationTokenHeaderRequestFilter</code>.
	 *
	 * @param authorizationTokenHeader
	 *            contains the token of the calling client.
	 *
	 * @throws IllegalArgumentException
	 *             if given parameter is empty or null.
	 */
	public AuthorizationTokenHeaderRequestFilter(final String authorizationTokenHeader) {
		if (authorizationTokenHeader != null && !authorizationTokenHeader.isEmpty()) {
			this.authorizationTokenHeader = authorizationTokenHeader;
		} else {
			throw new IllegalArgumentException("authorizationTokenHeader must not be null!");
		}
	}

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		LOGGER.debug("AuthorizationTokenHeaderRequestFilter#filter [path=" + requestContext.getUri().getPath() + "]");

		// Add header to define the authorizationToken header.
		if (requestContext.getHeaders().get(AUTHORIZATION_PROPERTY) != null) {
			LOGGER.debug("AuthorizationTokenHeaderRequestFilter#filter - header '" + AUTHORIZATION_PROPERTY
					+ "' is not null. [header="
					+ requestContext.getHeaders().get(AUTHORIZATION_PROPERTY).iterator().next() + "]");
			requestContext.getHeaders().get(AUTHORIZATION_PROPERTY).clear();
		}

		requestContext.getHeaders().add(AUTHORIZATION_PROPERTY, this.authorizationTokenHeader);

		// Log all headers.
		MultivaluedMap<String, Object> headers = requestContext.getHeaders();
		Iterator<?> it = headers.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<?, ?> pair = (Map.Entry<?, ?>) it.next();

			LOGGER.debug("AuthorizationTokenHeaderRequestFilter#filter [header=" + pair.getKey() + ", value="
					+ pair.getValue() + "]");
		}
	}
}

