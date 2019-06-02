package de.bomc.poc.business.mapper;

import java.util.HashSet;
import java.util.Set;

import de.bomc.poc.api.dto.AccountDTO;
import de.bomc.poc.model.account.AccountUser;
import de.bomc.poc.model.account.AuthorizationTypeEnum;

/**
 * Customize the {@link AccountUser}, that maps the {@link AuthorizationTypeEnum}
 * to the 'accountUser_authType' attribute.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
public abstract class AccountUserMapperDecorator implements AccountDTO_To_AccountUser_Mapper {
	private final AccountDTO_To_AccountUser_Mapper delegate;

	public AccountUserMapperDecorator(final AccountDTO_To_AccountUser_Mapper delegate) {
		this.delegate = delegate;
	}

	@Override
	public AccountUser map_AccountDTO_To_AccountUser(final AccountDTO accountDTO) {

		final AccountUser accountUser = delegate.map_AccountDTO_To_AccountUser(accountDTO);
		
		if(accountDTO.getAccountUser_authType() != null && !accountDTO.getAccountUser_authType().isEmpty()) {
			final AuthorizationTypeEnum authorizationTypeEnum = AuthorizationTypeEnum.valueOf(accountDTO.getAccountUser_authType());
			final Set<AuthorizationTypeEnum> authorizationTypeEnumSet = new HashSet<>(1);
			authorizationTypeEnumSet.add(authorizationTypeEnum);
			
			accountUser.setAuthTypes(authorizationTypeEnumSet);
		}
		
		return accountUser;
	}
	
	@Override
	public void map_AccountDTO_Into_AccountUser(AccountDTO accountDTO, AccountUser accountUser) {
		delegate.map_AccountDTO_To_AccountUser(accountDTO);

		if (accountUser != null && accountUser.getAuthTypes().size() > 0) {
			final AuthorizationTypeEnum authorizationTypeEnum = accountUser.getAuthTypes().iterator().next();
			accountDTO.setAccountUser_authType(authorizationTypeEnum.name());
		}
	}
}
