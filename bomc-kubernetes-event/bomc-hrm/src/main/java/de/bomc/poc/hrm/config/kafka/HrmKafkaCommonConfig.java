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

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
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

	@Value(value = "${kafka.topic.data.name}")
	private String topicName;

	private KafkaProperties kafkaProperties;

	/**
	 * Creates a new instance <code>HrmKafkaCommonConfig</code>.
	 * 
	 * @param kafkaProperties contains already the default parameter.
	 */
	public HrmKafkaCommonConfig(final KafkaProperties kafkaProperties) {

		this.kafkaProperties = kafkaProperties;
	}

	/**
	 * The KafkaAdmin bean in application context can automatically add topics to
	 * the broker. To do so, a NewTopic -at Bean can added for each topic to the
	 * application context. Version 2.3 introduced a new class TopicBuilder to make
	 * creation of such beans more convenient.
	 * 
	 * Is used for
	 * 
	 * @return a configured KafkaAdmin.
	 */
	@Bean
	public KafkaAdmin admin() {

		final Map<String, Object> configs = new HashMap<>(this.kafkaProperties.buildAdminProperties());

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
		return TopicBuilder.name(topicName) //
		        .partitions(NUM_PARTITION) //
		        .replicas(REPLICATION_FACTOR) //
//	    		.compact() //
		        .build(); //
	}

}
