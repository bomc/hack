/**
 * Project: hrm
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
package de.bomc.poc.hrm.objmother;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bomc.poc.hrm.EntityManagerProvider;

/**
 * This class helps creating a instance in db or load from it.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */ 
public abstract class SingleAlternateKeyObjectMother<T, A, S extends SingleAlternateKeyObjectMother<T, A, S>>
		extends ObjectMother<T> {

	private static final String LOG_PREFIX = "SingleAlternateKeyObjectMother#";
	private static final Logger LOGGER = LoggerFactory.getLogger(SingleAlternateKeyObjectMother.class);
	
	// _______________________________________________
	// Member variables
	// -----------------------------------------------
	private final Class<T> objectType;
	private final String alternateKeyName;
	private A alternateKey;

	/**
	 * Creates a new instance of <code>SingleAlternateKeyObjectMother</code>.
	 * 
	 * @param entityManager
	 *            the given entityManagerProvider.
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
	@Override
	@SuppressWarnings("unchecked")
	final protected T loadInstance(final EntityManagerProvider entityManagerProvider) {
		T entity = null;

		//
		// objectType == null means, only a instance should be created, without
		// trying first reading instance from db.
		if (this.objectType != null) {
			final CriteriaBuilder criteriaBuilder = entityManagerProvider.getEntityManager().getCriteriaBuilder();
			final CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery();
			final Root<?> from = criteriaQuery.from(objectType);

			final List<Predicate> predicateList = new ArrayList<>();
			predicateList.add(criteriaBuilder.equal(from.get(alternateKeyName), getAlternateKey()));

			criteriaQuery.select(from);
			criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));

			final TypedQuery<T> typedQuery = (TypedQuery<T>) entityManagerProvider.getEntityManager().createQuery(criteriaQuery);

			try {
				entity = typedQuery.getSingleResult();
			} catch (final NoResultException ex) {
				LOGGER.error(LOG_PREFIX + "SingleAlternateKeyObjectMother#loadInstance [message=" + ex.getMessage() + "]");

				entity = null;
			}
		}

		return entity;
	}

	@SuppressWarnings("unchecked")
	public S alternateKey(final A theAlternateKey) {
		alternateKey = theAlternateKey;
		
		return (S) this;
	}

	public A getAlternateKey() {
		return alternateKey;
	}
}
