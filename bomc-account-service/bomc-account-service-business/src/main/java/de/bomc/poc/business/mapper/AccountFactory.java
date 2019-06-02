package de.bomc.poc.business.mapper;

import de.bomc.poc.model.account.Account;

/**
 * <pre>
 * A factory for creating a <code>Account</code> instance.
 * This is necessary, because the default constructor of Account is not visible.
 * So a instance with the given constructor has to be created. 
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
public class AccountFactory {

	private static final String DEFAULT_FACTORY_ACCOUNT_NAME = "DEFAULT_FACTORY_ACCOUNT_NAME";
	
	public static Account createAccount() {
		
		final Account account = new Account(DEFAULT_FACTORY_ACCOUNT_NAME);
		
		return account;
	}
}
