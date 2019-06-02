/**
 * Project: bomc-exception-lib-ext
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
package de.bomc.poc.exception.core;

import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.exception.core.web.ApiErrorResponseObject;
import de.bomc.poc.exception.core.web.WebRuntimeException;

/**
 * Collection of general utility methods with respect to working with
 * exceptions.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: bomc $ $Date: $
 * @since 09.02.2018
 */
public final class ExceptionUtil {

	// private logger members.
	private static final Logger LOGGER = Logger.getLogger(ExceptionUtil.class);
	private static final String LOG_PREFIX = "ExceptionUtil#";

	// public header codes.
	public static final String HTTP_HEADER_X_EXCEPTION_UUID = "X-EXCEPTION-UUID";
	public static final String HTTP_HEADER_X_ERROR_CODE = "X-ERROR-CODE";

	private ExceptionUtil() {
		// Prevents instantiation.
	}

	/**
	 * Unwrap the nested causes of given exception as long as until it is not an
	 * instance of the given type. If the given exception is already an instance
	 * of the given type, then it will directly be returned. Or if the
	 * exception, unwrapped or not, does not have a nested cause anymore and is
	 * not of given type, null is returned. This is particularly useful if you
	 * want to unwrap the real root cause out of a nested hierarchy.
	 * 
	 * @param <T>
	 *            the exception that has to be unwrapped.
	 * @param exception
	 *            The exception to be unwrapped.
	 * @param type
	 *            The type which needs to be unwrapped.
	 * @return The unwrapped root cause of given type, or null if no root cause
	 *         of given type found.
	 */
	public static <T extends Throwable> T unwrap(Throwable exception, Class<T> type) {
		while (exception != null && !type.isInstance(exception) && exception.getCause() != null) {
			exception = exception.getCause();
		}

		if (type.isInstance(exception)) {
			return type.cast(exception);
		}

		return null;
	}

