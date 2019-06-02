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

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.bomc.poc.hrm.application.CustomerService;
import de.bomc.poc.hrm.domain.CustomerEntity;
import de.bomc.poc.hrm.interfaces.mapper.CustomerDto;
import de.bomc.poc.hrm.interfaces.mapper.CustomerEmailDto;
import de.bomc.poc.hrm.interfaces.mapper.CustomerMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The controller for customer handling.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@RestController
@RequestMapping(value = "/customer")
@CrossOrigin(origins = "*") // TODO: security issue
@Api(value = "Customer Management System", description = "Operations pertaining to customer in Customer Management System")
public class CustomerController {

	private static final String LOG_PREFIX = "CustomerController#";
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class.getName());

	private final CustomerService customerService;
	private final CustomerMapper customerMapper;

	/**
	 * Creates a new instance of <code>CustomerController</code>.
	 * 
	 * @param customerService the given customer service.
	 * @param customerMapper  the given customer mapper.
	 */
	@Autowired
	public CustomerController(final CustomerService customerService, final CustomerMapper customerMapper) {
		this.customerService = customerService;
		this.customerMapper = customerMapper;
	}

	
	@ApiOperation(value = "Get customer by technical id.", response = CustomerDto.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved a customer dto by the given id."),
			@ApiResponse(code = 401, message = "Not authorized to view the resource."),
			@ApiResponse(code = 403, message = "Accessing the resource that trying to reach is forbidden."),
			@ApiResponse(code = 404, message = "The resource that trying to reach is not found.")
	})
	@GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
	public CustomerDto getCustomerById(@ApiParam(value = "Given technical id to get customer.", required = true) @PathVariable final Long id) {
		LOGGER.debug(LOG_PREFIX + "getCustomerById [id=" + id + "]");

		return this.customerMapper.mapEntityToDto(customerService.findById(id));
	}

	
	@ApiOperation(value = "Get customer by email address.", response = CustomerDto.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved a customer dto by the given email address."),
			@ApiResponse(code = 401, message = "Not authorized to view the resource."),
			@ApiResponse(code = 403, message = "Accessing the resource that trying to reach is forbidden."),
			@ApiResponse(code = 404, message = "The resource that trying to reach is not found.")
	})
	@PostMapping(value = "/email-address", produces = "application/json;charset=UTF-8", consumes = "application/json;charset=UTF-8")
	public CustomerDto getCustomerByEmailAddress(@ApiParam(value = "The customer email address.", required = true) @Valid @RequestBody final CustomerEmailDto customerEmailDto) {
		LOGGER.debug(LOG_PREFIX + "getCustomerByEmailAddress [customerEmailDto=" + customerEmailDto + "]");

		try {
			// Return the customer by the given email.
			return this.customerMapper.mapEntityToDto(customerService.findByEmailAddress(customerEmailDto));
		} catch(final IllegalStateException illegalStateException) {
			return null;
		}
	}

	
	@ApiOperation(value = "Creates a customer.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully create customer in db."),
			@ApiResponse(code = 401, message = "Not authorized to view the resource."),
			@ApiResponse(code = 403, message = "Accessing the resource that trying to reach is forbidden."),
			@ApiResponse(code = 404, message = "The resource that trying to reach is not found.")
	})
	@PostMapping(produces = "application/json;charset=UTF-8", consumes = "application/json;charset=UTF-8")
	public CustomerEntity createCustomer(@ApiParam(value = "The customer data.", required = true) @Valid @RequestBody final CustomerDto customerDto) {
		LOGGER.debug(LOG_PREFIX + "createCustomer [customerDto=" + customerDto + "]");

		return this.customerService.createCustomer(customerMapper.mapDtoToEntity(customerDto)); 
	}

	@ApiOperation(value = "Delete a customer by given id.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully delete a customer by given id in db."),
			@ApiResponse(code = 401, message = "Not authorized to view the resource."),
			@ApiResponse(code = 403, message = "Accessing the resource that trying to reach is forbidden."),
			@ApiResponse(code = 404, message = "The resource that trying to reach is not found.")
	})
	@DeleteMapping(value = "/{id}")
	public void deleteCustomer(@ApiParam(value = "The customer id.", required = true) @PathVariable final Long id) {
		LOGGER.debug(LOG_PREFIX + "deleteCustomer [id=" + id + "]");

		this.customerService.deleteCustomerById(id);
	}

	@ApiOperation(value = "Update a customer.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully update a customer in db."),
			@ApiResponse(code = 401, message = "Not authorized to view the resource."),
			@ApiResponse(code = 403, message = "Accessing the resource that trying to reach is forbidden."),
			@ApiResponse(code = 404, message = "The resource that trying to reach is not found.")
	})
	@PutMapping(produces = "application/json;charset=UTF-8", consumes = "application/json;charset=UTF-8")
	public void updateCustomer(@ApiParam(value = "The customer data.", required = true) @Valid @RequestBody final CustomerDto customerDto) {
		LOGGER.debug(LOG_PREFIX + "updateCustomer [customerDto=" + customerDto + "]");

		final CustomerEntity customerEntity = this.customerMapper.mapDtoToEntity(customerDto);
		
		this.customerService.updateCustomer(customerEntity);
	}

	@ApiOperation(value = "Find all customer.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully reads a list of customers from db."),
			@ApiResponse(code = 401, message = "Not authorized to view the resource."),
			@ApiResponse(code = 403, message = "Accessing the resource that trying to reach is forbidden."),
			@ApiResponse(code = 404, message = "The resource that trying to reach is not found.")
	})
	@GetMapping(produces = "application/json;charset=UTF-8")
	public List<CustomerDto> findAll() {
		LOGGER.debug(LOG_PREFIX + "findAll");

		return this.customerMapper.mapEntitiesToDtos(this.customerService.findAll());
	}
	
	// _______________________________________________
	// Helper methods
	// -----------------------------------------------
	
	/**
	 * A fall-back handler – a catch-all type of logic that deals with all other
	 * exceptions that don’t have specific handlers
	 * 
	 * @param exception the given exception.
	 * @return a ApiErrorResponseObject.
	 */
	@ExceptionHandler({ Exception.class })
	public ApiErrorResponseObject handleException(final MethodArgumentNotValidException exception) {
		LOGGER.debug(LOG_PREFIX + "handleException [exception=" + exception + "]");

		final String errorMsg = exception.getBindingResult().getFieldErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage).findFirst().orElse(exception.getMessage());

		return ApiErrorResponseObject.builder().shortErrorCodeDescription(errorMsg)
				.errorCode(HttpStatus.INTERNAL_SERVER_ERROR.name()).uuid(UUID.randomUUID().toString()).build();
	}
}
