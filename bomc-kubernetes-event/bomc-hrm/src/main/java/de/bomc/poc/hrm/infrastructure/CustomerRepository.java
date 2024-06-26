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
package de.bomc.poc.hrm.infrastructure;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.bomc.poc.hrm.domain.model.CustomerEntity;

/**
 * The repository for customer handling.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@Repository
public interface CustomerRepository extends CrudRepository<CustomerEntity, Long> {

	Optional<CustomerEntity> findByEmailAddress(String emailAddress);
}
