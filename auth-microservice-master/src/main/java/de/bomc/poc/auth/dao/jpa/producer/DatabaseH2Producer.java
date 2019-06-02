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

import de.bomc.poc.auth.dao.jpa.qualifier.H2Database;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * A producer for a H2-<code>EntityManager</code>.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
public class DatabaseH2Producer {

    public static final String UNIT_NAME = "poc-auth-h2-pu";
    @Produces
    @PersistenceContext(unitName = DatabaseH2Producer.UNIT_NAME)
    @H2Database
    private EntityManager entityManager;
}
