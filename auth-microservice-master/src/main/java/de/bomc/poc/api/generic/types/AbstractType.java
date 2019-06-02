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

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Allgemeine abstrakte Superclass für {@link Type} Implementationen.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public abstract class AbstractType<T> implements Type<T> {

    public AbstractType() {
        //
    }

    @Override
    public String getLiteral(final T value) {
        return value.toString();
    }

    static class Adapter extends XmlAdapter<AbstractType<?>, Type<?>> {

        public Type<?> unmarshal(final AbstractType<?> v) {
            return v;
        }

        public AbstractType<?> marshal(final Type<?> v) {
            return (AbstractType<?>)v;
        }
    }
}
