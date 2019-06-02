/**
 * <pre>
 *
 * Last change:
 *
 *  by: $Author$
 *
 *  date: $Date$
 *
 *  revision: $Revision$
 *
 *    © Bomc 2018
 *
 * </pre>
 */
package de.bomc.poc.rest.endpoints.arq;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.log4j.Logger;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.rest.api.HystrixDTO;
import de.bomc.poc.retry.interceptor.RetryHandlerInterceptor;
import de.bomc.poc.retry.qualifier.RetryPolicy;
import de.bomc.poc.retry.timeout.TimeoutSingletonEJB;

/**
 * A mock ejb for testing the retry interceptor.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
@Stateless
public class MockEJB {

	private static final String LOG_PREFIX = "MockEJB#";
	//
	// Member variables
	private static final long INITIAL_INTERVAL = 100L;
	private static final double MULTIPLIER = 1.75d;
	private static final int MAX_RETRY_COUNT = 3;
	@Inject
	@LoggerQualifier
	private Logger logger;
	@EJB
	private TimeoutSingletonEJB timeoutSingletonEJB;

	@Interceptors(RetryHandlerInterceptor.class)
	@RetryPolicy(initialInterval = INITIAL_INTERVAL, multiplier = MULTIPLIER, maxRetryCount = MAX_RETRY_COUNT)
	public HystrixDTO invokeMethod() {
		this.logger.debug(LOG_PREFIX + "invokeMethod");

		final HystrixDTO hystrixDTO = new HystrixDTO();
		hystrixDTO.setRetry(true);

		return hystrixDTO;
	}
}
