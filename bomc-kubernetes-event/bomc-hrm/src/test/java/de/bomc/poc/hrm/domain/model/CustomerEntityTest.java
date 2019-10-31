/**
 * Project: hrm
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.hrm.domain.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bomc.poc.hrm.AbstractBaseUnit;

/**
 * Tests the customer entity.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomerEntityTest extends AbstractBaseUnit {

	private static final String LOG_PREFIX = "CustomerEntityTest#";
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerEntityTest.class);

	// _______________________________________________
	// Test constants
	// -----------------------------------------------

	// _______________________________________________
	// Member variables
	// -----------------------------------------------

	// _______________________________________________
	// Test preparation
	// -----------------------------------------------
	@Before
	public void initialize() {
		LOGGER.debug(LOG_PREFIX + "initialize");

		this.entityManager1 = this.emProvider1.getEntityManager();

		assertThat(this.entityManager1, notNullValue());
	}

	// _______________________________________________
	// Test methods
	// -----------------------------------------------/
	@Test
	public void test010_createCustomer_pass() {
		LOGGER.info(LOG_PREFIX + "test010_createCustomer_pass");

		// GIVEN
		final CustomerEntity customerEntity = createNonPersistedCustomerEntity();
		customerEntity.setCreateUser(CUSTOMER_CREATE_USER);

		// WHEN
		this.emProvider1.tx().begin();

		this.entityManager1.persist(customerEntity);

		this.emProvider1.tx().commit();

		// THEN
		assertThat(customerEntity.getId(), notNullValue());
	}
}
