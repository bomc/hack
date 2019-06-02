/**
 * Project: MY_POC
 * <p/>
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
 * <p/>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.auth.model.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * JPA 2.1 does not directly support the java.time API. so an
 * <code>AttributeConverter</code> is necessary to integrate Java 8 with JPA.
 *
 * Converts {@link Date} to {@link LocalDate} and back.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, Date> {

    @Override
    public Date convertToDatabaseColumn(final LocalDate localDate) {
        if (localDate != null) {
            return Date.from(localDate.atStartOfDay()
                                      .atZone(ZoneId.systemDefault())
                                      .toInstant());
        } else {
            return null;
        }
    }

    @Override
    public LocalDate convertToEntityAttribute(final Date date) {
        if (date != null) {
            return date.toInstant()
                       .atZone(ZoneId.systemDefault())
                       .toLocalDate();
        } else {
            return null;
        }
    }
}
