/**
 * Project: POC PaaS
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
package de.bomc.poc.hrm.domain.model.basis;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * An AbstractMetadataEntity is as a base class for all domain classes.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@MappedSuperclass
public abstract class AbstractMetadataEntity implements DomainObject {

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
    @Column(name = "c_version")
    protected Long version;
    @Column(name = "c_createuser"/*, nullable = false*/, updatable = false)
    protected String createUser;
    @Column(name = "c_createdatetime", nullable = false, updatable = false)
    @JsonFormat(pattern="dd.MMyyyy HH:mm:ss")
    protected LocalDateTime createDateTime;
    @Column(name = "c_modifyuser", nullable = true)
    protected String modifyUser;
    @Column(name = "c_modifydatetime", nullable = true)
    @JsonFormat(pattern="dd.MMyyyy HH:mm:ss")
    protected LocalDateTime modifyDateTime;

    /* ----------------------------- methods ------------------------- */
    @PrePersist
    public void prePersist() {
    	
        if (this.isNew()) {
            this.setCreateDateTime(LocalDateTime.now());
        } else {
            this.setModifyDateTime(LocalDateTime.now());
        }
    }

    @PreUpdate
    public void preUpdate() {

        if (this.isNew()) {
            this.setCreateDateTime(LocalDateTime.now());
        } else {
            this.setModifyDateTime(LocalDateTime.now());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getVersion() {
        return this.version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVersion(final Long version) {
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

    @Override
    public String toString() {
        return "version=" + this.version + ", createUser=" + this.createUser + ", createDateTime=" + this.createDateTime
                + ", modifyUser=" + this.modifyUser + ", modifyDateTime=" + this.modifyDateTime;
    }
}
