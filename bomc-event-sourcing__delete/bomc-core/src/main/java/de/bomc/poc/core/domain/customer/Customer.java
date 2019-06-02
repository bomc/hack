package de.bomc.poc.core.domain.customer;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.apache.log4j.Logger;

import de.bomc.poc.core.domain.AbstractEntity;

/**
 * A customer entity.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.07.2018
 */
@Entity
@Table(name = "T_CUSTOMER")
public class Customer extends AbstractEntity implements Serializable {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 7221114685249393330L;
	/**
	 * The log prefix
	 */
	private static final String LOG_PREFIX = "Customer#";
	/**
	 * The logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(Customer.class);
	/**
	 * Name of <code>Customer</code> (not nullable).
	 */
	@Size(min = 3, max = 64)
	@Column(name = "C_CUSTOMER_NAME", unique = true, nullable = false)
	private String customerName;
	/**
	 * Customer status, see <code>CustomerStatus</code>.
	 */
	@Column(name = "C_CUSTOMER_STATUS")
	private Long customerStatus = CustomerStatusEnum.BRONZE.getValue();

	/**
	 * Creates a new instance of <code>Customer</code>.
	 */
	public Customer() {
		// Used by JPA provider.
	}

	/**
	 * Creates a new instance of <code>Customer</code>.
	 * 
	 * @param customerName
	 *            the name of the customer
	 */
	public Customer(final String customerName) {
		this.customerName = customerName;
	}

	/**
	 * @return the customerName
	 */
	public String getCustomerName() {
		return this.customerName;
	}

	/**
	 * @param customerName
	 *            the customerName to set
	 */
	public void setCustomerName(final String customerName) {
		this.customerName = customerName;
	}

	/**
	 * @return the customerStatus
	 */
	public CustomerStatusEnum getCustomerStatus() {
		return this.customerStatus == null ? null : CustomerStatusEnum.fromValue(this.customerStatus);
	}

	/**
	 * @param customerStatus
	 *            the customerStatus to set.
	 */
	public void setCustomerStatus(final CustomerStatusEnum customerStatus) {
		this.customerStatus = customerStatus == null ? null : customerStatus.getValue();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Customer [customerName=" + customerName + ", customerStatus=" + customerStatus + ", id=" + this.getId()
				+ ", version=" + this.getVersion() + ", isNew=" + this.isNew() + "]";
	}

	// _______________________________________________
	// Domain methods to change the behaviour of this object.
	// -----------------------------------------------

	public void changeCustomerStatus(final CustomerStatusEnum customerStatusEnum) {
		LOGGER.debug(LOG_PREFIX + "changeCustomerStatus [customerStatusEnum=" + customerStatusEnum.name() + "]");

		if (customerStatusEnum != null && customerStatusEnum.getValue().equals(this.customerStatus)) {
			LOGGER.debug(LOG_PREFIX
					+ "changeCustomerStatus - nothing todo, new customerStatus is same as stored customerStatus. [customerStatus=" + customerStatusEnum.name() + "]");
			return;
		}

		this.setCustomerStatus(customerStatusEnum);

		// Sample Case: give 10% rebate for all draft orders - communication via
		// events with different Bounded Context to achieve decoupling
		eventPublisher.publish(new CustomerStatusChangedEvent(getId(), CustomerStatusEnum.fromValue(this.customerStatus)));
	}
}
