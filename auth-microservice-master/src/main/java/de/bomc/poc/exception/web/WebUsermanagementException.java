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

import javax.ws.rs.core.Response;
import java.util.Locale;

/**
 * This exception is thrown during usermangement operations.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class WebUsermanagementException extends WebAuthRuntimeException {

    /**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 8129018047109359820L;

	/**
     * Creates a new instance of <code>WebUsermanagementException</code>.
     * @param apiError the given apiError object.
     * @param locale   the given locale.
     */
    public WebUsermanagementException(ApiError apiError, Locale locale) {
        super(apiError, locale);
    }

    /**
     * Creates a new instance of <code>WebUsermanagementException</code>.
     * @param exception the given exception.
     * @param locale    the given locale.
     */
    public WebUsermanagementException(Exception exception, Locale locale) {
        super(exception, locale);
    }

    /**
     * Creates a new instance of <code>WebUsermanagementException</code>.
     * @param status     the current response status.
     * @param errorCode  the given error code.
     * @param messageKey the given message key for i18n.
     * @param locale     the given locale.
     */
    public WebUsermanagementException(Response.Status status, String errorCode, String messageKey, Locale locale) {
        super(status, errorCode, messageKey, locale);
    }
}
