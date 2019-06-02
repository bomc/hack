package de.bomc.poc.events.control;

import javax.json.bind.adapter.JsonbAdapter;

import org.apache.log4j.Logger;

import java.util.UUID;

/**
 * Adapts to and from Json the UUID.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
public class UUIDAdapter implements JsonbAdapter<UUID, String> {
	private static final String LOG_PREFIX = "UUIDAdapter#";

	private Logger logger = Logger.getLogger(UUIDAdapter.class);

	@Override
	public String adaptToJson(final UUID uuid) {
		this.logger.debug(LOG_PREFIX + "adaptToJson [uuid=" + uuid.toString() + "]");

		return uuid.toString();
	}

	@Override
	public UUID adaptFromJson(final String string) {
		this.logger.debug(LOG_PREFIX + "adaptFromJson [json=" + string + "]");

		return UUID.fromString(string);
	}

}
