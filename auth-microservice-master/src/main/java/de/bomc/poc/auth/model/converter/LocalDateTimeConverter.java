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
 * Copyright (c): BOMC, 2015
 */
package de.bomc.poc.auth.model.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * JPA 2.1 does not directly support the java.time API. so an
 * <code>AttributeConverter</code> is necessary to integrate Java 8 with JPA.
 *
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Date> {

    @Override
    public Date convertToDatabaseColumn(final LocalDateTime localDateTime) {
        if (localDateTime != null) {
            return Date.from(localDateTime.atZone(ZoneId.systemDefault())
                                          .toInstant());
        } else {
            return null;
        }
    }

    @Override
    public LocalDateTime convertToEntityAttribute(final Date date) {
        if (date != null) {
            return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        } else {
            return null;
        }
    }
}
