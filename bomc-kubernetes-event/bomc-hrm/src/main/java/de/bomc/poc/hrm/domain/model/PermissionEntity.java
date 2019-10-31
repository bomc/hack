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

import lombok.ToString;

/**
 * A PermissionEntity gives permission to access some kind of application object. Permissions to
 * security aware application objects can be permitted or denied for a certain
 * <code>RoleEntity</code>, depending on the security configuration. Usually
 * <code>PermissionEntity</code>s are assigned to a <code>RoleEntity</code> and on or more
 * <code>UserEntity</code> s are assigned to each <code>RoleEntity</code>s. A PermissionEntity is
 * security aware, that means it is an concrete <code>SecurityObjectEntity</code>.
 * <p>
 * Permissions to UI actions are managed with <code>PermissionEntity</code>s.
 * </p>
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
// LOMBOK
@ToString(callSuper = true, includeFieldNames = true)
// JPA
@Entity
@DiscriminatorValue("permission")
public class PermissionEntity extends SecurityObjectEntity implements Serializable {

	private static final String LOG_PREFIX = "SecurityObjectEntity#"; 
	private static final Logger LOGGER = LoggerFactory.getLogger(PermissionEntity.class.getName());
	private static final long serialVersionUID = 3161772453074013724L;

	/**
	 * Create a new PermissionEntity.
	 */
	protected PermissionEntity() {
		super();
		
		// Used by JPA provider.
		LOGGER.debug(LOG_PREFIX + "co");
	}

	/**
	 * Create a new PermissionEntity.
	 * 
	 * @param name
	 *            The name of the <code>PermissionEntity</code>
	 */
	public PermissionEntity(final String name) {
		super(name);
		
		LOGGER.debug(LOG_PREFIX + "co [name=" + name + "]");
	}

	/**
	 * Create a new PermissionEntity.
	 * 
	 * @param name
	 *            The name of the <code>PermissionEntity</code>
	 * @param description
	 *            The description text of the <code>PermissionEntity</code>
	 */
	public PermissionEntity(final String name, final String description) {
		super(name, description);
		
		LOGGER.debug(LOG_PREFIX + "co [name=" + name + ", description=" + description + "]");
	}

}
