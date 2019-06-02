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
package de.bomc.poc.hrm.interfaces.mapper;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bomc.poc.hrm.AbstractBaseUnit;
import de.bomc.poc.hrm.domain.CustomerEntity;

/**
 * Tests the customer mapping entity to dto.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomerMapperTest extends AbstractBaseUnit {

	private static final String LOG_PREFIX = "CustomerMapperTest#";
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerMapperTest.class.getName());

	/* --------------------- member variables ----------------------- */
	private CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);

	/* --------------------- methods -------------------------------- */
	
	@Test
	public void test010_mapEntityToDto_pass() {
		LOGGER.info(LOG_PREFIX + "test010_mapEntityToDto_pass");

		final CustomerDto dto = this.customerMapper.mapEntityToDto(createCustomerEntity());
		
		assertThat(dto, notNullValue());
		assertThat(dto.getId(), nullValue());
	}

	@Test
	public void test020_mapDtoToEntity_pass() {
		LOGGER.info(LOG_PREFIX + "test020_mapDtoToEntity_pass");

		final CustomerEntity entity = this.customerMapper.mapDtoToEntity(createCustomerDto());
		
		assertThat(entity, notNullValue());
		assertThat(entity.getId(), equalTo(CUSTOMER_ID));
	}
	
	@Test
	public void test030_mapEntitiesToDtos_pass() {
		LOGGER.info(LOG_PREFIX + "test030_mapEntitiesToDtos_pass");
		
		final CustomerEntity customerEntity1 = createCustomerEntity();
		final String newCity = "Honululu";
		final CustomerEntity customerEntity2 = createCustomerEntity();
		customerEntity2.setCity(newCity);
		
		final List<CustomerEntity> customerEntityList = new ArrayList<>();
		customerEntityList.add(customerEntity1);
		customerEntityList.add(customerEntity2);
		
		final List<CustomerDto> customerDtoList = this.customerMapper.mapEntitiesToDtos(customerEntityList);
		
		assertThat(customerDtoList.size(), equalTo(2));
	}
}
