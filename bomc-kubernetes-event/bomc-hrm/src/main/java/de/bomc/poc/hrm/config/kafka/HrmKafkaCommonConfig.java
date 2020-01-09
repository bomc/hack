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

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;

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

	@Value(value = "${kafka.topic.data.name}")
	private String topicName;
	// The number of partitions for the new topic.
	private static final int NUM_PARTITION = 3;
	// The replication factor for the new topic.
	private static final short REPLICATION_FACTOR = 1;

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
		return new NewTopic(topicName, NUM_PARTITION, (short) REPLICATION_FACTOR);
	}
}
