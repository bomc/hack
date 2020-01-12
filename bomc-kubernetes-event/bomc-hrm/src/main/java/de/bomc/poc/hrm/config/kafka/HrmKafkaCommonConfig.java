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
package de.bomc.poc.hrm.config.kafka;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

/**
 * Instructing the Kafka's AdminClient bean (already in the context) to create a
 * topic with the given configuration.
 *
 *
 * KafkaHealthIndicator 
 *
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 04.01.2020
 */
@EnableKafka
@Configuration
@Profile({ "prod" })
public class HrmKafkaCommonConfig {

	// The number of partitions for the new topic.
	private static final int NUM_PARTITION = 3;
	// The replication factor for the new topic.
	private static final short REPLICATION_FACTOR = 1;
	
	@Value(value = "${kafka.bootstrap-servers}")
	private String bootstrapAddress;
	@Value(value = "${kafka.topic.data.name}")
	private String topicName;

//	@Autowired
//	private KafkaAdmin admin;
//
//	@Autowired
//	private MeterRegistry meterRegistry;
//
//	@Autowired
//	private Map<String, KafkaTemplate<?, ?>> kafkaTemplates;
//
//	@Bean
//	public AdminClient kafkaAdminClient() {
//		return AdminClient.create(admin.getConfig());
//	}
	
	/**
	 * The KafkaAdmin bean in application context can automatically add topics to
	 * the broker. To do so, a NewTopic -at Bean can added for each topic to the
	 * application context. Version 2.3 introduced a new class TopicBuilder to make
	 * creation of such beans more convenient.
	 * 
	 * Is used for 
	 * @return a configured KafkaAdmin.
	 */
	@Bean
	public KafkaAdmin admin() {

		final Map<String, Object> configs = new HashMap<>();

		configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapAddress);

		return new KafkaAdmin(configs);
	}

	/**
	 * Creates a new topic with the specified replication factor and number of
	 * partitions.
	 * 
	 * @return a new topic with the specified replication factor and number of
	 *         partitions.
	 */
	@Bean
	public NewTopic bomcHrmDataTopic() {

		// Instructing the Kafka's AdminClient bean (already in the context) to create a
		// topic with the given configuration.
		return TopicBuilder
				.name(topicName) //
				.partitions(NUM_PARTITION) //
				.replicas(REPLICATION_FACTOR) //
//	    		.compact() //
		        .build(); //
	}
	
//	@SuppressWarnings("deprecation") // Can be avoided by relying on Double.NaN for non doubles.
//	@PostConstruct
//	private void initMetrics() {
//		final String kafkaPrefix = "kafka.";
//		for (Entry<String, KafkaTemplate<?, ?>> templateEntry : kafkaTemplates.entrySet()) {
//			final String name = templateEntry.getKey();
//			final KafkaTemplate<?, ?> kafkaTemplate = templateEntry.getValue();
//			for (Metric metric : kafkaTemplate.metrics().values()) {
//				final MetricName metricName = metric.metricName();
//				final Builder<Metric> gaugeBuilder = Gauge
//						.builder(kafkaPrefix + metricName.name(), metric, Metric::value) // <-- Here
//						.description(metricName.description());
//				for (Entry<String, String> tagEntry : metricName.tags().entrySet()) {
//					gaugeBuilder.tag(kafkaPrefix + tagEntry.getKey(), tagEntry.getValue());
//				}
//				gaugeBuilder.tag("bean", name);
//				gaugeBuilder.register(meterRegistry);
//			}
//		}
//	}
//
//	@Bean
//	public HealthIndicator kafkaHealthIndicator() {
//		final DescribeClusterOptions describeClusterOptions = new DescribeClusterOptions().timeoutMs(1000);
//		final AdminClient adminClient = kafkaAdminClient();
//		return () -> {
//			final DescribeClusterResult describeCluster = adminClient.describeCluster(describeClusterOptions);
//			try {
//				final String clusterId = describeCluster.clusterId().get();
//				final int nodeCount = describeCluster.nodes().get().size();
//				return Health.up()
//						.withDetail("clusterId", clusterId)
//						.withDetail("nodeCount", nodeCount)
//						.build();
//			} catch (InterruptedException | ExecutionException e) {
//				return Health.down()
//						.withException(e)
//						.build();
//			}
//		};
//
//	}
}
