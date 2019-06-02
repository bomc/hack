package de.bomc.poc.model;

import org.apache.log4j.Logger;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;
import java.util.Objects;

/**
 * An AbstractAccountEntity is as a base class for all entities that uses a own SequenceGenerator for generating the technical id.
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 09.08.2016
 */
@MappedSuperclass
public abstract class AbstractAccountEntity<T> extends AbstractMetadataEntity {

    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractAccountEntity.class);
    /**
     * A log prefix.
     */
    private static final String LOG_PREFIX = "AbstractAccountEntity#";

    /* --------------------- constants ------------------------------ */
    public static final int SEQ_INITIAL_VALUE = 700000000;
    public static final int SEQ_ALLOCATION_SIZE = 1;

    /* --------------------- columns -------------------------------- */
    /**
     * Unique technical key.
     */
    @Id
    @Column(name = "C_ID", unique = true, updatable = false)
	
	
	@TableGenerator(
			name = AbstractMetadataEntity.T_GENERATOR_ACCOUNT_NAME, 
			table = AbstractMetadataEntity.T_GENERATOR_ACCOUNT_TABLE, 
			pkColumnName = AbstractMetadataEntity.PK_C_SEQ_NAME, 
			valueColumnName = AbstractMetadataEntity.C_VAL_SEQ_COUNT, 
			pkColumnValue = AbstractMetadataEntity.PK_C_SEQ_NEXT_VAL_ACCOUNT,
			allocationSize = AbstractAccountEntity.SEQ_ALLOCATION_SIZE,
	        initialValue = AbstractAccountEntity.SEQ_INITIAL_VALUE)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = AbstractMetadataEntity.T_GENERATOR_ACCOUNT_NAME)
    private Long id;

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
        LOGGER.debug(LOG_PREFIX + "getId [id=" + this.id + ", " + this.getEntityClass()+  "]");

        return this.id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setId(final Long id) {
        LOGGER.debug(LOG_PREFIX + "setId [id=" + id + ", " + this.getEntityClass()+  "]");

        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNew() {
        LOGGER.debug(LOG_PREFIX + "isNew [isNew=" + (this.id == null) + ", " + this.getEntityClass()+  "]");

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

        final AbstractAccountEntity<?> other = (AbstractAccountEntity<?>)obj;

        return Objects.equals(this.id, other.getId());
    }

    /**
     * {@inheritDoc}
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int hashCode = 17;

        // Must be constant, because an auto-generated id changes after being persisted.
        return hashCode;
    }
}

