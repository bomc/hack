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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

/**
 * Boot the application.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@Slf4j
@EnableScheduling
@SpringBootApplication(scanBasePackages = { "de.bomc.poc.*" })
public class HrmApplication {

	private static final String LOG_PREFIX = "HrmApplication#";

	@Value(value = "${kafka.topic.consumer.group-id}")
	private String consumerGroupId;

	@Autowired
	private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

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
                + LOG_PREFIX + "onApplicationEvent - Receiving application ready event, so the kafka consumer will started. \n"
                + "-----------------------------------");
         // @formatter:on

		// Start the consumer from configuration HrmKafkaConsumerConfig and the consumer
		// HrmKafkaConsumer.
		this.kafkaListenerEndpointRegistry.getListenerContainer(this.consumerGroupId);
	}

}
