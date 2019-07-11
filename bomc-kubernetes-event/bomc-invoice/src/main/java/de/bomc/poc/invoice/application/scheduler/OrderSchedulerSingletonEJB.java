/**
 * Project: bomc-invoice
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.invoice.application.scheduler;

import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.bomc.poc.invoice.application.internal.ApplicationUserEnum;

/**
 * Schedules for new orders from bomc-order service.
 * 
 * mvn thorntail:run -Dwildfly-swarm.useUberJar=true
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 05.04.2019
 */
@Startup
@Singleton
public class OrderSchedulerSingletonEJB {

	private static final String LOG_PREFIX = "OrderSchedulerEJB#";
	private static final Logger LOGGER = Logger.getLogger(OrderSchedulerSingletonEJB.class.getName());
	// _______________________________________________
	// Configuration application constants.
	// -----------------------------------------------
	private static final String SCHEDULE_ORDER_DELAY_KEY = "schedule.order.delay";
	private static final String SCHEDULE_ORDER_START_DELAY_KEY = "schedule.order.startdelay";
	private static final String DEFAULT_EXPRESSION_VALUE = "15000";
	//private static final String INITIAL_LAST_MODIFIED_DATE = "Thu, 01 Jan 1970 00:00:01 GMT";
	
	// _______________________________________________
	// Injected configuration application variables (microprofile-config).
	// -----------------------------------------------
	@Inject
	@ConfigProperty(name = SCHEDULE_ORDER_START_DELAY_KEY, defaultValue = DEFAULT_EXPRESSION_VALUE)
	private String scheduleOrderStartDelay;
	@Inject
	@ConfigProperty(name = SCHEDULE_ORDER_DELAY_KEY, defaultValue = DEFAULT_EXPRESSION_VALUE)
	private String scheduleOrderDelay;
	// _______________________________________________
	// Other member variables.
	// -----------------------------------------------
	@Resource
	private TimerService timerService;
	@Inject
	private OrderSchedulerService orderSchedulerService;
	// Cache last modified date.
	private String lastModifiedDate = null;

	@PostConstruct
	public void init() {
		LOGGER.log(Level.INFO, LOG_PREFIX + "init [inject.scheduleOrderStartDelay=" + this.scheduleOrderStartDelay
				+ ", inject.scheduleOrderDelay=" + this.scheduleOrderDelay + "]");

		this.initScheduler(scheduleOrderStartDelay, scheduleOrderDelay);
	}

	public void initScheduler(final String scheduleOrderStartDelay, final String scheduleOrderDelay) {
		LOGGER.log(Level.INFO, LOG_PREFIX + "initScheduler [scheduleOrderStartDelay=" + this.scheduleOrderStartDelay
				+ ", scheduleOrderDelay=" + this.scheduleOrderDelay + "]");

		// Cleanup all timers.
		this.cleanup();

		final TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo("Check for new orders");
		timerConfig.setPersistent(false);

		this.timerService.createIntervalTimer(Long.parseLong(scheduleOrderStartDelay),
				Long.parseLong(scheduleOrderDelay), timerConfig);
	}

	/**
	 * Do the work and deletes all entries that are older than today -
	 * daysToSubtract.
	 * 
	 * @param timer the expired timer.
	 * @throws MalformedURLException
	 * @throws IllegalStateException
	 */
	@Timeout
	public void timerRunningOff(final Timer timer) {
		LOGGER.log(Level.INFO, LOG_PREFIX + "timerRunningOff - [timer.info=" + timer.getInfo() + "]");

		// Set MDC header, do userId
//		MDC.put(MDCFilter.HEADER_REQUEST_ID_ATTR, UUID.randomUUID().toString());

		//
		// Get new orders by given date.
		this.lastModifiedDate = this.orderSchedulerService.doWork(this.lastModifiedDate);

		// Print out next timeout.
		this.printoutNextTimeout(ApplicationUserEnum.SYSTEM_USER.name());
	}

	public void printoutNextTimeout(final String userId) {
		this.timerService.getTimers().forEach(t -> LOGGER.log(Level.INFO, LOG_PREFIX + "printoutNextTimeout [timeout="
				+ t.getNextTimeout() + ", timer.info=" + t.getInfo() + "]"));
	}

	/**
	 * Cancel all running timers.
	 */
	@PreDestroy
	public void cleanup() {
		LOGGER.log(Level.FINE, LOG_PREFIX + "cleanup - cancel running timers!");

		if (this.timerService != null) {
			this.timerService.getTimers().forEach(t -> t.cancel());
		}
	}
}
