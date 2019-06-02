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

import de.bomc.poc.retry.qualifier.RetryPolicy;

/**
 * <pre>
 * Implementation of {@link BackOff} that increases the back off period for each
 * retry attempt. When the interval has reached the {@link #setMaxInterval(long)
 * max interval}, it is no longer increased. Stops retrying once the
 * {@link #setMaxElapsedTime(long) max elapsed time} has been reached.
 *
 * Example: The default interval is DEFAULT_INITIAL_INTERVAL ms,
 * the default multiplier is DEFAULT_MULTIPLIER, and the default max
 * interval is DEFAULT_MAX_INTERVAL. For 10 attempts the sequence will be
 * as follows:
 *
 * request#     back off
 *
 *  1              2000
 *  2              3000
 *  3              4500
 *  4              6750
 *  5             10125
 *  6             15187
 *  7             22780
 *  8             30000
 *  9             30000
 * 10             30000
 *
 * NOTE: that the default max elapsed time is {@link Long#MAX_VALUE}. Use
 * {@link #setMaxElapsedTime(long)} to limit the maximum length of time
 * that an instance should accumulate before returning
 * {@link BackOffExecution#STOP}.
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 13.01.2018
 */
public class ExponentialBackOff implements BackOff {

	/**
	 * The logger.
	 */
	private static final String LOG_PREFIX = "ExponentialBackOff#";
	private static final Logger LOGGER = Logger.getLogger(ExponentialBackOff.class);
	// Init member variables.
	private long initialInterval = RetryPolicy.DEFAULT_INITIAL_INTERVAL;
	private double multiplier = RetryPolicy.DEFAULT_MULTIPLIER;
	private long maxInterval = RetryPolicy.DEFAULT_MAX_INTERVAL;
	private long maxElapsedTime = RetryPolicy.DEFAULT_MAX_ELAPSED_TIME;
	private int maxRetryCount = RetryPolicy.DEFAULT_MAX_RETRY_COUNT;

	/**
	 * Create an instance with the default settings.
	 */
	public ExponentialBackOff() {
		LOGGER.debug(LOG_PREFIX + "co");
	}

	/**
	 * Create an instance with the supplied settings.
	 * 
	 * @param initialInterval
	 *            the initial interval in milliseconds
	 * @param multiplier
	 *            the multiplier (should be greater than or equal to 1)
	 * @param maxRetryCount
	 *            the max retry count.
	 */
	public ExponentialBackOff(final long initialInterval, final double multiplier, final int maxRetryCount) {
		LOGGER.debug(LOG_PREFIX + "co [initialInterval=" + initialInterval + ", multiplier=" + multiplier
				+ ", maxRetryCount=" + maxRetryCount + "]");

		this.checkMultiplier(multiplier);

		this.initialInterval = initialInterval;
		this.multiplier = multiplier;
		this.maxRetryCount = maxRetryCount;
	}

	/**
	 * The initial interval in milliseconds.
	 * 
	 * @param initialInterval
	 *            initial interval in milliseconds.
	 */
	public void setInitialInterval(final long initialInterval) {
		LOGGER.debug(LOG_PREFIX + "setInitialInterval [initialInterval=" + initialInterval + "]");

		this.initialInterval = initialInterval;
	}

	/**
	 * @return the initial interval in milliseconds.
	 */
	public long getInitialInterval() {
		LOGGER.debug(LOG_PREFIX + "getInitialInterval [initialInterval=" + this.initialInterval + "]");

		return this.initialInterval;
	}

	/**
	 * The max retry count.
	 * 
	 * @param maxRetryCount
	 *            max retry count.
	 */
	public void setMaxRetryCount(final int maxRetryCount) {
		LOGGER.debug(LOG_PREFIX + "setMaxRetryCount [maxRetryCount=" + maxRetryCount + "]");

		this.maxRetryCount = maxRetryCount;
	}

	/**
	 * @return the max retry count.
	 */
	public int getMaxRetryCount() {
		LOGGER.debug(LOG_PREFIX + "getMaxRetryCount [maxRetryCount=" + this.maxRetryCount + "]");

		return this.maxRetryCount;
	}

	/**
	 * The value to multiply the current interval by for each retry attempt.
	 * 
	 * @param multiplier
	 *            value to multiply the current interval by for each retry
	 *            attempt.
	 */
	public void setMultiplier(final double multiplier) {
		LOGGER.debug(LOG_PREFIX + "setMultiplier [multiplier=" + multiplier + "]");

		this.checkMultiplier(multiplier);
		this.multiplier = multiplier;
	}

