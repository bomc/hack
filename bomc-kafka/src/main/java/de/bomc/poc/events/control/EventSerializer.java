package de.bomc.poc.events.control;

import de.bomc.poc.events.entity.AppEvent;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.log4j.Logger;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Serializes the event for kafka treatment.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
public class EventSerializer implements Serializer<AppEvent> {

	private static final String LOG_PREFIX = "EventSerializer#";

	private Logger logger = Logger.getLogger(EventSerializer.class);

	@Override
	public void configure(final Map<String, ?> configs, final boolean isKey) {
		// nothing to configure
	}

	@Override
	public byte[] serialize(final String topic, final AppEvent event) {
		this.logger.debug(LOG_PREFIX + "serialize [topic=" + topic + ", event=" + event + "]");

		try {
			if (event == null) {
				return null;
			}

			final JsonbConfig config = new JsonbConfig().withAdapters(new UUIDAdapter())
					.withSerializers(new EventJsonbSerializer());

			final Jsonb jsonb = JsonbBuilder.create(config);

			final String jsonString = jsonb.toJson(event, AppEvent.class);
			this.logger.debug(LOG_PREFIX + "serialize [json=" + jsonString + "]");
			
			return jsonString.getBytes(StandardCharsets.UTF_8);
		} catch (final Exception e) {
			logger.error(LOG_PREFIX + "serialize - Could not serialize event [message=" + e.getMessage() + "]");

			throw new SerializationException(LOG_PREFIX + "serialize - Could not serialize event ", e);
		}
	}

	@Override
	public void close() {
		// nothing to do
	}

}
