/**
 * Project: ping
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
package de.bomc.poc.ping.domain.model.basis;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

/**
 * An AbstractEntity is as a base class for all entities that uses the
 * 'GenerationType.AUTO'.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@MappedSuperclass
@JsonIgnoreProperties(value = { "isNew" })
public abstract class AbstractEntity<T> extends AbstractMetadataEntity {

	/* --------------------- constants ------------------------------ */

	/* --------------------- columns -------------------------------- */
	/**
	 * Unique technical key.
	 */
	@Id
	@Column(name = "c_id", unique = true, updatable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	protected Long id;

	/* --------------------- methods -------------------------------- */

	/**
	 * @return the type of this entity.
	 */
	protected abstract Class<T> getEntityClass();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getId() {
		return this.id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setId(final Long id) {
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNew() {
		return this.id == null || this.id == 0L;
	}

	/**
	 * <pre>
	 * https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
	 * 
	 * Two entities are considered equal if they are of the same class and have the same ID.
	 * An entity that has no ID (i.e. it is not persistent yet) is only equal to itself.
	 * </pre>
	 * {@inheritDoc}
	 * 
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!(obj instanceof AbstractEntity)) {
			return false;
		}

		final AbstractEntity<?> other = (AbstractEntity<?>) obj;

		return Objects.equals(this.id, other.getId());
	}

	/**
	 * <pre>
	 * {@inheritDoc} 
	 * 
	 * https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
	 * </pre>
	 * 
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int hashCode = 17;

		// NOTE: _____________________________________
		// Must be constant, because an auto-generated id changes after being
		// persisted.
		return hashCode;
	}
}
