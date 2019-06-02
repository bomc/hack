package de.bomc.poc.events.entity;

import java.util.stream.Stream;

public enum EventType {

    BOMC_EVENT_A, BOMC_EVENT_B, BOMC_EVENT_C;

    public static EventType fromString(final String name) {
        return Stream.of(values())
                .filter(v -> v.name().equalsIgnoreCase(name))
                .findAny().orElse(null);
    }

}
