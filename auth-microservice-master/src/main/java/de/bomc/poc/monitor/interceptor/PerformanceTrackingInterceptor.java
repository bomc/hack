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
package de.bomc.poc.monitor.interceptor;

import java.io.Serializable;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.apache.log4j.Logger;

import de.bomc.poc.exception.app.AppAuthRuntimeException;
import de.bomc.poc.exception.app.AppMonitorException;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.monitor.controller.MBeanController;
import de.bomc.poc.monitor.jmx.performance.PerformanceTracking;
import de.bomc.poc.monitor.jmx.performance.PerformanceTrackingMXBean;
import de.bomc.poc.monitor.qualifier.PerformanceTrackingQualifier;

/**
 * <pre>
 * This interceptor that tracks the performance of a TYPE or a METHOD.
 * 
 * at- PerformanceTrackingQualifier
 * public class MyClass
 * 	...
 * 
 * or
 * 
 * at- PerformanceTrackingQualifier
 * public void myMethod
 * 	...
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 *
 */
@Interceptor
@PerformanceTrackingQualifier
public class PerformanceTrackingInterceptor implements Serializable {
	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = -2826181834330789307L;
	private static final String LOG_PREFIX = "PerformanceTrackingInterceptor#";
	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	private MBeanController mBeanController;

	/**
	 * Logs invocation duration and success of a method/service
	 * 
	 * @param invocationContext
	 * @return
	 * @throws Exception
	 */
	@AroundInvoke
	public Object logMethodEntry(InvocationContext invocationContext) throws Exception {
		this.logger.debug(LOG_PREFIX + "logMethodEntry");

		final PerformanceTrackingMXBean performanceTracking = (PerformanceTrackingMXBean) this.mBeanController
				.getMBeanBySimpleName(PerformanceTracking.class.getSimpleName());

		// Time in milliseconds.
		long timeInMs = System.currentTimeMillis();
		try {
			final Object response = invocationContext.proceed();

			final long duration = System.currentTimeMillis() - timeInMs;
			performanceTracking.track(invocationContext.getMethod().getDeclaringClass().getCanonicalName(),
					invocationContext.getMethod().getName(), duration, "success");

			return response;
		} catch (Exception ex) {
			final long duration = System.currentTimeMillis() - timeInMs;

			performanceTracking.track(invocationContext.getMethod().getDeclaringClass().getCanonicalName(),
					invocationContext.getMethod().getName(), duration, "exception occurs");

			final String errorMessage = LOG_PREFIX + "logMethodEntry - failed! ";

			if (ex instanceof AppAuthRuntimeException) {

				if (((AppAuthRuntimeException) ex).isLogged()) {
					this.logger.error(errorMessage + ex.getMessage());
				} else {
					this.logger.error(errorMessage, ex);
				}

				throw new AppMonitorException(ex, true);
			} else {
				this.logger.error(errorMessage, ex);

				throw new AppMonitorException(errorMessage, true);
			}

		}
	}
}
