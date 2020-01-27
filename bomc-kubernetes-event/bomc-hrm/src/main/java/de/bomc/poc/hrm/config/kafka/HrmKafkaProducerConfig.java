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

	/**
	 * Default properties will be injected to obtain the default KafkaProperties
	 * bean and then map (see configProps) will be built passing the default values
	 * for the producer, and overriding the default Kafka key and value serializers.
	 * The producer will serialize keys as Strings using the Kafka library's
	 * StringSerializer and will do the same for values but this time using JSON,
	 * with a JsonSerializer, in this case provided by Spring Kafka.
	 */
	private KafkaProperties kafkaProperties;

	/**
	 * Creates a new instance <code>HrmKafkaProducerConfig</code>.
	 * 
	 * @param kafkaProperties contains already the default properties for the
	 *                        producer.
	 */
	public HrmKafkaProducerConfig(final KafkaProperties kafkaProperties) {

		this.kafkaProperties = kafkaProperties;
	}

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
	 * See 'https://kafka.apache.org/documentation/#producerconfigs' for more
	 * properties.
	 */
	@Bean
	public Map<String, Object> producerConfigs() {

		final Map<String, Object> configProps = new HashMap<>(kafkaProperties.buildProducerProperties());

		return configProps;
	}

}
