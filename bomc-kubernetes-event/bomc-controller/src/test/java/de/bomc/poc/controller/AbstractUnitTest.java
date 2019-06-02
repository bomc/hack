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
package de.bomc.poc.controller;

import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import javax.persistence.EntityManager;

/**
 * A base class for all unit test classes.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
public abstract class AbstractUnitTest {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(AbstractUnitTest.class);
    @SuppressWarnings("unused")
    private static final String LOG_PREFIX = "AbstractUnitTest#";
    // Read this from 'test/resources/META-INF/persistence.xml',
    // 'persistence-unit name'.
    // NOTE@MVN: unit name.
    private static final String PERSISTENCE_UNIT_NAME = "bomc-h2-pu";
    @Rule
    public final EntityManagerProvider emProvider = EntityManagerProvider.persistenceUnit(PERSISTENCE_UNIT_NAME);
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    protected EntityManager entityManager;
    //
    // Constants

    // _______________________________________________
    // Helper methods.
    // -----------------------------------------------
}
