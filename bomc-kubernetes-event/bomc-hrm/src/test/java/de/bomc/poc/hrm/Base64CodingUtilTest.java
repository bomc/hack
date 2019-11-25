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
package de.bomc.poc.hrm;

import java.util.Base64;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bomc.poc.hrm.application.CustomerServiceTest;

/**
 * A utility class for base64 encoding.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 25.11.2019
 */
public class Base64CodingUtilTest {

	private static final String LOG_PREFIX = Base64CodingUtilTest.class.getSimpleName() + "#";
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceTest.class.getName());
		
	@Test
	public void base64encoding() {
		
		final String originalInput = "bomc";
		
		final String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
		
		LOGGER.info(LOG_PREFIX + "base64encoding [encodedString=" + encodedString + "]");
	}
	
	@Test
	public void base64decoding() {
		
		final String originalInput = "bomc";
		
		final byte[] decodedBytes = Base64.getDecoder().decode(originalInput);
		final String decodedString = new String(decodedBytes);
		
		LOGGER.info(LOG_PREFIX + "base64decoding [decodedString=" + decodedString + "]");
	}
}
