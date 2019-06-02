package de.bomc.poc.dao.jpa.impl;

import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import de.bomc.poc.dao.generic.impl.AbstractJpaDao;
import de.bomc.poc.dao.generic.qualifier.JpaDao;
import de.bomc.poc.dao.jpa.JpaAccountUserDao;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.model.account.AccountUser;

/**
 * <pre>
 * A JpaAccountDaoImpl is an extension of the {@link AbstractJpaDao} about functionality regarding {@link AccountUser}s.
 * All methods have to be invoked within an active transaction context.
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
@JpaDao
public class JpaAccountUserDaoImpl extends AbstractJpaDao<AccountUser> implements JpaAccountUserDao {

	private static final String LOGGER_PREFIX = "JpaAccountUserDaoImpl#";
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
    protected Class<AccountUser> getPersistentClass() {
        return AccountUser.class;
    }
    
    @Override
    public Optional<AccountUser> findAccountUserWithAccountAndUserDataByAccountId(final Long accountId) {
    	this.logger.debug(LOGGER_PREFIX + "findAccountUserWithAccountAndUserDataByAccountId [accountId=" + accountId + "]");
    	
    	final TypedQuery<AccountUser> typedQuery = this.getEntityManager().createNamedQuery(AccountUser.NQ_FIND_ACCOUNT_USER_WITH_ACCOUNT_AND_USER_DATA_BY_ACCOUNT_ID, AccountUser.class);
    	typedQuery.setParameter(1, accountId);
    	final AccountUser accountUser = typedQuery.getSingleResult();
    	
    	return Optional.ofNullable(accountUser);
    }
}
