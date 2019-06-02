/**
 * Project: MY_POC
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
 * Copyright (c): BOMC, 2015
 */
package de.bomc.poc.logger.producer;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import de.bomc.poc.logger.qualifier.LoggerQualifier;
import org.apache.log4j.Logger;

/**
 * A factory for creating a logger instance.
 *  
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
public class LoggerProducer {

	@Produces
	@LoggerQualifier
	public Logger getPrintLog(final InjectionPoint injectionPoint) {
		final LoggerQualifier loggerQualifier = injectionPoint.getAnnotated().getAnnotation(LoggerQualifier.class);

		if (loggerQualifier != null && !loggerQualifier.logPrefix().equals(LoggerQualifier.DEFAULT_PREFIX)) {
			return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName()
					+ LoggerQualifier.DEFAULT_PREFIX + loggerQualifier.logPrefix());
		} else {
			return Logger.getLogger(
					injectionPoint.getMember().getDeclaringClass().getName());
		}
	}
}
