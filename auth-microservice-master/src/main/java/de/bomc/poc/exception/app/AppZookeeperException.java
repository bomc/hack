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
 * A AppZookeeperException indicates failures in connection with zookeeper
 * registrations and discovery.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class AppZookeeperException extends AppAuthRuntimeException {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = -4671667940639753548L;

	/**
	 * Create a new AppZookeeperException.
	 */
	public AppZookeeperException() {
		super();
	}

	/**
	 * Create a new AppZookeeperException.
	 *
	 * @param message
	 *            The message text
	 */
	public AppZookeeperException(final String message) {
		super(message);
	}

	/**
	 * Create a new AppZookeeperException.
	 * 
	 * @param message
	 *            detail message
	 * @param isLogged
	 *            mark the exception is logged.
	 */
	public AppZookeeperException(final String message, final boolean isLogged) {
		super(message, isLogged);
	}

	/**
	 * Create a new AppZookeeperException.
	 *
	 * @param cause
	 *            The root cause of the exception
	 */
	public AppZookeeperException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Create a new AppZookeeperException.
	 * 
	 * @param cause
	 *            Root cause
	 * @param isLogged
	 *            mark the exception is logged.
	 */
	public AppZookeeperException(final Throwable cause, final boolean isLogged) {
		super(cause, isLogged);
	}
	
	/**
	 * Create a new AppZookeeperException.
	 *
	 * @param message
	 *            The message text
	 * @param cause
	 *            The root cause of the exception
	 */
	public AppZookeeperException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
