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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;

import lombok.extern.slf4j.Slf4j;

/**
 * Boot the application.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@Slf4j
@SpringBootApplication(scanBasePackages = { "de.bomc.poc.*" })
public class HrmApplication {

	private static final String LOG_PREFIX = "HrmApplication#";

	@SuppressWarnings("unused")
	public static void main(String[] args) {

		final ConfigurableApplicationContext configurableApplicationContext = SpringApplication
		        .run(HrmApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

		// @formatter:off
    	log.info("\n"
                + "___________________________________\n"
                + LOG_PREFIX + "onApplicationEvent - Receiving application ready event. \n"
                + "-----------------------------------");
         // @formatter:on
	}

}
