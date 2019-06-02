package de.bomc.poc.model;

import org.apache.log4j.Logger;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

/**
 * An AbstractEntity is as a base class for all entities that uses the 'GenerationType.AUTO'.
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 09.08.2016
 */
@MappedSuperclass
public abstract class AbstractEntity<T> extends AbstractMetadataEntity {

    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractEntity.class);
    /**
     * A log prefix.
     */
    private static final String LOG_PREFIX = "AbstractEntity#";

    /* --------------------- constants ------------------------------ */

    /* --------------------- columns -------------------------------- */
    /**
     * Unique technical key.
     */
    @Id
    @Column(name = "C_ID", unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
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
        LOGGER.debug(LOG_PREFIX + "getId [id=" + this.id + "]");

        return this.id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setId(final Long id) {
        LOGGER.debug(LOG_PREFIX + "setId [id=" + id + "]");

        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNew() {
        LOGGER.debug(LOG_PREFIX + "isNew [isNew=" + (this.id == null) + "]");

        return this.id == null;
    }

    /**
     * <pre>
     * Two entities are considered equal if they are of the same class and have the same ID.
     * An entity that has no ID (i.e. it is not persistent yet) is only equal to itself.
     * </pre>
     * {@inheritDoc}
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

        final AbstractEntity<?> other = (AbstractEntity<?>)obj;

        return Objects.equals(this.id, other.getId());
    }


	/**
	 * Use the id to calculate the hashCode.
	 * 
	 * {@inheritDoc}
	 * 
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// Must be constant, because an auto-generated id changes after being
		// persisted.
		// https://vladmihalcea.com/2016/06/06/how-to-implement-equals-and-hashcode-using-the-entity-identifier/
		return 31;
	}
}
