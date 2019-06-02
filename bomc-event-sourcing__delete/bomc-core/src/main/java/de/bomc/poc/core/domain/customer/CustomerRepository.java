package de.bomc.poc.core.domain.customer;

import de.bomc.poc.core.infrastructure.repositories.jpa.JpaGenericDao;

/**
 * An CustomerRepository offers functionality regarding {@link Customer} entity classes.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 15.08.2018
 */
public interface CustomerRepository extends JpaGenericDao<Customer> {

	// Define here methods for functionality from db for Customer entity.
	// ...
}
