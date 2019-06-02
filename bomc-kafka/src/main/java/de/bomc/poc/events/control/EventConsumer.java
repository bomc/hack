package de.bomc.poc.events.control;

import de.bomc.poc.events.entity.AppEvent;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.log4j.Logger;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static java.util.Arrays.asList;

/**
 * Consumes a event from topic and deserialize the event for further treatment.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
public class EventConsumer implements Runnable {

	private static final String LOG_PREFIX = "EventConsumer#";

	// NOTE: Don't use here CDI, -> -at LoggerQualifier.
	private Logger logger = Logger.getLogger(EventConsumer.class);

	private final KafkaConsumer<String, AppEvent> consumer;
	private final Consumer<AppEvent> eventConsumer;
	private final AtomicBoolean closed = new AtomicBoolean();

	/**
	 * Creates a new instance of <code>EventConsumer</code>.
	 * 
	 * @param kafkaProperties
	 *            the config parameter for the kafka connection.
	 * @param eventConsumer
	 *            a consumer that consumes the event.
	 * @param topics
	 *            the topics of which the consumer should consume messages.
	 */
	public EventConsumer(final Properties kafkaProperties, final Consumer<AppEvent> eventConsumer,
			final String... topics) {
		this.eventConsumer = eventConsumer;
		this.consumer = new KafkaConsumer<>(kafkaProperties);
		this.consumer.subscribe(asList(topics));
	}

	@Override
	public void run() {
		this.logger.debug(LOG_PREFIX + "run");

		try {
			while (!closed.get()) {
				consume();
			}
		} catch (WakeupException e) {
			// Will wakeup for closing.
			this.logger.error(LOG_PREFIX + "run - will wake up for closing. " + e);
		} finally {
			// Cleanup ressources.
			consumer.close();
		}
	}

	/**
	 * Consume the message from kafka topic.
	 */
	private void consume() {
		this.logger.debug(LOG_PREFIX + "consume");

		// Poll from topics.
		final ConsumerRecords<String, AppEvent> records = consumer.poll(Long.MAX_VALUE);

		for (ConsumerRecord<String, AppEvent> record : records) {
			eventConsumer.accept(record.value());

			this.logger.debug(LOG_PREFIX + "consume and accept - [record=" + record.value().toString() + "]");
		}

		consumer.commitSync();
	}

	/**
	 * Stop consuming from topic and cleanup resources.
	 */
	public void stop() {
		this.logger.debug(LOG_PREFIX + "stop");

		// Stop consuming from topic and cleanup resources.
		closed.set(true);
		consumer.wakeup();
	}

}
