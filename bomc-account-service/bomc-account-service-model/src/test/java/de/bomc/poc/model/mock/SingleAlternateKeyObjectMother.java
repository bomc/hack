package de.bomc.poc.model.mock;

import org.apache.log4j.Logger;

import de.bomc.poc.model.test.EntityManagerProvider;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * This class helps creating a instance in db or load from it.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public abstract class SingleAlternateKeyObjectMother<T, A, S extends SingleAlternateKeyObjectMother<T, A, S>> extends ObjectMother<T> {
    private static final Logger LOGGER = Logger.getLogger(SingleAlternateKeyObjectMother.class);
    private static final String LOG_PREFIX = "SingleAlternateKeyObjectMother#";

    private final Class<T> objectType;
    private final String alternateKeyName;
    private A alternateKey;

    /**
     * Creates a new instance of <code>SingleAlternateKeyObjectMother</code>.
     * @param entityManagerProvider helps to use the entitymanager.
     * @param theObjectType         the class type.
     * @param defaultAlternateKey   the alternate key value.
     * @param theAlternateKeyName   the key, corresponds to the attribute name in the entity.
     */
    protected SingleAlternateKeyObjectMother(final EntityManagerProvider entityManagerProvider, final Class<T> theObjectType, final A defaultAlternateKey, final String theAlternateKeyName) {
        super(entityManagerProvider);

        this.objectType = theObjectType;
        this.alternateKeyName = theAlternateKeyName;
        this.alternateKey(defaultAlternateKey);
    }

    /**
     * Load the instance by the given predicates from db.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected final T loadInstance(final EntityManagerProvider emProvider) {
        T entity = null;

        //
        // objectType == null means, only a instance should be created, without
        // trying first reading instance from db.
        if (this.objectType != null) {
            final EntityManager entityManager = emProvider.getEntityManager();
            final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery();
            final Root<?> from = criteriaQuery.from(this.objectType);

            final List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(criteriaBuilder.equal(from.get(this.alternateKeyName), this.getAlternateKey()));

            criteriaQuery.select(from);
            criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));

            final TypedQuery<T> typedQuery = (TypedQuery<T>)entityManager.createQuery(criteriaQuery);

            try {
                entity = typedQuery.getSingleResult();
            } catch (final NoResultException ex) {
                LOGGER.info(LOG_PREFIX + "loadInstance - entity has to be created. [message=" + ex.getMessage() + "]");

                entity = null;
            }
        }

        return entity;
    }

    @SuppressWarnings("unchecked")
    public S alternateKey(final A theAlternateKey) {
        this.alternateKey = theAlternateKey;
        return (S)this;
    }

    protected A getAlternateKey() {
        return this.alternateKey;
    }
}
