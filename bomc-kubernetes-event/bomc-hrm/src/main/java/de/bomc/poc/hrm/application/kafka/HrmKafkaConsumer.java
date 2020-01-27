/**
 * Project: hrm
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: micha
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.hrm.application.kafka;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import de.bomc.poc.hrm.application.log.method.Loggable;
import lombok.extern.slf4j.Slf4j;

/**
 * A kafka consumer.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 28.12.2019
 */
@Slf4j
@Component
public class HrmKafkaConsumer {

	private static final String LOG_PREFIX = HrmKafkaConsumer.class + "#";

	@Loggable(time = true)
	@KafkaListener(containerGroup = "${spring.kafka.consumer.group-id}", id = "${spring.kafka.consumer.group-id}", clientIdPrefix = "bomc", topics = "${kafka.topic.data.name}", containerFactory = "concurrentKafkaListenerContainerFactory", properties = {
	        "enable.auto.commit=true", "auto.commit.interval.ms=1000", "poll-interval=100" })
	public void listen(final ConsumerRecord<String, String> consumerRecord, //
	        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) final Integer partition, //
	        @Header(KafkaHeaders.OFFSET) final Long offset) {

		consumerRecord.headers().forEach(header -> {
			final String value = new String(header.value(), StandardCharsets.UTF_8);

			log.info(LOG_PREFIX + "listen [header.key=" + header.key() + ", header.value=" + value + "]");
		});

		log.info(LOG_PREFIX + "listen [consumerRecord=" + consumerRecord + ", consumerRecord.value="
		        + consumerRecord.value() + ", partition=" + partition + ", offset=" + offset + "]");
	}
}
