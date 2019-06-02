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

import org.apache.log4j.Logger;

import de.bomc.poc.auth.model.AbstractEntity;
import de.bomc.poc.auth.model.values.CoreTypeDefinitions;

/**
 * A SecurityObject is the generalization of <code>Role</code>s and
 * <code>Grant</code>s and combines common used properties of both.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Entity
@Table(name = "T_ROLE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING, length = 20)
@NamedQueries({
		@NamedQuery(name = SecurityObject.NQ_FIND_ALL, query = "select g from SecurityObject g"),
		@NamedQuery(name = SecurityObject.NQ_FIND_BY_UNIQUE_QUERY, query = "select g from SecurityObject g where g.name = ?1")
})
public class SecurityObject extends AbstractEntity implements Serializable {

	private static final Logger LOGGER = Logger.getLogger(SecurityObject.class);

	private static final long serialVersionUID = 7585736035228078754L;
	/**
	 * Unique name of the <code>SecurityObject</code>.
	 * 
	 * A name of a SecurityObject must not be null.
	 */
	@NotNull
	@Column(name = "C_NAME", unique = true)
	@OrderBy
	private String name;
	/**
	 * Description of the <code>SecurityObject</code>.
	 */
	@Column(name = "C_DESCRIPTION", length = CoreTypeDefinitions.DESCRIPTION_LENGTH)
	private String description;
	/**
	 * Query to find all {@link SecurityObject}s. Name is {@value} .
	 */
	public static final String NQ_FIND_ALL = "SecurityObject.findAll";
	/**
	 * Query to find <strong>one</strong> {@link SecurityObject} by its natural
	 * key.
	 * <li>Query parameter index <strong>1</strong> : The name of the
	 * <code>SecurityObject</code> to search for.</li><br />
	 * Name is {@value} .
	 */
	public static final String NQ_FIND_BY_UNIQUE_QUERY = "SecurityObject.findByName";

	/* ---------------------- constructors ------------------- */
	/**
	 * Accessed by persistence provider.
	 */
	protected SecurityObject() {
		// Used by JPA provider.

		LOGGER.debug("SecurityObject#co");
	}

	/**
	 * Create a new <code>SecurityObject</code> with a name.
	 * 
	 * @param name
	 *            The name of the <code>SecurityObject</code>
	 * @throws IllegalArgumentException
	 *             when name is <code>null</code> or an empty String
	 */
	public SecurityObject(String name) {
		this.name = name;

		LOGGER.debug("SecurityObject#co [name=" + name + "]");
	}

	/**
	 * Create a new <code>SecurityObject</code> with name and description.
	 * 
	 * @param name
	 *            The name of the <code>SecurityObject</code>
	 * @param description
	 *            The description text of the <code>SecurityObject</code>
	 * @throws IllegalArgumentException
	 *             when name is <code>null</code> or an empty String. A name of
	 *             a SecurityObject must not be null.
	 */
	public SecurityObject(@NotNull final String name, String description) {
		LOGGER.debug("SecurityObject#co [name=" + name + ", description=" + description + "]");

		this.name = name;
		this.description = description;
	}

	/* ----------------------------- methods ------------------- */

	/**
	 * Returns the name.
	 * 
	 * @return The name of the <code>SecurityObject</code>
	 */
	public String getName() {
		LOGGER.debug("SecurityObject#getName [name=" + this.name + "]");

		return this.name;
	}

	/**
	 * Set the name. Necessary for using with Mapstruct.
	 *
	 * @param name
	 * 			The name of the <code>SecurityObject</code>
	 */
	public void setName(final String name) {
		LOGGER.debug("SecurityObject#setName [name=" + name + "]");

		this.name = name;
	}

	/**
	 * Returns the description text.
	 * 
	 * @return The description of the <code>SecurityObject</code> as text
	 */
	public String getDescription() {
		LOGGER.debug("SecurityObject#getDescription [description=" + this.description + "]");

		return this.description;
	}

	/**
	 * Set the description for the <code>SecurityObject</code>.
	 * 
	 * @param description
	 *            The description of the <code>SecurityObject</code> as text
	 */
	public void setDescription(String description) {
		LOGGER.debug("SecurityObject#setDescription [description=" + description + "]");

		this.description = description;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());

		return result;
	}

	/**
	 * {@inheritDoc} Compare the name.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SecurityObject other = (SecurityObject) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "SecurityObject [id=" + this.getId()
			   + ", name=" + this.name
			   + ", description=" + this.description
			   + ", version=" + this.getVersion()
			   + "]";
	}
}
