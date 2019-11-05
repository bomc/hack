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
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.bomc.poc.hrm.application.exception.AppErrorCodeEnum;
import de.bomc.poc.hrm.application.exception.AppRuntimeException;
import de.bomc.poc.hrm.application.log.method.Loggable;
import de.bomc.poc.hrm.domain.model.CustomerEntity;
import de.bomc.poc.hrm.infrastructure.CustomerRepository;
import de.bomc.poc.hrm.interfaces.mapper.CustomerDto;
import de.bomc.poc.hrm.interfaces.mapper.CustomerEmailDto;
import de.bomc.poc.hrm.interfaces.mapper.CustomerMapper;

/**
 * The implementation of {@link CustomerService}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 07.05.2019
 */
@Service
public class CustomerServiceImpl implements CustomerService {

	private static final String LOG_PREFIX = "CustomerService#";
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class.getName());

	/* --------------------- member variables ----------------------- */
	
	private final CustomerRepository customerRepository;
	private final CustomerMapper customerMapper;
	
	/* --------------------- constructor ---------------------------- */
	
	/**
	 * Creates a new instance of <code>CustomerService</code>.
	 * 
	 * @param customerRepository the given customer repository.
	 * @param customerService    the
	 */
	public CustomerServiceImpl(final CustomerRepository customerRepository, final CustomerMapper customerMapper) {

		this.customerRepository = customerRepository;
		this.customerMapper = customerMapper;
	}

	/* --------------------- methods -------------------------------- */

	@Transactional(readOnly = true)
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
	public CustomerDto findById(final Long id) {

		final CustomerEntity customerEntity = this.customerRepository.findById(id).orElseThrow(
				() -> new AppRuntimeException("There is no customer available with the given id [id=" + id + "]", AppErrorCodeEnum.JPA_PERSISTENCE_ENTITY_NOT_AVAILABLE_10401));

		return this.customerMapper.mapEntityToDto(customerEntity);
	}

	@Transactional(readOnly = true)
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
	public CustomerDto findByEmailAddress(final CustomerEmailDto customerEmailDto) {

		final CustomerEntity customerEntity =  this.customerRepository.findByEmailAddress(customerEmailDto.getEmailAddress())
				.orElseThrow(() -> new IllegalStateException(
						"There is no customer available by the given email address [emailAddress="
								+ customerEmailDto.getEmailAddress() + "]"));
		
		return this.customerMapper.mapEntityToDto(customerEntity);
	}

	@Transactional
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
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

	@Transactional
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
	public void deleteCustomerById(final Long id) {

		this.customerRepository.deleteById(id);
	}

	@Transactional
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
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

	@Transactional(readOnly = true)
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
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
