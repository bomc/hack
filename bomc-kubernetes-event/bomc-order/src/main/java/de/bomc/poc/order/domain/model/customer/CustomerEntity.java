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
package de.bomc.poc.order.domain.model.customer;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.log4j.Logger;

import de.bomc.poc.order.domain.model.basis.AbstractEntity;

/**
 * This entity represents a customer in the shop context.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Entity
@Table(name = "T_CUSTOMER")
@NamedQueries({
        @NamedQuery(name = CustomerEntity.NQ_FIND_BY_LATEST_MODIFIED_DATE_TIME_CUSTOMER, query = "select max(c.modifyDateTime) from CustomerEntity c"),
        @NamedQuery(name = CustomerEntity.NQ_FIND_BY_USERNAME, query = "select c from CustomerEntity c where c.username = :username"), })
public class CustomerEntity extends AbstractEntity<CustomerEntity> implements Serializable {

    /**
     * The serial UID
     */
    private static final long serialVersionUID = 5945895602909245641L;
    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CustomerEntity.class);
    /**
     * A log prefix.
     */
    private static final String LOG_PREFIX = "CustomerEntity#";

    /* --------------------- constants ------------------------------ */
    /**
     * The default prefix String for each created
     * <code>CustomerEntity</code>-NamedQuery.
     */
    protected static final String NQ_PREFIX = "CUSTOMER.";
    /**
     * <pre>
     * Query to find latest modified date.
     * </pre>
     */
    public static final String NQ_FIND_BY_LATEST_MODIFIED_DATE_TIME_CUSTOMER = NQ_PREFIX
            + "findByLatestModifiedDateTime";
    /**
     * Query to find <strong>one</strong> <code>CustomerEntity</code> by the
     * username.
     * <li>Query parameter name <strong>username</strong> : The username of the
     * <code>Customer</code> to search for.</li> Name is {@value} .
     */
    public static final String NQ_FIND_BY_USERNAME = NQ_PREFIX + "findByUsername";

    /* --------------------- columns -------------------------------- */
    @Column(name = "C_NAME", nullable = false)
    private String name;
    @Column(name = "C_FIRSTNAME", nullable = false)
    private String firstname;
    @Column(name = "C_USERNAME", unique = true, nullable = false)
    private String username;

    /* --------------------- associations --------------------------- */

    /* --------------------- constructors --------------------------- */
    public CustomerEntity() {
        LOGGER.debug(LOG_PREFIX + "co");

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

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "CustomerEntity [id=" + super.getId() + ", name=" + name + ", firstname=" + firstname + ", username="
                + username + "]";
    }
}
