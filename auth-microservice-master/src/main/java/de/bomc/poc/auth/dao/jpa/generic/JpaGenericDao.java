/**
 * Project: MY_POC
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * Copyright (c): BOMC, 2015
 */
package de.bomc.poc.auth.dao.jpa.generic;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * A interface for dao operations.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
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
