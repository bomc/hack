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
 * An AppInvalidPasswordException indicates that a password is not confirm with
 * the defined rules.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class AppInvalidPasswordException extends AppAuthRuntimeException {

	private static final long serialVersionUID = -5096102582367759165L;

	/**
	 * Create a new AppInvalidPasswordException.
	 */
	public AppInvalidPasswordException() {
		super();
	}

	/**
	 * Create a new AppInvalidPasswordException.
	 * 
	 * @param message
	 *            The message text
	 */
	public AppInvalidPasswordException(final String message) {
		super(message);
	}

	/**
	 * Create a new AppInvalidPasswordException.
	 *
	 * @param message
	 *            The message text
	 * @param isLogged
	 *            mark the exception is logged.
	 */
	public AppInvalidPasswordException(final String message, final boolean isLogged) {
		super(message, isLogged);
	}

	/**
	 * Create a new AppInvalidPasswordException.
	 * 
	 * @param cause
	 *            The root cause of the exception
	 */
	public AppInvalidPasswordException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Create a new AppInvalidPasswordException.
	 * 
	 * @param cause
	 *            Root cause
	 * @param isLogged
	 *            mark the exception is logged.
	 */
	public AppInvalidPasswordException(final Throwable cause, final boolean isLogged) {
		super(cause, isLogged);
	}
	
	/**
	 * Create a new AppInvalidPasswordException.
	 * 
	 * @param message
	 *            The message text
	 * @param cause
	 *            The root cause of the exception
	 */
	public AppInvalidPasswordException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
