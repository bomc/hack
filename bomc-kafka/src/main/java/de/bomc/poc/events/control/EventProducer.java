package de.bomc.poc.events.control;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.log4j.Logger;

import de.bomc.poc.config.ConfigurationPropertiesConstants;
import de.bomc.poc.config.qualifier.PropertiesConfigTypeQualifier;
import de.bomc.poc.config.type.PropertiesConfigTypeEnum;
import de.bomc.poc.events.entity.AppEvent;
import de.bomc.poc.logging.qualifier.LoggerQualifier;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Properties;
import java.util.UUID;

/**
 * Produces a event, the event will be sent to the kafka middleware.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
@ApplicationScoped
public class EventProducer {

	// _______________________________________________
	// Class constants.
	// -----------------------------------------------
	private static final String LOG_PREFIX = "EventProducer#";
	// _______________________________________________
	// Member variables.
	// -----------------------------------------------
	private Producer<String, AppEvent> producer;
	private String topic;
	@Inject
	@PropertiesConfigTypeQualifier(type = PropertiesConfigTypeEnum.KAFKA_CONFIG)
	private Properties kafkaProperties;
	@Inject
	@LoggerQualifier
	private Logger logger;

	@PostConstruct
	private void init() {
		this.logger.debug(LOG_PREFIX + "init");

		final String transactionalIdValue = UUID.randomUUID().toString();
		kafkaProperties.put(ConfigurationPropertiesConstants.KAFKA_TRANSACTIONAL_ID_KEY, transactionalIdValue);
		producer = new KafkaProducer<>(kafkaProperties);
		topic = kafkaProperties.getProperty(ConfigurationPropertiesConstants.KAFKA_BOMC_TOPIC_PROPERTY_KEY);
		producer.initTransactions();
	}

	public void publish(AppEvent event) {
		this.logger.debug(LOG_PREFIX + "publish [eventType=" + event.getClass().getName() + "]");

		final ProducerRecord<String, AppEvent> record = new ProducerRecord<>(topic, event);

		try {
			producer.beginTransaction();

			logger.info(LOG_PREFIX + "publish - publishing record [topic=" + this.topic + ", record=" + record + "]");

			producer.send(record);
			producer.commitTransaction();
		} catch (final ProducerFencedException e) {
			this.logger.error(LOG_PREFIX
					+ "publish - fatal exception indicates that another producer with the same 'transactional.id' has been started. [message="
					+ e.getMessage() + "]");

			// Cleanup.
			producer.close();
		} catch (final KafkaException e) {
			this.logger.error(LOG_PREFIX + "publish - failed! " + e);

			producer.abortTransaction();
		}
	}

	@PreDestroy
	public void close() {
		// Cleanup resources.
		producer.close();
	}

}
