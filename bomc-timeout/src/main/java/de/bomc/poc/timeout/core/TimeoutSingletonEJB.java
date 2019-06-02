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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.NoMoreTimeoutsException;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import org.apache.log4j.Logger;

/**
 * Shows handling of a timer.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 09.12.2017
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class TimeoutSingletonEJB {

	private static final String LOG_PREFIX = "SingletonTimeoutEJB#";
    private static final Logger LOGGER = Logger.getLogger(TimeoutSingletonEJB.class);
    private static int TIMER_CALL_WAITING_S = 5;

    @Resource
    private TimerService timerService;

    private Timer timer;

    private CountDownLatch timeoutNotifyingLatch;
    private CountDownLatch timeoutWaiter;

    public void createSingleActionTimer(final long delay, final TimerConfig config,
                                        final CountDownLatch timeoutNotifyingLatch, final CountDownLatch timeoutWaiter) {
    	LOGGER.debug(LOG_PREFIX + "createSingleActionTimer [delay=" + delay + ", config=" + config.getInfo() + "]");
    	
        this.timer = this.timerService.createSingleActionTimer(delay, config);
        this.timeoutNotifyingLatch = timeoutNotifyingLatch;
        this.timeoutWaiter = timeoutWaiter;
    }

    @Timeout
    private void onTimeout(final Timer timer) throws InterruptedException {
        LOGGER.debug(LOG_PREFIX + "onTimeout - timeout is invoked [this=" + this + ", timer=" + timer + "]");
        this.timeoutNotifyingLatch.countDown();
        
        LOGGER.debug(LOG_PREFIX + "onTimer - Waiting for timer will be permitted to continue.");
        this.timeoutWaiter.await(TIMER_CALL_WAITING_S, TimeUnit.SECONDS);
        
        LOGGER.debug(LOG_PREFIX + "onTimer - End of onTimeout on singleton.");
    }

    public Timer getTimer() {
    	LOGGER.debug(LOG_PREFIX + "getTimer [timer=" + timer + "]");
    	
        return this.timer;
    }

    public void invokeTimeRemaining() throws NoMoreTimeoutsException, NoSuchObjectLocalException {
    	LOGGER.debug(LOG_PREFIX + "invokeTimeRemaining");
    	
        this.timer.getTimeRemaining();
    }

    public void invokeGetNext() throws NoMoreTimeoutsException, NoSuchObjectLocalException {
    	LOGGER.debug(LOG_PREFIX + "invokeGetNext");
    	
        this.timer.getNextTimeout();
    }
}
