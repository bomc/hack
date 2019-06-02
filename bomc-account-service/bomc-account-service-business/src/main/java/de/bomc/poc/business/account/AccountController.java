package de.bomc.poc.business.account;

import java.util.Optional;

import de.bomc.poc.api.dto.AccountDTO;
import de.bomc.poc.model.account.Account;

/**
 * An JpaAccountDao offers functionality regarding {@link Account} entity
 * classes.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
public interface AccountController {

	/**
	 * Create a account with a user and its added address.
	 * 
	 * @param accountDTO
	 *            the account data for creation.
	 * @return the technical id of the created <code>Account</code> object.
	 */
	Optional<Long> createAccount(AccountDTO accountDTO);

	/**
	 * Update the account.
	 * 
	 * @param accountDTO
	 *            the given account data to update.
	 */
	void updateAccount(AccountDTO accountDTO);

	/**
	 * Remove the account with given id. By removing the account means also the
	 * user and its added address will be removed.
	 * 
	 * @param accountDTO
	 *            contains the given id.
	 */
	void removeAccount(AccountDTO accountDTO);

	/**
	 * Return the account by the given account id.
	 * 
	 * @param accountId
	 *            the given account id.
	 * @return a accountDTO with relevant account data from the owner of this account.
	 */
	AccountDTO getAccountById(Long accountId);

	/**
	 * Add a user to the given account.
	 * 
	 * @param accountDTO
	 *            contains the user and account data.
	 */
	void addUserToAccount(AccountDTO accountDTO);

	/**
	 * Remove a user from account.
	 * 
	 * @param accountDTO
	 *            contains the user and account data.
	 */
	void removeUserFromAccount(AccountDTO accountDTO);

	/**
	 * Get all user referenced by the given account id.
	 * 
	 * @param accountDTO
	 *            contains the user and account data.
	 */
	void getAllUsersReferencedByAccountId(AccountDTO accountDTO);

}
