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

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

import de.bomc.poc.hrm.application.log.method.Loggable;
import lombok.extern.slf4j.Slf4j;

/**
 * This class implements a kafka producer with headers. A custom header is added
 * while messages are produced. Depending on the custom header the message can
 * filter out while consuming message.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 27.12.2019
 */
@Slf4j
@Component
public class HrmKafkaProducer {

	private static final String LOG_PREFIX = HrmKafkaProducer.class.getName() + "#";

	protected static final String X_B3_TRACE_ID_HEADER = "X-B3-TraceId";
	protected static final String ODD_VALUE = "odd";
	
	@Value(value = "${kafka.topic.data.name}")
	private String topicName;

	private KafkaTemplate<String, String> kafkaTemplate;

	public HrmKafkaProducer(final KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
		
	}
	
	@Loggable(time = true)
	public void publishMessageToTopic(final String kafkaKey, final String message) {

		// Read requestId from MDC (Mapping Diagnostic Context). 
		final String traceId = MDC.get(X_B3_TRACE_ID_HEADER);
		// Create producer message.
		final ProducerRecord<String, String> record = new ProducerRecord<String, String>(topicName, kafkaKey, message);

		if (System.currentTimeMillis() % 2 == 0) {
			// Set traceId if number is even.
			record.headers().add(X_B3_TRACE_ID_HEADER, traceId.getBytes());
		} else {
			// Set a simple string if number is odd.
			record.headers().add(X_B3_TRACE_ID_HEADER, ODD_VALUE.getBytes());
		}

		final ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(record);
		future.addCallback(new SuccessCallback<SendResult<String, String>>() {

			@Loggable(time = true)
			@Override
			public void onSuccess(final SendResult<String, String> result) {

				log.info(LOG_PREFIX + "onSuccess - [message=" + message + ", with.offset="
				        + result.getRecordMetadata().offset() + ", record.headers=" + record.headers().iterator().next().value() + "]");
			}
		}, new FailureCallback() {

			@Loggable(time = true)
			@Override
			public void onFailure(final Throwable throwable) {

				log.error(LOG_PREFIX + "onFailure - Unable to send [message=" + message + ", error="
				        + throwable.getMessage() + "]");
			}
		});

	} // end method
}
