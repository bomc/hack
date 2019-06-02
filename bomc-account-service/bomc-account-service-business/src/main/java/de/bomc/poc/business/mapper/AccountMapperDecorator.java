package de.bomc.poc.business.mapper;

import de.bomc.poc.api.dto.AccountDTO;
import de.bomc.poc.model.account.Account;
import de.bomc.poc.model.account.AccountUser;
import de.bomc.poc.model.account.Address;
import de.bomc.poc.model.account.AuthorizationTypeEnum;
import de.bomc.poc.model.account.User;

/**
 * Customize the {@link AccountDTO}, for mapping the
 * {@link AuthorizationTypeEnum} to the 'accountUser_authType' attribute.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
public abstract class AccountMapperDecorator implements Account_To_AccountDTO_Mapper {
	private final Account_To_AccountDTO_Mapper delegate;

	public AccountMapperDecorator(final Account_To_AccountDTO_Mapper delegate) {
		this.delegate = delegate;
	}

	@Override
	public AccountDTO map_Account_To_AccountDTO(final Account account, final AccountUser accountUser, final User user,
			final Address address) {

		final AccountDTO accountDTO = delegate.map_Account_To_AccountDTO(account, accountUser, user, address);

		if (accountUser != null && accountUser.getAuthTypes().size() > 0) {
			final AuthorizationTypeEnum authorizationTypeEnum = accountUser.getAuthTypes().iterator().next();
			accountDTO.setAccountUser_authType(authorizationTypeEnum.name());
		}

		return accountDTO;
	}

	@Override
	public void map_Account_Into_AccountDTO(final Account account, final AccountUser accountUser, final User user,
			final Address address, final AccountDTO accountDTO) {
		delegate.map_Account_Into_AccountDTO(account, accountUser, user, address, accountDTO);

		if (accountUser != null && accountUser.getAuthTypes().size() > 0) {
			final AuthorizationTypeEnum authorizationTypeEnum = accountUser.getAuthTypes().iterator().next();
			accountDTO.setAccountUser_authType(authorizationTypeEnum.name());
		}
	}
}
