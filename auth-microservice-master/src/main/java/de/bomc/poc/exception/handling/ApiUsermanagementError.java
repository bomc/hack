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
public enum ApiUsermanagementError implements ApiError {

	U10001(Response.Status.BAD_REQUEST, "no_valid_username"),

	U10010(Response.Status.NOT_FOUND, "username_not_found"),

	U10020(Response.Status.INTERNAL_SERVER_ERROR, "configration_error");

	private final Response.Status status;

	private final String messageKey;

	/**
	 * Creates a new instance of <code>ApiUsermanagementError</code>.
	 * 
	 * @param status
	 *            the rest status.
	 * @param messageKey
	 *            the locale message key.
	 */
	ApiUsermanagementError(final Response.Status status, final String messageKey) {
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
