package de.bomc.poc.model.mock;

import javax.persistence.EntityManager;

import de.bomc.poc.model.test.EntityManagerProvider;

/**
 * <pre>
 * A pattern for creating test instances by the object mother pattern. This
 * class uses the template method pattern in order to coordinate the
 * provisioning of instances. This class tries to load a matching instance from
 * db., if this doesn't, create it, configure all attributes of the instance,
 * store it in the db and return it. The loading, creating and configuring needs
 * to get implemented in subclasses.
 *
 * http://martinfowler.com/bliki/ObjectMother.html
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 10.08.2016
 */
public abstract class ObjectMother<T> {

    private final EntityManagerProvider entityManagerProvider;

    /**
     * Creates a new instance of <code>ObjectMother</code>.
     * @param entityManagerProvider the given <code>EnityManagerProvider</code>
     */
    public ObjectMother(final EntityManagerProvider entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    /**
     * Returns an instance based on the configuration of this object mother.
     */
    public T instance() {
        T t = this.loadInstance(this.entityManagerProvider);

        if (t == null) {
            t = this.createInstance();
        }

        this.configureInstance(t);

        final EntityManager entityManager = this.entityManagerProvider.getEntityManager();

        this.entityManagerProvider.tx()
                                  .begin();

        entityManager.persist(t);

        this.entityManagerProvider.tx()
                                  .commit();

        return t;
    }

    /**
     * Configure the instance <tt>t</tt> according to the configuration of this ObjectMother.
     * Add here attribute hardcoded, all attributes are the same along the test run.
     */
    protected abstract void configureInstance(T t);

    /**
     * Try to load an instance based on the alternate key.
     * @return null if no such instance exists
     */
    protected abstract T loadInstance(EntityManagerProvider emProvider);

    /**
     * Create a fresh instance with the alternate key set according to the configuration of this ObjectMother.
     */
    protected abstract T createInstance();
}
