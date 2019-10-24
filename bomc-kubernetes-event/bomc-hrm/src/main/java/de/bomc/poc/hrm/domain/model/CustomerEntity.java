/**
 * Project: hrm
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
package de.bomc.poc.hrm.domain.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import de.bomc.poc.hrm.domain.model.basis.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * This entity represents a customer.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
// LOMBOK
@AllArgsConstructor
@Builder
@Getter
@Setter
// JPA
@Entity
@Table(name = "t_customer", schema = "public")
@NamedQueries({
		@NamedQuery(name = "Customer.NQ_FIND_CUSTOMER_BY_EMAIL_ADDRESS", query = "SELECT c FROM CustomerEntity c WHERE c.emailAddress = :emailAddress"),
		@NamedQuery(name = "Customer.NQ_FIND_CUSTOMER_BY_ID", query = "SELECT c FROM CustomerEntity c WHERE c.id= :id") })
public class CustomerEntity extends AbstractEntity<CustomerEntity> implements Serializable {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = -1121920457428004273L;

	/* --------------------- constants ------------------------------ */
	/**
	 * The default prefix String for each created
	 * <code>CustomerEntity</code>-NamedQuery.
	 */
	protected static final String NQ_PREFIX = "CUSTOMER.";
	/**
	 * <pre>
	 * Query to find customer by email address.
	 * <li>
	 * 	Query parameter name <strong>emailAddress</strong> : The emailAddress of the <code>Customer</code> to search for.
	 * </li>
	 * Name is {@value}.
	 * </pre>
	 */
	public static final String NQ_FIND_CUSTOMER_BY_EMAIL_ADDRESS = NQ_PREFIX + "findByEmailAddress";

	/* --------------------- columns -------------------------------- */
	@Column(name = "c_email_address", unique = true)
	private String emailAddress;

	@Column(name = "c_phone_number", length = 15)
	private String phoneNumber;

	@Column(name = "c_first_name", length = 30)
	private String firstName;

	@Column(name = "c_last_name", length = 30)
	private String lastName;

	@Column(name = "c_date_of_birth", nullable = false)
//	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonFormat(pattern="dd.MM.yyyy")
	private LocalDate dateOfBirth;

	@Column(name = "c_city", length = 40)
	private String city;

	@Column(name = "c_postal_code", length = 10)
	private String postalCode;

	@Column(name = "c_street", length = 60)
	private String street;

	@Column(name = "c_house_number", length = 5)
	private String houseNumber;

	@Column(name = "c_country", length = 3)
	private String country;

	/* --------------------- associations --------------------------- */

	/* --------------------- constructors --------------------------- */
	public CustomerEntity() {
		// Used by Jpa-Provider.

	}

    /* ----------------------------- methods ------------------------- */
	
    /**
     * @return the type of this entity.
     */
    @Override
    protected Class<CustomerEntity> getEntityClass() {
        return CustomerEntity.class;
    }

	@Override
	public String toString() {
		return "CustomerEntity [id=" + id + ", emailAddress=" + emailAddress + ", phoneNumber=" + phoneNumber + ", firstName="
				+ firstName + ", lastName=" + lastName + ", dateOfBirth=" + dateOfBirth + ", city=" + city
				+ ", postalCode=" + postalCode + ", street=" + street + ", houseNumber=" + houseNumber + ", country="
				+ country + "]";
	}

}
