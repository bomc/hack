/**
 * Project: Poc-upload
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: 2017-01-18 08:28:21 +0100 (Mi, 18 Jan 2017) $
 *
 *  revision: $Revision: 9696 $
 *
 * </pre>
 */
package de.bomc.poc.upload.ping;

import org.apache.log4j.Logger;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.upload.configuration.ConfigKeys;
import de.bomc.poc.upload.configuration.qualifier.ConfigQualifier;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Send a ping request to the validation server to determine if the service is available in the event of a suspected outage.
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 21.12.2016
 */
@Startup
@Singleton
public class PingSchedulerSingletonEJB {

    private static final String LOG_PREFIX = "PingSchedulerSingletonEJB#";
    @Inject
    @LoggerQualifier
    private Logger logger;
    @NotNull
    @Inject
    @ConfigQualifier(key = ConfigKeys.PING_INTERVAL)
    private String pingInterval;
    @EJB
    private PingWorkerSingletonEJB pingerWorkerSingletonEJB;
    @Resource
    private TimerService timerService;
    private Timer timer;

    /**
     * Create a calendar based Timer instance.
     */
    @PostConstruct
    public void init() {

        final ScheduleExpression schedule = this.createScheduleExpression();
        final TimerConfig timerConfig = this.createTimerConfig();

        this.timer = this.timerService.createCalendarTimer(schedule, timerConfig);

        this.logger.info(String.format("%s initialized with schedule %s.", LOG_PREFIX + "init", this.timer.getSchedule()));
    }

    /**
     * Create schedule expression.
     * @return the timeout expression.
     */
    private ScheduleExpression createScheduleExpression() {

        final String interval = "0/" + this.pingInterval;

        // Build and return the ScheduleExpression as required by the TimerService.
        return new ScheduleExpression().second("0")
                                       .minute(interval)
                                       .hour("*");
    }

    /**
     * Create TimerConfig.
     * @return the created timerConfig.
     */
    private TimerConfig createTimerConfig() {
        final TimerConfig timerConfig = new TimerConfig();

        // The name of the scheduler.
        timerConfig.setInfo("PingScheduler");

        // The scheduler is not persistent.
        timerConfig.setPersistent(false);

        return timerConfig;
    }

    /**
     * Is invoked at every scheduling event.
     */
    @Timeout
    public void process() {
        this.logger.debug(LOG_PREFIX + "process");

        final String
            requestId =
            UUID.randomUUID()
                .toString();

        try {
            this.pingerWorkerSingletonEJB.doPing(requestId);
        } catch (final InterruptedException e) {
            this.logger.warn(LOG_PREFIX + "process. PingerWorker throws a 'InterruptedException' when 'doPing is invoked.'");
        }
    }
}
