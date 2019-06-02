package de.bomc.poc.events.control;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.log4j.Logger;

import de.bomc.poc.events.entity.AppEvent;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.util.Map;

/**
 * This class deserializes consumed events from kafka-topics.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
public class EventDeserializer implements Deserializer<AppEvent> {
	private static final String LOG_PREFIX = "EventJsonbSerializer#";

	private Logger logger = Logger.getLogger(EventDeserializer.class);

	@Override
	public void configure(final Map<String, ?> configs, final boolean isKey) {
		// nothing to configure
	}

	@Override
	@SuppressWarnings("unchecked")
	public AppEvent deserialize(final String topic, final byte[] data) {
		this.logger.debug(LOG_PREFIX + "deserialize [topic=" + topic + ", data=" + data + "]");

		try (ByteArrayInputStream input = new ByteArrayInputStream(data)) {
			final JsonObject jsonObject = Json.createReader(input).readObject();

			final Class<? extends AppEvent> eventClass = (Class<? extends AppEvent>) Class
					.forName(jsonObject.getString("class"));

			return eventClass.getConstructor(JsonObject.class).newInstance(jsonObject.getJsonObject("data"));
		} catch (final Exception e) {
			this.logger
					.error(LOG_PREFIX + "deserialize - Could not deserialize event [message= " + e.getMessage() + "]");

			throw new SerializationException("Could not deserialize event", e);
		}
	}

	@Override
	public void close() {
		// nothing to do
	}

}
