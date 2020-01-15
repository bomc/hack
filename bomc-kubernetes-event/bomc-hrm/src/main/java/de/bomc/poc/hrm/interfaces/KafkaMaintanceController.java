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
package de.bomc.poc.hrm.interfaces;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.bomc.poc.hrm.application.log.method.Loggable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * A controller for starting stopping kafka consumer.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 13.01.2020
 */
@Slf4j
@Profile("prod")
@RestController
@RequestMapping(value = "/kafka")
@CrossOrigin(origins = "*") // TODO: security issue
@Api(tags = "Kafka queries", value = "Kafka Maintance System", description = ".", produces = "application/vnd.hrm-kafka-v1+json;charset=UTF-8")
public class KafkaMaintanceController {

	private static final String LOG_PREFIX = KafkaMaintanceController.class.getName() + "#";

	public static final String MEDIA_TYPE_JSON_V1 = "application/vnd.hrm-kafka-v1+json;charset=UTF-8";

	@Autowired
	private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

	/**
	 * Creates a new instance of <code>KafkaMaintanceController</code>.
	 * 
	 * @param kafkaListenerEndpointRegistry
	 */
	public KafkaMaintanceController(final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry) {

		this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
	}

	@ApiOperation(value = "Starts the consumer by given group-id.", notes = "The unique identifier of the container managing for this kafka consumer endpoint.")
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "Successfully starts the consumer by the given group-id."),
	        @ApiResponse(code = 401, message = "Not authorized to view the resource."),
	        @ApiResponse(code = 403, message = "Accessing the resource that trying to reach is forbidden."),
	        @ApiResponse(code = 404, message = "The resource that trying to reach is not found.") })
	@ApiImplicitParams(@ApiImplicitParam(name = "group-id", value = "The unique identifier of the container managing for this kafka consumer endpoint.", dataType = "String", dataTypeClass = java.lang.String.class, required = true))
	@GetMapping(value = "/start/{group-id}")
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
	public void startConsumerByGroupId(@PathVariable final String groupId) {

		final MessageListenerContainer messageListenerContainer = kafkaListenerEndpointRegistry
		        .getListenerContainer(groupId);

		if (messageListenerContainer.isRunning()) {
			messageListenerContainer.start();
		} else {
			log.info(LOG_PREFIX + "startConsumerByGroupId - consumer is already running. [group-id=" + groupId + "]");
		}
	}

	@ApiOperation(value = "Stops the consumer by given group-id.", notes = "The unique identifier of the container managing for this kafka consumer endpoint.")
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "Successfully stops the consumer by the given group-id."),
	        @ApiResponse(code = 401, message = "Not authorized to view the resource."),
	        @ApiResponse(code = 403, message = "Accessing the resource that trying to reach is forbidden."),
	        @ApiResponse(code = 404, message = "The resource that trying to reach is not found.") })
	@ApiImplicitParams(@ApiImplicitParam(name = "group-id", value = "The unique identifier of the container managing for this kafka consumer endpoint.", dataType = "String", dataTypeClass = java.lang.String.class, required = true))
	@GetMapping(value = "/stop/{group-id}")
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
	public void stopConsumerByGroupId(@PathVariable final String groupId) {

		final MessageListenerContainer messageListenerContainer = kafkaListenerEndpointRegistry
		        .getListenerContainer(groupId);

		messageListenerContainer.stop();
	}

	@ApiOperation(value = "Get all kafka metrics.", response = String.class, notes = "The unique identifier of the container managing for this kafka consumer endpoint.")
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "Successfully stops the consumer by the given group-id."),
	        @ApiResponse(code = 401, message = "Not authorized to view the resource."),
	        @ApiResponse(code = 403, message = "Accessing the resource that trying to reach is forbidden."),
	        @ApiResponse(code = 404, message = "The resource that trying to reach is not found.") })
	@ApiImplicitParams(@ApiImplicitParam(name = "group-id", value = "The unique identifier of the container managing for this kafka consumer endpoint.", dataType = "String", dataTypeClass = java.lang.String.class, required = true))
	@GetMapping(value = "/metrics", produces = MEDIA_TYPE_JSON_V1)
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
	public void getMetrics() {

		 // TODO
	}

	/**
	 * Print out all kafka metrics in alphabetical order.
	 * 
	 * @param metrics the metrics to be printed out.
	 */
	private void printMetrics(final Map<MetricName, ? extends Metric> metrics) {

		if (metrics != null && !metrics.isEmpty()) {
			int maxLengthOfDisplayName = 0;

			final TreeMap<String, Double> sortedMetrics = new TreeMap<>(new Comparator<String>() {
				@Override
				public int compare(final String o1, final String o2) {
					return o1.compareTo(o2);
				}
			});

			for (final Metric metric : metrics.values()) {
				final MetricName mName = metric.metricName();
				final String mergedName = mName.group() + ":" + mName.name() + ":" + mName.tags();
				maxLengthOfDisplayName = maxLengthOfDisplayName < mergedName.length() ? mergedName.length()
				        : maxLengthOfDisplayName;
				sortedMetrics.put(mergedName, metric.value());
			}

			final String outputFormat = "%-" + maxLengthOfDisplayName + "s : %.3f";
			log.debug(String.format("\n%-" + maxLengthOfDisplayName + "s   %s", "Metric Name", "Value"));

			for (Map.Entry<String, Double> entry : sortedMetrics.entrySet()) {
				log.debug(String.format(outputFormat, entry.getKey(), entry.getValue()));
			}
		}
	}
}
