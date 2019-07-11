/**
 * Project: bomc-onion-architecture
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
package de.bomc.poc.invoice.application.scheduler;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.invoice.application.internal.AppErrorCodeEnum;

/**
 * OrderRestClientExceptionMapper command to handle exceptions.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Provider
public class OrderRestClientExceptionMapper implements ResponseExceptionMapper<AppRuntimeException> {

	private static final String LOG_PREFIX = "OrderRestClientExceptionMapper#";
	private static final Logger LOGGER = Logger.getLogger(OrderRestClientExceptionMapper.class.getName());

	@Override
	public boolean handles(final int statusCode, final MultivaluedMap<String, Object> headers) {
		if (headers != null) {
			final String strHeaders = headers.entrySet().stream()
					.map(entry -> entry.getKey() + " - " + entry.getValue()).collect(Collectors.joining(", "));

			LOGGER.log(Level.INFO, LOG_PREFIX + "handles [statusCode=" + statusCode + ", headers=" + strHeaders + "]");
		} else {
			LOGGER.log(Level.INFO, LOG_PREFIX + "handles [statusCode=" + statusCode + ", headers=null]");
		}

		return statusCode == 404 // Not Found
				|| statusCode == 409 // Conflict
				|| statusCode == 500; // App exception
	}

	@Override
	public AppRuntimeException toThrowable(final Response response) {
		LOGGER.log(Level.INFO, LOG_PREFIX + "toThrowable ");

		String errMsg = "";
		AppRuntimeException appRuntimeException;

		switch (response.getStatus()) {
		case 404:
			errMsg = LOG_PREFIX + "toThrowable - Resource is not available";
			appRuntimeException = new AppRuntimeException(errMsg, AppErrorCodeEnum.APP_REST_CLIENT_FAILURE_10601);

			LOGGER.log(Level.SEVERE, appRuntimeException.stackTraceToString());

			return appRuntimeException;
		case 409:
			errMsg = LOG_PREFIX + "toThrowable - Resource is not available";
			appRuntimeException = new AppRuntimeException(errMsg, AppErrorCodeEnum.APP_REST_CLIENT_FAILURE_10601);

			LOGGER.log(Level.SEVERE, appRuntimeException.stackTraceToString());

			return appRuntimeException;
		case 500:
			// TODO read ApiResponseObject.
			errMsg = LOG_PREFIX + "toThrowable - internal error on remote service.";
			appRuntimeException = new AppRuntimeException(errMsg, AppErrorCodeEnum.APP_REST_CLIENT_FAILURE_10601);

			LOGGER.log(Level.SEVERE, appRuntimeException.stackTraceToString());

			return appRuntimeException;
		}

		return null;
	}

}