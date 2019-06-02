/**
 * Project: MY_POC
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.api.jaxb;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.log4j.Logger;

/**
 * An adapter for xml mapping of {@link LocalDateTime}. Jaxb does not support
 * LocalDateTime.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {
	private static final String LOG_PREFIX = "transfer#LocalDateTimeAdapter#";
	private static final Logger LOGGER = Logger.getLogger(LocalDateTimeAdapter.class);
	private static final String DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";

	public LocalDateTime unmarshal(String date) {
		try {
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LocalDateTimeAdapter.DATE_TIME_FORMATTER);

			return LocalDateTime.parse(date, formatter);
		} catch (DateTimeParseException ex) {
			LOGGER.error(LOG_PREFIX + "Could not parse date=" + date, ex);
			return null;
		}
	}

	public String marshal(LocalDateTime dateTime) {
		return dateTime.format(DateTimeFormatter.ofPattern(LocalDateTimeAdapter.DATE_TIME_FORMATTER));
	}
}
