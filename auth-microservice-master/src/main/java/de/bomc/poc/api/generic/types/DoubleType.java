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
 * <code>DoubleType</code> maps Double to Double to the request object.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DoubleType extends AbstractType<Double> {

    @XmlAttribute
    private Double value;

    protected DoubleType() {
        //
        // Wird von Jaxb benötigt.
    }

    public DoubleType(final double value) {
        this.value = value;
    }

    @Override
    public Class<Double> getReturnedClass() {
        return Double.class;
    }

    @Override
    public Double getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DoubleType)) {
            return false;
        }

        final DoubleType that = (DoubleType)o;

        return this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String toString() {
        return "DoubleType [value=" + this.value + "]";
    }
}
