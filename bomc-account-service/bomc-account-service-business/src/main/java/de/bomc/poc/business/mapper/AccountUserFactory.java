package de.bomc.poc.business.mapper;

import java.util.Arrays;
import java.util.LinkedHashSet;

import de.bomc.poc.model.account.AccountUser;
import de.bomc.poc.model.account.AuthorizationTypeEnum;

/**
 * <pre>
 * A factory for creating a <code>AccountUser</code> instance.
 * This is necessary, because the default constructor of AccountUser is not visible.
 * So a instance with the given constructor has to be created. 
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
public class AccountUserFactory {

	private static final Boolean DEFAULT_OWNER_FLAG = Boolean.TRUE;

	public AccountUser createAccountUser() {

		final AccountUser accountUser = new AccountUser(UserFactory.createUser(), AccountFactory.createAccount(),
				DEFAULT_OWNER_FLAG, new LinkedHashSet<AuthorizationTypeEnum>(Arrays.asList(AuthorizationTypeEnum.UNKNOWN)));

		return accountUser;
	}
}
