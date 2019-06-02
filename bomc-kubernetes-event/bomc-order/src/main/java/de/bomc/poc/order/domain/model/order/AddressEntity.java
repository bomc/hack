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
package de.bomc.poc.order.domain.model.order;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.apache.log4j.Logger;

/**
 * This entity represents the address of a customer.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Embeddable
public class AddressEntity implements Serializable {

    /**
     * The serial UID.
     */
    private static final long serialVersionUID = 5046224714237272788L;
    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AddressEntity.class);
    /**
     * A log prefix.
     */
    private static final String LOG_PREFIX = "AddressEntity#";

    /* --------------------- constants ------------------------------ */

    /* --------------------- columns -------------------------------- */
    private String street;
    private String zip;
    private String city;

    /* --------------------- associations --------------------------- */

    /* --------------------- constructors --------------------------- */
    /**
     * Create a new instance of <code>AddressEntity</code> (default#co).
     */
    public AddressEntity() {
        LOGGER.debug(LOG_PREFIX + "co");

        // Used by Jpa-Provider.
    }

    /**
     * Creates a new instance of <code>AddressEntity</code>.
     * 
     * @param street
     *            the given street.
     * @param zip
     *            the given zip.
     * @param city
     *            the given city.
     */
    public AddressEntity(final String street, final String zip, final String city) {
        this.street = street;
        this.zip = zip;
        this.city = city;
    }

    /* ----------------------------- methods ------------------------- */
    public String getStreet() {
        return this.street;
    }

    public void setStreet(final String street) {
        this.street = street;
    }

    public String getZip() {
        return this.zip;
    }

    public void setZip(final String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "AddressEntity [street=" + street + ", zip=" + zip + ", city=" + city + "]";
    }
}
