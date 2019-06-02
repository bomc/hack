package de.bomc.registrator.events.entity;

import javax.json.JsonObject;
import java.util.UUID;

/**
 * Metadata for describing an event.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
public class EventInfo {

	private final UUID eventId;
	private final EventType type;
	private final String payload;

	/**
	 * Creates a new instance of <code>EventInfo</code>.
	 * 
	 * @param eventId
	 *            a unique identifier of the event.
	 * @param type
	 *            the type of event.
	 * @param payload
	 *            the payload.
	 */
	public EventInfo(final UUID eventId, final EventType type, final String payload) {

		this.eventId = eventId;
		this.type = type;
		this.payload = payload;
	}

	public EventInfo(final JsonObject jsonObject) {
		this(UUID.fromString(jsonObject.getString("eventId")), EventType.fromString(jsonObject.getString("type")),
				jsonObject.getString("payload"));
	}

	public UUID getEventId() {
		return eventId;
	}

	public EventType getType() {
		return type;
	}

	public String getPayload() {
		return payload;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eventId == null) ? 0 : eventId.hashCode());
		result = prime * result + ((payload == null) ? 0 : payload.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventInfo other = (EventInfo) obj;
		if (eventId == null) {
			if (other.eventId != null)
				return false;
		} else if (!eventId.equals(other.eventId))
			return false;
		if (payload == null) {
			if (other.payload != null)
				return false;
		} else if (!payload.equals(other.payload))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EventInfo [eventId=" + eventId + ", type=" + type + ", payload=" + payload + "]";
	}

}