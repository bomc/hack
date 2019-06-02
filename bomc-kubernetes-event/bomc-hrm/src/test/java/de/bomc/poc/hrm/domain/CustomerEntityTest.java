/**
 * Project: POC PaaS
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
package de.bomc.poc.hrm.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.bomc.poc.hrm.AbstractBaseUnit;
import de.bomc.poc.hrm.domain.CustomerEntity;

/**
 * Tests the customer entity.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@DataJpaTest
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomerEntityTest extends AbstractBaseUnit {

	private static final String LOG_PREFIX = "CustomerEntityTest#";
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerEntityTest.class);

	/* --------------------- constants ------------------------------ */
	private static final String CUSTOMER_CREATE_USER = "myCreateUser";

	/* --------------------- member variables ----------------------- */
	@Autowired
	private TestEntityManager entityManager;

	/* --------------------- test methods --------------------------- */
	@Test
	public void test010_createCustomer_pass() {
		LOGGER.info(LOG_PREFIX + "test010_createCustomer_pass");

		// GIVEN
		final CustomerEntity customerEntity = createCustomerEntity();
		customerEntity.setCreateUser(CUSTOMER_CREATE_USER);

		// WHEN
		this.entityManager.persist(customerEntity);

		// THEN
		assertThat(customerEntity.getId(), notNullValue());
	}
}