	/**
	 * Returns <code>true</code> if the given exception or one of its nested
	 * causes is an instance of the given type.
	 * 
	 * @param <T>
	 *            the exception that has to be unwrapped.
	 * @param exception
	 *            The exception to be checked.
	 * @param type
	 *            The type to be compared to.
	 * @return <code>true</code> if the given exception or one of its nested
	 *         causes is an instance of the given type.
	 */
	public static <T extends Throwable> boolean is(Throwable exception, final Class<T> type) {
		for (; exception != null; exception = exception.getCause()) {
			if (type.isInstance(exception)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Error handling of a response with a http status not in the 200-family.
	 * 
	 * @param originalResponse
	 *            May be created by the error handling of another node or it may
	 *            be created by the web server when calling the other node. It
	 *            will NOT be closed here, this must be done in the calling
	 *            method.
	 * @return a new Response with status 500, if the originalResponse had a
	 *         status = 500 and contained an ApiErrorResponseObject. It will be
	 *         "re-packed" in the returned Response.
	 * @throws WebRuntimeException
	 *             if the Response was an error from the web server itself. Or
	 *             if the error object could not be unpacked. Or if the method
	 *             was misused.
	 */
	public static Response processErrorResponse(final Response originalResponse) {
		final ApiErrorResponseObject apiErrorResponseObject = extractApiResponseObject(originalResponse);
		final Response.ResponseBuilder builder = Response.serverError().entity(apiErrorResponseObject)
				.header(HTTP_HEADER_X_EXCEPTION_UUID, apiErrorResponseObject.getUuid())
				.header(HTTP_HEADER_X_ERROR_CODE, apiErrorResponseObject.getErrorCode());

		return builder.build();
	}

	/**
	 * Throws a WebRuntimeException containing the information from an error
	 * Response. Can be used by methods that call a web service but do not
	 * return a Response themselves.
	 * 
	 * @param originalResponse
	 *            The original error - response. It is the status 500 expected
	 *            and contains an ApiErrorResponseObject. If not, a
	 *            WebRuntimeException will be thrown.
	 * @return WebRuntimeException containing the information from the
	 *         originalResponse
	 */
	public static AppRuntimeException createExceptionFromErrorResponse(final Response originalResponse) {
		final ApiErrorResponseObject apiErrorResponseObject = extractApiResponseObject(originalResponse);

		// Close the response.
		originalResponse.close();

		try {
			final ErrorCode errorCode = BasisErrorCodeEnum.errorCodeFromString(apiErrorResponseObject.getErrorCode());
			final AppRuntimeException appRuntimeException = new AppRuntimeException(
					apiErrorResponseObject.getShortErrorCodeDescription(), errorCode);
			appRuntimeException.setUuid(apiErrorResponseObject.getUuid());

			return appRuntimeException;
		} catch (final AppRuntimeException ex) {
			return ex;
		}
	}

	/**
	 * Extract the error object from an error Response.
	 * 
	 * @param originalResponse
	 *            The original error - response. Status 500 is expected and
	 *            contains an ApiErrorResponseObject. If not, a
	 *            WebRuntimeException will be thrown.
	 * @return a new Response with status 500 is returned, if the
	 *         originalResponse was status = 500 and contained an
	 *         ApiErrorResponseObject. It will be "re-packed" in the returned
	 *         Response.
	 * @throws WebRuntimeException
	 *             if the Response was an error from the web server itself. Or
	 *             if the error object could not be unpacked. Or if the method
	 *             was misused.
	 */
	public static ApiErrorResponseObject extractApiResponseObject(final Response originalResponse) {

		final String localPrefix = LOG_PREFIX + "extractApiResponseObject ";
		if (originalResponse.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {

			final String uuid = originalResponse.getHeaderString(HTTP_HEADER_X_EXCEPTION_UUID);
			final String errorCode = originalResponse.getHeaderString(HTTP_HEADER_X_ERROR_CODE);

			// both, a uuid and an error code is expected.
			if (StringUtils.isNotEmpty(uuid) && StringUtils.isNotEmpty(errorCode)) {
				// the error Response was created by a preceding service; it
				// should contain a ApiErrorResponseObject
				LOGGER.warn(localPrefix + "Response-status is 500 -> return Response with existing error object");
				try {
					// re-package error object
					final ApiErrorResponseObject apiErrorResponseObject = originalResponse
							.readEntity(ApiErrorResponseObject.class);
					return apiErrorResponseObject;
				} catch (final Exception ex) {
					// should never happen if the error was processed correctly
					final String errorMessage = "error in error handling: could not unpack error object. Will throw new Exception";
					LOGGER.error(localPrefix + errorMessage, ex);
					// throw exception here, interceptor will catch it and
					// handle it standardized.
					throw new AppRuntimeException(errorMessage, BasisErrorCodeEnum.UNEXPECTED_10000);
				}
			} else {
				// http 500 because of technical reason (not created by our
				// error handling)
				LOGGER.error(localPrefix + "A response with Http internal_server_error [status="
						+ originalResponse.getStatus() + "] was received, but either no uuid or error code was set. "
						+ "Thus could be a communication problem, not caused by the application.");
				// throw exception here, interceptor will catch it and handle it
				// standardized.
				throw new WebRuntimeException(BasisErrorCodeEnum.CONNECTION_FAILURE_10500);
			}
		} else if (Response.Status.Family.SUCCESSFUL.equals(originalResponse.getStatusInfo().getFamily())) {
			// programming error: invalid use of this method. It must only be
			// used for Responses with http errors! Should never happen in
			// production
			final String errorMessage = "Response-Code is in Family.SUCCESSFUL: " + originalResponse.getStatus()
					+ ". this method is only for processing errors!";
			LOGGER.error(localPrefix + errorMessage);
			// throw exception here, interceptor will catch it and handle it
			// standardized.
			throw new WebRuntimeException(errorMessage, BasisErrorCodeEnum.UNEXPECTED_10000);
		} else {
			// any other Http-error (404, 401, ..) because of technical reasons
			// (when calling a server). Not created by our error handling
			LOGGER.error(localPrefix + "Response for service call is not expected INTERNAL_SERVER_ERROR (500), but: "
					+ originalResponse.getStatus()
					+ ", thus we assume a communication problem, not caused by our implementations.");
			// throw exception here, interceptor will catch it and handle it
			// standardized.
			throw new WebRuntimeException(BasisErrorCodeEnum.CONNECTION_FAILURE_10500);
		}
	}
}