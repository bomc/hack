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

/**
 * Auth microservice MBean info interface. MBeanController uses the interface to generate
 * JMX object name.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public interface MBeanInfo {
	
	/**
	 * @return a string identifying the MBean
	 */
	String getName();
	
	/**
	 * @return the start time of this mbean.
	 */
	String getStartTime();
}
