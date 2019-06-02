package de.bomc.poc.core.domain;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.bomc.poc.core.domain.customer.CustomerStatusEnum;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomerStatusTest {

	private static final String LOG_PREFIX = "CustomerStatusTest#";
	private static final Logger LOGGER = Logger.getLogger(CustomerStatusTest.class);

	@Test
	public void test010_customerStatusEnum_pass() {
		LOGGER.debug(LOG_PREFIX + "test010_customerStatusEnum_pass");

		final CustomerStatusEnum customerStatus = CustomerStatusEnum.GOLD;
		assertThat(customerStatus.getValue(), equalTo(20L));

		final CustomerStatusEnum customerStatusBronze = CustomerStatusEnum.fromString(CustomerStatusEnum.BRONZE.name());
		assertThat(customerStatusBronze, equalTo(CustomerStatusEnum.BRONZE));

		final CustomerStatusEnum customerStatusPlatinum = CustomerStatusEnum
				.fromValue(CustomerStatusEnum.PLATINUM.getValue());
		assertThat(customerStatusPlatinum, equalTo(CustomerStatusEnum.PLATINUM));
	}
}
