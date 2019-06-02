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

import java.time.LocalDateTime;
import java.util.List;

import de.bomc.poc.order.infrastructure.persistence.basis.JpaGenericDao;

/**
 * An JpaOrderDao offers functionality regarding {@link OrderEntity} entity
 * classes.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 03.02.2019
 */
public interface JpaOrderDao extends JpaGenericDao<OrderEntity> {

    // Define here methods for functionality from db for Order entity.
    // ...

    /**
     * Find the latest modified date for order entries in db.
     * 
     * @param userId
     *            the executing user used for auditing.
     * @return latest modified date/time.
     */
    LocalDateTime findLatestModifiedDateTime(String userId);

    /**
     * Find all orders that create or modify date is older than the given date.
     * 
     * @param modifyDateTime
     *            the given modify date.
     * @param userId
     *            the executing user used fro auditing.
     * @return A list with orders.
     */
    List<OrderEntity> findByAllOlderThanGivenDate(LocalDateTime modifyDateTime, String userId);
}
