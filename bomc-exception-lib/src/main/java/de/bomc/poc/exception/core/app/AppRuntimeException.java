package de.bomc.poc.exception.core.app;

import de.bomc.poc.exception.RootRuntimeException;
import de.bomc.poc.exception.core.ErrorCode;

/**
 * This class represents the root exception for all RuntimeExceptions in the context of the application boundaries.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: 6790 $ $Author: tzdbmm $ $Date: 2016-07-19 09:06:34 +0200 (Di, 19 Jul 2016) $
 * @since 11.07.2016
 */
public class AppRuntimeException extends RootRuntimeException {

    /**
	 * The serial UUID.
	 */
	private static final long serialVersionUID = -1764073712704763074L;

	/**
     * Creates a new instance of <code>AppRuntimeException</code>.
     * @param errorCode the error description.
     */
    public AppRuntimeException(final ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * Creates a new instance of <code>AppRuntimeException</code>.
     * @param message   the specified detail message.
     * @param errorCode the error description.
     */
    public AppRuntimeException(final String message, final ErrorCode errorCode) {
        super(message, errorCode);
    }

    /**
     * Creates a new instance of <code>AppRuntimeException</code>.
     * @param cause     the <code>Throwable</code> of the exception.
     * @param errorCode the error description.
     */
    public AppRuntimeException(final Throwable cause, final ErrorCode errorCode) {
        super(cause, errorCode);
    }

    /**
     * Creates a new instance of <code>AppRuntimeException</code>.
     * @param message   the specified detail message.
     * @param cause     the <code>Throwable</code> of the exception.
     * @param errorCode the error description.
     */
    public AppRuntimeException(final String message, final Throwable cause, final ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    /**
     * Wraps the <code>Throwable</code> in a Application-RuntimeException.
     * @param exception the given exception to wrap.
     * @return a Application-RuntimeException.
     */
    public static AppRuntimeException wrap(final Throwable exception) {
        return wrap(exception, null);
    }

    /**
     * Wraps the <code>Throwable</code> and the <code>ErrorCode</code> in a Application-RuntimeException.
     * @param exception the given exception to wrap.
     * @param errorCode the given errorCode.
     * @return a Application-RuntimeException.
     */
    public static AppRuntimeException wrap(final Throwable exception, final ErrorCode errorCode) {
        if (exception != null) {
            if (exception instanceof AppRuntimeException) {
                final AppRuntimeException se = (AppRuntimeException)exception;

                if (errorCode != null && errorCode != se.getErrorCode()) {
                    return new AppRuntimeException(exception.getMessage(), exception, errorCode);
                }

                return se;
            } else {
                return new AppRuntimeException(exception.getMessage(), exception, errorCode);
            }
        } else {
            throw new NullPointerException("Parameter 'exception' can not be null! ");
        }
    }
}
