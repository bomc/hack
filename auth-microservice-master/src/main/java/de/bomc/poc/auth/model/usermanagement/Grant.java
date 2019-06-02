/**
 * Project: MY_POC_MICROSERVICE
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.auth.model.usermanagement;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.log4j.Logger;

/**
 * A Grant gives permission to access some kind of application object. Grants to
 * security aware application objects can be permitted or denied for a certain
 * <code>Role</code>, depending on the security configuration. Usually
 * <code>Grant</code>s are assigned to a <code>Role</code> and on or more
 * <code>User</code> s are assigned to each <code>Role</code>s. A Grant is
 * security aware, that means it is an concrete <code>SecurityObject</code>.
 * <p>
 * Permissions to UI actions are managed with <code>Grant</code>s.
 * </p>
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Entity
@DiscriminatorValue("GRANT")
public class Grant extends SecurityObject implements Serializable {

	private static final Logger LOGGER = Logger.getLogger(Grant.class);
	
	private static final long serialVersionUID = 3161772453074013724L;

	/**
	 * Create a new Grant.
	 */
	protected Grant() {
		// Used by JPA provider.
		LOGGER.debug("Grant#co");
	}

	/**
	 * Create a new Grant.
	 * 
	 * @param name
	 *            The name of the <code>Grant</code>
	 */
	public Grant(final String name) {
		super(name);
		
		LOGGER.debug("Grant#co [name=" + name + "]");
	}

	/**
	 * Create a new Grant.
	 * 
	 * @param name
	 *            The name of the <code>Grant</code>
	 * @param description
	 *            The description text of the <code>Grant</code>
	 */
	public Grant(final String name, final String description) {
		super(name, description);
		
		LOGGER.debug("Grant#co [name=" + name + ", description=" + description + "]");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Use the hashCode of the superclass with the hashCode of 'GRANT' to
	 * distinguish between <code>Grant</code>s and other
	 * <code>SecurityObject</code>s like <code>Role</code>s.
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		
		int result = super.hashCode();
		result = prime * result + "GRANT".hashCode();
		
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bomc.poc.auth.model.usermanagement.SecurityObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Grant)) {
			return false;
		}
		Grant other = (Grant) obj;
		if (this.getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!this.getName().equals(other.getName())) {
			return false;
		}
		return true;
	}
}
