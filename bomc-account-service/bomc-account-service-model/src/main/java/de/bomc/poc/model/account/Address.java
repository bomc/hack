package de.bomc.poc.model.account;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;

import de.bomc.poc.model.AbstractEntity;
import de.bomc.poc.model.AbstractMetadataEntity;

/**
 * This class <code>Address</code> is a sample entity.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 16.09.2016
 */
@Entity
@Table(name = "T_ADDRESS")
@NamedQueries({ 
	@NamedQuery(name = Address.NQ_FIND_ALL, query = "select a from Address a") })
public class Address extends AbstractEntity<Address> implements Serializable {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = -2096844448338376419L;
	/**
	 * The logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(Address.class);
	/**
	 * A log prefix.
	 */
	private static final String LOG_PREFIX = Address.class + "#";

	/* --------------------- constants ------------------------------ */
	/**
	 * The default prefix String for each created
	 * <code>Address</code>-NamedQuery.
	 */
	protected static final String ADDRESS_PREFIX = "ADRESS.";
	/**
	 * <pre>
	 * Query to find all Adresses.
	 * </pre>
	 */
	public static final String NQ_FIND_ALL = ADDRESS_PREFIX + AbstractMetadataEntity.FIND_ALL;

	/* --------------------- columns -------------------------------- */
	/**
	 * The city name of this address.
	 */
	@Column(name = "C_CITY", length = 32, nullable = false)
	private String city;

	/**
	 * The country of this address.
	 */
	@Column(name = "C_COUNTRY", length = 20, nullable = false)
	private String country;

	/**
	 * The street name of this address.
	 */
	@Column(name = "C_STREET", length = 255, nullable = false)
	private String street;

	/**
	 * The zip-code of this address.
	 */
	@Column(name = "C_ZIP_CODE", length = 10, nullable = false)
	private String zipCode;

	/* --------------------- collections ---------------------------- */
	/**
	 * The associated <code>Person</code> (handles a bi-directional relationship).
	 */
	@ManyToOne
	@JoinColumn(name = "JOIN_COL_ADDRESS_USER", nullable = false)
	private User user;

	/* --------------------- constructors --------------------------- */
	/**
	 * Accessed by persistence provider.
	 */
	public Address() {
		// Accessed by the jpa provider.
		LOGGER.debug(LOG_PREFIX + "co");
	}

	/* --------------------- methods -------------------------------- */

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<Address> getEntityClass() {
		return Address.class;
	}

	/**
	 * @return the street.
	 */
	public String getStreet() {
		return this.street;
	}

	/**
	 * @param street
	 *            the street to set.
	 */
	public void setStreet(final String street) {
		this.street = street;
	}

	/**
	 * @return the city.
	 */
	public String getCity() {
		return this.city;
	}

	/**
	 * @param city
	 *            the city to set.
	 */
	public void setCity(final String city) {
		this.city = city;
	}

	/**
	 * @return the zipCode.
	 */
	public String getZipCode() {
		return this.zipCode;
	}

	/**
	 * @param zipCode
	 *            the zipCode to set.
	 */
	public void setZipCode(final String zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * @return the country.
	 */
	public String getCountry() {
		return this.country;
	}

	/**
	 * @param country
	 *            the country to set.
	 */
	public void setCountry(final String country) {
		this.country = country;
	}

	// _____________________________________________________________
	// Person handling.
	// -------------------------------------------------------------

	/**
	 * Get the {@link Person} of this address.
	 * 
	 * @return the user of this address.
	 */

	public User getUser() {
		return this.user;
	}

	/**
	 * <pre>
	 * Add a {@link Person} to this address. 
	 * ______________________________________________________________
	 * NOTE:
	 * The relationship is bidirectional, 
	 * the relationship has to be removed manually on the other side.
	 * </pre>
	 * 
	 * @param user
	 *            the given user to add.
	 */
	public void addUser(@NotNull final User user) {
		this.user = user;
		user.internalAddAddress(this);
	}

	/**
	 * <pre>
	 * Remove the {@link Person} from this address.
	 * ______________________________________________________________
	 * NOTE:
	 * The relationship is bidirectional, 
	 * the relationship has to be removed manually on the other side.
	 * </pre>
	 */
	public void removeUser() {
		if (this.user != null) {
			this.user.internalRemoveAddress(this);
			this.user = null;
		}
	}

	/**
	 * Uses all attributes for calculation.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;

		int result = super.hashCode();

		result = prime * result + ((this.city == null) ? 0 : this.city.hashCode());
		result = prime * result + ((this.country == null) ? 0 : this.country.hashCode());
		result = prime * result + ((this.street == null) ? 0 : this.street.hashCode());
		result = prime * result + ((this.user == null) ? 0 : this.user.hashCode());
		result = prime * result + ((this.zipCode == null) ? 0 : this.zipCode.hashCode());

		return result;
	}

	/**
	 * Uses all attributes for calculation.
	 * 
	 * (non-Javadoc)
	 * 
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
		if (!(obj instanceof Address)) {
			return false;
		}

		Address other = (Address) obj;

		if (this.city == null) {
			if (other.city != null) {
				return false;
			}
		} else if (!this.city.equals(other.city)) {
			return false;
		}
		if (this.country == null) {
			if (other.country != null) {
				return false;
			}
		} else if (!this.country.equals(other.country)) {
			return false;
		}
		if (this.street == null) {
			if (other.street != null) {
				return false;
			}
		} else if (!this.street.equals(other.street)) {
			return false;
		}
		if (this.user == null) {
			if (other.user != null) {
				return false;
			}
		} else if (!this.user.equals(other.user)) {
			return false;
		}
		if (this.zipCode == null) {
			if (other.zipCode != null) {
				return false;
			}
		} else if (!this.zipCode.equals(other.zipCode)) {
			return false;
		}

		return true;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Address [id=" + this.getId() + ", version=" + this.getVersion() + ", city=" + this.city + ", country="
				+ this.country + ", street=" + this.street + ", zipCode=" + this.zipCode + ", user=" + this.user + "]";
	}

}
