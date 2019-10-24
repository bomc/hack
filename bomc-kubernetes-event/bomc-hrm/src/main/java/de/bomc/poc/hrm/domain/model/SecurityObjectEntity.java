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

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bomc.poc.hrm.domain.model.basis.AbstractEntity;
import de.bomc.poc.hrm.domain.model.values.CoreTypeDefinitions;
import lombok.ToString;

/**
 * A SecurityObjectEntity is the generalization of <code>RoleEntity</code>s and
 * <code>PermissionEntity</code>s and combines common used properties of both.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
// LOMBOK
@ToString
// JPA
@Entity
@Table(name = "t_security_object", schema = "public")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING, length = 20)
@NamedQueries({
		@NamedQuery(name = SecurityObjectEntity.NQ_FIND_ALL, query = "select g from SecurityObjectEntity g"),
		@NamedQuery(name = SecurityObjectEntity.NQ_FIND_BY_UNIQUE_QUERY, query = "select g from SecurityObjectEntity g where g.name = ?1")
})
public class SecurityObjectEntity extends AbstractEntity<SecurityObjectEntity> implements Serializable {

	private static final String LOG_PREFIX = "SecurityObjectEntity#"; 
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityObjectEntity.class.getName());
	private static final long serialVersionUID = 7585736035228078754L;
	/**
	 * Unique name of the <code>SecurityObjectEntity</code>.
	 * 
	 * A name of a SecurityObjectEntity must not be null.
	 */
	@NotNull
	@Column(name = "c_name", unique = true)
	@OrderBy
	private String name;
	/**
	 * Description of the <code>SecurityObjectEntity</code>.
	 */
	@Column(name = "c_description", length = CoreTypeDefinitions.DESCRIPTION_LENGTH)
	private String description;
	/**
	 * Query to find all {@link SecurityObjectEntity}s. Name is {@value} .
	 */
	public static final String NQ_FIND_ALL = "SecurityObjectEntity.findAll";
	/**
	 * Query to find <strong>one</strong> {@link SecurityObjectEntity} by its natural
	 * key.
	 * <li>Query parameter index <strong>1</strong> : The name of the
	 * <code>SecurityObjectEntity</code> to search for.</li><br />
	 * Name is {@value} .
	 */
	public static final String NQ_FIND_BY_UNIQUE_QUERY = "SecurityObjectEntity.findByName";

	/* ---------------------- constructors ------------------- */
	/**
	 * Accessed by persistence provider.
	 */
	protected SecurityObjectEntity() {
		// Used by JPA provider.

		LOGGER.debug(LOG_PREFIX + "co");
	}

	/**
	 * Create a new <code>SecurityObjectEntity</code> with a name.
	 * 
	 * @param name
	 *            The name of the <code>SecurityObjectEntity</code>
	 * @throws IllegalArgumentException
	 *             when name is <code>null</code> or an empty String
	 */
	public SecurityObjectEntity(String name) {
		this.name = name;

		LOGGER.debug(LOG_PREFIX + "co [name=" + name + "]");
	}

	/**
	 * Create a new <code>SecurityObjectEntity</code> with name and description.
	 * 
	 * @param name
	 *            The name of the <code>SecurityObjectEntity</code>
	 * @param description
	 *            The description text of the <code>SecurityObjectEntity</code>
	 * @throws IllegalArgumentException
	 *             when name is <code>null</code> or an empty String. A name of
	 *             a SecurityObjectEntity must not be null.
	 */
	public SecurityObjectEntity(@NotNull final String name, String description) {
		LOGGER.debug(LOG_PREFIX + "co [name=" + name + ", description=" + description + "]");

		this.name = name;
		this.description = description;
	}

	/* ----------------------------- methods ------------------- */

    /**
     * @return the type of this entity.
     */
    @Override
    protected Class<SecurityObjectEntity> getEntityClass() {
        return SecurityObjectEntity.class;
    }
	
	/**
	 * Returns the name.
	 * 
	 * @return The name of the <code>SecurityObjectEntity</code>
	 */
	public String getName() {
		LOGGER.debug(LOG_PREFIX + "getName [name=" + this.name + "]");

		return this.name;
	}

	/**
	 * Set the name. Necessary for using with Mapstruct.
	 *
	 * @param name
	 * 			The name of the <code>SecurityObjectEntity</code>
	 */
	public void setName(final String name) {
		LOGGER.debug(LOG_PREFIX + "setName [name=" + name + "]");

		this.name = name;
	}

	/**
	 * Returns the description text.
	 * 
	 * @return The description of the <code>SecurityObjectEntity</code> as text
	 */
	public String getDescription() {
		LOGGER.debug(LOG_PREFIX + "getDescription [description=" + this.description + "]");

		return this.description;
	}

	/**
	 * Set the description for the <code>SecurityObjectEntity</code>.
	 * 
	 * @param description
	 *            The description of the <code>SecurityObjectEntity</code> as text
	 */
	public void setDescription(String description) {
		LOGGER.debug(LOG_PREFIX + "setDescription [description=" + description + "]");

		this.description = description;
	}

}
