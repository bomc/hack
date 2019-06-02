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
package de.bomc.poc.exception.web;

import de.bomc.poc.exception.handling.ApiError;
import de.bomc.poc.exception.handling.ApiErrorResponse;

import javax.ws.rs.core.Response;
import java.util.Locale;

/**
 * A WebAuthRuntimeException is an unchecked top-level exception for all unhandled runtime exceptions of http requests.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class WebAuthRuntimeException extends RuntimeException {
    /**
	 * The serial UID.
	 */
	private static final long serialVersionUID = -6222778081590945341L;
	public static final String HTTP_HEADER_X_ERROR = "X-ERROR";
    public static final String HTTP_HEADER_X_ERROR_CODE = "X-ERROR-CODE";
    private Response httpResponse;

    /**
     * Creates a new instance of <code>WebAuthRuntimeException</code>.
     * @param apiError the given apiError object.
     * @param locale   the given locale.
     */
    public WebAuthRuntimeException(final ApiError apiError, final Locale locale) {
        httpResponse = createHttpResponse(new ApiErrorResponse(apiError, locale));
    }

    /**
     * Creates a new instance of <code>WebAuthRuntimeException</code>.
     * @param exception the given exception.
     * @param locale    the given locale.
     */
    public WebAuthRuntimeException(final Exception exception, final Locale locale) {
        httpResponse = createHttpResponse(new ApiErrorResponse(exception, locale));
    }

    /**
     * Creates a new instance of <code>WebAuthRuntimeException</code>.
     * @param status     the current response status.
     * @param errorCode  the given error code.
     * @param messageKey the given message key for i18n.
     * @param locale     the given locale.
     */
    public WebAuthRuntimeException(final Response.Status status, final String errorCode, final String messageKey, final Locale locale) {
        new ApiErrorResponse(status, errorCode, messageKey, locale);
    }

    public Response getHttpResponse() {
        return httpResponse;
    }

    private static Response createHttpResponse(final ApiErrorResponse apiErrorResponse) {
        return Response.status(apiErrorResponse.getStatus())
                       .entity(apiErrorResponse)
                       .header(HTTP_HEADER_X_ERROR, apiErrorResponse.getMessage())
                       .header(HTTP_HEADER_X_ERROR_CODE, apiErrorResponse.getErrorCode())
                       .build();
    }
}
