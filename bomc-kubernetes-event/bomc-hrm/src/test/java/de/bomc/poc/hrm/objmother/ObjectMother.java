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

import de.bomc.poc.hrm.EntityManagerProvider;

/**
 * A pattern for creating test instances by the object mother pattern. This
 * class uses the template method pattern in order to coordinate the
 * provisioning of instances. This class tries to load a matching instance from
 * db., if this doesn't, create it, configure all attributes of the instance,
 * store it in the db and return it. The loading, creating and configuring needs
 * to get implemented in subclasses.
 * <p/>
 * http://martinfowler.com/bliki/ObjectMother.html
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public abstract class ObjectMother<T> {

	private EntityManagerProvider entityManagerProvider;

	/**
	 * Creates a new instance of <code>ObjectMother</code>.
	 * 
	 * @param entityManagerProvider
	 *            the given <code>EntityManagerProvider</code>
	 */
	public ObjectMother(final EntityManagerProvider entityManagerProvider) {
		this.entityManagerProvider = entityManagerProvider;
	}

	/**
	 * Returns an instance based on the configuration of this object mother.
	 */
	public T instance() {
		T t = loadInstance(this.entityManagerProvider);

		if (t == null) {
			t = createInstance();
		}

		configureInstance(t);

		this.entityManagerProvider.tx().begin();
		
		this.entityManagerProvider.getEntityManager().persist(t);
		
		this.entityManagerProvider.tx().commit();

		return t;
	}

	/**
	 * Configure the instance <tt>t</tt> according to the configuration of this
	 * ObjectMother.
	 */
	abstract protected void configureInstance(T t);

	/**
	 * Try to load an instance based on the alternate key.
	 *
	 * @return null if no such instance exists
	 */
	abstract protected T loadInstance(EntityManagerProvider entityManagerProvider);

	/**
	 * Create a fresh instance with the alternate key set according to the
	 * configuration of this ObjectMother.
	 */
	abstract protected T createInstance();
}
