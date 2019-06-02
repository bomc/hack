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
import java.time.LocalDate;
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
public class CustomLocalDateSerializer extends StdSerializer<LocalDate> {

	/**
	 * The serial uid.
	 */
	private static final long serialVersionUID = 4387983210745172351L;
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	/**
	 * Creates a new instance of <code>CustomLocalDateSerializer</code>.
	 */
	public CustomLocalDateSerializer() {
		this(null);
	}

	/**
	 * Creates a new instance of <code>CustomLocalDateSerializer</code>.
	 * 
	 * @param clazz the given class.
	 */
	public CustomLocalDateSerializer(final Class<LocalDate> clazz) {
		super(clazz);
	}

	@Override
	public void serialize(final LocalDate value, final JsonGenerator jsonGenerator,
			final SerializerProvider serializerProvider) throws IOException, JsonProcessingException {

		jsonGenerator.writeString(formatter.format(value));
	}
}
