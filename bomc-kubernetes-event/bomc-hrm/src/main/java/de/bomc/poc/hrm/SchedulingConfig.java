/**
 * Project: hrm
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
package de.bomc.poc.hrm;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import de.bomc.poc.hrm.application.log.method.Loggable;
import de.bomc.poc.hrm.application.poller.PollerJob;

/**
 * A configuration for scheduling.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 01.11.2019
 */
@Configuration
// By default, -at EnableScheduling annotation creates a thread pool with only one 
// thread. The invocation of all -at Scheduled tasks is queued and executed by an 
// only thread. So if there are multiple scheduled tasks in the application, it 
// might see weird behavior of invocation (since the tasks are queued).
@EnableScheduling
@PropertySource("classpath:scheduled.properties")
public class SchedulingConfig {
	
    @Bean
    @Loggable(result = false, params = false, value = LogLevel.DEBUG, time = false)
    @ConditionalOnProperty(value = "bomc.hrm.schedule.poller.enabled", matchIfMissing = true, havingValue = "true")
    public PollerJob pollerJob() {
    	return new PollerJob();
    }
}
