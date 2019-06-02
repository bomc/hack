/**
 * Project: MY_POC_MICROSERVICE
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
package de.bomc.poc.monitor.jmx.performance;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A data object that holds measured time and the timestamp for this invocation.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class PerformanceEntryNotifyData {
	/**
	 * Formatter for date time.
	 */
	protected static final String DATE_TIME_FORMATTER = "dd.MM.yyyy HH:mm:ss.SSS";
	private final LocalDateTime timestamp;
	private final long time;

	/**
	 * Creates a new instance of <code>PerformanceEntryNotifyData</code>. The
	 * timestamp is set during the creation of the object.
	 * 
	 * @param time
	 *            the measured time, that is exceeded.
	 */
	public PerformanceEntryNotifyData(final long time) {
		this.time = time;
		this.timestamp = LocalDateTime.now();
	}

	/**
	 * @return the measured time for this invocation.
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @return the timestamp
	 */
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();

		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
		final String timestamp = this.getTimestamp().format(formatter);

		sb.append("PerformanceEntryNotifyData [time=").append(this.time).append(", timestamp=").append(timestamp)
				.append("]");

		return sb.toString();
	}
}
