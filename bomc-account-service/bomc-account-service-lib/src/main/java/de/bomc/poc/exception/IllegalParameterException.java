package de.bomc.poc.exception;

import de.bomc.poc.exception.core.ErrorCode;
import de.bomc.poc.exception.core.app.AppRuntimeException;

/**
 * Exception is thrown by illegal parameters.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.08.2016
 */
public class IllegalParameterException extends AppRuntimeException {
	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 5986243728160997400L;

	/**
	 * Creates a new instance of <code>IllegalParameterException</code>.
	 * 
	 * @param errorCode
	 *            the error description.
	 */
	public IllegalParameterException(final ErrorCode errorCode) {
		super(errorCode);
	}

	/**
	 * Creates a new instance of <code>IllegalParameterException</code>.
	 * 
	 * @param message
	 *            the specified detail message.
	 * @param errorCode
	 *            the error description.
	 */
	public IllegalParameterException(final String message, final ErrorCode errorCode) {
		super(message, errorCode);
	}

	/**
	 * Creates a new instance of <code>IllegalParameterException</code>.
	 * 
	 * @param cause
	 *            the <code>Throwable</code> of the exception.
	 * @param errorCode
	 *            the error description.
	 */
	public IllegalParameterException(final Throwable cause, final ErrorCode errorCode) {
		super(cause, errorCode);
	}

	/**
	 * Creates a new instance of <code>IllegalParameterException</code>.
	 * 
	 * @param message
	 *            the specified detail message.
	 * @param cause
	 *            the <code>Throwable</code> of the exception.
	 * @param errorCode
	 *            the error description.
	 */
	public IllegalParameterException(final String message, final Throwable cause, final ErrorCode errorCode) {
		super(message, cause, errorCode);
	}
}

