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
package de.bomc.poc.api.mapper;

import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.validation.constraints.NotNull;

/**
 * A mapper for {@link LocalDateTime} instances using Mapstruct.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
public class LocalDateTimeMapper {
	public static final String DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";
	private static final Logger LOGGER = Logger.getLogger(LocalDateTimeMapper.class);

	public String asString(@NotNull final LocalDateTime localDateTime) {

		final String formattedLocalDateTime = localDateTime
				.format(DateTimeFormatter.ofPattern(LocalDateTimeMapper.DATE_TIME_FORMATTER));

		LOGGER.debug("LocalDateTimeMapper#asString [formattedLocalDateTime=" + formattedLocalDateTime + "]");

		return formattedLocalDateTime;
	}

	public LocalDateTime asDate(@NotNull final String strDate) {
		LOGGER.debug("LocalDateTimeMapper#asDate [strDate=" + strDate + "]");

		final LocalDateTime strLocalDateTime = LocalDateTime.parse(strDate,
				DateTimeFormatter.ofPattern(LocalDateTimeMapper.DATE_TIME_FORMATTER, new Locale("ch")));

		return strLocalDateTime;
	}
}
