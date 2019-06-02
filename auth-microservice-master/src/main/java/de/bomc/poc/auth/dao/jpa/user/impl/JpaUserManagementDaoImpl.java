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
package de.bomc.poc.auth.dao.jpa.user.impl;

import de.bomc.poc.auth.dao.jpa.generic.impl.AbstractJpaDao;
import de.bomc.poc.auth.dao.jpa.qualifier.JpaDao;
import de.bomc.poc.auth.dao.jpa.user.JpaUserManagementDao;
import de.bomc.poc.auth.model.usermanagement.SystemUser;
import de.bomc.poc.auth.model.usermanagement.User;
import de.bomc.poc.auth.model.usermanagement.UserPassword;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * An JpaUserManagementDaoImpl is an extension of a {@link AbstractJpaDao} about functionality regarding {@link User}s.
 * <p> All methods have to be invoked within an active transaction context. </p>
 * </pre>
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@JpaDao
public class JpaUserManagementDaoImpl extends AbstractJpaDao<User> implements JpaUserManagementDao {

    private static final String LOGGER_PREFIX = "userManagementDao";
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
    protected Class<User> getPersistentClass() {
        return User.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findByNameAndPassword(UserPassword userPassword) {
        logger.debug("JpaUserManagementDaoImpl#findByNameAndPassword [userPassword=" + userPassword.toString() + "]");

        Map<String, String> params = new HashMap<>();
        params.put("username", userPassword.getUser()
                                           .getUsername());
        params.put("password", userPassword.getPassword());

        List<User> users = super.findByNamedParameters(User.NQ_FIND_BY_USERNAME_PASSWORD, params);

        if (users == null || users.isEmpty()) {
            return null;
        }

        return users.get(0);
    }

    @SuppressWarnings("unused")
	private boolean isSuperUser(User user) {
        return (user == null || user instanceof SystemUser || SystemUser.SYSTEM_USERNAME.equals(user.getFullname()));
    }
}
