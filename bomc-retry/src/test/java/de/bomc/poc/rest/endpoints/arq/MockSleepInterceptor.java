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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.log4j.Logger;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.retry.timeout.TimeoutSingletonEJB;

/**
 * A mock interceptor that handles the retry mechanism.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
public class MockSleepInterceptor {

	private static final String LOG_PREFIX = "MockSleepInterceptor#";
	private static final Long JITTER = 1000L;
	// Other member variables.
	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	private TimeoutSingletonEJB timeoutSingletonEJB;

	@AroundInvoke
	public Object interceptAndRetryIfNeeded(final InvocationContext ic) {
		this.logger.debug(LOG_PREFIX + "interceptAndRetryIfNeeded [class=" + ic.getTarget().getClass().getName()
				+ ", method=" + ic.getMethod().getName() + "]");

		Object result = null;

		try {
			for (int i = 0; i < 2; i++) {

				// Invoke method on EJB.
				result = ic.proceed();

				// Sleep for a while...
				final CountDownLatch timeoutNotifyingLatch = this.timeoutSingletonEJB.createSingleActionTimer(350L);

				// isTimeoutInvoked is 'true' if the count reached zero,
				// means the started timer is running off and 'false' if
				// the waiting time elapsed before the count reached
				// zero.
				final boolean isTimeoutInvoked = timeoutNotifyingLatch.await(250 + JITTER, TimeUnit.MILLISECONDS);

				this.logger.info(LOG_PREFIX + "interceptAndRetryIfNeeded awake from sleep [delay=" + 250
						+ ", isTimeoutInvoked=" + isTimeoutInvoked + "]");
			}
		} catch (final Exception ex) {
			this.logger.error(
					LOG_PREFIX + "interceptAndRetryIfNeeded - exception encountered [message=" + ex.getMessage() + "]");

		}

		return result;
	}
}
