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
package de.bomc.poc.exception.cdi.interceptor;

import de.bomc.poc.exception.cdi.qualifier.ExceptionHandlerQualifier;
import de.bomc.poc.exception.core.ErrorCode;
import de.bomc.poc.exception.core.ExceptionUtil;
import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.exception.core.event.ExceptionLogEvent;
import de.bomc.poc.exception.core.handler.WebRuntimeExceptionHandler;
import de.bomc.poc.exception.core.web.ApiErrorResponseObject;
import de.bomc.poc.exception.core.web.WebRuntimeException;

import javax.annotation.Priority;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;

/**
 * This interceptor handles a unified exception handling for REST invocations.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: bomc $ $Date: $
 * @since 09.02.2018
 */
@Interceptor
@ExceptionHandlerQualifier
@Priority(Interceptor.Priority.APPLICATION + 1)
public class ExceptionHandlerInterceptor {

	private static final String LOG_PREFIX = "ExceptionHandlerInterceptor#";
	// for Response-Http header
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	@Inject
	private Event<ExceptionLogEvent> event;

	@AroundInvoke
	public Object handleException(final InvocationContext context) {
		try {
			final Object proceedResponse;

			try {
				// Handles a invocation without an error.
				proceedResponse = context.proceed();
			} catch (final Exception ex) {
				//
				// Handles a invocation with an error.
				// Processing a unified exception handling.
				final Response errorResponse;

				if (ExceptionUtil.unwrap(ex, WebRuntimeException.class) != null) {
					errorResponse = this.createHttpResponse(ExceptionUtil.unwrap(ex, WebRuntimeException.class));
				} else if (ExceptionUtil.unwrap(ex, AppRuntimeException.class) != null) {
					errorResponse = this.createHttpResponse(ExceptionUtil.unwrap(ex, AppRuntimeException.class));
				} else if (ex.getCause() instanceof ConstraintViolationException) {
					throw (ConstraintViolationException) ex.getCause();
				} else {
					// default behavior also for unexpected errors in web layer.
					errorResponse = this.createHttpResponse(
							WebRuntimeExceptionHandler.handleException(LOG_PREFIX + "handleException", ex));
				}

				return errorResponse;
			}

			return proceedResponse;
		} catch (final Throwable th) {
			th.printStackTrace();
			return null;
		}
	}

	// _______________________________________________
	// Helper Methods
	// -----------------------------------------------

	private Response createHttpResponse(final WebRuntimeException webRuntimeException) {
		final ErrorCode errorCode = webRuntimeException.getErrorCode();
		final Response.Status responseStatus = webRuntimeException.getResponseStatus();
		final String uuid = webRuntimeException.getUuid();

		return this.createHttpResponse(errorCode, responseStatus, uuid);
	}

	private Response createHttpResponse(final AppRuntimeException appRuntimeException) {
		final ErrorCode errorCode = appRuntimeException.getErrorCode();
		final Response.Status responseStatus = Response.Status.INTERNAL_SERVER_ERROR;
		final String uuid = appRuntimeException.getUuid();

		return this.createHttpResponse(errorCode, responseStatus, uuid);
	}

	private Response createHttpResponse(final ErrorCode errorCode, final Response.Status responseStatus,
			final String uuid) {

		final ExceptionLogEvent exceptionLogEvent = ExceptionLogEvent.category(errorCode.getCategory().name())
				.shortErrorCodeDescription(errorCode.getShortErrorCodeDescription())
				.responseStatus(responseStatus.name()).uuid(uuid).build();

		// Fire event for logging to database.
		event.fire(exceptionLogEvent);

		return Response.status(responseStatus).entity(new ApiErrorResponseObject(uuid, responseStatus, errorCode))
				.header(ExceptionUtil.HTTP_HEADER_X_EXCEPTION_UUID, uuid)
				.header(ExceptionUtil.HTTP_HEADER_X_ERROR_CODE, errorCode.getShortErrorCodeDescription())
				.header(HEADER_CONTENT_TYPE, "application/json").build();
	}
}
