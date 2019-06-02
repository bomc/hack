#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: ${symbol_dollar}Author: bomc ${symbol_dollar}
 *
 *  date: ${symbol_dollar}Date: ${symbol_dollar}
 *
 *  revision: ${symbol_dollar}Revision: ${symbol_dollar}
 *
 * </pre>
 */
package ${package};

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnitUtil;

/**
 * Implements a test rule for using the entityManager,
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
public final class EntityManagerProvider implements TestRule {

    private final EntityManager entityManager;
    private final EntityTransaction tx;
    private final PersistenceUnitUtil persistenceUnitUtil;

    private EntityManagerProvider(final String unitName) {
        final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(unitName);
        this.entityManager = entityManagerFactory.createEntityManager();
        this.persistenceUnitUtil = entityManagerFactory.getPersistenceUnitUtil();
        this.tx = this.entityManager.getTransaction();
    }

    public static EntityManagerProvider persistenceUnit(final String unitName) {
        return new EntityManagerProvider(unitName);
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public PersistenceUnitUtil getPersistenceUnitUtil() {
        return this.persistenceUnitUtil;
    }

    public EntityTransaction tx() {
        return this.tx;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                base.evaluate();
                EntityManagerProvider.this.entityManager.clear();
            }
        };
    }
}
