package de.bomc.poc.dao.jpa.impl;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.bomc.poc.dao.generic.impl.AbstractJpaDao;
import de.bomc.poc.dao.generic.qualifier.JpaDao;
import de.bomc.poc.dao.jpa.JpaUserDao;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.model.account.User;

/**
 * <pre>
 * A JpaAccountDaoImpl is an extension of the {@link AbstractJpaDao} about functionality regarding {@link Person}s.
 * All methods have to be invoked within an active transaction context.
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
@JpaDao
public class JpaUserDaoImpl extends AbstractJpaDao<User> implements JpaUserDao {

	private static final String LOGGER_PREFIX = "JpaUserDaoImpl#";
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
}
