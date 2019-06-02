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
 * <code>BooleanType</code> maps Boolean to Boolean to the request object.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BooleanType extends AbstractType<Boolean> {

    @XmlAttribute
    private Boolean value;

    protected BooleanType() {
        //
        // Wird von Jaxb benötigt.
    }

    public BooleanType(final boolean value) {
        this.value = value;
    }

    @Override
    public Class<Boolean> getReturnedClass() {
        return Boolean.class;
    }

    @Override
    public String getLiteral(final Boolean value) {
        return value ? "1" : "0";
    }

    @Override
    public Boolean getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BooleanType)) {
            return false;
        }

        final BooleanType that = (BooleanType)o;

        return this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String toString() {
        return "BooleanType [value=" + this.value + "]";
    }
}
