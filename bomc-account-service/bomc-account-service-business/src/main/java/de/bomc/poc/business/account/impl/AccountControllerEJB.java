package de.bomc.poc.business.account.impl;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.bomc.poc.api.dto.AccountDTO;
import de.bomc.poc.business.account.AccountController;
import de.bomc.poc.business.mapper.AccountDTO_To_Account_Mapper;
import de.bomc.poc.business.mapper.AccountDTO_To_Address_Mapper;
import de.bomc.poc.business.mapper.AccountDTO_To_User_Mapper;
import de.bomc.poc.dao.generic.qualifier.JpaDao;
import de.bomc.poc.dao.jpa.JpaAccountDao;
import de.bomc.poc.dao.jpa.JpaAccountUserDao;
import de.bomc.poc.dao.jpa.JpaUserDao;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.model.account.Account;
import de.bomc.poc.model.account.AccountUser;
import de.bomc.poc.model.account.Address;
import de.bomc.poc.model.account.AuthorizationTypeEnum;
import de.bomc.poc.model.account.User;

/**
 * An JpaAccountDao offers functionality regarding {@link Account} entity classes.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
@Stateless
public class AccountControllerEJB implements AccountController {

	private static final String LOG_PREFIX = "AccountControllerEJB#";
	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	@JpaDao
	private JpaUserDao jpaUserDao;
	@Inject
	@JpaDao
	private JpaAccountDao jpaAccountDao;
	@Inject
	@JpaDao
	private JpaAccountUserDao jpaAccountUserDao;
	
	@Override
	public Optional<Long> createAccount(final AccountDTO accountDTO) {
		this.logger.debug(LOG_PREFIX + "createAccount [accountDTO=" + accountDTO + "]");
		
		// Indicates the user is always the owner of the account.
		final Boolean IS_OWNER = Boolean.TRUE;
		
		final Account account = AccountDTO_To_Account_Mapper.INSTANCE.map_AccountDTO_To_Account(accountDTO);
		this.jpaAccountDao.persist(account);
		
		final User user = AccountDTO_To_User_Mapper.INSTANCE.map_AccountDTO_To_User(accountDTO);
		final Address address = AccountDTO_To_Address_Mapper.INSTANCE.map_AccountDTO_To_Address(accountDTO);
		user.addAddress(address);
		this.jpaUserDao.persist(user);
		
		final Set<AuthorizationTypeEnum> authTypes = new LinkedHashSet<>(Arrays.asList(AuthorizationTypeEnum.valueOf(accountDTO.getAccountUser_authType())));
		final AccountUser accountUser = new AccountUser(user, account, IS_OWNER, authTypes);
		this.jpaAccountUserDao.persist(accountUser);
		
		return Optional.ofNullable(account.getId());
	}

	@Override
	public AccountDTO getAccountById(Long accountId) {
		this.logger.debug(LOG_PREFIX + "getAccountById [accountId=" + accountId + "]");
		
		return null;
	}
	
	@Override
	public void updateAccount(final AccountDTO accountDTO) {
		this.logger.debug(LOG_PREFIX + "updateAccount [accountDTO=" + accountDTO + "]");
		
	}
	
	@Override
	public void removeAccount(final AccountDTO accountDTO) {
		this.logger.debug(LOG_PREFIX + "removeAccount [accountDTO=" + accountDTO + "]");
		
	}

	@Override
	public void addUserToAccount(final AccountDTO accountDTO) {
		this.logger.debug(LOG_PREFIX + "addUserToAccount [accountDTO=" + accountDTO + "]");
		
	}

	@Override
	public void removeUserFromAccount(final AccountDTO accountDTO) {
		this.logger.debug(LOG_PREFIX + "removeUserFromAccount [accountDTO=" + accountDTO + "]");
		
	}

	@Override
	public void getAllUsersReferencedByAccountId(final AccountDTO accountDTO) {
		this.logger.debug(LOG_PREFIX + "removeUserFromAccount [accountDTO=" + accountDTO + "]");
		
	}

	// _______________________________________________
	// Helper methods.
	// -----------------------------------------------
	
	
}
