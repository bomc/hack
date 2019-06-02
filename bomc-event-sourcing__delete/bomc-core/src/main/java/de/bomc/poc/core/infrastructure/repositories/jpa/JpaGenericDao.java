package de.bomc.poc.core.infrastructure.repositories.jpa;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * A interface for dao operations.
 *
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 15.08.2018
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
