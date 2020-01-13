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
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
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

	// Used to filter just the metrics we want
	private final Set<String> metricsNameFilter = new HashSet<>();

	public HrmKafkaProducer(final KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;

		metricsNameFilter.add("record-queue-time-avg");
		metricsNameFilter.add("record-send-rate"); // Producer, Per-Topic Metrics: The average number of records sent per second for a topic.
		metricsNameFilter.add("records-per-request-avg"); // Consumer Metrics, Fetch Metrics: The average number of records in each request.
		metricsNameFilter.add("request-size-max"); // Producer Metrics, Per-Broker Metrics: The maximum size of any request sent in the window for a broker.
		metricsNameFilter.add("network-io-rate");
		metricsNameFilter.add("incoming-byte-rate"); // Producer Metrics, Global Request Metrics: The average number of incoming bytes received per second from all servers.
		metricsNameFilter.add("batch-size-avg"); 
		metricsNameFilter.add("response-rate");  // Producer Metrics, Global Request Metrics: The average number of responses received per second. 
		metricsNameFilter.add("requests-in-flight");
	}
	
	
	/*
	 * 
	 * Producer Metrics: 
	 * Global Request Metrics
request-latency-avg
The average request latency in ms.
request-latency-max
The maximum request latency in ms.
request-rate
The average number of requests sent per second.
response-rate
The average number of responses received per second.
incoming-byte-rate
The average number of incoming bytes received per second from all servers.
outgoing-byte-rate
The average number of outgoing bytes sent per second to all servers.
	 *
	 * Connection Metrics
connection-count
The current number of active connections.
connection-creation-rate
New connections established per second in the window.
connection-close-rate
Connections closed per second in the window.
io-ratio
The fraction of time the I/O thread spent doing I/O.
io-time-ns-avg
The average length of time for I/O per select call in nanoseconds.
io-wait-ratio
The fraction of time the I/O thread spent waiting.
select-rate
Number of times the I/O layer checked for new I/O to perform per second.
io-wait-time-ns-avg
The average length of time the I/O thread spent waiting for a socket ready for reads or writes in nanoseconds.
	 *
	 * Per-Broker Metrics
request-size-max
The maximum size of any request sent in the window for a broker.
request-size-avg
The average size of all requests in the window for a broker.
request-rate
The average number of requests sent per second to the broker.
response-rate
The average number of responses received per second from the broker.
incoming-byte-rate
The average number of bytes received per second from the broker.
outgoing-byte-rate
The average number of bytes sent per second to the broker.
	 *
	 * Per-Topic Metrics
byte-rate
The average number of bytes sent per second for a topic.
record-send-rate
The average number of records sent per second for a topic.
compression-rate
The average compression rate of record batches for a topic.
record-retry-rate
The average per-second number of retried record sends for a topic.
record-error-rate
The average per-second number of record sends that resulted in errors for a topic.
	 *
	 * New Consumer Metrics
	 * Fetch Metrics
records-lag-max
The maximum lag in terms of number of records for any partition in this window. An increasing value over time is your best indication that the consumer group is not keeping up with the producers.
fetch-size-avg
The average number of bytes fetched per request.
fetch-size-max
The average number of bytes fetched per request.
bytes-consumed-rate
The average number of bytes consumed per second.
records-per-request-avg
The average number of records in each request.
records-consumed-rate
The average number of records consumed per second.
fetch-rate
The number of fetch requests per second.
fetch-latency-avg
The average time taken for a fetch request.
fetch-latency-max
The max time taken for a fetch request.
fetch-throttle-time-avg
The average throttle time in ms. When quotas are enabled, the broker may delay fetch requests in order to throttle a consumer which has exceeded its limit. This metric indicates how throttling time has been added to fetch requests on average.
fetch-throttle-time-max
The maximum throttle time in ms.
	 *
	 * Topic-level Fetch Metrics
fetch-size-avg
The average number of bytes fetched per request for a specific topic.
fetch-size-max
The maximum number of bytes fetched per request for a specific topic.
bytes-consumed-rate
The average number of bytes consumed per second for a specific topic.
records-per-request-avg
The average number of records in each request for a specific topic.
records-consumed-rate
The average number of records consumed per second for a specific topic.
	 *
	 * Consumer Group Metrics
assigned-partitions
The number of partitions currently assigned to this consumer.
commit-latency-avg
The average time taken for a commit request.
commit-latency-max
The max time taken for a commit request.
commit-rate
The number of commit calls per second.
join-rate
The number of group joins per second. Group joining is the first phase of the rebalance protocol. A large value indicates that the consumer group is unstable and will likely be coupled with increased lag.
join-time-avg
The average time taken for a group rejoin. This value can get as high as the configured session timeout for the consumer, but should usually be lower.
join-time-max
The max time taken for a group rejoin. This value should not get much higher than the configured session timeout for the consumer.
sync-rate
The number of group syncs per second. Group synchronization is the second and last phase of the rebalance protocol. Similar to join-rate, a large value indicates group instability.
sync-time-avg
The average time taken for a group sync.
sync-time-max
The max time taken for a group sync.
heartbeat-rate
The average number of heartbeats per second. After a rebalance, the consumer sends heartbeats to the coordinator to keep itself active in the group. You can control this using the heartbeat.interval.ms setting for the consumer. You may see a lower rate than configured if the processing loop is taking more time to handle message batches. Usually this is OK as long as you see no increase in the join rate.
heartbeat-response-time-max
The max time taken to receive a response to a heartbeat request.
last-heartbeat-seconds-ago
The number of seconds since the last controller heartbeat.
	 *
	 * Global Request Metrics
request-latency-avg
The average request latency in ms.
request-latency-max
The maximum request latency in ms.
request-rate
The average number of requests sent per second.
response-rate
The average number of responses received per second.
incoming-byte-rate
The average number of incoming bytes received per second from all servers.
outgoing-byte-rate
The average number of outgoing bytes sent per second to all servers.
	 * 
	 * Global Connection Metrics
connection-count
The current number of active connections.
connection-creation-rate
New connections established per second in the window.
connection-close-rate
Connections closed per second in the window.
io-ratio
The fraction of time the I/O thread spent doing I/O.
io-time-ns-avg
The average length of time for I/O per select call in nanoseconds.
io-wait-ratio
The fraction of time the I/O thread spent waiting.
select-rate
Number of times the I/O layer checked for new I/O to perform per second.
io-wait-time-ns-avg
The average length of time the I/O thread spent waiting for a socket ready for reads or writes in nanoseconds.
	 *
	 * Per-Broker Metrics
request-size-max
The maximum size of any request sent in the window for a broker.
request-size-avg
The average size of all requests in the window for a broker.
request-rate
The average number of requests sent per second to the broker.
response-rate
The average number of responses received per second from the broker.
incoming-byte-rate
The average number of bytes received per second from the broker.
outgoing-byte-rate
The average number of bytes sent per second to the broker.
	 *
	 * 
	 */

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

				log.info(LOG_PREFIX + "onSuccess - [message=" + message + ", record.headers="
				        + record.headers().iterator().next().value() + "]");

				try {
					displayRecordMetaData(record, future.get().getRecordMetadata());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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

		// Printout kafka metrics.
//		this.displayMetrics(this.kafkaTemplate.metrics());
//
//		this.printMetrics(this.kafkaTemplate.metrics());

	} // end method

	private void displayRecordMetaData(final ProducerRecord<String, String> record,
	        final RecordMetadata recordMetadata) {

		log.info(String.format(
		        LOG_PREFIX + "onSuccess#displayRecordMetaData\n\t\t\tkey=%s, value=%s "
		                + "\n\t\t\tsent to topic=%s part=%d off=%d at time=%s",
		        record.key(), record.value()/* .toJson() */, recordMetadata.topic(), recordMetadata.partition(),
		        recordMetadata.offset(), new Date(recordMetadata.timestamp())));
	}

	private void displayMetrics(Map<MetricName, ? extends Metric> metrics) {

		final Map<String, MetricPair> metricsDisplayMap = metrics.entrySet().stream()
		        // Filter out metrics not in metricsNameFilter
		        .filter(metricNameEntry -> metricsNameFilter.contains(metricNameEntry.getKey().name()))
		        // Filter out metrics not in metricsNameFilter
		        .filter(metricNameEntry -> !Double.isInfinite(metricNameEntry.getValue().value())
		                && !Double.isNaN(metricNameEntry.getValue().value()) && metricNameEntry.getValue().value() != 0)
		        // Turn Map<MetricName,Metric> into TreeMap<String, MetricPair>
		        .map(entry -> new MetricPair(entry.getKey(), entry.getValue()))
		        .collect(Collectors.toMap(MetricPair::toString, it -> it, (a, b) -> a, TreeMap::new));

		// Output metrics
		final StringBuilder builder = new StringBuilder(255);
		builder.append("\n---------------------------------------\n");
		metricsDisplayMap.entrySet().forEach(entry -> {
			MetricPair metricPair = entry.getValue();
			String name = entry.getKey();
			builder.append(String.format(Locale.GERMAN, "%50s%25s\t\t%,-10.2f\t\t%s\n", name,
			        metricPair.metricName.name(), metricPair.metric.value(), metricPair.metricName.description()));
		});
		builder.append("\n---------------------------------------\n");
		log.info(builder.toString());
	}

	static class MetricPair {
		private final MetricName metricName;
		private final Metric metric;

		MetricPair(MetricName metricName, Metric metric) {
			this.metricName = metricName;
			this.metric = metric;
		}

		public String toString() {
			return metricName.group() + "." + metricName.name();
		}
	}
	
	// -----------------------------------------------
	/**
	 * print out the metrics in alphabetical order
	 * @param metrics   the metrics to be printed out
	 */
	private void printMetrics(Map<MetricName, ? extends Metric> metrics) {
	    if (metrics != null && !metrics.isEmpty()) {
	        int maxLengthOfDisplayName = 0;
	        TreeMap<String, Double> sortedMetrics = new TreeMap<>(new Comparator<String>() {
	            @Override
	            public int compare(String o1, String o2) {
	                return o1.compareTo(o2);
	            }
	        });
	        for (Metric metric : metrics.values()) {
	            MetricName mName = metric.metricName();
	            String mergedName = mName.group() + ":" + mName.name() + ":" + mName.tags();
	            maxLengthOfDisplayName = maxLengthOfDisplayName < mergedName.length() ? mergedName.length() : maxLengthOfDisplayName;
	            sortedMetrics.put(mergedName, metric.value());
	        }
	        String outputFormat = "%-" + maxLengthOfDisplayName + "s : %.3f";
	        log.debug(String.format("\n%-" + maxLengthOfDisplayName + "s   %s", "Metric Name", "Value"));

	        for (Map.Entry<String, Double> entry : sortedMetrics.entrySet()) {
	            log.debug(String.format(outputFormat, entry.getKey(), entry.getValue()));
	        }
	    }
	}
}
