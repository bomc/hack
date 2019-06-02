package de.bomc.poc.dao.generic;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * A interface for dao operations.
 *
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
public interface JpaGenericDao<T> {

	T findById(Serializable id);

	T merge(T entity);

	void remove(T entity);

	void persist(T entity);

	List<T> findAll();

	List<T> findByNamedParameters(String queryName, Map<String, ?> params);

	List<T> findByPositionalParameters(String queryName, Object... values);
}
