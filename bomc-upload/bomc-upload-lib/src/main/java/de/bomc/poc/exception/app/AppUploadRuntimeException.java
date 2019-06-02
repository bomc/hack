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
package de.bomc.poc.exception.app;

import javax.ejb.ApplicationException;

import de.bomc.poc.exception.core.ErrorCode;
import de.bomc.poc.exception.core.app.AppRuntimeException;

/**
 * Exception is thrown in application context.
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 03.08.2016
 */
@ApplicationException
public class AppUploadRuntimeException extends AppRuntimeException {

    /**
     * The serial UID.
     */
    private static final long serialVersionUID = -5985889911505494054L;

    /**
     * Creates a new instance of <code>AppUploadRuntimeException</code>.
     * @param errorCode the error description.
     */
    public AppUploadRuntimeException(final ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * Creates a new instance of <code>AppUploadRuntimeException</code>.
     * @param message   the specified detail message.
     * @param errorCode the error description.
     */
    public AppUploadRuntimeException(final String message, final ErrorCode errorCode) {
        super(message, errorCode);
    }

    /**
     * Creates a new instance of <code>AppUploadRuntimeException</code>.
     * @param cause     the <code>Throwable</code> of the exception.
     * @param errorCode the error description.
     */
    public AppUploadRuntimeException(final Throwable cause, final ErrorCode errorCode) {
        super(cause, errorCode);
    }

    /**
     * Creates a new instance of <code>AppUploadRuntimeException</code>.
     * @param message   the specified detail message.
     * @param cause     the <code>Throwable</code> of the exception.
     * @param errorCode the error description.
     */
    public AppUploadRuntimeException(final String message, final Throwable cause, final ErrorCode errorCode) {
        super(message, cause, errorCode);
    }
}

