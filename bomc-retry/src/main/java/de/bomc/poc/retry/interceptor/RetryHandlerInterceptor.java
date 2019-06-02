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
package de.bomc.poc.retry.interceptor;

import org.apache.log4j.Logger;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.rest.api.HystrixDTO;
import de.bomc.poc.retry.qualifier.RetryPolicy;
import de.bomc.poc.retry.timeout.TimeoutSingletonEJB;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 * A interceptor that starts a retry. The start requiremnet for a retry is
 * indicated by the HystrixDTO.isRetry.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 *         created 10.01.2018
 */
public class RetryHandlerInterceptor {

	private static final String LOG_PREFIX = "RetryHandlerInterceptor#";
	private static final Long JITTER = 1000L;
	@Inject
	@LoggerQualifier
	private Logger logger;
	@EJB
	private TimeoutSingletonEJB timeoutSingletonEJB;

	@AroundInvoke
	public Object interceptAndRetryIfNeeded(final InvocationContext ic) {
		this.logger.debug(LOG_PREFIX + "interceptAndRetryIfNeeded [class=" + ic.getTarget().getClass().getName()
				+ ", method=" + ic.getMethod().getName() + "]");

		Object result = null;

		final RetryPolicy policy = ic.getMethod().getAnnotation(RetryPolicy.class);

		if (policy == null) {
			this.logger.debug(String.format(
					LOG_PREFIX
							+ "interceptAndRetryIfNeeded - method '%s' from class '%s' is being retried by thread %s",
					ic.getMethod().getName(), ic.getTarget().getClass().getName(), Thread.currentThread().getName()));
		}

		// Read retry data from policy (Qualifier).
		final int maxRetryCount = policy.maxRetryCount();
		final long initialInterval = policy.initialInterval();
		final double multiplier = policy.multiplier();

		// Create backoff instance.
		final ExponentialBackOff backoff = new ExponentialBackOff(initialInterval, multiplier, maxRetryCount);
		final BackOffExecution backOffExecution = backoff.start();

		boolean failed = true;
		long delay = 0;

		do {
			try {
				if (backOffExecution.getRetryCount() > 0) {
					this.logger.debug(String.format(
							LOG_PREFIX
									+ "interceptAndRetryIfNeeded - method '%s' from class '%s' is being retried by thread %s",
							ic.getMethod().getName(), ic.getTarget().getClass().getName(),
							Thread.currentThread().getName()));
				}

				// Invoke method on EJB.
				result = ic.proceed();

				final HystrixDTO hystrixDTO = (HystrixDTO) result;

				if (hystrixDTO.isRetry()) {
					delay = backOffExecution.nextBackOff();

					if (delay != -1) {
						// Unlock while loop.
						failed = true;
						// Sleep for a while...
						final CountDownLatch timeoutNotifyingLatch = this.timeoutSingletonEJB
								.createSingleActionTimer(delay);

						// isTimeoutInvoked is 'true' if the count reached zero,
						// means the started timer is running off and 'false' if
						// the waiting time elapsed before the count reached
						// zero.
						final boolean isTimeoutInvoked = timeoutNotifyingLatch.await(delay + JITTER,
								TimeUnit.MILLISECONDS);

						this.logger.info(LOG_PREFIX + "interceptAndRetryIfNeeded awake from sleep [delay=" + delay
								+ ", isTimeoutInvoked=" + isTimeoutInvoked + "]");
					} else {
						failed = false;
					}
				} else {
					// Stop running while loop.s
					failed = false;
				}
			} catch (final Exception ex) {
				this.logger.error(LOG_PREFIX + "interceptAndRetryIfNeeded - exception encountered [message="
						+ ex.getMessage() + "]");

				final HystrixDTO hystrixDTO = new HystrixDTO();
				hystrixDTO.setErrorMsg(ex.getMessage());
				hystrixDTO.setFallback(false);
				hystrixDTO.setRetry(false);

				return hystrixDTO;
			}
		} while (failed && (delay != -1));

		return result;
	}
}
