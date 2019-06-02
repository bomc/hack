/**
 * Project: MY_POC_MICROSERVICE
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
package de.bomc.poc.exception.handling;

import javax.ws.rs.core.Response;

/**
 * API error object for usermanagement endpoints.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public enum ApiRuntimeError implements ApiError {

	O10001(Response.Status.INTERNAL_SERVER_ERROR, "unexpected_internal_error");

	private final Response.Status status;

	private final String messageKey;

	/**
	 * Creates a new instance of <code>ApiRuntimeError</code>.
	 *
	 * @param status
	 *            the rest status.
	 * @param messageKey
	 *            the locale message key.
	 */
    ApiRuntimeError(final Response.Status status, final String messageKey) {
		this.status = status;
		this.messageKey = messageKey;
	}

	/**
	 * Return the REST response status.
	 */
	@Override
	public Response.Status getStatus() {
		return this.status;
	}

	/**
	 * Return the error code.
	 */
	@Override
	public String getErrorCode() {
		return this.name();
	}

	/**
	 * Return the messsage key.
	 */
	@Override
	public String getMessageKey() {
		return this.messageKey;
	}
}

