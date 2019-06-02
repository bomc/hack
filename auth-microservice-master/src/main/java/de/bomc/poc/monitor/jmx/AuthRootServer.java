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
package de.bomc.poc.monitor.jmx;

import java.time.format.DateTimeFormatter;

/**
 * The implementation of the root mbean.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class AuthRootServer extends AuthMBean implements AuthRootServerMBean {

	/**
	 * Creates a instance of <code>AuthRootServer</code>.
	 * 
	 */
	public AuthRootServer() {
		super("AuthMicroservice");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(AuthMBean.DATE_TIME_FORMATTER);
		final String strStartTime = startTime.format(formatter).toString();

		return "AuthRootServer [startTime=" + strStartTime + ", name=" + name + "]";
	}

	@Override
	public void start() {
		// Nothing todo here.

	}

	@Override
	public void stop() {
		// Nothing todo here.

	}
}
