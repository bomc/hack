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

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.jboss.resteasy.annotations.interception.ClientInterceptor;

/**
 * A request filter for setting the authorization token as cookie.
 *
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
@Provider
@ClientInterceptor
public class AuthorizationTokenCookieRequestFilter implements ClientRequestFilter {
	private static final Logger LOGGER = Logger.getLogger(AuthorizationTokenCookieRequestFilter.class);
	private static final String AUTHORIZATION_PROPERTY = "X-BOMC-AUTHORIZATION";
	private String authorizationTokenHeader;

	/**
	 * Creates a new instance of
	 * <code>AuthorizationTokenCookieRequestFilter</code>.
	 *
	 * @param authorizationTokenHeader
	 *            contains the token of the calling client.
	 *
	 * @throws IllegalArgumentException
	 *             if given parameter is empty or null.
	 */
	public AuthorizationTokenCookieRequestFilter(final String authorizationTokenHeader) {
		if (authorizationTokenHeader != null && !authorizationTokenHeader.isEmpty()) {
			this.authorizationTokenHeader = authorizationTokenHeader;
		} else {
			throw new IllegalArgumentException("authorizationTokenHeader must not be null!");
		}
	}

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		LOGGER.debug("AuthorizationTokenCookieRequestFilter#filter [path=" + requestContext.getUri().getPath() + "]");

		final Cookie cookie = new Cookie(AUTHORIZATION_PROPERTY, authorizationTokenHeader);
		requestContext.getHeaders().putSingle(HttpHeaders.COOKIE, cookie);
	}
}

