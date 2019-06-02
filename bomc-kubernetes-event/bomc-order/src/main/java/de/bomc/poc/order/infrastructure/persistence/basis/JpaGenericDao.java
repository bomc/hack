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
package de.bomc.poc.order.infrastructure.persistence.basis;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * A interface for dao operations.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
public interface JpaGenericDao<T> {

    T findById(Serializable id);

    T merge(T entity, String executingUser);

    void remove(T entity);

    void persist(T entity, String executingUser);

    List<T> findAll();

    List<T> findByNamedParameters(String queryName, Map<String, ?> params);

    List<T> findByPositionalParameters(String queryName, Object... values);

    int count();

    List<T> findRange(int[] range);

    void clearEntityManagerCache();
}
