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
package de.bomc.poc.auth.model;

import org.apache.log4j.Logger;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Version;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * <pre>
 * An AbstractEntity, used as a base class for all domain classes.
 * <p>
 * Adds an unique identifier to a subclassed domain class that is created when
 * the class is instantiated on the client tier. At least this uid is created
 * before the instance is persisted the first time. The uid may not be mistaken
 * with the id property that is usually used for database identity (primary key)
 * or with a business key column.
 * </p>
 * <p>
 * This class has an inner static declared class that is registered as a JPA
 * EntityListener and forces the creation of an uid if not already created
 * before. This assures that each persisted entity has an uid.
 * </p>
 * <strong>NOTE:</strong><br />
 * This class uses the uid for comparison with {@link #equals(Object)} and
 * calculation of {@link #hashCode()}.
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@MappedSuperclass
@EntityListeners({AbstractEntity.AbstractEntityListener.class})
public abstract class AbstractEntity implements DomainObject {

    private static final Logger LOGGER = Logger.getLogger(AbstractEntity.class);
    private static final int UID_LENGTH = 36;
    /** 
     * Suffix for the FIND_ALL named query. Default {@value}
     */
    public static final String FIND_ALL = ".findAll";
    /**
     * Suffix for the FIND_BY_ID named query. Default {@value}
     */
    public static final String FIND_BY_ID = ".findById";
    /**
     * Unique technical key.
     */
    @Id
    @Column(name = "C_ID", unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
   	 * Version field.
   	 */
   	@Version
   	@Column(name = "C_VERSION")
   	private Long version;
    /**
     * Unique identifier column, used for ActionScript clients.
     */
    /* "UUID" and "UID" are Oracle reserved keywords -> "ENTITY_UID" */
    @Column(name = "C_ENTITY_UID", unique = true, nullable = false, updatable = false, length = UID_LENGTH)
    private String uid;

    /* ----------------------------- methods ------------------------- */

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getId() {
        LOGGER.debug("AbstractEntity#getId [id=" + this.id + "]");

        return this.id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setId(Long id) {
        LOGGER.debug("AbstractEntity#setId [id=" + id + "]");

        this.id = id;
    }

    /**
   	 * {@inheritDoc}
   	 */
   	@Override
   	public Long getVersion() {
   		LOGGER.debug("AbstractEntity#getVersion [version=" + this.version + "]");

   		return this.version;
   	}

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNew() {
        LOGGER.debug("AbstractEntity#isNew [isNew=" + (this.id == null) + "]");

        return this.id == null;
    }

    /**
     * Format the localDateTime to ISO-8601 standard.
     * @param localDateTime the localDateTime to format.
     * @return the localDateTime in ISO-8601 format.
     */
    public String formatLocalDateTime(final LocalDateTime localDateTime) {
        if (localDateTime != null) {
            return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } else {
            // TODO exception
            return null;
        }
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Compare the uid property field.
     * @see java.lang.Object#equals(Object)
     */
    @Override
    public boolean equals(final Object o) {
        LOGGER.debug("AbstractEntity#equals");

        return (o == this || (o instanceof AbstractEntity && uid().equals(((AbstractEntity)o).uid())));
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Use the uid to calculate the hashCode.
     * @see java.lang.String#hashCode()
     */
    @Override
    public int hashCode() {
        LOGGER.debug("AbstractEntity#hashCode");

        return uid().hashCode();
    }

    /* ----------------------------- inner class ------------------------- */

    /**
     * An AbstractEntityListener forces the creation of an uid before the entity is persisted.
     * @author <a href="mailto:bomc@bomc.org">bomc</a>
     */
    public static class AbstractEntityListener {

        /**
         * Before a new entity is persisted, an UUID is generated for it.
         * @param abstractEntity The entity to persist.
         */
        @PrePersist
        public void onPreInsert(final AbstractEntity abstractEntity) {
            LOGGER.debug("AbstractEntity.AbstractEntityListener#onPreInsert");

            abstractEntity.uid();
        }
    }

    private String uid() {
        if (uid == null) {
            uid =
                UUID.randomUUID()
                    .toString();
        }

        LOGGER.debug("AbstractEntity.AbstractEntityListener#uid [uid=" + uid + "]");

        return uid;
    }
}
