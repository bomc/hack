/**
 * Project: MY_POC
 * <p/>
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
 * <p/>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.cdi.monitor.arq;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.bomc.poc.exception.app.AppMonitorException;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.monitor.qualifier.PerformanceTrackingQualifier;

/**
 * A mock object for testing the performance tracking.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class SutMethodCdiBeanMock {
	@Inject
	@LoggerQualifier
	private Logger logger;

	@PerformanceTrackingQualifier
	public void methodUnderTest1() {
		this.logger.debug("SutMethodCdiBeanMock#methodUnderTest1 - started.");

		try {
			TimeUnit.MILLISECONDS.sleep(getRandomNumberInRange(100, 500));
		} catch (InterruptedException e) {
			// Ignore
		}

		this.logger.debug("SutMethodCdiBeanMock#methodUnderTest1 - finished.");
	}

	@PerformanceTrackingQualifier
	public void methodUnderTest2() {
		this.logger.debug("SutMethodCdiBeanMock#methodUnderTest2 - started.");

		try {
			TimeUnit.MILLISECONDS.sleep(getRandomNumberInRange(100, 500));
		} catch (InterruptedException e) {
			// Ignore
		}

		this.logger.debug("SutMethodCdiBeanMock#methodUnderTest2 - finished.");
	}

	@PerformanceTrackingQualifier
	public void methodUnderTest3() {
		this.logger.debug("SutMethodCdiBeanMock#methodUnderTest3 - started.");

		try {
			TimeUnit.MILLISECONDS.sleep(getRandomNumberInRange(100, 500));
		} catch (InterruptedException e) {
			// Ignore
		}
		
		throw new AppMonitorException();
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
