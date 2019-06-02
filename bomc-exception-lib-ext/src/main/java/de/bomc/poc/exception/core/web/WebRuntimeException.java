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
package de.bomc.poc.exception.core.web;

import de.bomc.poc.exception.RootRuntimeException;
import de.bomc.poc.exception.core.ErrorCode;

import javax.ejb.ApplicationException;
import javax.ws.rs.core.Response;

/**
 * WebRuntimeException is a unchecked top-level exception for all unhandled
 * runtime exceptions of http requests.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: bomc $ $Date: $
 * @since 09.02.2018
 */
@ApplicationException
public class WebRuntimeException extends RootRuntimeException {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 3805300969197659650L;
	/**
	 * Defines the response status, default for WebRuntimeException is set to
	 * INTERNAL SERVER ERROR (500).
	 */
	private Response.Status responseStatus = Response.Status.INTERNAL_SERVER_ERROR;

	/**
	 * Creates a new instance of <code>WebRuntimeException</code>. The exception
	 * is initialized with response status 500 (Internal server error).
	 * 
	 * @param errorCode
	 *            the error description.
	 */
	public WebRuntimeException(final ErrorCode errorCode) {
		super(errorCode);
	}

	/**
	 * Creates a new instance of <code>WebRuntimeException</code>.
	 * 
	 * @param errorCode
	 *            the error description.
	 * @param responseStatus
	 *            the response status for this exception.
	 */
	public WebRuntimeException(final ErrorCode errorCode, final Response.Status responseStatus) {
		super(errorCode);

		if (responseStatus != null) {
			this.responseStatus = responseStatus;
		}
	}

	/**
	 * Creates a new instance of <code>WebRuntimeException</code>. The exception
	 * is initialized with response status 500 (Internal server error).
	 * 
	 * @param message
	 *            the specified detail message.
	 * @param errorCode
	 *            the error description.
	 */
	public WebRuntimeException(final String message, final ErrorCode errorCode) {
		super(message, errorCode);
	}

	/**
	 * Creates a new instance of <code>WebRuntimeException</code>.
	 * 
	 * @param message
	 *            the specified detail message.
	 * @param errorCode
	 *            the error description.
	 * @param responseStatus
	 *            the response status for this exception.
	 */
	public WebRuntimeException(final String message, final ErrorCode errorCode, final Response.Status responseStatus) {
		super(message, errorCode);

		if (responseStatus != null) {
			this.responseStatus = responseStatus;
		}
	}

	/**
	 * Creates a new instance of <code>WebRuntimeException</code>. The exception
	 * is initialized with response status 500 (Internal server error).
	 * 
	 * @param cause
	 *            the <code>Throwable</code> of the exception.
	 * @param errorCode
	 *            the error description.
	 */
	public WebRuntimeException(final Throwable cause, final ErrorCode errorCode) {
		super(cause, errorCode);
	}

	/**
	 * Creates a new instance of <code>WebRuntimeException</code>.
	 * 
	 * @param cause
	 *            the <code>Throwable</code> of the exception.
	 * @param errorCode
	 *            the error description.
	 * @param responseStatus
	 *            the response status for this exception.
	 */
	public WebRuntimeException(final Throwable cause, final ErrorCode errorCode, final Response.Status responseStatus) {
		super(cause, errorCode);

		if (responseStatus != null) {
			this.responseStatus = responseStatus;
		}
	}

	/**
	 * Creates a new instance of <code>AppRuntimeException</code>. The exception
	 * is initialized with response status 500 (Internal server error).
	 * 
	 * @param message
	 *            the specified detail message.
	 * @param cause
	 *            the <code>Throwable</code> of the exception.
	 * @param errorError
	 *            the error description.
	 */
	public WebRuntimeException(final String message, final Throwable cause, final ErrorCode errorError) {
		super(message, cause, errorError);
	}

	/**
	 * Creates a new instance of <code>AppRuntimeException</code>.
	 * 
	 * @param message
	 *            the specified detail message.
	 * @param cause
	 *            the <code>Throwable</code> of the exception.
	 * @param errorError
	 *            the error description.
	 * @param responseStatus
	 *            the response status for this exception.
	 */
	public WebRuntimeException(final String message, final Throwable cause, final ErrorCode errorError,
			final Response.Status responseStatus) {
		super(message, cause, errorError);

		if (responseStatus != null) {
			this.responseStatus = responseStatus;
		}
	}

	/**
	 * @return the response status code.
	 */
	public Response.Status getResponseStatus() {
		return this.responseStatus;
	}

	/**
	 * Wraps the <code>Throwable</code> in a Web-Application-RuntimeException.
	 * 
	 * @param exception
	 *            the given exception to wrap.
	 * @return a Web-Application-RuntimeException.
	 */
	public static WebRuntimeException wrap(final Throwable exception) {
		return wrap(exception, null);
	}

	/**
	 * Wraps the <code>Throwable</code> and the <code>ErrorCode</code> in a
	 * Web-Application-RuntimeException.
	 * 
	 * @param exception
	 *            the given exception to wrap.
	 * @param errorCode
	 *            the given errorCode.
	 * @return a Web-Application-RuntimeException.
	 */
	public static WebRuntimeException wrap(final Throwable exception, final ErrorCode errorCode) {
		if (exception != null) {
			if (exception instanceof WebRuntimeException) {
				final WebRuntimeException se = (WebRuntimeException) exception;

				if (errorCode != null && errorCode != se.getErrorCode()) {
					if (exception.getMessage() != null) {
						return new WebRuntimeException(exception.getMessage(), exception, errorCode);
					} else {
						return new WebRuntimeException(exception, errorCode);
					}
				}

				return se;
			} else {
				return new WebRuntimeException(exception.getMessage(), exception, errorCode);
			}
		} else {
			throw new NullPointerException("Parameter 'exception' can not be null! ");
		}
	}
}
