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
@PerformanceTrackingQualifier
public class SutTypeCdiBeanMock {
	@Inject
	@LoggerQualifier
	private Logger logger;

	public void methodUnderTest1() {
		this.logger.debug("SutTypeCdiBeanMock#methodUnderTest1 - started.");

		try {
			TimeUnit.MILLISECONDS.sleep(getRandomNumberInRange(100, 500));
		} catch (InterruptedException e) {
			// Ignore
		}

		this.logger.debug("SutTypeCdiBeanMock#methodUnderTest1 - finished.");
	}
	
	public void methodUnderTest2() {
		this.logger.debug("SutTypeCdiBeanMock#methodUnderTest2 - started.");

		try {
			TimeUnit.MILLISECONDS.sleep(getRandomNumberInRange(100, 500));
		} catch (InterruptedException e) {
			// Ignore
		}

		this.logger.debug("SutTypeCdiBeanMock#methodUnderTest2 - finished.");
	}

	public void methodUnderTest3() {
		this.logger.debug("SutTypeCdiBeanMock#methodUnderTest3 - started.");

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
