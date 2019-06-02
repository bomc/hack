#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: ${symbol_dollar}Author: bomc ${symbol_dollar}
 *
 *  date: ${symbol_dollar}Date: ${symbol_dollar}
 *
 *  revision: ${symbol_dollar}Revision: ${symbol_dollar}
 *
 * </pre>
 */
 package ${package}.domain.model.basis;

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
    private static final String LOG_PREFIX = "AbstractMetadataEntity${symbol_pound}";

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
    @Column(name = "C_CREATEDATE", nullable = false, updatable = false)
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
        } else {
            this.setModifyDate(LocalDate.now());
        }
    }

    @PreUpdate
    public void preUpdate() {
        LOGGER.debug(LOG_PREFIX + "preUpdate");

        if (this.isNew()) {
            this.setCreateDate(LocalDate.now());
        } else {
            this.setModifyDate(LocalDate.now());
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
        return "version=" + this.version + ", createUser=" + this.createUser + ", createDate=" + this.createDate + ", modifyUser=" + this.modifyUser + ", modifyDate=" + this.modifyDate;
    }
}
