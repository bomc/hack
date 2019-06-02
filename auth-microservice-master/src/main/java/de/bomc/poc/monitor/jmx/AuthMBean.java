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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.log4j.Logger;

/**
 * A common class that is inhertied by all MBean's.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public abstract class AuthMBean implements MBeanInfo {
	private static final String LOG_PREFIX = "AuthMBean#";
	protected static final Logger LOGGER = Logger.getLogger(AuthMBean.class);
	public static final String DOMAIN_NAME = "de.bomc.auth.microservice";
	/**
	 * Formatter for date time.
	 */
	protected static final String DATE_TIME_FORMATTER = "dd.MM.yyyy HH:mm:ss.SSS";
	/**
	 * Holds the start time of this MBean.
	 */
	protected final LocalDateTime startTime;
	/**
	 * The name of this MBean.
	 */
	protected final String name;
	
	/**
	 * Creates a new instance of <code>AuthMBean</code>.
	 * 
	 * @param name
	 *            the registered name of the MBean.
	 */
	public AuthMBean(final String name) {
		LOGGER.debug(LOG_PREFIX + "co");

		this.name = name;
		this.startTime = LocalDateTime.now();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getName() {
		LOGGER.debug(LOG_PREFIX + "getName [name=" + this.name + "]");

		return this.name;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getStartTime() {
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
		final String strStartTime = startTime.format(formatter).toString();

		return strStartTime;
	}
	
	/**
	 * Initialize the mbean.
	 */
	public abstract void start();
	
	/**
	 * Cleanup resources.
	 */
	public abstract void stop();
}
