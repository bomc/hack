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
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.auth.dao.jpa.generic.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.bomc.poc.auth.dao.jpa.generic.JpaGenericDao;
import de.bomc.poc.auth.dao.jpa.producer.DatabaseMySqlProducer;
import org.apache.log4j.Logger;

/**
 * A abstract class for the GenricDao implementation.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
public abstract class AbstractJpaDao<T> implements JpaGenericDao<T> {

	private static final Logger LOGGER = Logger.getLogger(AbstractJpaDao.class.getName());
	private static final String LOG_PREFIX = "#dao#";

	@PersistenceContext(unitName = DatabaseMySqlProducer.UNIT_NAME)
	private EntityManager em;

	protected AbstractJpaDao() {
		//
	}

	protected final EntityManager getEntityManager() {
		return this.em;
	}

	protected abstract Class<T> getPersistentClass();

	/**
	 * This method is considered as a hook to do something before an update is
	 * performed.
	 * 
	 * @param entity
	 *            The Entity that is updated
	 */
	protected void beforeUpdate(final T entity) {
		LOGGER.debug(LOG_PREFIX + "beforeUpdate");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T merge(final T entity) {
		this.beforeUpdate(entity);

		return this.em.merge(entity);
	}

	/**
	 * NOTE:
	 * 
	 * <pre>
	 * This works only on entities which are managed in the current
	 * transaction/context. This means this method should be only invoked inside a DAO context.
	 *
	 * 1. Read a entity from db (e.g. findById)
	 * 2. and than invoke remove, this should happen in one method to prevent a <code>IllegalArgumentException</code> (Removing a detached instance).
	 * </pre>
	 */
	@Override
	public void remove(final T entity) {
		this.em.remove(entity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// TODO: Wo kommt die her?
	// @Transactional(Transactional.TxType.REQUIRES_NEW)
	public void persist(final T entity) {
		this.beforeUpdate(entity);

		this.em.persist(entity);
		this.em.flush();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<T> findAll() {
		final CriteriaQuery cq = this.getEntityManager().getCriteriaBuilder().createQuery();
		cq.select(cq.from(this.getPersistentClass()));

		List<T> entityList = this.getEntityManager().createQuery(cq).getResultList();

		if (entityList == null) {
			entityList = Collections.emptyList();
		}

		return entityList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findByNamedParameters(final String queryName, final Map<String, ?> params) {
		final Query queryObject = this.getEntityManager().createNamedQuery(queryName);

		if (params != null) {
			for (final Map.Entry<String, ?> entry : params.entrySet()) {
				queryObject.setParameter(entry.getKey(), entry.getValue());
			}
		}

		return queryObject.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findByPositionalParameters(final String queryName, final Object... values) {
		final Query queryObject = this.getEntityManager().createNamedQuery(queryName);

		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				queryObject.setParameter(i + 1, values[i]);
			}
		}

		return queryObject.getResultList();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <pre>
	 * Mit dieser Methode wird nach einem Entity mit dem Persistence Context gesucht.
	 * Das heisst es werden alle Beziehungen mitgeladen die im Entity als 'Eager' annotiert sind.
	 * </pre>
	 * 
	 * @param id
	 *            die unique id.
	 * @return das gesuchte Entity oder <code>null</code>, wenn kein Entity
	 *         gefunden wurde mit der gegebenen id.
	 */
	@Override
	public T findById(final Serializable id) {
		LOGGER.debug(LOG_PREFIX + "findById [clazz=" + this.getPersistentClass().getName() + ", id=" + id + "]");

		return this.em.find(this.getPersistentClass(), id);
	}

	/**
	 * Find entities in defined range. The range is given as two int's. [0] the minimum, [1] the maximum.
	 * @param range the given range as array with two elements..
	 * @return entities in defined range.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<T> findRange(final int[] range) {
		final CriteriaQuery cq = this.getEntityManager().getCriteriaBuilder().createQuery();
		cq.select(cq.from(this.getPersistentClass()));
		final Query q = this.getEntityManager().createQuery(cq);
		q.setMaxResults(range[1] - range[0]);
		q.setFirstResult(range[0]);

		return q.getResultList();
	}

	/**
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int count() {
		final CriteriaQuery cq = this.getEntityManager().getCriteriaBuilder().createQuery();
		final Root<T> rt = cq.from(this.getPersistentClass());
		cq.select(this.getEntityManager().getCriteriaBuilder().count(rt));
		final Query q = this.getEntityManager().createQuery(cq);

		final Long result = (Long) q.getSingleResult();

		if (result == null) {
			return 0;
		} else {
			return ((Long) q.getSingleResult()).intValue();
		}
	}
}
