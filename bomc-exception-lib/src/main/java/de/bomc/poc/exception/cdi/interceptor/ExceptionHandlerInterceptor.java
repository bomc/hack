package de.bomc.poc.exception.cdi.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.core.Response;

import de.bomc.poc.exception.cdi.qualifier.ExceptionHandlerQualifier;
import de.bomc.poc.exception.core.ErrorCode;
import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.exception.core.web.ApiErrorResponseObject;
import de.bomc.poc.exception.core.web.WebRuntimeException;

/**
 * This interceptor handles a unified exception handling for REST invocations.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: 6790 $ $Author: tzdbmm $ $Date: 2016-07-19 09:06:34 +0200 (Di, 19 Jul 2016) $
 * @since 11.07.2016
 */
@Interceptor
@ExceptionHandlerQualifier
public class ExceptionHandlerInterceptor {

    private static final String HTTP_HEADER_X_ERROR_CODE = "X-ERROR-CODE";

    @AroundInvoke
    public Object handleException(final InvocationContext context) {
        final Object proceedResponse;

        try {
            // Handles a invocation without an error.
            proceedResponse = context.proceed();
        } catch (final Exception ex) {
            //
            // Handles a invocation with an error.
            // Processing a unified exception handling.
            final Response errorResponse;

            if (ex instanceof WebRuntimeException) {
                final ErrorCode errorCode = ((WebRuntimeException)ex).getErrorCode();
                final Response.Status responseStatus = ((WebRuntimeException)ex).getResponseStatus();
                final String uuid = ((WebRuntimeException)ex).getUuid();
                errorResponse = this.createHttpResponse(new ApiErrorResponseObject(uuid, responseStatus, errorCode));
            } else if(ex.getCause() instanceof WebRuntimeException) {
                final WebRuntimeException webRuntimeException = (WebRuntimeException)ex.getCause();
                final ErrorCode errorCode = webRuntimeException.getErrorCode();
                final Response.Status responseStatus = webRuntimeException.getResponseStatus();
                final String uuid = webRuntimeException.getUuid();
                errorResponse = this.createHttpResponse(new ApiErrorResponseObject(uuid, responseStatus, errorCode));
            } else if (ex instanceof AppRuntimeException) {
                final ErrorCode errorCode = ((AppRuntimeException)ex).getErrorCode();
                final String uuid = ((AppRuntimeException)ex).getUuid();
                errorResponse = this.createHttpResponse(new ApiErrorResponseObject(uuid, Response.Status.INTERNAL_SERVER_ERROR, errorCode));
            //} else if (ex.getCause() instanceof ConstraintViolationException) {
            //    throw (ConstraintViolationException)ex.getCause();
            } else {
                errorResponse = this.createHttpResponse(new ApiErrorResponseObject(null, Response.Status.INTERNAL_SERVER_ERROR, "Not a defined app exception."));
            }

            return errorResponse;
        }

        return proceedResponse;
    }

    private Response createHttpResponse(final ApiErrorResponseObject apiErrorResponseObject) {
        return Response.status(apiErrorResponseObject.getStatus())
                       .entity(apiErrorResponseObject)
                       .header(HTTP_HEADER_X_ERROR_CODE, apiErrorResponseObject.getShortErrorCodeDescription())
                       .build();
    }
}

