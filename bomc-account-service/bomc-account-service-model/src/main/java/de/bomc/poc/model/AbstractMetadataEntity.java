package de.bomc.poc.model;

import org.apache.log4j.Logger;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * An AbstractMetadataEntity is as a base class for all domain classes.
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 09.08.2016
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
    // Start: Constants that are used for sequence generation.
	protected static final String T_GENERATOR_ACCOUNT_TABLE = "T_GENERATOR_ACCOUNT";
	protected static final String T_GENERATOR_ACCOUNT_NAME = "T_GENERATOR_ACCOUNT";
	protected static final String PK_C_SEQ_NAME = "PK_C_SEQ_NAME";
	protected static final String C_VAL_SEQ_COUNT = "C_VAL_SEQ_COUNT";
	// This is used for generating the {@link Account} sequence.
	protected static final String PK_C_SEQ_NEXT_VAL_ACCOUNT = "PK_C_SEQ_NEXT_VAL_ACCOUNT";
	// End
    /**
     * Suffix for the FIND_ALL named query. Default {@value}
     */
    public static final String FIND_ALL = "findAll";
    /**
     * Suffix for the FIND_BY_ID named query. Default {@value}
     */
    public static final String FIND_BY_ID = "findById";

    /* --------------------- columns -------------------------------- */
    /**
     * Version field.
     */
    @Version
    @Column(name = "C_VERSION")
    protected Long version;
    // TODO: nullable = false
    @Column(name = "C_CREATEUSER", nullable = true)
    protected String createUser;
    @Column(name = "C_CREATEDATE", nullable = false)
    protected LocalDate createDate;
    @Column(name = "C_MODIFYUSER", nullable = true)
    protected String modifyUser;
    @Column(name = "C_MODIFYDATE", nullable = true)
    protected LocalDate modifyDate;

    /* ----------------------------- methods ------------------------- */
    @PrePersist
    public void prePersist() {
        LOGGER.debug(LOG_PREFIX + "prePersist");

        if (this.isNew()) {
            this.setCreateDate(LocalDate.now());
        }
    }

    @PreUpdate
    public void preUpdate() {
        LOGGER.debug(LOG_PREFIX + "preUpdate");

        this.setModifyDate(LocalDate.now());
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
    public LocalDate getCreateDate() {
        return this.createDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCreateDate(final LocalDate createDate) {
        this.createDate = createDate;
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
    public LocalDate getModifyDate() {
        return this.modifyDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setModifyDate(final LocalDate modifyDate) {
        this.modifyDate = modifyDate;
    }

    /**
     * Format the localDateTime to ISO-8601 standard.
     * @param localDateTime the localDateTime to format.
     * @return the localDateTime in ISO-8601 format.
     */
    public String formatLocalDateTime(@NotNull final LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public String toString() {
        return "AbstractMetadataEntity [version=" + this.version + ", createUser=" + this.createUser + ", createDate=" + this.createDate + ", modifyUser=" + this.modifyUser + ", modifyDate=" + this.modifyDate + "]";
    }
}


