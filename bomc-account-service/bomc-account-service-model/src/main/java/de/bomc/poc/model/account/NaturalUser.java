package de.bomc.poc.model.account;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.log4j.Logger;

import de.bomc.poc.model.AbstractMetadataEntity;

/**
 * A private user.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 09.08.2016
 */
@Entity
@Table(name = "T_PRIVATE_USER")
@NamedQueries({ 
	@NamedQuery(name = NaturalUser.NQ_FIND_ALL, query = "select u from NaturalUser u")
})
public class NaturalUser extends User {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = -1L;
    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(NaturalUser.class);
    /**
     * A log prefix.
     */
    private static final String LOG_PREFIX = "NaturalUser#";

    /* --------------------- constants ------------------------------ */
	/**
	 * The default prefix String for each created <code>NaturalUser</code>-NamedQuery.
	 */
	protected static final String PRIVATE_USER_PREFIX = "PRIVATE_USER.";
	/**
	 * <pre>
	 * Query to find all NaturalUser.
	 * </pre>
	 */
	public static final String NQ_FIND_ALL = PRIVATE_USER_PREFIX + AbstractMetadataEntity.FIND_ALL;
	
    /* --------------------- columns -------------------------------- */
	// TODO check older than 18
    @Column(name = "C_BIRTH_DATE", nullable = false)
    private LocalDate birthDate;

    /* --------------------- collections ---------------------------- */
    /* --------------------- constructors --------------------------- */
    protected NaturalUser() {
        LOGGER.debug(LOG_PREFIX + "co");

        // Used by Jpa-Provider.
    }

    public NaturalUser(final String username) {
        super(username);

        LOGGER.debug(LOG_PREFIX + " [username=" + username + "]");
    }

    /* ----------------------------- methods ------------------------- */
    /**
	 * @return the birthDate
	 */
	public LocalDate getBirthDate() {
		return birthDate;
	}

	/**
	 * @param birthDate the birthDate to set
	 */
	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		
		int result = super.hashCode();
		
		result = prime * result + ((this.birthDate == null) ? 0 : this.birthDate.hashCode());
		
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
		if (!(obj instanceof NaturalUser)) {
			return false;
		}
		
		NaturalUser other = (NaturalUser) obj;
		
		if (this.birthDate == null) {
			if (other.birthDate != null) {
				return false;
			}
		} else if (!this.birthDate.equals(other.birthDate)) {
			return false;
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NaturalUser [birthDate=" + this.birthDate + ", username=" + this.username + ", id=" + id + "]";
	}
}
