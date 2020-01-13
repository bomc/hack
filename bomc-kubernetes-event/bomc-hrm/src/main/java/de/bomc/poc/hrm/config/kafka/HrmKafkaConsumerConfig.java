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

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import de.bomc.poc.hrm.application.kafka.HrmKafkaRecordFilter;

/**
 * Configures the consumer.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 28.12.2019
 */
@Configuration
@Profile({ "prod" })
public class HrmKafkaConsumerConfig {

	@Value(value = "${kafka.bootstrap-servers}")
	private String bootstrapAddress;
	@Value(value = "${kafka.consumer.topic.group-id}")
	private String groupId;
	@Value(value = "${kafka.consumer.factory.concurrent}")
	private String concurrentConsumer;
	@Value(value = "${kafka.consumer.factory.auto-startup}")
	private String autoStartup;
	
	private HrmKafkaRecordFilter hrmKafkaRecordFilter;

	/**
	 * Creates a new instance of <code>HrmKafkaConsumerConfig</code>.
	 * 
	 * @param hrmKafkaRecordFilter the given record filter.
	 */
	public HrmKafkaConsumerConfig(final HrmKafkaRecordFilter hrmKafkaRecordFilter) {
		this.hrmKafkaRecordFilter = hrmKafkaRecordFilter;
	}

	// If only one kind of deserialization is needed, only the commented consumer
	// configuration properties is needed. Uncomment this and remove all others
	// below.
//    @Bean
//    public Map<String, Object> consumerConfigs() {
//        Map<String, Object> props = new HashMap<>(
//                kafkaProperties.buildConsumerProperties()
//        );
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
//                StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
//                JsonDeserializer.class);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG,
//                this.groupId);
//
//        return props;
//    }

	/**
	 * Creates one or more kafka message listeners/consumers based on the configured
	 * number of consumers(concurrency).
	 * 
	 * @return a configured ConcurrentKafkaListenerContainerFactory.
	 */
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> concurrentKafkaListenerContainerFactory() {

		final ConcurrentKafkaListenerContainerFactory<String, String> concurrentKafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
		concurrentKafkaListenerContainerFactory.setConsumerFactory(consumerFactory());
		// Set record filter strategy to Listener Container Factory.
		concurrentKafkaListenerContainerFactory.setRecordFilterStrategy(this.hrmKafkaRecordFilter);

		concurrentKafkaListenerContainerFactory.setConcurrency(Integer.parseInt(this.concurrentConsumer));
	    
//		concurrentKafkaListenerContainerFactory.getContainerProperties().setPollTimeout(3000);
		
		// ___________________________________________
		// Warum gibt es das?
		// -------------------------------------------
		concurrentKafkaListenerContainerFactory.getContainerProperties().setPollTimeout(3000);
	    // Set auto  startup to false, so the consumer has to start manually.
		concurrentKafkaListenerContainerFactory.setAutoStartup(Boolean.valueOf(this.autoStartup));
		
		return concurrentKafkaListenerContainerFactory;
	}

	@Bean
	public ConsumerFactory<Object, Object> consumerFactory() {

		return new DefaultKafkaConsumerFactory<>(this.consumerConfigs());
	}

	@Bean
	public Map<String, Object> consumerConfigs() {

		final Map<String, Object> configProps = new HashMap<>();
		configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapAddress);
		configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

		return configProps;
	}

}
