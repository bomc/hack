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
package de.bomc.poc.order.domain.model.item;

import de.bomc.poc.order.infrastructure.persistence.basis.JpaGenericDao;

/**
 * An JpaItemDao offers functionality regarding {@link ItemEntity} entity
 * classes.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 03.02.2019
 */
public interface JpaItemDao extends JpaGenericDao<ItemEntity> {

    // Define here methods for functionality from db for Item entity.
    // ...

    /**
     * Find <code>ItemEntity</code> by item name.
     * 
     * @param name
     *            name of item.
     * @param userId
     *            executed user.
     * @return the searched item.
     */
    ItemEntity findByName(String name, String userId);
}
