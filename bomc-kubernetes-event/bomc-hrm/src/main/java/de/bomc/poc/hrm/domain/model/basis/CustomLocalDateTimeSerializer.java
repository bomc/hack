/**
 * Project: POC PaaS
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.hrm.domain.model.basis;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * A custom serializer to write out the Java 8 DateTime to JSON.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
public class CustomLocalDateTimeSerializer extends StdSerializer<LocalDateTime> {

	/**
	 * The serial uid.
	 */
	private static final long serialVersionUID = 4387983210745172351L;
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	/**
	 * Creates a new instance of <code>CustomLocalDateTimeSerializer</code>.
	 */
	public CustomLocalDateTimeSerializer() {
		this(null);
	}

	/**
	 * Creates a new instance of <code>CustomLocalDateTimeSerializer</code>.
	 * 
	 * @param clazz the given class.
	 */
	public CustomLocalDateTimeSerializer(final Class<LocalDateTime> clazz) {
		super(clazz);
	}

	@Override
	public void serialize(final LocalDateTime value, final JsonGenerator jsonGenerator,
			final SerializerProvider serializerProvider) throws IOException, JsonProcessingException {

		jsonGenerator.writeString(formatter.format(value));
	}
}
