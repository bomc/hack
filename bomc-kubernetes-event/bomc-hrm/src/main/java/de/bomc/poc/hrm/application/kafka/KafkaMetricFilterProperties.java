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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * @formatter:off
 * Producer Metrics: 
 * ______________________________________________________
 * app-info
 * ------------------------------------------------------
 * commit-id
 * start-time-ms
 * version
 * ______________________________________________________
 * kafka-metrics-count
 * ------------------------------------------------------
 * count
 * ______________________________________________________
 * producer-metrics
 * ------------------------------------------------------
 * batch-size-avg
 * batch-size-max
 * batch-split-rate
 * batch-split-total
 * buffer-available-bytes
 * buffer-exhausted-rate
 * buffer-exhausted-total
 * buffer-total-bytes
 * bufferpool-wait-ratio
 * bufferpool-wait-time-total
 * compression-rate-avg
 * connection-close-rate
 * connection-close-total
 * connection-count
 * connection-creation-rate
 * connection-creation-total
 * failed-authentication-rate
 * failed-authentication-total
 * failed-reauthentication-rate
 * failed-reauthentication-total
 * incoming-byte-rate
 * incoming-byte-total
 * io-ratio
 * io-time-ns-avg
 * io-wait-ratio
 * io-wait-time-ns-avg
 * io-waittime-total
 * iotime-total
 * metadata-age
 * network-io-rate
 * network-io-total
 * outgoing-byte-rate
 * outgoing-byte-total
 * produce-throttle-time-avg
 * produce-throttle-time-max
 * reauthentication-latency-avg
 * reauthentication-latency-max
 * record-error-rate
 * record-error-total
 * record-queue-time-avg
 * record-queue-time-max
 * record-retry-rate
 * record-retry-total
 * record-send-rate
 * record-send-total
 * record-size-avg
 * record-size-max
 * records-per-request-avg
 * request-latency-avg
 * request-latency-max
 * request-rate
 * request-size-avg
 * request-size-max
 * request-total
 * requests-in-flight
 * response-rate
 * response-total
 * select-rate
 * select-total
 * successful-authentication-no-reauth-total
 * successful-authentication-rate
 * successful-authentication-total
 * successful-reauthentication-rate
 * successful-reauthentication-total
 * waiting-threads
 * ______________________________________________________
 * producer-node-metrics
 * ------------------------------------------------------
 * incoming-byte-rate
 * incoming-byte-total
 * outgoing-byte-rate
 * outgoing-byte-total
 * request-latency-avg
 * request-latency-max
 * request-rate
 * request-size-avg
 * request-size-max
 * request-total
 * response-rate
 * response-total
 * ______________________________________________________
 * producer-topic-metrics
 * ------------------------------------------------------
 * byte-rate
 * byte-total
 * compression-rate
 * record-error-rate
 * record-error-total
 * record-retry-rate
 * record-retry-total
 * record-send-rate
 * record-send-total
 * @formatter:on
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 20.01.2020
 */
public class KafkaMetricFilterProperties {

	// Used to filter just the metrics we want
	private final static Set<String> metricsNameFilter = new HashSet<>();

	public static Set<String> getFilterMetricSet() {
		metricsNameFilter.add("batch-size-avg");
		metricsNameFilter.add("buffer-available-bytes");
		metricsNameFilter.add("compression-rate-avg");
		metricsNameFilter.add("connection-count");
		metricsNameFilter.add("incoming-byte-rate");
		metricsNameFilter.add("incoming-byte-total");
		metricsNameFilter.add("iotime-total");
		metricsNameFilter.add("records-lag-max");
		metricsNameFilter.add("outgoing-byte-total");
		metricsNameFilter.add("record-error-total");
		metricsNameFilter.add("record-queue-time-max");
		metricsNameFilter.add("record-send-total");
		metricsNameFilter.add("record-size-avg");
		metricsNameFilter.add("request-total");
		
		return Collections.unmodifiableSet(metricsNameFilter);
	}

}
