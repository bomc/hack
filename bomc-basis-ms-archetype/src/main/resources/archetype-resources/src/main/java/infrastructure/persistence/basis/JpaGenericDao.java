#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: ${symbol_dollar}Author: bomc ${symbol_dollar}
 *
 *  date: ${symbol_dollar}Date: ${symbol_dollar}
 *
 *  revision: ${symbol_dollar}Revision: ${symbol_dollar}
 *
 * </pre>
 */
package ${package}.infrastructure.persistence.basis;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
/**
 * A interface for dao operations.
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
