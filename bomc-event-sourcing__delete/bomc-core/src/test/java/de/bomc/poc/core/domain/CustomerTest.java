package de.bomc.poc.core.domain;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.bomc.poc.core.domain.customer.Customer;
import de.bomc.poc.core.domain.customer.CustomerStatusEnum;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomerTest {

	private static final String LOG_PREFIX = "CustomerTest#";
	private static final Logger LOGGER = Logger.getLogger(CustomerTest.class);
    private static final String PERSISTENCE_UNIT_NAME = "poc-core-pu";
    private static final String CUSTOMER_NAME = "myCustomerName";
    
    @Rule
    public EntityManagerProvider emProvider = EntityManagerProvider.persistenceUnit(PERSISTENCE_UNIT_NAME);
    
	@Test
	public void test010_pass() {
		LOGGER.debug(LOG_PREFIX + "test010_pass");

		final EntityManager entityManager = this.emProvider.getEntityManager();
		
		final Customer customer = new Customer(CUSTOMER_NAME);
		
		assertThat(customer.getId(), nullValue());
		
		this.emProvider.tx().begin();
		
		entityManager.persist(customer);
		
		this.emProvider.tx().commit();
		
		assertThat(customer.getId(), notNullValue());
		assertThat(customer.getCustomerStatus(), equalTo(CustomerStatusEnum.BRONZE));
	}
}
