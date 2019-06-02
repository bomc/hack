/**
 * Project: bomc-timeout
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
 */
package de.bomc.poc.timeout.core;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import javax.ejb.EJB;
import javax.ejb.NoMoreTimeoutsException;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.TimerConfig;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Tests the timeout handling of the {@link TimeoutSingletonEJB}.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 */
@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TimeoutSingletonEJBTestIT {

	private static final String LOG_PREFIX = "SingletonTimerEJBTestIT#";
	private static final String ARCHIVE_NAME = "expired-timer-test";
	private static final Logger LOGGER = Logger.getLogger(TimeoutSingletonEJBTestIT.class);
	private static final int TIMER_CALL_WAITING_S = 5;
	private static final int TIMER_TIMEOUT_TIME_MS = 300;

	@EJB(mappedName = "java:module/TimeoutSingletonEJB")
	private TimeoutSingletonEJB timeoutSingletonEJB;

	// 'testable = true', means all the tests are running inside the container.
	@Deployment(testable = true)
	public static Archive<?> createDeployment() {
		final JavaArchive jarArchive = ShrinkWrap.create(JavaArchive.class, ARCHIVE_NAME + ".jar");
		jarArchive.addPackage(TimeoutSingletonEJBTestIT.class.getPackage());

		System.out.println("archiveContent: " + jarArchive.toString(true));

		return jarArchive;
	}

	/**
	 * Setup.
	 */
	@Before
	public void setupClass() {
		//
	}

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=SingletonTimerEJBTestIT#test010_timeout_pass
	 * </pre>
	 */
	@Test
	@InSequence(10)
	public void test010_timeout_pass() throws Exception {
		LOGGER.debug(LOG_PREFIX + "test010_timeout_pass");

		final CountDownLatch timeoutNotifier = new CountDownLatch(1);
		final CountDownLatch timeoutWaiter = new CountDownLatch(1);
		this.timeoutSingletonEJB.createSingleActionTimer(TIMER_TIMEOUT_TIME_MS, new TimerConfig(null, false),
				timeoutNotifier, timeoutWaiter);

		// Wait for the timeout to be invoked.
		final boolean timeoutInvoked = timeoutNotifier.await(TIMER_CALL_WAITING_S, TimeUnit.SECONDS);
		assertThat("timeout method was not invoked (within " + TIMER_CALL_WAITING_S + " seconds)", timeoutInvoked,
				equalTo(true));

		// The timer stays in timeout method - checking how the invoke of method
		// getNext and getTimeRemaining behave.
		try {
			this.timeoutSingletonEJB.invokeTimeRemaining();

			Assert.fail("Expecting exception " + NoMoreTimeoutsException.class.getSimpleName());
		} catch (NoMoreTimeoutsException e) {
			LOGGER.debug(LOG_PREFIX + "test010_timeout_pass - Expected exception " + e.getClass().getSimpleName()
					+ " was thrown on method getTimeRemaining");
		}
		try {
			this.timeoutSingletonEJB.invokeGetNext();

			Assert.fail("Expecting exception " + NoMoreTimeoutsException.class.getSimpleName());
		} catch (NoMoreTimeoutsException e) {
			LOGGER.debug(LOG_PREFIX + "test010_timeout_pass - Expected exception " + e.getClass().getSimpleName()
					+ " was thrown on method getNextTimeout");
		}

		// The timeout can finish.
		timeoutWaiter.countDown();

		// As we can't be exactly sure when the timeout method is finished in
		// this moment we invoke in a loop, can check the exception type.
		int count = 0;
		boolean passed = false;
		while (count < 20 && !passed) {
			try {
				this.timeoutSingletonEJB.invokeTimeRemaining();
				Assert.fail("Expected to fail on invoking on an expired timer");
			} catch (NoSuchObjectLocalException nsole) {
				// Expected...
				LOGGER.debug(LOG_PREFIX + "test010_timeout_pass - Got the expected exception " + nsole);
				passed = true;
			} catch (NoMoreTimeoutsException e) {
				// This will be thrown if the timer is still active.
				LOGGER.debug(LOG_PREFIX + "test010_timeout_pass - Is thrown because the timer is still active.");

				TimeUnit.MILLISECONDS.sleep(100);

				count++;
			}
		}

		if (!passed) {
			Assert.fail("Got NoMoreTimeoutsException rather than  NoSuchObjectLocalException");
		}
	}
}