/**
 * Project: Poc-upload
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: 2016-12-23 14:20:47 +0100 (Fr, 23 Dez 2016) $
 *
 *  revision: $Revision: 9598 $
 *
 * </pre>
 */
package de.bomc.poc.exception.web;

import javax.ejb.ApplicationException;

import de.bomc.poc.exception.core.web.ApiError;
import de.bomc.poc.exception.core.web.WebRuntimeException;

/**
 * This exception indicates an error in the web layer.
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 03.08.2016
 */
@ApplicationException
public class WebUploadRuntimeException extends WebRuntimeException {

    /**
     * The serial UID.
     */
    private static final long serialVersionUID = -1L;

    /**
     * Creates a new instance of <code>WebUploadRuntimeException</code>.
     * @param errorCode the error description.
     */
    public WebUploadRuntimeException(final ApiError errorCode) {
        super(errorCode);
    }

    /**
     * Creates a new instance of <code>WebUploadRuntimeException</code>.
     * @param message   the specified detail message.
     * @param errorCode the error description.
     */
    public WebUploadRuntimeException(final String message, final ApiError errorCode) {
        super(message, errorCode);
    }

    /**
     * Creates a new instance of <code>WebUploadRuntimeException</code>.
     * @param cause     the <code>Throwable</code> of the exception.
     * @param errorCode the error description.
     */
    public WebUploadRuntimeException(final Throwable cause, final ApiError errorCode) {
        super(cause, errorCode);
    }

    /**
     * Creates a new instance of <code>WebUploadRuntimeException</code>.
     * @param message   the specified detail message.
     * @param cause     the <code>Throwable</code> of the exception.
     * @param errorCode the error description.
     */
    public WebUploadRuntimeException(final String message, final Throwable cause, final ApiError errorCode) {
        super(message, cause, errorCode);
    }
}

