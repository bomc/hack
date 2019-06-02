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
package de.bomc.poc.api.generic.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <code>DateType</code> maps Date to the request object.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DateType extends AbstractType<Date> {

    @XmlTransient
    private final DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    @XmlAttribute
    private Date value;

    protected DateType() {
        //
        // Wird von Jaxb benötigt.
    }

    public DateType(final Date value) {
        value.getTime();
        this.value = value;
    }

    @Override
    public Class<Date> getReturnedClass() {
        return Date.class;
    }

    @Override
    public Date getValue() {
        return this.value;
    }

    @Override
    public String getLiteral(final Date value) {
        return this.df.format(value);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DateType)) {
            return false;
        }

        final DateType that = (DateType)o;

        return this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String toString() {
        return "DateType [value=" + this.df.format(this.value) + "]";
    }
}
