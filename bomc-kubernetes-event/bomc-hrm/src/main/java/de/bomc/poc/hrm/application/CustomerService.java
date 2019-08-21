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
package de.bomc.poc.hrm.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;

import de.bomc.poc.hrm.application.log.method.Loggable;
import de.bomc.poc.hrm.domain.model.CustomerEntity;
import de.bomc.poc.hrm.infrastructure.CustomerRepository;
import de.bomc.poc.hrm.interfaces.mapper.CustomerDto;
import de.bomc.poc.hrm.interfaces.mapper.CustomerEmailDto;
import de.bomc.poc.hrm.interfaces.mapper.CustomerMapper;

/**
 * The service handles the business logic for customers.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 07.05.2019
 */
@Service
public class CustomerService {

	private static final String LOG_PREFIX = "CustomerService#";
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class.getName());

	/* --------------------- member variables ----------------------- */
	private final CustomerRepository customerRepository;
	private final CustomerMapper customerMapper;
	
	/**
	 * Creates a new instance of <code>CustomerService</code>.
	 * 
	 * @param customerRepository the given customer repository.
	 * @param customerService    the
	 */
	@Autowired
	public CustomerService(final CustomerRepository customerRepository, final CustomerMapper customerMapper) {

		this.customerRepository = customerRepository;
		this.customerMapper = customerMapper;
	}

	/**
	 * Find customer by given id.
	 * 
	 * @param id the given id.
	 * @return the customer.
	 * @throws IllegalStateException if no customer is available by the given id.
	 */
	@Loggable(result = true, params = true, value = LogLevel.DEBUG)
	public CustomerDto findById(final Long id) {

		final CustomerEntity customerEntity = this.customerRepository.findById(id).orElseThrow(
				() -> new IllegalStateException("There is no customer available by the given id [id=" + id + "]"));

		return this.customerMapper.mapEntityToDto(customerEntity);
	}

	/**
	 * Find customer by given emailAddress.
	 * 
	 * @param customerEmailDto the given emailAddress.
	 * @return the customer.
	 * @throws IllegalStateException if no customer is available by the given
	 *                               emailAddress.
	 */
	@Loggable(result = true, params = true, value = LogLevel.DEBUG)
	public CustomerDto findByEmailAddress(final CustomerEmailDto customerEmailDto) {

		final CustomerEntity customerEntity =  this.customerRepository.findByEmailAddress(customerEmailDto.getEmailAddress())
				.orElseThrow(() -> new IllegalStateException(
						"There is no customer available by the given email address [emailAddress="
								+ customerEmailDto.getEmailAddress() + "]"));
		
		return this.customerMapper.mapEntityToDto(customerEntity);
	}

	/**
	 * Create a new customer in db.
	 * 
	 * @param customerEntity the given customer to persist.
	 * @return the created customerEntity with new technical id.
	 */
	@Loggable(result = true, params = true, value = LogLevel.DEBUG)
	public CustomerDto createCustomer(final CustomerEntity customerEntity) {
		// Set metadata.
		// TODO Hibernate hook PrePersist not working.
		customerEntity.setCreateDateTime(LocalDateTime.now());
		customerEntity.setCreateUser(customerEntity.getEmailAddress());
		// Write down to db.
		final CustomerEntity retCustomerEntity = this.customerRepository.save(customerEntity);
		final CustomerDto customerDto = this.customerMapper.mapEntityToDto(retCustomerEntity);
		
		return customerDto;
	}

	/**
	 * Delete customer by given id.
	 * 
	 * @param id the given user id.
	 */
	@Loggable(result = true, params = true, value = LogLevel.DEBUG)
	public void deleteCustomerById(final Long id) {

		this.customerRepository.deleteById(id);
	}

	/**
	 * Updates the given entity. Use the returned instance for further operations as
	 * the save operation might have changed the entity instance completely.
	 * 
	 * @param customerDto the edited entity.
	 * @return the edited dto.
	 */
	@Loggable(result = true, params = true, value = LogLevel.DEBUG)
	public CustomerDto updateCustomer(final CustomerDto customerDto) {

		final CustomerEntity customerEntity = this.customerMapper.mapDtoToEntity(customerDto);
	
		final Optional<CustomerEntity> optionalCustomerEntity = this.customerRepository
				.findById(customerEntity.getId());

		if (optionalCustomerEntity.isPresent()) {
			LOGGER.debug(LOG_PREFIX + "updateCustomer - will be updated. [customerEntity=" + customerEntity + "]");
			//
			// Update entity.
			final CustomerEntity updatableCustomerEntity = optionalCustomerEntity.get();

			// Map new values.
			this.updateEntity(customerEntity, updatableCustomerEntity);

			// Save to db.
			final CustomerEntity updatedCustomerEntity = this.customerRepository.save(updatableCustomerEntity);

			return this.customerMapper.mapEntityToDto(updatedCustomerEntity);
		} else {
			LOGGER.debug(LOG_PREFIX + "updateCustomer - is new. [customerEntity=" + customerEntity + "]");
			//
			// Write down to db.
			final CustomerEntity newCustomerEntity = this.customerRepository.save(customerEntity);
			
			return this.customerMapper.mapEntityToDto(newCustomerEntity);
		}
	}

	/**
	 * Return a list with all customers.
	 * 
	 * @return a list with all customers.
	 */
	@Loggable(result = true, params = true, value = LogLevel.DEBUG)
	public List<CustomerDto> findAll() {

		final Iterable<CustomerEntity> iterableCustomerEntity = this.customerRepository.findAll();

		return this.customerMapper.mapEntitiesToDtos(this.toList(iterableCustomerEntity));
	}

	// _______________________________________________
	// Helper methods
	// -----------------------------------------------

	private void updateEntity(final CustomerEntity customerEntity, final CustomerEntity updatableCustomerEntity) {

		updatableCustomerEntity.setFirstName(customerEntity.getFirstName());
		updatableCustomerEntity.setLastName(customerEntity.getLastName());
		updatableCustomerEntity.setCountry(customerEntity.getCountry());
		updatableCustomerEntity.setEmailAddress(customerEntity.getEmailAddress());
		updatableCustomerEntity.setPhoneNumber(customerEntity.getPhoneNumber());
		updatableCustomerEntity.setCity(customerEntity.getCity());
		updatableCustomerEntity.setStreet(customerEntity.getStreet());
		updatableCustomerEntity.setPostalCode(customerEntity.getPostalCode());
		updatableCustomerEntity.setHouseNumber(customerEntity.getHouseNumber());
		updatableCustomerEntity.setDateOfBirth(customerEntity.getDateOfBirth());
		updatableCustomerEntity.setId(customerEntity.getId());
	}

	private <T> List<T> toList(final Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
	}

}
