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
package de.bomc.poc.test.monitor.arq;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.monitor.qualifier.PerformanceTrackingQualifier;

/**
 * A cdi bean for testing the jmx monitoring.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@PerformanceTrackingQualifier
public class SutCdiBean {
	private static final String LOG_PREFIX = "SutCdiBean#";
	private long MIN_VALUE = 90;
	private long MAX_VALUE = 110;
	@Inject
	@LoggerQualifier
	private Logger logger;

	public SutCdiBean() {

	}

	public void methodUnderTest(final int warmup) {
		this.logger.debug(LOG_PREFIX + "methodUnderTest");

		if (warmup == 0) {
			final long duration = this.getRandomNumberInRange(MIN_VALUE, MAX_VALUE);

			// Only for testing.
			try {
				TimeUnit.MILLISECONDS.sleep(duration);
			} catch (InterruptedException e) {
				// Ignore...
			}
		} else {
			// Only for testing.
			try {
				TimeUnit.MILLISECONDS.sleep(warmup);
			} catch (InterruptedException e) {
				// Ignore...
			}
		}
	}

	/**
	 * Defines a value between min and max.
	 * 
	 * @param min
	 *            the minimum value.
	 * @param max
	 *            the maximum value.
	 * @return a random value between min and max.
	 */
	private long getRandomNumberInRange(long min, long max) {
		Random r = new Random();
		return r.longs(min, (max + 1)).limit(1).findFirst().getAsLong();

	}
}
