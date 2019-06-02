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
package de.bomc.poc.exception.app;

import de.bomc.poc.exception.app.AppAuthRuntimeException;

/**
 * Thrown to indicate that a method has been passed an illegal or inappropriate argument.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class AppInvalidArgumentException extends AppAuthRuntimeException {

	private static final long serialVersionUID = 8653057738615241128L;

	/**
	 * Create a new AppInvalidArgumentException.
	 */
	public AppInvalidArgumentException() {
		super();
	}

	/**
	 * Create a new AppInvalidArgumentException.
	 *
	 * @param message
	 *            The message text
	 */
	public AppInvalidArgumentException(final String message) {
		super(message);
	}
	
	/**
	 * Create a new AppInvalidArgumentException.
	 *
	 * @param message
	 *            The message text
	 * @param isLogged
	 *            mark the exception is logged.         
	 */
	public AppInvalidArgumentException(final String message, final boolean isLogged) {
		super(message, isLogged);
	}
    
	/**
	 * Create a new AppInvalidArgumentException.
	 *
	 * @param cause
	 *            The root cause of the exception
	 */
	public AppInvalidArgumentException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Create a new AppInvalidArgumentException.
	 * 
	 * @param cause
	 *            Root cause
	 * @param isLogged
	 *            mark the exception is logged.
	 */
	public AppInvalidArgumentException(final Throwable cause, final boolean isLogged) {
		super(cause, isLogged);
	}
	
	/**
	 * Create a new AppInvalidArgumentException.
	 *
	 * @param message
	 *            The message text
	 * @param cause
	 *            The root cause of the exception
	 */
	public AppInvalidArgumentException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
