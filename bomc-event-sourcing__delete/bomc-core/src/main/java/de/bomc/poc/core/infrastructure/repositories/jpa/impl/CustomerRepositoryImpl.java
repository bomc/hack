package de.bomc.poc.core.infrastructure.repositories.jpa.impl;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.bomc.poc.core.domain.customer.Customer;
import de.bomc.poc.core.domain.customer.CustomerRepository;
import de.bomc.poc.core.infrastructure.repositories.jpa.qualifier.RepositoryQualifier;
import de.bomc.poc.logging.qualifier.LoggerQualifier;

/**
 * <pre>
 * 	An CustomerRepositoryImpl is an extension of a {@link AbstractJpaDao} about functionality regarding {@link Customer}s.
 * <p> All methods have to be invoked within an active transaction context. </p>
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 */
@RepositoryQualifier
public class CustomerRepositoryImpl extends AbstractJpaDao<Customer> implements CustomerRepository {

	private static final String LOGGER_PREFIX = "CustomerRepositoryImpl";
	/**
	 * Logger.
	 */
	@Inject
	@LoggerQualifier(logPrefix = LOGGER_PREFIX)
	private Logger logger;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<Customer> getPersistentClass() {
		return Customer.class;
	}
}
