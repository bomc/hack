/**
 * Project: bomc-invoice
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
package de.bomc.poc.invoice.domain.model.core;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import de.bomc.poc.invoice.domain.model.basis.AbstractEntity;

/**
 * This entity represents a customer in the shop context.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Entity
@Table(name = "T_CUSTOMER")
public class CustomerEntity extends AbstractEntity<CustomerEntity> implements Serializable {

	/**
	 * The serial UID
	 */
	private static final long serialVersionUID = 5945895602909245641L;
	/**
	 * The logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(CustomerEntity.class.getName());
	/**
	 * A log prefix.
	 */
	private static final String LOG_PREFIX = "CustomerEntity#";

	/* --------------------- constants ------------------------------ */
	/**
	 * The default prefix String for each created
	 * <code>CustomerEntity</code>-NamedQuery.
	 */
	protected static final String NQ_PREFIX = "CUSTOMER.";

	/* --------------------- columns -------------------------------- */
	@Column(name = "C_NAME", nullable = false)
	private String name;
	@Column(name = "C_FIRSTNAME", nullable = false)
	private String firstname;

	/* --------------------- associations --------------------------- */

	/* --------------------- constructors --------------------------- */
	public CustomerEntity() {
		LOGGER.log(Level.INFO, LOG_PREFIX + "co");

		// Used by Jpa-Provider.
	}

	/* ----------------------------- methods ------------------------- */
	/**
	 * @return the type of this entity.
	 */
	@Override
	protected Class<CustomerEntity> getEntityClass() {
		return CustomerEntity.class;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(final String firstname) {
		this.firstname = firstname;
	}

	@Override
	public String toString() {
		return "CustomerEntity [id=" + super.getId() + ", name=" + name + ", firstname=" + firstname + "]";
	}
}
