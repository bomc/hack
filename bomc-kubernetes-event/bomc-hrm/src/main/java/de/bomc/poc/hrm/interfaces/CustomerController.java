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
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import de.bomc.poc.hrm.application.CustomerService;
import de.bomc.poc.hrm.application.log.method.Loggable;
import de.bomc.poc.hrm.interfaces.mapper.CustomerDto;
import de.bomc.poc.hrm.interfaces.mapper.CustomerEmailDto;
import de.bomc.poc.hrm.interfaces.mapper.CustomerMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
@Api(tags = "Customer queries", value = "Customer Management System", description = "Operations pertaining to customer in Customer Management System", produces = "application/json;charset=UTF-8")
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
	// @Autowired: is here not necessary, is done by IOC container.
	public CustomerController(final CustomerService customerService, final CustomerMapper customerMapper) {
		this.customerService = customerService;
		this.customerMapper = customerMapper;
	}

	@ApiOperation(value = "Get customer by technical id.", response = CustomerDto.class, notes = "The 'id' is the technical id.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved a customer dto by the given id."),
			@ApiResponse(code = 401, message = "Not authorized to view the resource."),
			@ApiResponse(code = 403, message = "Accessing the resource that trying to reach is forbidden."),
			@ApiResponse(code = 404, message = "The resource that trying to reach is not found.") })
	@ApiImplicitParams(@ApiImplicitParam(name = "id", value = "The technical id.", dataType = "Long", dataTypeClass = java.lang.Long.class, required = true))
	@GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.OK)
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
	public ResponseEntity<CustomerDto> getCustomerById(@PathVariable final Long id) {

		return new ResponseEntity<CustomerDto>(customerService.findById(id), HttpStatus.OK);
	}

	@ApiOperation(value = "Get customer by email address.", response = CustomerDto.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved a customer dto by the given email address."),
			@ApiResponse(code = 401, message = "Not authorized to view the resource."),
			@ApiResponse(code = 403, message = "Accessing the resource that trying to reach is forbidden."),
			@ApiResponse(code = 404, message = "The resource that trying to reach is not found.") })
	@ApiImplicitParams(@ApiImplicitParam(name = "customerEmailDto", value = "The identifier to search the customer.", dataType = "CustomerEmailDto", dataTypeClass = de.bomc.poc.hrm.interfaces.mapper.CustomerEmailDto.class, required = true))
	@PostMapping(value = "/email-address", produces = "application/json;charset=UTF-8", consumes = "application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.OK)
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
	public ResponseEntity<CustomerDto> getCustomerByEmailAddress(
			@Valid @RequestBody final CustomerEmailDto customerEmailDto) {

		try {
			// Return the customer by the given email.
			return new ResponseEntity<CustomerDto>(customerService.findByEmailAddress(customerEmailDto), HttpStatus.OK);
		} catch (final IllegalStateException illegalStateException) {
			return null;
		}
	}

	@ApiOperation(value = "Creates a customer.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully create customer in db."),
			@ApiResponse(code = 401, message = "Not authorized to view the resource."),
			@ApiResponse(code = 403, message = "Accessing the resource that trying to reach is forbidden."),
			@ApiResponse(code = 404, message = "The resource that trying to reach is not found.") })
	@ApiImplicitParams(@ApiImplicitParam(name = "customerDto", value = "The updated customer to persist.", dataType = "CustomerDto", dataTypeClass = de.bomc.poc.hrm.interfaces.mapper.CustomerDto.class, required = true))
	@PostMapping(produces = "application/json;charset=UTF-8", consumes = "application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.OK)
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
	public ResponseEntity<CustomerDto> createCustomer(@Valid @RequestBody final CustomerDto customerDto) {

		return new ResponseEntity<CustomerDto>(
				this.customerService.createCustomer(customerMapper.mapDtoToEntity(customerDto)), HttpStatus.OK);
	}

	@ApiOperation(value = "Delete a customer by given id.", notes = "The technical id is expected.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully delete a customer by given id in db."),
			@ApiResponse(code = 401, message = "Not authorized to view the resource."),
			@ApiResponse(code = 403, message = "Accessing the resource that trying to reach is forbidden."),
			@ApiResponse(code = 404, message = "The resource that trying to reach is not found.") })
	@ApiImplicitParams(@ApiImplicitParam(name = "id", value = "The identifier for deleting a customer.", dataType = "Long", dataTypeClass = java.lang.Long.class, required = true))
	@DeleteMapping(value = "/{id}")
	@Loggable(result = false, params = true, value = LogLevel.DEBUG, time = true)
	public void deleteCustomer(@ApiParam(value = "The customer id.", required = true) @PathVariable final Long id) {
		LOGGER.debug(LOG_PREFIX + "deleteCustomer [id=" + id + "]");

		this.customerService.deleteCustomerById(id);
	}

	@ApiOperation(value = "Update a customer.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully update a customer in db."),
			@ApiResponse(code = 401, message = "Not authorized to view the resource."),
			@ApiResponse(code = 403, message = "Accessing the resource that trying to reach is forbidden."),
			@ApiResponse(code = 404, message = "The resource that trying to reach is not found.") })
	@ApiImplicitParams(@ApiImplicitParam(name = "customerDto", value = "The updated customer to persist.", dataType = "CustomerDto", dataTypeClass = de.bomc.poc.hrm.interfaces.mapper.CustomerDto.class, required = true))
	@PutMapping(produces = "application/json;charset=UTF-8", consumes = "application/json;charset=UTF-8")
	@Loggable(result = false, params = true, value = LogLevel.DEBUG, time = true)
	public void updateCustomer(@Valid @RequestBody final CustomerDto customerDto) {

		this.customerService.updateCustomer(customerDto);
	}

	@ApiOperation(value = "Find all customer.", responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully reads a list of customers from db."),
			@ApiResponse(code = 401, message = "Not authorized to view the resource."),
			@ApiResponse(code = 403, message = "Accessing the resource that trying to reach is forbidden."),
			@ApiResponse(code = 404, message = "The resource that trying to reach is not found.") })
	@GetMapping(produces = "application/json;charset=UTF-8")
	@ResponseStatus(HttpStatus.OK)
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
	public ResponseEntity<List<CustomerDto>> findAll() {

		return new ResponseEntity<List<CustomerDto>>(this.customerService.findAll(), HttpStatus.OK);
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
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
	public ApiErrorResponseObject handleException(final MethodArgumentNotValidException exception) {

		final String errorMsg = exception.getBindingResult().getFieldErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage).findFirst().orElse(exception.getMessage());

		return ApiErrorResponseObject.builder().shortErrorCodeDescription(errorMsg)
				.errorCode(HttpStatus.INTERNAL_SERVER_ERROR.name()).uuid(UUID.randomUUID().toString()).build();
	}
}
