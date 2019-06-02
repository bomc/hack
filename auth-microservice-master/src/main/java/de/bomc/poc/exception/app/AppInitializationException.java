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
 * Thrown for startup config errors.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class AppInitializationException extends AppAuthRuntimeException {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 7274216345714816528L;

	/**
	 * Create a new AppInitializationException.
	 */
	public AppInitializationException() {
		super();
	}

	/**
	 * Create a new AppInitializationException.
	 * 
	 * @param message
	 *            The message text
	 */
	public AppInitializationException(final String message) {
		super(message);
	}

	/**
	 * Create a new AppInitializationException.
	 * 
	 * @param message
	 *            The message text.
	 * @param isLogged
	 *            mark the exception is logged.
	 */
	public AppInitializationException(final String message, final boolean isLogged) {
		super(message, isLogged);
	}

	/**
	 * Create a new AppInitializationException.
	 * 
	 * @param cause
	 *            The root cause of the exception
	 */
	public AppInitializationException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Create a new AppInitializationException.
	 * 
	 * @param cause
	 *            Root cause
	 * @param isLogged
	 *            mark the exception is logged.
	 */
	public AppInitializationException(final Throwable cause, final boolean isLogged) {
		super(cause, isLogged);
	}
	
	/**
	 * Create a new AppInitializationException.
	 * 
	 * @param message
	 *            The message text
	 * @param cause
	 *            The root cause of the exception
	 */
	public AppInitializationException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
