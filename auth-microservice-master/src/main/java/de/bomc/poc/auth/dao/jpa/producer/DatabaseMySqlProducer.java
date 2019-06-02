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
 * Copyright (c): BOMC, 2015
 */
package de.bomc.poc.auth.dao.jpa.producer;

import de.bomc.poc.auth.dao.jpa.qualifier.MySQLDatabase;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * A producer for a MySql-<code>EntityManager</code>.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
public class DatabaseMySqlProducer {

    public static final String UNIT_NAME = "poc-auth-mysql-pu";
    @Produces
    @PersistenceContext(unitName = DatabaseMySqlProducer.UNIT_NAME)
    @MySQLDatabase
    private EntityManager entityManager;
}
