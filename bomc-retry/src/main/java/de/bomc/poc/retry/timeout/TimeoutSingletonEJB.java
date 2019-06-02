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
*    © Kanton Basel-Stadt 2018
*
* </pre>
*/
package de.bomc.poc.retry.timeout;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.bomc.poc.logging.qualifier.LoggerQualifier;

/**
 * Creates a timer that
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a> created
 *         10.01.2018
 */
@Startup
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class TimeoutSingletonEJB {

	private static final String LOG_PREFIX = "SingletonTimeoutEJB#";
	@Inject
	@LoggerQualifier
	private Logger logger;
	@Resource
	private TimerService timerService;

	/**
	 * Creates a singleton timer by the given delay in milliseconds.
	 * 
	 * @param delay
	 *            the given delay in milliseconds.
	 * @return a countDownLatch that notifies the waiter after the timer is
	 *         expired.
	 */
	@Lock(javax.ejb.LockType.WRITE)
	public CountDownLatch createSingleActionTimer(final long delay) {
		this.logger.debug(LOG_PREFIX + "createSingleActionTimer [delay=" + delay + "]");

		final Collection<Timer> collectionTimer = this.timerService.getTimers();
		collectionTimer.forEach(timer -> this.logger.debug("createSingleActionTimer [timer="
				+ ((LatchWrapper) timer.getInfo()).getUuid() + ", timer.remaining=" + timer.getTimeRemaining() + "]"));

		final CountDownLatch timeoutNotifyingLatch = new CountDownLatch(1);
		final LatchWrapper latchWrapper = new LatchWrapper(timeoutNotifyingLatch);
		final TimerConfig timerConfig = new TimerConfig(latchWrapper, false);
		final Timer timer = this.timerService.createSingleActionTimer(delay, timerConfig);

		this.logger.debug(LOG_PREFIX + "createSingleActionTimer [delay=" + delay + ", timer.remaining="
				+ timer.getTimeRemaining() + "]");

		return timeoutNotifyingLatch;
	}

	@Timeout
	private void onTimeout(final Timer timer) {
		this.logger.debug(LOG_PREFIX + "onTimeout - timeout is invoked [this=" + this + ", timer=" + timer + "]");

		final LatchWrapper latchWrapper = (LatchWrapper) timer.getInfo();
		latchWrapper.countDown();
	}

	// _______________________________________________
	// Inner classes
	// -----------------------------------------------
	private class LatchWrapper implements Serializable {

		/**
		 * The serial UID.
		 */
		private static final long serialVersionUID = 1963075435106440022L;
		//
		// Further member variables.
		private final CountDownLatch countDownLatch;
		private final String uuid;
		private final long start;

		/**
		 * Creates a new instance of <code>LatchWrapper</code> co.
		 * 
		 * @param countDownLatch
		 *            unlock the waiting thread.
		 */
		public LatchWrapper(final CountDownLatch countDownLatch) {
			this.uuid = UUID.randomUUID().toString();

			logger.debug(LOG_PREFIX + "LatchWrapper#co [uuid=" + this.uuid + "]");

			this.start = System.currentTimeMillis();
			this.countDownLatch = countDownLatch;
		}

		public void countDown() {
			logger.debug(LOG_PREFIX + "LatchWrapper#countDown [uuid=" + this.uuid + ", delay="
					+ (System.currentTimeMillis() - this.start) + "]");

			this.countDownLatch.countDown();
		}

		public String getUuid() {
			return this.uuid;
		}
	}
}
