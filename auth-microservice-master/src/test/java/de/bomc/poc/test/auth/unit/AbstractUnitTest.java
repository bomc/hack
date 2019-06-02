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
package de.bomc.poc.test.auth.unit;

import javax.persistence.EntityManager;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * A abstract class for unit testing.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public abstract class AbstractUnitTest {

    public static final String PERSISTENCE_UNIT_NAME = "poc-auth-pu";
    @Rule
    public EntityManagerProvider emProvider1 = EntityManagerProvider.persistenceUnit(PERSISTENCE_UNIT_NAME);
    @Rule
    public EntityManagerProvider emProvider2 = EntityManagerProvider.persistenceUnit(PERSISTENCE_UNIT_NAME);
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    protected EntityManager entityManager1;
    protected EntityManager entityManager2;
    
    // ________________________________________________
    // Helper methods
    // ------------------------------------------------
}
