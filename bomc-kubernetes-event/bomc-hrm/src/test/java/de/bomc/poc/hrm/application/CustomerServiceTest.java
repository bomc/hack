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
package de.bomc.poc.hrm.application;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import de.bomc.poc.hrm.AbstractBaseUnit;
import de.bomc.poc.hrm.domain.model.CustomerEntity;
import de.bomc.poc.hrm.infrastructure.CustomerRepository;
import de.bomc.poc.hrm.interfaces.mapper.CustomerDto;
import de.bomc.poc.hrm.interfaces.mapper.CustomerMapper;

/**
 * Test the service for customer handling.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 07.05.2019
 */
@RunWith(MockitoJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest(classes = { CustomerRepository.class, CustomerService.class })
public class CustomerServiceTest extends AbstractBaseUnit {

	private static final String LOG_PREFIX = "CustomerServiceTest#";
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceTest.class.getName());

	/* --------------------- member variables ----------------------- */
	@Rule
	public final ExpectedException thrown = ExpectedException.none();
	@Mock
	private CustomerRepository customerRepository;
	@Mock
	private CustomerMapper customerMapper;
	
	/* --------------------- methods -------------------------------- */

	@Test
	public void test010_findById_pass() {
		LOGGER.info(LOG_PREFIX + "test010_findById_pass");

		// GIVEN
		final CustomerService customerService = new CustomerService(this.customerRepository, this.customerMapper);

		final Optional<CustomerEntity> optionalCustomerEntity = Optional.of(createCustomerEntity());
		when(this.customerRepository.findById(CUSTOMER_ID)).thenReturn(optionalCustomerEntity);

		// WHEN
		customerService.findById(CUSTOMER_ID);

		// THEN
		// verify if the 'findById' method is called when 'findById' is called too.
		verify(this.customerRepository, times(1)).findById(any(Long.class));
	}

	@Test
	public void test020_findById_fail() {
		LOGGER.info(LOG_PREFIX + "test020_findById_fail");

		this.thrown.expect(IllegalStateException.class);

		// GIVEN
		final CustomerService customerService = new CustomerService(this.customerRepository, this.customerMapper);

		final Optional<CustomerEntity> optionalCustomerEntity = Optional.ofNullable(null);
		when(this.customerRepository.findById(CUSTOMER_ID)).thenReturn(optionalCustomerEntity);

		// WHEN
		customerService.findById(CUSTOMER_ID);

		// THEN
		// verify if the 'findById' method is called when 'findById' is called too.
		verify(this.customerRepository, times(1)).findById(any(Long.class));
		//
		// A IllegalStateException is expected.
	}
	
	@Test
	public void test030_createCustomer_pass() {
		LOGGER.info(LOG_PREFIX + "test030_createCustomer_pass");
		
		// GIVEN
		final CustomerService customerService = new CustomerService(this.customerRepository, this.customerMapper);
		
		final CustomerEntity customerEntity = createCustomerEntity();
		final CustomerEntity retCustomerEntity = createCustomerEntity();
		retCustomerEntity.setId(CUSTOMER_ID);
		
		final CustomerDto retCustomerDto = createCustomerDto();
		retCustomerDto.setId(CUSTOMER_ID);
		
		when(this.customerRepository.save(customerEntity)).thenReturn(retCustomerEntity);
		when(this.customerMapper.mapEntityToDto(retCustomerEntity)).thenReturn(retCustomerDto);
		
		// WHEN
		final CustomerDto createdCustomerDto = customerService.createCustomer(customerEntity);
		
		// THEN
		assertThat(createdCustomerDto.getId(), notNullValue());
	}
	
	@Test
	public void test040_deleteCustomerById_pass() {
		LOGGER.info(LOG_PREFIX + "test040_deleteCustomerById_pass");
		
		// GIVEN
		final CustomerService customerService = new CustomerService(this.customerRepository, this.customerMapper);
		doNothing().when(this.customerRepository).deleteById(CUSTOMER_ID);
		
		// WHEN
		customerService.deleteCustomerById(CUSTOMER_ID);
		
		// THEN
		// verify if the 'deleteCustomerById' method is called when 'deleteById' is called too.
		verify(this.customerRepository, times(1)).deleteById(CUSTOMER_ID);
	}
	
	@Test
	public void test050_updateCustomer_pass() {
		LOGGER.info(LOG_PREFIX + "test050_updateCustomer_pass");
		
		// GIVEN
		final CustomerService customerService = new CustomerService(this.customerRepository, this.customerMapper);
		
		// The incoming dto
		final String newCity = "Honululu";
		final CustomerDto customerDto = createCustomerDto();
		customerDto.setId(CUSTOMER_ID);
		// The dto mapped from incoming dto.
		final CustomerEntity customerEntity = createCustomerEntity();
		customerEntity.setCity(newCity);
		customerEntity.setId(CUSTOMER_ID);
		when(this.customerMapper.mapDtoToEntity(customerDto)).thenReturn(customerEntity);
		
		
		final CustomerEntity mergedCustomerEntity = createCustomerEntity();
		mergedCustomerEntity.setId(CUSTOMER_ID);
		mergedCustomerEntity.setCity(newCity);
		
		final CustomerEntity dbCustomerEntity = createCustomerEntity();
		dbCustomerEntity.setId(CUSTOMER_ID);

		final Optional<CustomerEntity> optionalCustomerEntity = Optional.of(dbCustomerEntity);
		assertThat(optionalCustomerEntity.get(), notNullValue());
		
		when(this.customerRepository.findById(customerDto.getId())).thenReturn(optionalCustomerEntity);
		when(this.customerRepository.save(optionalCustomerEntity.get())).thenReturn(mergedCustomerEntity);
		
		final CustomerDto mappedCustomerDto = createCustomerDto();
		mappedCustomerDto.setId(CUSTOMER_ID);
		mappedCustomerDto.setCity(newCity);
		when(this.customerMapper.mapEntityToDto(mergedCustomerEntity)).thenReturn(mappedCustomerDto);
		
		// WHEN
		final CustomerDto retCustomerDto = customerService.updateCustomer(customerDto);
		
		// THEN	
		// verify if the 'findById' method is called when 'findById' is called too.
		verify(this.customerRepository, times(1)).findById(customerDto.getId());
		assertThat(retCustomerDto.getId(), notNullValue());
	}
	
	@Test
	public void test060_findAll_pass() {
		LOGGER.info(LOG_PREFIX + "test060_findAll_pass");
		
		// GIVEN
		final CustomerService customerService = new CustomerService(this.customerRepository, this.customerMapper);
		
		final CustomerEntity customerEntity1 = createCustomerEntity();
		customerEntity1.setId(1L);
		final CustomerEntity customerEntity2 = createCustomerEntity();
		customerEntity1.setId(2L);
		
		when(this.customerRepository.findAll()).thenReturn(Arrays.asList(customerEntity1, customerEntity2));
		
		final CustomerDto customerDto1 = createCustomerDto();
		customerDto1.setId(1L);
		final CustomerDto customerDto2 = createCustomerDto();
		customerDto1.setId(2L);
		final List<CustomerDto> mappedCustomerDtoList = new ArrayList<>(2);
		mappedCustomerDtoList.add(customerDto1);
		mappedCustomerDtoList.add(customerDto2);
		when(this.customerMapper.mapEntitiesToDtos(Arrays.asList(customerEntity1, customerEntity2))).thenReturn(mappedCustomerDtoList);
		
		// WHEN
		final List<CustomerDto> customerDtoList = customerService.findAll();
		
		// THEN
		assertThat(customerDtoList.size(), equalTo(2));
	}
}
