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
package de.bomc.poc.hrm.domain.model;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger LOGGER = LoggerFactory.getLogger(Grant.class.getName());
	
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

}
