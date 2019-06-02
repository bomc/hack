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
 * <code>IntegerType</code> maps Integer to Integer to the request object.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class IntegerType extends AbstractType<Integer> {

    @XmlAttribute
    private Integer value;

    protected IntegerType() {
        //
        // Wird von Jaxb benötigt.
    }

    public IntegerType(final int value) {
        this.value = value;
    }

    @Override
    public Class<Integer> getReturnedClass() {
        return Integer.class;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntegerType)) {
            return false;
        }

        final IntegerType that = (IntegerType)o;

        return this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String toString() {
        return "IntegerType [value=" + this.value + "]";
    }
}

