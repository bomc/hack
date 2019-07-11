/**
 * Project: bomc-onion-architecture
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
package de.bomc.poc.order.domain.model.basis;

import org.apache.log4j.Logger;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * An AbstractMetadataEntity is as a base class for all domain classes.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
@MappedSuperclass
public abstract class AbstractMetadataEntity implements DomainObject {

    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractMetadataEntity.class);
    /**
     * A log prefix.
     */
    private static final String LOG_PREFIX = "AbstractMetadataEntity#";

    /* --------------------- constants ------------------------------ */
    /**
     * Suffix for the FIND_ALL named query. Default {@value}
     */
    public static final String FIND_ALL = ".findAll";
    /**
     * Suffix for the FIND_BY_ID named query. Default {@value}
     */
    public static final String FIND_BY_ID = ".findById";

    /* --------------------- columns -------------------------------- */
    /**
     * Version field.
     */
    @Version
    @Column(name = "C_VERSION")
    protected Long version;
    @Column(name = "C_CREATEUSER", nullable = false, updatable = false)
    protected String createUser;
    @Column(name = "C_CREATEDATETIME", nullable = false, updatable = false)
    protected LocalDateTime createDateTime;
    @Column(name = "C_MODIFYUSER", nullable = true)
    protected String modifyUser;
    @Column(name = "C_MODIFYDATETIME", nullable = true)
    protected LocalDateTime modifyDateTime;

    /* ----------------------------- methods ------------------------- */
    @PrePersist
    public void prePersist() {
        LOGGER.debug(LOG_PREFIX + "prePersist");

        if (this.isNew()) {
            this.setCreateDateTime(LocalDateTime.now());
            this.setModifyDateTime(LocalDateTime.now());
        } else {
            this.setModifyDateTime(LocalDateTime.now());
        }
    }

    @PreUpdate
    public void preUpdate() {
        LOGGER.debug(LOG_PREFIX + "preUpdate");

        if (this.isNew()) {
            this.setCreateDateTime(LocalDateTime.now());
            this.setModifyDateTime(LocalDateTime.now());
        } else {
            this.setModifyDateTime(LocalDateTime.now());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getVersion() {
        LOGGER.debug(LOG_PREFIX + "getVersion [version=" + this.version + "]");

        return this.version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVersion(final Long version) {
        LOGGER.debug(LOG_PREFIX + "setVersion [version=" + version + "]");

        this.version = version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCreateUser() {
        return this.createUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCreateUser(final String createUser) {
        this.createUser = createUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime getCreateDateTime() {
        return this.createDateTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCreateDateTime(final LocalDateTime createDateTime) {
        this.createDateTime = createDateTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModifyUser() {
        return this.modifyUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setModifyUser(final String modifyUser) {
        this.modifyUser = modifyUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime getModifyDateTime() {
        return this.modifyDateTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setModifyDateTime(final LocalDateTime modifyDateTime) {
        this.modifyDateTime = modifyDateTime;
    }

    /**
     * Format the localDateTime to ISO-8601 standard.
     * 
     * @param localDateTime
     *            the localDateTime to format.
     * @return the localDateTime in ISO-8601 format.
     */
    public String formatLocalDateTime(@NotNull final LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public String toString() {
        return "version=" + this.version + ", createUser=" + this.createUser + ", createDateTime=" + this.createDateTime
                + ", modifyUser=" + this.modifyUser + ", modifyDateTime=" + this.modifyDateTime;
    }
}
