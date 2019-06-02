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
package de.bomc.poc.hrm.infrastructure;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Optional;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.bomc.poc.hrm.AbstractBaseUnit;
import de.bomc.poc.hrm.domain.CustomerEntity;
import de.bomc.poc.hrm.infrastructure.CustomerRepository;

/**
 * Tests the customer repository.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@DataJpaTest
@RunWith(SpringJUnit4ClassRunner.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data-h2.sql")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomerRepositoryTest extends AbstractBaseUnit {

	private static final String LOG_PREFIX = "CustomerRepositoryTest#";
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerRepositoryTest.class.getName());

	/* --------------------- member variables ----------------------- */
	@Autowired
	private CustomerRepository customerRepository;

	/* --------------------- test methods --------------------------- */

	@Test
	public void test010_findByEmailAddress_pass() {
		LOGGER.info(LOG_PREFIX + "test010_findByEmailAddress_pass");

		// WHEN
		final Optional<CustomerEntity> optCustomerEntity = this.customerRepository.findByEmailAddress(CUSTOMER_E_MAIL);

		// THEN
		assertThat(optCustomerEntity.isPresent(), equalTo(true));
	}
	
	@Test
	public void test020_count_pass() {
		LOGGER.info(LOG_PREFIX + "test020_count_pass");
		
		// WHEN
		final long customerEntityCount = this.customerRepository.count();
		
		// THEN
		assertThat(customerEntityCount, equalTo(2L));
	}
}
