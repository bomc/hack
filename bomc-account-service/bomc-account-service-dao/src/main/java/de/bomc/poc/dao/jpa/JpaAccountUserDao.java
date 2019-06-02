package de.bomc.poc.dao.jpa;

import java.util.Optional;

import de.bomc.poc.dao.generic.JpaGenericDao;
import de.bomc.poc.model.account.AccountUser;

/**
 * An JpaAccountUserDao offers functionality regarding {@link AccountUser}
 * entity classes.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
public interface JpaAccountUserDao extends JpaGenericDao<AccountUser> {

	// Define here methods for functionality from db for AccountUser entity.
	// ...

	/**
	 * Find the owner of the relationship <code>Person</code> and
	 * <code>Account</code> by the given account id.
	 * 
	 * @param accountId
	 *            the given account id.
	 * @return the owner of the relationship user and account. 
	 */
	Optional<AccountUser> findAccountUserWithAccountAndUserDataByAccountId(Long accountId);
}
