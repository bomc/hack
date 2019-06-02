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

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * An generic Jaxb adapter for marshalling and unmarshalling of a map.
 * @param <K> entspricht dem 'Key'.
 * @param <V> entspricht dem 'Value'.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class JaxbGenMapAdapter<K, V> extends XmlAdapter<MapType<K, V>, Map<K, V>> {

    @Override
    public Map<K, V> unmarshal(final MapType<K, V> v) throws Exception {
        final HashMap<K, V> map = new HashMap<>();

        for (final MapEntryType<K, V> mapEntryType : v.getEntry()) {
            map.put(mapEntryType.getKey(), mapEntryType.getValue());
        }
        return map;
    }

    @Override
    public MapType<K, V> marshal(final Map<K, V> v) throws Exception {
        final MapType<K, V> mapType = new MapType<>();

        for (final Map.Entry<K, V> entry : v.entrySet()) {
            final MapEntryType<K, V> mapEntryType = new MapEntryType<>();
            mapEntryType.setKey(entry.getKey());
            mapEntryType.setValue(entry.getValue());
            mapType.getEntry()
                   .add(mapEntryType);
        }
        return mapType;
    }
}
