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

import java.time.LocalDateTime;

import de.bomc.poc.order.infrastructure.persistence.basis.JpaGenericDao;

/**
 * An JpaCustomerDao offers functionality regarding {@link CustomerEntity}
 * entity classes.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 03.02.2019
 */
public interface JpaCustomerDao extends JpaGenericDao<CustomerEntity> {

    // Define here methods for functionality from db for Customer entity.
    // ...

    /**
     * Find the latest modified date for customer entries in db.
     * 
     * @param userId
     *            the executing user used fro auditing.
     * @return latest modified date/time.
     */
    LocalDateTime findLatestModifiedDateTime(String userId);

    /**
     * Find customer by username.
     * 
     * @param username
     *            the given username.
     * @param userId
     *            the executed user.
     * @return the searched customer.
     */
    CustomerEntity findByUsername(String username, String userId);
}
