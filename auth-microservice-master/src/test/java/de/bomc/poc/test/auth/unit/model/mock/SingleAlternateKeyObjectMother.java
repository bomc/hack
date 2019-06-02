/**
 * Project: MY_POC
 * <p/>
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
 * <p/>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.auth.unit.model.mock;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.bomc.poc.test.auth.unit.EntityManagerProvider;

/**
 * This class helps creating a instance in db or load from it.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public abstract class SingleAlternateKeyObjectMother<T, A, S extends SingleAlternateKeyObjectMother<T, A, S>>
		extends ObjectMother<T> {

	private final Class<T> objectType;
	private final String alternateKeyName;
	private A alternateKey;

	/**
	 * Creates a new instance of <code>SingleAlternateKeyObjectMother</code>.
	 * 
	 * @param entityManagerProvider
	 *            helps to use the entitymanager.
	 * @param theObjectType
	 *            the class type.
	 * @param defaultAlternateKeyValue
	 *            the alternate key value.
	 * @param theAlternateKeyName
	 *            the key, corresponds to the attribute name in the entity.
	 */
	public SingleAlternateKeyObjectMother(final EntityManagerProvider entityManagerProvider, Class<T> theObjectType,
			A defaultAlternateKey, String theAlternateKeyName) {
		super(entityManagerProvider);

		objectType = theObjectType;
		alternateKeyName = theAlternateKeyName;
		alternateKey(defaultAlternateKey);
	}

	/**
	 * Load the instance by the given predicates from db.
	 */
	@SuppressWarnings("unchecked")
	@Override
	final protected T loadInstance(final EntityManagerProvider emProvider) {
		T entity = null;

		//
		// objectType == null means, only a instance should be created, without
		// trying first reading instance from db.
		if (this.objectType != null) {
			final EntityManager entityManager = emProvider.getEntityManager();
			final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			final CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery();
			final Root<?> from = criteriaQuery.from(objectType);

			final List<Predicate> predicateList = new ArrayList<>();
			predicateList.add(criteriaBuilder.equal(from.get(alternateKeyName), getAlternateKey()));

			criteriaQuery.select(from);
			criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));

			final TypedQuery<T> typedQuery = (TypedQuery<T>) entityManager.createQuery(criteriaQuery);

			try {
				entity = typedQuery.getSingleResult();
			} catch (NoResultException ex) {
				System.out.println("SingleAlternateKeyObjectMother#loadInstance [message=" + ex.getMessage() + "]");

				entity = null;
			}
		}

		return entity;
	}

	@SuppressWarnings("unchecked")
	public S alternateKey(A theAlternateKey) {
		alternateKey = theAlternateKey;
		return (S) this;
	}

	public A getAlternateKey() {
		return alternateKey;
	}
}
