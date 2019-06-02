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
package de.bomc.poc.auth.dao.jpa.security.impl;

import de.bomc.poc.auth.dao.jpa.generic.impl.AbstractJpaDao;
import de.bomc.poc.auth.dao.jpa.qualifier.JpaDao;
import de.bomc.poc.auth.dao.jpa.security.JpaGrantManagementDao;
import de.bomc.poc.auth.model.usermanagement.Grant;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import org.apache.log4j.Logger;

import javax.inject.Inject;

/**
 * <pre>
 * An JpaGrantManagementDaoImpl is an extension of a {@link AbstractJpaDao} about functionality regarding {@link Grant}s.
 * <p> All methods have to be invoked within an active transaction context. </p>
 * </pre>
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@JpaDao
public class JpaGrantManagementDaoImpl extends AbstractJpaDao<Grant> implements JpaGrantManagementDao {

    private static final String LOGGER_PREFIX = "grantDao";
    /**
     * Logger.
     */
    @Inject
    @LoggerQualifier(logPrefix = LOGGER_PREFIX)
    private Logger logger;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<Grant> getPersistentClass() {
        return Grant.class;
    }
}
