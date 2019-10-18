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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import brave.Tracer;
import de.bomc.poc.hrm.AbstractBaseUnit;
import de.bomc.poc.hrm.application.CustomerService;
import de.bomc.poc.hrm.interfaces.mapper.CustomerDto;
import de.bomc.poc.hrm.interfaces.mapper.CustomerEmailDto;

/**
 * <pre>
 * https://reflectoring.io/spring-boot-mock/
 * 
 * Tests the customer controller. There are times when we have to rely on Spring
 * Boot to set up an application context for us because it would be too much
 * work to instantiate the whole network of classes manually. 
 * We may not want to test the integration between all the beans in a certain 
 * test, however, so we need a way to replace certain beans within Spring’s 
 * application context with a mock. Spring Boot provides the -at MockBean and -at 
 * SpyBean annotations for this purpose. 
 * A prime example for using mocks is using Spring Boot’s -at WebMvcTest to 
 * create an application context that contains all the beans necessary for 
 * testing a Spring web controller.
 * 
 * NOTE:
 * -at WebMvcTest auto-configures the Spring MVC infrastructure and limits 
 * scanned beans to -at Controller, -at ControllerAdvice, -at JsonComponent, 
 * Converter, GenericConverter, Filter, WebMvcConfigurer, and 
 * HandlerMethodArgumentResolver. 
 * ___________________________________________________
 * Regular -at Component beans are not scanned when using this annotation.
 * 
 * So in this case '{@link TraceHeaderFilter.class}' and the dependent 
 * {@link brave.Tracer} bean has to be register manually.
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@WebMvcTest(CustomerController.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("local")
@ComponentScan( value = {"de.bomc.poc"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomerControllerTest extends AbstractBaseUnit {

	private static final String LOG_PREFIX = "CustomerControllerTest#";
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerControllerTest.class.getName());

	/* --------------------- constants ------------------------------ */

	/* --------------------- member variables ----------------------- */
	@Autowired
	private MockMvc mvc;
	@MockBean
	private CustomerService customerService;
	@MockBean
	private Tracer tracer;
	
	/* --------------------- methods -------------------------------- */

	@Test
	public void test010_createCustomer_pass() throws Exception {
		LOGGER.info(LOG_PREFIX + "test010_createCustomer_pass");

		// GIVEN
		final CustomerDto createdCustomerDto = createCustomerDto();
		createdCustomerDto.setId(CUSTOMER_ID);

		// WHEN
		when(this.customerService.createCustomer(createCustomerEntity())).thenReturn(createdCustomerDto);

		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		final String requestJson = objectMapper.writeValueAsString(createCustomerDto());

		// THEN
		this.mvc.perform(post("/customer").accept(CustomerController.MEDIA_TYPE_JSON_V1)
				.contentType(CustomerController.MEDIA_TYPE_JSON_V1).content(requestJson)).andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	public void test020_getCustomerByEmailAddress_pass() throws Exception {
		LOGGER.info(LOG_PREFIX + "test020_getCustomerByEmailAddress_pass");

		// GIVEN
		final CustomerEmailDto customerEmailDto = new CustomerEmailDto();
		customerEmailDto.setEmailAddress(CUSTOMER_E_MAIL);

		// WHEN 
		when(this.customerService.findByEmailAddress(customerEmailDto)).thenReturn(createCustomerDto());

		final ObjectMapper objectMapper = new ObjectMapper();
		final String requestJson = objectMapper.writeValueAsString(customerEmailDto);

		// THEN
		this.mvc.perform(post("/customer/email-address", CUSTOMER_E_MAIL).accept(CustomerController.MEDIA_TYPE_JSON_V1)
				.contentType(CustomerController.MEDIA_TYPE_JSON_V1).content(requestJson)).andDo(print())
				.andExpect(status().isOk()).andExpect(content().contentType(CustomerController.MEDIA_TYPE_JSON_V1))
				.andExpect(jsonPath("$.emailAddress").value(CUSTOMER_E_MAIL));
	}

	@Test
	public void test030_getCustomerById_pass() throws Exception {
		LOGGER.info(LOG_PREFIX + "test030_getCustomerById_pass");

		// GIVEN
		when(this.customerService.findById(CUSTOMER_ID)).thenReturn(createCustomerDto());

		// THEN
		this.mvc.perform(get("/customer/{id}", Long.toString(CUSTOMER_ID)).accept(CustomerController.MEDIA_TYPE_JSON_V1)
				.contentType(CustomerController.MEDIA_TYPE_JSON_V1)).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(CustomerController.MEDIA_TYPE_JSON_V1))
				.andExpect(jsonPath("$.emailAddress").value(CUSTOMER_E_MAIL));
	}

	@Test
	public void test040_findAll_pass() throws Exception {
		LOGGER.info(LOG_PREFIX + "test040_findAll_pass");

		// GIVEN
		final List<CustomerDto> customerDtoList = new ArrayList<>();

		final CustomerDto customerDto1 = createCustomerDto();
		customerDto1.setId(1L);
		final CustomerDto customerDto2 = createCustomerDto();
		customerDto2.setId(2L);

		customerDtoList.add(customerDto1);
		customerDtoList.add(customerDto2);

		when(this.customerService.findAll()).thenReturn(customerDtoList);

		// THEN
		this.mvc.perform(get("/customer")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(CustomerController.MEDIA_TYPE_JSON_V1))
				.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$.[0].emailAddress").value(equalTo(CUSTOMER_E_MAIL)));
	}

	@Test
	public void test050_updateCustomer_pass() throws Exception {
		LOGGER.info(LOG_PREFIX + "test040_findAll_pass");

		// GIVEN
		final CustomerDto customerDto = createCustomerDto();
		final CustomerDto updatedCustomerDto = createCustomerDto();
		updatedCustomerDto.setCity("Honululu");

		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		final String requestJson = objectMapper.writeValueAsString(customerDto);

		when(this.customerService.updateCustomer(customerDto)).thenReturn(updatedCustomerDto);

		// THENs
		this.mvc.perform(put("/customer").accept(CustomerController.MEDIA_TYPE_JSON_V1)
				.contentType(CustomerController.MEDIA_TYPE_JSON_V1).content(requestJson)).andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	public void test060_deleteCustomer_pass() throws Exception {
		LOGGER.info(LOG_PREFIX + "test060_deleteCustomer_pass");

		// GIVEN
		doNothing().when(this.customerService).deleteCustomerById(CUSTOMER_ID);

		// THEN
		this.mvc.perform(delete("/customer/{id}", Long.toString(CUSTOMER_ID))
				.accept(CustomerController.MEDIA_TYPE_JSON_V1).contentType(CustomerController.MEDIA_TYPE_JSON_V1))
				.andDo(print()).andExpect(status().isOk());
	}

}
