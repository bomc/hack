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
package de.bomc.poc.api.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Map;

/**
 * The entry type for the genric map.
 * @param <K> der Key
 * @param <V> der Value
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class MapEntryType<K, V> {

    private K key;
    private V value;

    public MapEntryType() {
    }

    public MapEntryType(final Map.Entry<K, V> e) {
        this.key = e.getKey();
        this.value = e.getValue();
    }

    @XmlElement
    public K getKey() {
        return this.key;
    }

    public void setKey(final K key) {
        this.key = key;
    }

    @XmlElement
    public V getValue() {
        return this.value;
    }

    public void setValue(final V value) {
        this.value = value;
    }
}
