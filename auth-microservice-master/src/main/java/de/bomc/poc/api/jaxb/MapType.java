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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type for the generic map.
 * @param <K> der Key
 * @param <V> der Value
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class MapType<K, V> {

    private List<MapEntryType<K, V>> entry = new ArrayList<>();

    public MapType() {
        //
        // Is used by jaxb.
    }

    public MapType(final Map<K, V> map) {
        for (final Map.Entry<K, V> e : map.entrySet()) {
            this.entry.add(new MapEntryType<>(e));
        }
    }

    public List<MapEntryType<K, V>> getEntry() {
        return this.entry;
    }

    public void setEntry(final List<MapEntryType<K, V>> entry) {
        this.entry = entry;
    }
}
