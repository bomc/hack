/**
 * Project: bomc-onion-architecture
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
package de.bomc.poc.order.application.customer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.DependsOn;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.order.application.config.ConfigKeys;
import de.bomc.poc.order.application.config.qualifier.ConfigQualifier;
import de.bomc.poc.order.application.internal.ApplicationUserEnum;
import de.bomc.poc.order.domain.model.customer.JpaCustomerDao;
import de.bomc.poc.order.infrastructure.persistence.basis.qualifier.JpaDao;

/**
 * A timer that lock expired customer.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Startup
@Singleton
@DependsOn("ConfigSingletonEJB")
public class CustomerExpirySchedulerSingletonEJB {

    private static final String LOG_PREFIX = "CustomerExpirySchedulerSingletonEJB#";
    // _______________________________________________
    // Constants
    // -----------------------------------------------
    //
    // _______________________________________________
    // Scheduler variables
    // -----------------------------------------------
    @Inject
    @ConfigQualifier(key = ConfigKeys.CUSTOMER_EXPIRY_SCHEDULER_DAY_OF_WEEK)
    private String customerExpirySchedulerDayOfWeek;
    @Inject
    @ConfigQualifier(key = ConfigKeys.CUSTOMER_EXPIRY_SCHEDULER_MINUTE)
    private String customerExpirySchedulerMinute;
    @Inject
    @ConfigQualifier(key = ConfigKeys.CUSTOMER_EXPIRY_SCHEDULER_HOUR)
    private String customerExpirySchedulerHour;
    @Inject
    @ConfigQualifier(key = ConfigKeys.CUSTOMER_EXPIRY_SCHEDULER_PERSISTENT)
    private String customerExpirySchedulerPersistent;
    @Inject
    @ConfigQualifier(key = ConfigKeys.CUSTOMER_EXPIRY_SCHEDULER_TEST_MODUS)
    private String customerExpirySchedulerTestModus;
    // _______________________________________________
    // Member variables
    // -----------------------------------------------
    @Inject
    @LoggerQualifier
    private Logger logger;
    @Resource
    private TimerService timerService;
    @Inject
    @JpaDao
    private JpaCustomerDao jpaCustomerDao;

    @PostConstruct
    public void init() {
        this.logger.debug(LOG_PREFIX + "init - scheduler runs with parameters: [scheduler.dayOfWeek="
                + this.customerExpirySchedulerDayOfWeek + ", scheduler.hour=" + this.customerExpirySchedulerHour
                + ", scheduler.minute=" + this.customerExpirySchedulerMinute + ", scheduler.persistent="
                + this.customerExpirySchedulerPersistent + ", scheduler.test.modus="
                + this.customerExpirySchedulerTestModus + "]");

        if (Boolean.parseBoolean(this.customerExpirySchedulerTestModus) == false) {
            this.initScheduler(this.customerExpirySchedulerDayOfWeek,
                    Integer.parseInt(this.customerExpirySchedulerMinute),
                    Integer.parseInt(this.customerExpirySchedulerHour),
                    Boolean.parseBoolean(this.customerExpirySchedulerPersistent),
                    ApplicationUserEnum.SYSTEM_USER.name());
        } else {
            this.logger.warn(LOG_PREFIX + "init - Start in test modus! [customerExpirySchedulerTestModus="
                    + this.customerExpirySchedulerTestModus + "]");
        }
    }

    /**
     * Create a ScheduleExpression, TimerConfig and a start a timer.
     * 
     * @param customerExpirySchedulerDayOfWeek
     *            the given schedule parameter.
     * @param customerExpirySchedulerMinute
     *            the given schedule parameter.
     * @param customerExpirySchedulerHour
     *            the given schedule parameter.
     * @param customerExpirySchedulerPersistent
     *            the given schedule parameter.
     */
    public void initScheduler(final String customerExpirySchedulerDayOfWeek, final int customerExpirySchedulerMinute,
            final int customerExpirySchedulerHour, final boolean customerExpirySchedulerPersistent,
            final String userId) {
        this.logger.debug(LOG_PREFIX + "initScheduler [customerExpirySchedulerDayOfWeek="
                + customerExpirySchedulerDayOfWeek + ", customerExpirySchedulerMinute=" + customerExpirySchedulerMinute
                + ", customerExpirySchedulerHour=" + customerExpirySchedulerHour
                + ", customerExpirySchedulerPersistent=" + customerExpirySchedulerPersistent + ", userId=" + userId
                + "]");
        // Cleanup all timers.
        this.cleanup();
        
        final ScheduleExpression scheduleExpression = new ScheduleExpression()
                .dayOfWeek(this.customerExpirySchedulerDayOfWeek).minute(customerExpirySchedulerMinute)
                .hour(customerExpirySchedulerHour);

        final TimerConfig timerConfig = new TimerConfig();
        timerConfig.setInfo("Check all customers if they expired and lock them if necessary.");

        this.timerService.createCalendarTimer(scheduleExpression, timerConfig);
    }

    /**
     * Do the work and deletes all entries that are older than today -
     * daysToSubtract.
     * 
     * @param timer
     *            the expired timer.
     */
    @Timeout
    public void timerRunningOff(final Timer timer) {
        this.logger.debug(LOG_PREFIX + "timerRunningOff - [timer.info=" + timer.getInfo() + "]");

        final long time = System.currentTimeMillis();

        this.doWork(ApplicationUserEnum.SYSTEM_USER.name());

        this.logger.info(LOG_PREFIX + "timerRunningOff [duration=" + (System.currentTimeMillis() - time) + "ms]");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void doWork(final String userId) {
        this.logger.debug(
                LOG_PREFIX + "doWork [userId=" + userId + "]");

        // TODO ...
    }

    public void printoutNextTimeout(final String userId) {
        this.timerService.getTimers().forEach(t -> this.logger.info(LOG_PREFIX + "printoutNextTimeout [timeout="
                + t.getNextTimeout() + ", timer.info='" + t.getInfo() + "', userId=" + userId + "]"));
    }

    /**
     * Cancel all running timers.
     */
    @PreDestroy
    public void cleanup() {
        this.logger.debug(LOG_PREFIX + "cleanup running timers!");

        if (this.timerService != null) {
            this.timerService.getTimers().forEach(t -> t.cancel());
        }
    }
}
