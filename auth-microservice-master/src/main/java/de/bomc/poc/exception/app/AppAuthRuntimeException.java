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

/**
 * A AppAuthRuntimeException is an unchecked top-level exception for all
 * unhandled runtime exceptions of this project.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class AppAuthRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -978429172808392497L;
	// Mark the exception as logged, so the exceptionmust not be logged to
	// console or file again.
	private boolean isLogged = false;

	/**
	 * Create a new AppAuthRuntimeException.
	 */
	public AppAuthRuntimeException() {

	}

	/**
	 * Create a new AppAuthRuntimeException.
	 * 
	 * @param message
	 *            detail message
	 */
	public AppAuthRuntimeException(final String message) {
		super(message);
	}

	/**
	 * Create a new AppAuthRuntimeException.
	 * 
	 * @param message
	 *            detail message
	 * @param isLogged
	 *            mark the exception is logged.
	 */
	public AppAuthRuntimeException(final String message, final boolean isLogged) {
		super(message);

		this.isLogged = isLogged;
	}

	/**
	 * Create a new AppAuthRuntimeException.
	 * 
	 * @param cause
	 *            Root cause
	 */
	public AppAuthRuntimeException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Create a new AppAuthRuntimeException.
	 * 
	 * @param cause
	 *            Root cause
	 * @param isLogged
	 *            mark the exception is logged.
	 */
	public AppAuthRuntimeException(final Throwable cause, final boolean isLogged) {
		super(cause);
		
		this.isLogged = isLogged;
	}
	
	/**
	 * Create a new AppAuthRuntimeException.
	 * 
	 * @param message
	 *            Detail message
	 * @param cause
	 *            Root cause
	 */
	public AppAuthRuntimeException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Mark the exception as logged, so the exceptionmust not be logged to
	 * console or file again.
	 * 
	 * @return true for the exception is already logged otherwise false.
	 */
	public boolean isLogged() {
		return isLogged;
	}

	public void setIsLogged(final boolean isLogged) {
		this.isLogged = isLogged;
	}
}
