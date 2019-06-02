package de.bomc.poc.model.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;

import de.bomc.poc.model.AbstractMetadataEntity;

/**
 * A legal user.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 09.08.2016
 */
@Entity
@Table(name = "T_LEGAL_USER")
@NamedQueries({ 
	@NamedQuery(name = LegalUser.NQ_FIND_ALL, query = "select u from LegalUser u")
})
public class LegalUser extends User {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = -1L;
    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LegalUser.class);
    /**
     * A log prefix.
     */
    private static final String LOG_PREFIX = "LegalUser#";

    /* --------------------- constants ------------------------------ */
	/**
	 * The default prefix String for each created <code>LegalUser</code>-NamedQuery.
	 */
	protected static final String LEGAL_USER_PREFIX = "LEGAL_USER.";
	/**
	 * <pre>
	 * Query to find all LegalUser.
	 * </pre>
	 */
	public static final String NQ_FIND_ALL = LEGAL_USER_PREFIX + AbstractMetadataEntity.FIND_ALL;
	
    /* --------------------- columns -------------------------------- */
    @Column(name = "C_COMPANY_ID", nullable = false)
    private String companyId;

    /* --------------------- collections ---------------------------- */
    /* --------------------- constructors --------------------------- */
    protected LegalUser() {
        LOGGER.debug(LOG_PREFIX + "co");

        // Used by Jpa-Provider.
    }

    public LegalUser(@NotNull final String username, final String companyId) {
        super(username);

        LOGGER.debug(LOG_PREFIX + " [username=" + username + ", companyId=" + companyId + "]");
    }

    /* ----------------------------- methods ------------------------- */
    /**
	 * @return the companyId
	 */
	public String getCompanyId() {
		return companyId;
	}

	/**
	 * @param companyId the companyId to set
	 */
	public void setCompanyId(final String companyId) {
		this.companyId = companyId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		
		int result = super.hashCode();
		
		result = prime * result + ((this.companyId == null) ? 0 : this.companyId.hashCode());
		
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof LegalUser)) {
			return false;
		}
		
		LegalUser other = (LegalUser) obj;
		
		if (this.companyId == null) {
			if (other.companyId != null) {
				return false;
			}
		} else if (!this.companyId.equals(other.companyId)) {
			return false;
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LegallUser [companyId=" + this.companyId + ", username=" + this.username + ", id=" + id + "]";
	}
}

