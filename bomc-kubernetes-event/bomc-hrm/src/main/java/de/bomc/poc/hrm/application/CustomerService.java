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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bomc.poc.hrm.domain.CustomerEntity;
import de.bomc.poc.hrm.infrastructure.CustomerRepository;
import de.bomc.poc.hrm.interfaces.mapper.CustomerEmailDto;

/**
 * The service for customer handling.
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

	/**
	 * Creates a new instance of <code>CustomerService</code>.
	 * 
	 * @param customerRepository the given customer repository.
	 * @param customerService    the
	 */
	@Autowired
	public CustomerService(final CustomerRepository customerRepository) {

		this.customerRepository = customerRepository;
	}

	/**
	 * Find customer by given id.
	 * 
	 * @param id the given id.
	 * @return the customer.
	 * @throws IllegalStateException if no customer is available by the given id.
	 */
	public CustomerEntity findById(final Long id) {
		LOGGER.debug(LOG_PREFIX + "findById [id=" + id + "]");

		final CustomerEntity customerEntity = this.customerRepository.findById(id).orElseThrow(
				() -> new IllegalStateException("There is no customer available by the given id [id=" + id + "]"));

		return customerEntity;
	}

	/**
	 * Find customer by given emailAddress.
	 * 
	 * @param customerEmailDto the given emailAddress.
	 * @return the customer.
	 * @throws IllegalStateException if no customer is available by the given
	 *                               emailAddress.
	 */
	public CustomerEntity findByEmailAddress(final CustomerEmailDto customerEmailDto) {
		LOGGER.debug(LOG_PREFIX + "findByEmailAddress [customerEmailDto=" + customerEmailDto + "]");

		return this.customerRepository.findByEmailAddress(customerEmailDto.getEmailAddress())
				.orElseThrow(() -> new IllegalStateException(
						"There is no customer available by the given email address [emailAddress="
								+ customerEmailDto.getEmailAddress() + "]"));
	}

	/**
	 * Create a new customer in db.
	 * 
	 * @param customerEntity the given customer to persist.
	 * @return the created customerEntity with new technical id.
	 */
	public CustomerEntity createCustomer(final CustomerEntity customerEntity) {
		LOGGER.debug(LOG_PREFIX + "createCustomer [customerEntity=" + customerEntity + "]");

		// Set metadata.
		// TODO Hibernate hook PrePersist not working.
		customerEntity.setCreateDateTime(LocalDateTime.now());
		customerEntity.setCreateUser(customerEntity.getEmailAddress());
		// Write down to db.
		final CustomerEntity retCustomerEntity = this.customerRepository.save(customerEntity);

		return retCustomerEntity;
	}

	/**
	 * Delete customer by given id.
	 * 
	 * @param id the given user id.
	 */
	public void deleteCustomerById(final Long id) {
		LOGGER.debug(LOG_PREFIX + "deleteCustomerById [id=" + id + "]");

		this.customerRepository.deleteById(id);
	}

	/**
	 * Updates the given entity. Use the returned instance for further operations as
	 * the save operation might have changed the entity instance completely.
	 * 
	 * @param customerEntity the edited entity.
	 * @return the edited entity.
	 */
	public CustomerEntity updateCustomer(final CustomerEntity customerEntity) {
		LOGGER.debug(LOG_PREFIX + "updateCustomer [customerEntity=" + customerEntity + "]");

		final Optional<CustomerEntity> optionalCustomerEntity = this.customerRepository
				.findById(customerEntity.getId());

		if (optionalCustomerEntity.isPresent()) {
			// Update entity.
			final CustomerEntity updatableCustomerEntity = optionalCustomerEntity.get();

			// Map new values.
			this.updateEntity(customerEntity, updatableCustomerEntity);

			// Save to db.
			final CustomerEntity mergedCustomerEntity = this.customerRepository.save(updatableCustomerEntity);

			return mergedCustomerEntity;
		}

		return customerEntity;
	}

	/**
	 * Return a list with all customers.
	 * 
	 * @return a list with all customers.
	 */
	public List<CustomerEntity> findAll() {
		LOGGER.debug(LOG_PREFIX + "findAll");

		final Iterable<CustomerEntity> iterableCustomerEntity = this.customerRepository.findAll();

		return this.toList(iterableCustomerEntity);
	}

	// _______________________________________________
	// Helper methods
	// -----------------------------------------------

	private void updateEntity(final CustomerEntity customerDto, final CustomerEntity customerEntity) {

		customerEntity.setFirstName(customerDto.getFirstName());
		customerEntity.setLastName(customerDto.getLastName());
		customerEntity.setCountry(customerDto.getCountry());
		customerEntity.setEmailAddress(customerDto.getEmailAddress());
		customerEntity.setPhoneNumber(customerDto.getPhoneNumber());
		customerEntity.setCity(customerDto.getCity());
		customerEntity.setStreet(customerDto.getStreet());
		customerEntity.setPostalCode(customerDto.getPostalCode());
		customerEntity.setHouseNumber(customerDto.getHouseNumber());
		customerEntity.setDateOfBirth(customerDto.getDateOfBirth());
		customerEntity.setId(customerDto.getId());
	}

	private <T> List<T> toList(final Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
	}

}
