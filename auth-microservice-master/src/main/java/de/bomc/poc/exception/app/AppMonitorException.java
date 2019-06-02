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
 * A AppMonitorException is an unchecked top-level exception for all unhandled
 * runtime exceptions of this project.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class AppMonitorException extends AppAuthRuntimeException {

	private static final long serialVersionUID = -978429172808392497L;

	/**
	 * Create a new AppMonitorException.
	 */
	public AppMonitorException() {

	}

	/**
	 * Create a new AppMonitorException.
	 * 
	 * @param message
	 *            detail message
	 */
	public AppMonitorException(final String message) {
		super(message);
	}

	/**
	 * Create a new AppMonitorException.
	 * 
	 * @param message
	 *            detail message
	 * @param isLogged
	 *            mark the exception is logged.
	 */
	public AppMonitorException(final String message, final boolean isLogged) {
		super(message, isLogged);
	}

	/**
	 * Create a new AppMonitorException.
	 * 
	 * @param cause
	 *            Root cause
	 */
	public AppMonitorException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Create a new AppMonitorException.
	 * 
	 * @param cause
	 *            Root cause
	 * @param isLogged
	 *            mark the exception is logged.
	 */
	public AppMonitorException(final Throwable cause, final boolean isLogged) {
		super(cause, isLogged);
	}
	
	/**
	 * Create a new AppMonitorException.
	 * 
	 * @param message
	 *            Detail message
	 * @param cause
	 *            Root cause
	 */
	public AppMonitorException(final String message, final Throwable cause) {
		super(message, cause);
	}
}