	/**
	 * @return the value to multiply the current interval by for each retry
	 *         attempt.
	 */
	public double getMultiplier() {
		LOGGER.debug(LOG_PREFIX + "getMultiplier [multiplier=" + this.multiplier + "]");

		return this.multiplier;
	}

	/**
	 * The maximum back off time.
	 * 
	 * @param maxInterval
	 *            maximum back off time.
	 */
	public void setMaxInterval(final long maxInterval) {
		LOGGER.debug(LOG_PREFIX + "setMaxInterval [maxInterval=" + maxInterval + "]");

		this.maxInterval = maxInterval;
	}

	/**
	 * @return the maximum back off time.
	 */
	public long getMaxInterval() {
		LOGGER.debug(LOG_PREFIX + "getMaxInterval [maxInterval=" + this.maxInterval + "]");

		return this.maxInterval;
	}

	/**
	 * The maximum elapsed time in milliseconds after which a call to
	 * {@link BackOffExecution#nextBackOff()} returns
	 * {@link BackOffExecution#STOP}.
	 * 
	 * @param maxElapsedTime
	 *            maximum elapsed time in milliseconds after which a call to
	 *            {@link BackOffExecution#nextBackOff()} returns
	 *            {@link BackOffExecution#STOP}.
	 */
	public void setMaxElapsedTime(final long maxElapsedTime) {
		LOGGER.debug(LOG_PREFIX + "setMaxElapsedTime [maxElapsedTime=" + maxElapsedTime + "]");

		this.maxElapsedTime = maxElapsedTime;
	}

	/**
	 * @return the maximum elapsed time in milliseconds after which a call to
	 *         {@link BackOffExecution#nextBackOff()} returns
	 *         {@link BackOffExecution#STOP}.
	 */
	public long getMaxElapsedTime() {
		LOGGER.debug(LOG_PREFIX + "getMaxElapsedTime [maxElapsedTime=" + this.maxElapsedTime + "]");

		return this.maxElapsedTime;
	}

	/**
	 * Start a backoff execution.
	 * 
	 * @return a {@link BackOffExecution} to handle the backoff execution.
	 */
	@Override
	public BackOffExecution start() {
		return new ExponentialBackOffExecution();
	}

	private void checkMultiplier(final double multiplier) {
		if (!(multiplier >= 1)) {
			LOGGER.warn(
					LOG_PREFIX + "checkMultiplier - Invalid multiplier '" + multiplier + "'. Should be greater than "
							+ "or equal to 1. A multiplier of 1 is equivalent to a fixed interval.");
		}
	}

	// _______________________________________________
	// Inner class
	// -----------------------------------------------
	private class ExponentialBackOffExecution implements BackOffExecution {

		// Other member variables.
		private long currentInterval = -1;
		private long currentElapsedTime;
		private int retryCount;

		@Override
		public long nextBackOff() {
			if (this.currentElapsedTime >= ExponentialBackOff.this.maxElapsedTime || retryCount == maxRetryCount) {
				return STOP;
			}

			final long nextInterval = this.computeNextInterval();
			this.currentElapsedTime += nextInterval;

			LOGGER.info(LOG_PREFIX + "ExponentialBackOffExecution#nextBackOff [nextInterval=" + nextInterval + "]");

			retryCount++;

			return nextInterval;
		}

		private long computeNextInterval() {
			final long maxInterval = ExponentialBackOff.this.getMaxInterval();

			if (this.currentInterval >= maxInterval) {

				return maxInterval;
			} else if (this.currentInterval < 0) {

				final long initialInterval = ExponentialBackOff.this.getInitialInterval();
				this.currentInterval = initialInterval < maxInterval ? initialInterval : maxInterval;
			} else {
				this.currentInterval = this.multiplyInterval(maxInterval);
			}

			return this.currentInterval;
		}

		private long multiplyInterval(final long maxInterval) {
			long i = this.currentInterval;
			i *= ExponentialBackOff.this.getMultiplier();

			return i > maxInterval ? maxInterval : i;
		}

		public int getRetryCount() {
			LOGGER.debug(LOG_PREFIX + "ExponentialBackOffExecution#getRetryCount [retryCount=" + retryCount + "]");

			return this.retryCount;
		}

		@Override
		public String toString() {
			final String sb = LOG_PREFIX + "ExponentialBackOff [" + "currentInterval="
					+ (this.currentInterval < 0 ? "n/a" : this.currentInterval + "ms") + ", multiplier="
					+ ExponentialBackOff.this.getMultiplier() + "]";
			return sb;
		}
	}
}
