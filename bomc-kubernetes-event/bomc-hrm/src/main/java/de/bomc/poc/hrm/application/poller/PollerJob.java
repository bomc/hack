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
package de.bomc.poc.hrm.application.poller;

import javax.annotation.PostConstruct;

import org.springframework.boot.logging.LogLevel;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;

import de.bomc.poc.hrm.application.log.method.Loggable;
import lombok.extern.slf4j.Slf4j;

/**
 * This components handles ... .
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 01.11.2019
 */
@Slf4j
//@Component
@PropertySource("classpath:scheduled.properties")
public class PollerJob {

	private static final String LOG_PREFIX = PollerJob.class.getName() + "#";
	
	public PollerJob() {
		System.out.println(LOG_PREFIX + "co");
		
	}
	
	@PostConstruct
	public void init() {
		log.info(LOG_PREFIX + "init - Order polling activated.");
	}

	@Loggable(result = false, params = false, value = LogLevel.DEBUG, time = false)
	@Scheduled(initialDelayString = "${bomc.hrm.schedule.poller.startDelay:PT1S}", fixedDelayString = "${bomc.hrm.schedule.poller.repeatInterval:PT10S}")
	public void poll() {
		//
	}

}
