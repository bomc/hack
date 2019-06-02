package de.bomc.poc.core.domain.customer;

import java.util.stream.Stream;

/**
 * Enum represents the status of the customer.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
public enum CustomerStatusEnum {

	BRONZE(10L), GOLD(20L), PLATINUM(30L);

	private Long value;

	CustomerStatusEnum(final Long value) {
		this.value = value;
	}

	public Long getValue() {
		return value;
	}

	public static CustomerStatusEnum fromValue(final Long value) {
		return Stream.of(values()).filter(v -> v.getValue().equals(value)).findAny().orElse(null);

	}

	public static CustomerStatusEnum fromString(final String name) {
		return Stream.of(values()).filter(v -> v.name().equalsIgnoreCase(name)).findAny().orElse(null);
	}
}
