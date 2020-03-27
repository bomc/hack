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

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
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

	/**
	 * Creates a new instance of <code>HrmKafkaProducer</code>.
	 * 
	 * @param kafkaTemplate the template for producing messages.
	 */
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

		final ListenableFuture<SendResult<String, String>> future = this.kafkaTemplate.send(record);
		future.addCallback(new SuccessCallback<SendResult<String, String>>() {

			@Loggable(time = true)
			@Override
			public void onSuccess(final SendResult<String, String> result) {

				log.info(LOG_PREFIX + "onSuccess - [message=" + message + "]");

				if(record != null && (result.getRecordMetadata().offset() % 10L == 0)) {
					// Printout kafka metrics.
					printMetrics(kafkaTemplate.metrics());
				}
				
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

	/**
	 * Print out the metrics in alphabetical order.
	 * 
	 * @param metrics the metrics to be printed out
	 */
	private void printMetrics(final Map<MetricName, ? extends Metric> metrics) {
		
		if (metrics != null && !metrics.isEmpty()) {
			int maxLengthOfDisplayName = 0;
			
			// Create a comparator for alphabetic ordering.
			final TreeMap<String, Double> sortedMetrics = new TreeMap<>(new Comparator<String>() {
				@Override
				public int compare(final String o1, final String o2) {
					return o1.compareTo(o2);
				}
			});
			
			for (final Metric metric : metrics.values()) {
				
				if(KafkaMetricFilterProperties.getFilterMetricSet().contains(metric.metricName().name())) {
					final MetricName mName = metric.metricName();
					final String mergedName = mName.group() + ":" + mName.name() + ":" + mName.tags();
					maxLengthOfDisplayName = maxLengthOfDisplayName < mergedName.length() ? mergedName.length()
				        : maxLengthOfDisplayName;
					sortedMetrics.put(mergedName, (Double)metric.metricValue());
				}
			}
			
			final StringBuilder sb = new StringBuilder();
			
			String outputFormat = "%-" + maxLengthOfDisplayName + "s : %.3f\n";
			// Add header
			sb.append(String.format("\n%-" + maxLengthOfDisplayName + "s   %s\n", "Metric Name", "Value"));

			for (final Map.Entry<String, Double> entry : sortedMetrics.entrySet()) {
				// Add metrics
				sb.append(String.format(outputFormat, entry.getKey(), entry.getValue()));
			}
			log.debug(sb.toString());
		}
	}

}
