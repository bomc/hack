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
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.stereotype.Component;

import de.bomc.poc.hrm.application.log.method.Loggable;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementations of RecordFilterStrategy interface can signal that a record
 * about to be delivered to a message listener should be discarded instead of
 * being delivered.
 *
 * NOTE: If the message has to be modified/enriched on a consumer side before
 * delivering to Listener than see for an example
 * 'http://springreboot.com/spring-boot-kafka-consumer-interceptor-example/'.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 27.12.2019
 */
@Slf4j
@Component
public class HrmKafkaRecordFilter implements RecordFilterStrategy<Object, Object> {

	private static final String LOG_PREFIX = HrmKafkaRecordFilter.class.getName() + "#";

	@Override
	@Loggable(time = true, result = true)
	public boolean filter(final ConsumerRecord<Object, Object> consumerRecord) {

		final Headers headers = consumerRecord.headers();

		if (headers != null && headers.headers(HrmKafkaProducer.X_B3_TRACE_ID_HEADER) != null
		        && headers.headers(HrmKafkaProducer.X_B3_TRACE_ID_HEADER).iterator().hasNext()) {

			// Filter out when the header delivered value is a odd value.
			final boolean retVal = HrmKafkaProducer.ODD_VALUE.equals(new String(
			        consumerRecord.headers().headers(HrmKafkaProducer.X_B3_TRACE_ID_HEADER).iterator().next().value(),
			        StandardCharsets.UTF_8));

			log.info(LOG_PREFIX + "filter [retVal=" + retVal + "]");

		}

		return false;
	}

}
