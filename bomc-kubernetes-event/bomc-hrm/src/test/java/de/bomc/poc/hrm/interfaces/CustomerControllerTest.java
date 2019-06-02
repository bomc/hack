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
package de.bomc.poc.hrm.interfaces;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.bomc.poc.hrm.AbstractBaseUnit;
import de.bomc.poc.hrm.application.CustomerService;
import de.bomc.poc.hrm.domain.CustomerEntity;
import de.bomc.poc.hrm.interfaces.mapper.CustomerEmailDto;

/**
 * Tests the customer controller.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@WebMvcTest(CustomerController.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan("de.bomc.poc.hrm.interfaces.mapper")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomerControllerTest extends AbstractBaseUnit {

	private static final String LOG_PREFIX = "CustomerControllerTest#";
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerControllerTest.class.getName());

	/* --------------------- constants ------------------------------ */
	private static final String MIME_TYPE_JSON_UTF8 = "application/json;charset=UTF-8";
	
	/* --------------------- member variables ----------------------- */
	@Autowired
	private MockMvc mvc;
	@MockBean
	private CustomerService customerService;

	/* --------------------- methods -------------------------------- */

	@Test
	public void test010_createCustomer_pass() throws Exception {
		LOGGER.info(LOG_PREFIX + "test010_createCustomer_pass");

		// GIVEN
		final CustomerEntity createdCustomerEntity = createCustomerEntity();
		createdCustomerEntity.setId(CUSTOMER_ID);
		
		when(this.customerService.createCustomer(createCustomerEntity())).thenReturn(createdCustomerEntity);

		final ObjectMapper objectMapper = new ObjectMapper();
		final String requestJson = objectMapper.writeValueAsString(createCustomerDto());

		// THEN
		this.mvc.perform(post("/customer")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	public void test020_getCustomerByEmailAddress_pass() throws Exception {
		LOGGER.info(LOG_PREFIX + "test020_getCustomerByEmailAddress_pass");

		// GIVEN
		final CustomerEmailDto customerEmailDto = new CustomerEmailDto();
		customerEmailDto.setEmailAddress(CUSTOMER_E_MAIL);
		
		when(this.customerService.findByEmailAddress(customerEmailDto)).thenReturn(createCustomerEntity());

		final ObjectMapper objectMapper = new ObjectMapper();
		final String requestJson = objectMapper.writeValueAsString(customerEmailDto);
		
		// THEN
		this.mvc.perform(post("/customer/email-address", CUSTOMER_E_MAIL)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(MIME_TYPE_JSON_UTF8))
				.andExpect(jsonPath("$.emailAddress").value(CUSTOMER_E_MAIL));
	}

	@Test
	public void test030_getCustomerById_pass() throws Exception {
		LOGGER.info(LOG_PREFIX + "test030_getCustomerById_pass");

		// GIVEN
		when(this.customerService.findById(CUSTOMER_ID)).thenReturn(createCustomerEntity());

		// THEN
		this.mvc.perform(get("/customer/{id}", Long.toString(CUSTOMER_ID)).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MIME_TYPE_JSON_UTF8))
				.andExpect(jsonPath("$.emailAddress").value(CUSTOMER_E_MAIL));
	}
	
	@Test
	public void test040_findAll_pass() throws Exception {
		LOGGER.info(LOG_PREFIX + "test040_findAll_pass");
		
		// GIVEN
		final List<CustomerEntity> customerEntityList = new ArrayList<>();  
		
		final CustomerEntity customerEntity1 = createCustomerEntity();
		customerEntity1.setId(1L);
		final CustomerEntity customerEntity2 = createCustomerEntity();
		customerEntity2.setId(2L);
		
		customerEntityList.add(customerEntity1);
		customerEntityList.add(customerEntity2);
		
		when(this.customerService.findAll()).thenReturn(customerEntityList);
		
		// THEN
		this.mvc.perform(get("/customer")).andDo(print()).andExpect(status().isOk())
			.andExpect(content().contentType(MIME_TYPE_JSON_UTF8))
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$.[0].emailAddress").value(equalTo("bomc1@bomc.org")));
	}
	
	@Test
	public void test050_updateCustomer_pass() throws Exception {
		LOGGER.info(LOG_PREFIX + "test040_findAll_pass");
		
		// GIVEN
		final CustomerEntity customerEntity = createCustomerEntity();
		final CustomerEntity updatedCustomerEntity = createCustomerEntity();
		updatedCustomerEntity.setCity("Honululu");
		
		final ObjectMapper objectMapper = new ObjectMapper();
		final String requestJson = objectMapper.writeValueAsString(customerEntity);
		
		when(this.customerService.updateCustomer(customerEntity)).thenReturn(updatedCustomerEntity);
		
		// THEN
		this.mvc.perform(put("/customer")
			.accept(MIME_TYPE_JSON_UTF8)
			.contentType(MIME_TYPE_JSON_UTF8)
			.content(requestJson))
		.andDo(print())
			.andExpect(status().isOk());		
	}
	
	@Test
	public void test060_deleteCustomer_pass() throws Exception {
		LOGGER.info(LOG_PREFIX + "test060_deleteCustomer_pass");
		
		// GIVEN
		doNothing().when(this.customerService).deleteCustomerById(CUSTOMER_ID);
		
		// THEN
		this.mvc.perform(delete("/customer/{id}", Long.toString(CUSTOMER_ID))
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk());
	}
}
