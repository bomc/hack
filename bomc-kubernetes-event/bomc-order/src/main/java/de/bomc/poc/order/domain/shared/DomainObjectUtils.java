/**
 * Project: bomc-onion-architecture
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
package de.bomc.poc.order.domain.shared;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

/**
 * A helper for domain objects.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @since 08.11.2018
 */
public final class DomainObjectUtils {

    // RFC 1123 constants
    public static final String RFC_1123_DATE_TIME = "EEE, dd MMM yyyy HH:mm:ss z";
    public static final String ZONE_ID_EUROPE_ZURICH = "Europe/Zurich";
    public static final String DATE_PATTERN = "dd.MM.yyyy HH:mm:ss";

    /**
     * Prevents instantiation.
     */
    private DomainObjectUtils() {
        //
    }

    /**
     * @param actual
     *            actual value
     * @param safe
     *            a null-safe value
     * @param <T>
     *            type
     * @return actual value, if it's not null, or safe value if the actual value
     *         is null.
     */
    public static <T> T nullSafe(final T actual, final T safe) {
        return actual == null ? safe : actual;
    }

    /**
     * Defines a value between min and max.
     * 
     * @param min
     *            the minimum value.
     * @param max
     *            the maximum value.
     * @return a random value between min and max.
     */
    public static long getRandomNumberInRange(final long min, final long max) {
        final Random r = new Random();

        return r.longs(min, (max + 1)).limit(1).findFirst().getAsLong();
    }

    /**
     * Formats <code>LocalDate</code> to string.
     * 
     * @param localDate
     *            the date to format.
     * @return a date as string.
     */
    public static String localDateTimeToString(final LocalDateTime localDateTime) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);

        return localDateTime.format(formatter);
    }

    /**
     * Format LocalDateTime to Date.
     * 
     * @param localDateTime
     *            the LocalDateTime to convert.
     * @return The converted date.
     */
    public static Date convertLocalDateTimeToDate(final LocalDateTime localDateTime) {

        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Formats the specified date to an RFC 1123-compliant string.
     *
     * @param date
     *            the date to format
     * @param timeZone
     *            the {@link TimeZone} to use when formatting the date
     * @return the formatted string
     */
    public static String formatRfc1123DateTime(final Date date, final TimeZone timeZone) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(RFC_1123_DATE_TIME, Locale.US);
        
        if (timeZone != null) {
            dateFormat.setTimeZone(timeZone);
        }
        
        return dateFormat.format(date);
    }
}
