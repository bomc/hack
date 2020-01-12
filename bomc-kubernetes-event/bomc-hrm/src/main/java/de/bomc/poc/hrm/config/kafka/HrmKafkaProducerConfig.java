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

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

/**
 * Configures the producer.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 27.12.2019
 */
@Configuration
@Profile({ "prod" })
public class HrmKafkaProducerConfig {

	@Value(value = "${kafka.bootstrap-servers}")
	private String bootstrapAddress;
	
	/**
	 * Default properties will be injected to obtain the default KafkaProperties
	 * bean and then map (see configProps) will be built passing the default values
	 * for the producer, and overriding the default Kafka key and value serializers.
	 * The producer will serialize keys as Strings using the Kafka library's
	 * StringSerializer and will do the same for values but this time using JSON,
	 * with a JsonSerializer, in this case provided by Spring Kafka.
	 */
	@Autowired
	private KafkaProperties kafkaProperties;

	/**
	 * The KafkaTemplate instance, which is the object that will be used to send
	 * messages to Kafka.
	 * 
	 * NOTE: The reason to have Object as a value is that multiple objects types
	 * will sent with the same template.
	 */
	@Bean
	public KafkaTemplate<String, String> kafkaTemplate() {

		return new KafkaTemplate<>(this.producerFactory());
	}

	@Bean
	public ProducerFactory<String, String> producerFactory() {

		return new DefaultKafkaProducerFactory<>(this.producerConfigs());
	}

	/**
	 * See 'https://kafka.apache.org/documentation/#producerconfigs' for more properties. 
	 */
	@Bean
	public Map<String, Object> producerConfigs() {

		final Map<String, Object> configProps = new HashMap<>(kafkaProperties.buildProducerProperties());

		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapAddress);
		configProps.put(ProducerConfig.CLIENT_ID_CONFIG, "bomc-producer");
		configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

		return configProps;
	}

}
