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

import java.util.List;

import de.bomc.poc.hrm.domain.model.CustomerEntity;
import de.bomc.poc.hrm.interfaces.mapper.CustomerDto;
import de.bomc.poc.hrm.interfaces.mapper.CustomerEmailDto;

/**
 * The service handles the business logic for customers.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 07.05.2019
 */
public interface CustomerService {

	/**
	 * Find customer by given id.
	 * 
	 * @param id the given id.
	 * @return the customer.
	 * @throws IllegalStateException if no customer is available by the given id.
	 */
	CustomerDto findById(Long id);
	
	/**
	 * Find customer by given emailAddress.
	 * 
	 * @param customerEmailDto the given emailAddress.
	 * @return the customer.
	 * @throws IllegalStateException if no customer is available by the given
	 *                               emailAddress.
	 */
	CustomerDto findByEmailAddress(CustomerEmailDto customerEmailDto);
	
	/**
	 * Create a new customer in db.
	 * 
	 * @param customerEntity the given customer to persist.
	 * @return the created customerEntity with new technical id.
	 */
	CustomerDto createCustomer(CustomerEntity customerEntity);
	
	/**
	 * Delete customer by given id.
	 * 
	 * @param id the given user id.
	 */
	void deleteCustomerById(Long id);

	/**
	 * Updates the given entity. Use the returned instance for further operations as
	 * the save operation might have changed the entity instance completely.
	 * 
	 * @param customerDto the edited entity.
	 * @return the edited dto.
	 */
	CustomerDto updateCustomer(CustomerDto customerDto);
	
	/**
	 * Return a list with all customers.
	 * 
	 * @return a list with all customers.
	 */
	List<CustomerDto> findAll();

}
