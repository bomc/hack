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
package de.bomc.poc.controller.infrastructure.persistence.basis.impl;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import org.apache.log4j.Logger;

import de.bomc.poc.controller.application.internal.AppErrorCodeEnum;
import de.bomc.poc.controller.domain.model.basis.AbstractEntity;
import de.bomc.poc.controller.infrastructure.persistence.basis.JpaGenericDao;

/**
 * A abstract class for the GenericDao implementation.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
public abstract class AbstractJpaDao<T> implements JpaGenericDao<T> {

    private static final String LOG_PREFIX = "AbstractJpaDao#";
    @Inject
    @LoggerQualifier
    private Logger logger;
    @Inject
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
     * @param entity        The Entity that is updated
     * @param executingUser The user that is executing this transaction
     */
    @SuppressWarnings("unchecked")
    protected void beforeUpdate(final T entity, final String executingUser) {
        this.logger.debug(LOG_PREFIX + "beforeUpdate");

        // Create or modify?
        if (((AbstractEntity<T>)entity).isNew()) {
            ((AbstractEntity<T>)entity).setCreateDate(LocalDate.now());
            ((AbstractEntity<T>)entity).setCreateUser(executingUser);
        } else {
            ((AbstractEntity<T>)entity).setModifyDate(LocalDate.now());
            ((AbstractEntity<T>)entity).setModifyUser(executingUser);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T merge(final T entity, final String executingUser) {
        this.beforeUpdate(entity, executingUser);

        return this.em.merge(entity);
    }

    /**
     * NOTE:
     * <p>
     * <pre>
     * This works only on entities which are managed in the current transaction/context.
     * This means this method should be only invoked inside a DAO context.
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
    public void persist(final T entity, final String executingUser) {
        this.beforeUpdate(entity, executingUser);

        this.em.persist(entity);
        this.em.flush();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<T> findAll() {
        final CriteriaQuery
            cq =
            this.getEntityManager()
                .getCriteriaBuilder()
                .createQuery();
        cq.select(cq.from(this.getPersistentClass()));

        List<T>
            entityList =
            this.getEntityManager()
                .createQuery(cq)
                .getResultList();

        if (entityList == null) {
            entityList = Collections.emptyList();
        }

        return entityList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> findByNamedParameters(final String queryName, final Map<String, ?> params) {
        final Query
            queryObject =
            this.getEntityManager()
                .createNamedQuery(queryName);

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
        final Query
            queryObject =
            this.getEntityManager()
                .createNamedQuery(queryName);

        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                queryObject.setParameter(i + 1, values[i]);
            }
        }

        return queryObject.getResultList();
    }

    /**
     * {@inheritDoc}
     * <p>
     * <pre>
     * Mit dieser Methode wird nach einem Entity mit dem Persistence Context gesucht.
     * Das heisst es werden alle Beziehungen mitgeladen die im Entity als 'Eager' annotiert sind.
     * </pre>
     * @param id die unique id.
     * @return das gesuchte Entity oder <code>null</code>, wenn kein Entity gefunden wurde mit der gegebenen id.
     */
    @Override
    public T findById(final Serializable id) {
        this.logger.debug(LOG_PREFIX + "findById [clazz=" + this.getPersistentClass()
                                                                .getName() + ", id=" + id + "]");

        return this.em.find(this.getPersistentClass(), id);
    }

    /**
     * Find entities in defined range. The range is given as two int's. [0] the
     * minimum, [1] the maximum.
     * @param range the given range as array with two elements..
     * @return entities in defined range.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<T> findRange(final int[] range) {
        final CriteriaQuery
            cq =
            this.getEntityManager()
                .getCriteriaBuilder()
                .createQuery();
        cq.select(cq.from(this.getPersistentClass()));
        final Query
            q =
            this.getEntityManager()
                .createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);

        return q.getResultList();
    }

    /**
     * Count all available entities in db.
     * @return a number of all available entites in db.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public int count() {
        final CriteriaQuery
            cq =
            this.getEntityManager()
                .getCriteriaBuilder()
                .createQuery();
        final Root<T> rt = cq.from(this.getPersistentClass());
        cq.select(this.getEntityManager()
                      .getCriteriaBuilder()
                      .count(rt));
        final Query
            q =
            this.getEntityManager()
                .createQuery(cq);

        final Long result = (Long)q.getSingleResult();

        if (result == null) {
            return 0;
        } else {
            return ((Long)q.getSingleResult()).intValue();
        }
    }

    /**
     * Clear cache of entityManager.
     * @return a number of all available entites in db.
     */
    public void clearEntityManagerCache() {
        try {
            this.em.flush();
            this.em.clear();
        } catch (final PersistenceException persistenceException) {
            final String errMessage = LOG_PREFIX + "clearEntityManagerCache - flush entityManager failed!";
            final AppRuntimeException appRuntimeException = new AppRuntimeException(errMessage, AppErrorCodeEnum.JPA_PERSISTENCE_10400);

            this.logger.error(appRuntimeException.stackTraceToString());

            throw appRuntimeException;
        }
    }
}
