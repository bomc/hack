package de.bomc.poc.strimzikafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import lombok.extern.slf4j.Slf4j;

/**
 * Boot the application.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.03.2020
 */
@Slf4j
@SpringBootApplication
public class StrimziKafkaApplication {

	private static final String LOG_PREFIX = StrimziKafkaApplication.class.getName() + "#";

	public static void main(String[] args) {
		SpringApplication.run(StrimziKafkaApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationEvent(final ApplicationReadyEvent applicationReadyEvent) {

		// @formatter:off
		log.info("\n" + "___________________________________\n" + LOG_PREFIX
				+ "onApplicationEvent - Receiving application ready event, so the kafka consumer is starting. \n"
				+ "-----------------------------------");
		// @formatter:on
	}
}
