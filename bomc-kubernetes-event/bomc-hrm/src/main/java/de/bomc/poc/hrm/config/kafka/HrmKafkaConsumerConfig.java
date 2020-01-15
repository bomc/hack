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

	@Value(value = "${kafka.consumer.topic.group-id}")
	private String groupId;
	@Value(value = "${kafka.consumer.factory.concurrent}")
	private String concurrentConsumer;
	@Value(value = "${kafka.consumer.factory.auto-startup}")
	private String autoStartup;

	// Comma-delimited list of host:port pairs to use for establishing the initial
	// connection to the Kafka cluster.
	@Value(value = "${kafka.bootstrap.servers}")
	private String bootstrapServers;
//	@Value(value = "${kafka.client.dns.lookup}")
//	private String clientDnsLookup; 	
	// Id to pass to the server when making requests; used for server-side logging.
	@Value(value = "${kafka.client.id}")
	private String clientId;
//	@Value(value = "${kafka.connections.max.idle.ms}")
//	private String connectionMaxIdleMs;
//	@Value(value = "${kafka.metadata.max.age.ms}")
//	private String metadataMaxAgeMs;
//	@Value(value = "${kafka.metric.reporters}")
//	private String metricReporters;
//	@Value(value = "${kafka.metrics.num.samples}")
//	private String metricsNumSamples;
//	@Value(value = "${kafka.metrics.recording.level}")
//	private String metricsRecordingLevel;
//	@Value(value = "${kafka.metrics.sample.window.ms}")
//	private String metricsSampleWindowMs;
//	@Value(value = "${kafka.receive.buffer.bytes}")
//	private String receiveBufferBytes;
//	@Value(value = "${kafka.reconnect.backoff.max.ms}")
//	private String reconnectBackoffMaxMs;
//	@Value(value = "${kafka.reconnect.backoff.ms}")
//	private String reconnectBackoffMs;
//	@Value(value = "${kafka.request.timeout.ms}")
//	private String requestTimeoutMs;
//	@Value(value = "${kafka.retries}")
//	private String retries;
//	@Value(value = "${kafka.retry.backoff.ms}")
//	private String retryBackoffMs;
//	@Value(value = "${kafka.sasl.client.callback.handler.class}")
//	private String saslClientCallbackHandlerClass;
//	@Value(value = "${kafka.sasl.jaas.config}")
//	private String saslJaasConfig;
//	@Value(value = "${kafka.sasl.kerberos.kinit.cmd}")
//	private String saslKerberosKinitCmd;
//	@Value(value = "${kafka.sasl.kerberos.min.time.before.relogin}")
//	private String saslKerberosMinTimeBeforeRelogin;
//	@Value(value = "${kafka.sasl.kerberos.service.name}")
//	private String saslKerberosServiceName;
//	@Value(value = "${kafka.sasl.kerberos.ticket.renew.jitter}")
//	private String saslKerberosTicketRenewJitter;
//	@Value(value = "${kafka.sasl.kerberos.ticket.renew.window.factor}")
//	private String saslKerberosTicketRenewWindowFactor;
//	@Value(value = "${kafka.sasl.login.callback.handler.class}")
//	private String saslLoginCallbackHandlerClass;
//	@Value(value = "${kafka.sasl.login.class}")
//	private String saslLoginClass;
//	@Value(value = "${kafka.sasl.login.refresh.buffer.seconds}")
//	private String saslLoginRefreshBufferSeconds;
//	@Value(value = "${kafka.sasl.login.refresh.min.period.seconds}")
//	private String saslLoginRefreshMinPeriodSeconds;
//	@Value(value = "${kafka.sasl.login.refresh.window.factor}")
//	private String saslLoginRefreshWindowFactor;
//	@Value(value = "${kafka.sasl.login.refresh.window.jitter}")
//	private String saslLoginRefreshWindowJitter;
//	@Value(value = "${kafka.sasl.mechanism}")
//	private String saslMechanism;
	@Value(value = "${kafka.security.protocol}")
	private String securityProtocol;
//	@Value(value = "${kafka.send.buffer.bytes}")
//	private String sendBufferBytes;
//	@Value(value = "${kafka.ssl.cipher.suites}")
//	private String sslCipherSuites;
//	@Value(value = "${kafka.ssl.enabled.protocols}")
//	private String sslEnabledProtocols;
//	@Value(value = "${kafka.ssl.endpoint.identification.algorithm}")
//	private String sslEndpointIdentificationAlgorithm;
//	@Value(value = "${kafka.ssl.key.password}")
//	private String sslKeyPassword;
//	@Value(value = "${kafka.ssl.keymanager.algorithm}")
//	private String sslKeymanagerAlgorithm;
	@Value(value = "${kafka.ssl.keystore.location}")
	private String sslKeystoreLocation;
	@Value(value = "${kafka.ssl.keystore.password}")
	private String sslKeystorePassword;
	@Value(value = "${kafka.ssl.keystore.type}")
	private String sslKeystoreType;
	@Value(value = "${kafka.ssl.protocol}")
	private String sslProtocol;
//	@Value(value = "${kafka.ssl.provider}")
//	private String sslProvider;
//	@Value(value = "${kafka.ssl.secure.random.implementation}")
//	private String sslSecureRandomImplementation;
//	@Value(value = "${kafka.ssl.trustmanager.algorithm}")
//	private String sslTrustmanagerAlgorithm;
	@Value(value = "${kafka.ssl.truststore.location}")
	private String sslTruststoreLocation;
	@Value(value = "${kafka.ssl.truststore.password}")
	private String sslTruststorePassword;
	@Value(value = "${kafka.ssl.truststore.type}")
	private String sslTruststoreType;

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
		// Set auto startup to false, so the consumer has to start manually.
		concurrentKafkaListenerContainerFactory.setAutoStartup(Boolean.valueOf(this.autoStartup));

		concurrentKafkaListenerContainerFactory.setErrorHandler(new HrmKafkaConsumerExceptionHandler());

		return concurrentKafkaListenerContainerFactory;
	}

	@Bean
	public ConsumerFactory<Object, Object> consumerFactory() {

		return new DefaultKafkaConsumerFactory<>(this.consumerConfigs());
	}

	@Bean
	public Map<String, Object> consumerConfigs() {

		final Map<String, Object> configProps = new HashMap<>();
		configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
		configProps.put(ConsumerConfig.CLIENT_ID_CONFIG, this.clientId);
		configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

		configProps.put("security.protocol", this.securityProtocol);
//		configProps.put("ssl.key.password", this.sslKeyPassword);
//		configProps.put("ssl.keymanager.algorithm", this.sslKeymanagerAlgorithm);
		configProps.put("ssl.keystore.location", this.sslKeystoreLocation);
		configProps.put("ssl.keystore.password", this.sslKeystorePassword);
		configProps.put("ssl.keystore.type", this.sslKeystoreType);
		configProps.put("ssl.protocol", this.sslProtocol);
//		configProps.put("ssl.provider", this.sslProvider);
//		configProps.put("ssl.secure.random.implementation", this.sslSecureRandomImplementation);
//		configProps.put("ssl.trustmanager.algorithm", this.sslTrustmanagerAlgorithm);
		configProps.put("ssl.truststore.location", this.sslTruststoreLocation);
		configProps.put("ssl.truststore.password", this.sslTruststorePassword);
		configProps.put("ssl.truststore.type", this.sslTruststoreType);
		
		return configProps;
	}

}
