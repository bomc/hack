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
package de.bomc.poc.controller.domain.shared;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * A helper for domain objects.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @since 08.11.2018
 */
public final class DomainObjectUtils {

    public static final String DATE_PATTERN = "dd.MM.yyyy";

    /**
     * Prevents instantiation.
     */
    private DomainObjectUtils() {
        //
    }

    /**
     * @param actual actual value
     * @param safe   a null-safe value
     * @param <T>    type
     * @return actual value, if it's not null, or safe value if the actual value is null.
     */
    public static <T> T nullSafe(final T actual, final T safe) {
        return actual == null ? safe : actual;
    }

    /**
     * Defines a value between min and max.
     * @param min the minimum value.
     * @param max the maximum value.
     * @return a random value between min and max.
     */
    public static long getRandomNumberInRange(final long min, final long max) {
        final Random r = new Random();

        return r.longs(min, (max + 1))
                .limit(1)
                .findFirst()
                .getAsLong();
    }

    /**
     * Formats <code>LocalDate</code> to string.
     * 
     * @param localDate
     *            the date to format.
     * @return a date as string.
     */
    public static String localDateToString(final LocalDate localDate) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        
        return localDate.format(formatter);
    }
}
