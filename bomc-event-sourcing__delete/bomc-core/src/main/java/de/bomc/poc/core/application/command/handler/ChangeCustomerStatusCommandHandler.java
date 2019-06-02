package de.bomc.poc.core.application.command.handler;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.bomc.poc.core.cqrs.command.handler.CommandHandler;
import de.bomc.poc.core.domain.customer.Customer;
import de.bomc.poc.core.domain.customer.CustomerRepository;
import de.bomc.poc.logging.qualifier.LoggerQualifier;

/**
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 16.08.2018
 */
public class ChangeCustomerStatusCommandHandler implements CommandHandler<ChangeCustomerStatusCommand, Void> {

	private static final String LOG_PREFIX = "ChangeCustomerStatusCommandHandler#";
	@Inject
	@LoggerQualifier(logPrefix = LOG_PREFIX)
	private Logger logger;
	@Inject
	private CustomerRepository customerRepository;

	@Override
	public Void handle(final ChangeCustomerStatusCommand command) {
		this.logger.debug("handle [command.customerId=" + command.getCustomerId() + "]");

		final Customer customer = customerRepository.findById(command.getCustomerId());
		customer.changeCustomerStatus(command.getCustomerStatusEnum());
		customerRepository.persist(customer);

		return null;
	}

}
