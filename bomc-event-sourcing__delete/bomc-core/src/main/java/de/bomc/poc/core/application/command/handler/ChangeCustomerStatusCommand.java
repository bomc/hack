package de.bomc.poc.core.application.command.handler;

import java.io.Serializable;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.bomc.poc.core.domain.customer.CustomerStatusEnum;
import de.bomc.poc.logging.qualifier.LoggerQualifier;

/**
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 16.08.2018
 */
public class ChangeCustomerStatusCommand implements Serializable {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 1332962136108097586L;

	private static final String LOG_PREFIX = "ChangeCustomerStatusCommand#";

	@Inject
	@LoggerQualifier(logPrefix = LOG_PREFIX)
	private Logger logger;

	private AggregateId customerId;
	private CustomerStatusEnum customerStatusEnum;

	/**
	 * Creates a new instance of <code>ChangeCustomerStatusCommand</code>.
	 * 
	 * @param customerId
	 *            the given id.
	 * @param customerStatusEnum
	 *            the given customerStatusEnum to set.
	 */
	public ChangeCustomerStatusCommand(final AggregateId customerId, final CustomerStatusEnum customerStatusEnum) {
		this.logger.debug("co [customerId=" + customerId + ", v=" + customerStatusEnum + "]");

		this.customerId = customerId;
		this.customerStatusEnum = customerStatusEnum;
	}

	public AggregateId getCustomerId() {
		this.logger.debug("getCustomerId [customerId=" + this.customerId + "]");

		return this.customerId;
	}

	public CustomerStatusEnum getCustomerStatusEnum() {
		this.logger.debug("getCustomerStatusEnum [" + this.customerStatusEnum + "]");

		return this.customerStatusEnum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ChangeCustomerStatusCommand [customerStatusEnum=" + customerStatusEnum.name() + "]";
	}

}