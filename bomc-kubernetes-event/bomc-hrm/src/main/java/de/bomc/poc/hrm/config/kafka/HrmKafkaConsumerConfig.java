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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
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

	@Autowired
	private HrmKafkaRecordFilter hrmKafkaRecordFilter;
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

	@Value(value = "${kafka.bootstrap-servers}")
	private String bootstrapAddress;
	@Value(value = "${kafka.topic.consumer.group-id}")
	private String groupId;
	
    // If only one kind of deserialization is needed, only the commented consumer 
	// configuration properties is needed. Uncomment this and remove all others below.
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
	
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> concurrentKafkaListenerContainerFactory() {

		final ConcurrentKafkaListenerContainerFactory<String, String> concurrentKafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
		concurrentKafkaListenerContainerFactory.setConsumerFactory(consumerFactory());
		// Set record filter strategy to Listener Container Factory.
		concurrentKafkaListenerContainerFactory.setRecordFilterStrategy(hrmKafkaRecordFilter);
		
		return concurrentKafkaListenerContainerFactory;
	}
	
    @Bean
    public ConsumerFactory<Object, Object> consumerFactory() {
    	
        return new DefaultKafkaConsumerFactory<>(this.consumerConfigs());
    }
    
    @Bean
    public Map<String, Object> consumerConfigs() {

        final Map<String, Object> configProps = new HashMap<>(kafkaProperties.buildProducerProperties());
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapAddress);
//        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        
        return configProps;
    }
   
}
