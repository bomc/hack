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

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Definiert den Typ des zu uebertragenen Datentypen eines Request-Objektes.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@XmlJavaTypeAdapter(AbstractType.Adapter.class)
public interface Type<T> {

    /**
     * Get the returned type
     * @return returned class
     */
    Class<T> getReturnedClass();

    /**
     * Get the literal representation
     * @param value value
     * @return literal representation
     */
    String getLiteral(T value);

    /**
     * Get the value from the paramter definition.
     * @return value
     */
    T getValue();
}
