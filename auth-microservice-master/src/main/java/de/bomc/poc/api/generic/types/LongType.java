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

/**
 * <code>LongType</code> maps Long to Long to the request object.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LongType extends AbstractType<Long> {

    @XmlAttribute
    private Long value;

    protected LongType() {
        //
        // Wird von Jaxb benötigt.
    }

    public LongType(final long value) {
        this.value = value;
    }

    @Override
    public Class<Long> getReturnedClass() {
        return Long.class;
    }

    @Override
    public Long getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LongType)) {
            return false;
        }

        final LongType that = (LongType)o;

        return this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String toString() {
        return "LongType [value=" + this.value + "]";
    }
}
