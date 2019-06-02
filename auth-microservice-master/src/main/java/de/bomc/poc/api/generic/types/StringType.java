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
 * <code>StringType</code> maps String to String to the request object.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class StringType extends AbstractType<String> {

    @XmlAttribute
    private String value;

    protected StringType() {
        //
        // Wird in Jaxb benötigt.
    }

    public StringType(final String value) {
        this.value = value;
    }

    @Override
    public Class<String> getReturnedClass() {
        return String.class;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StringType)) {
            return false;
        }

        final StringType that = (StringType)o;

        return this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String toString() {
        return "StringType [value=" + this.value + "]";
    }
}
