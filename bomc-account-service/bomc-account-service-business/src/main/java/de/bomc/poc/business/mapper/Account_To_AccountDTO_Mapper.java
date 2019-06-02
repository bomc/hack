package de.bomc.poc.business.mapper;

import org.mapstruct.DecoratedWith;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import de.bomc.poc.api.dto.AccountDTO;
import de.bomc.poc.model.account.Account;
import de.bomc.poc.model.account.AccountUser;
import de.bomc.poc.model.account.Address;
import de.bomc.poc.model.account.User;

/**
 * Maps the account data of the {@link AccountDTO} to the {@link Account},
 * {@link Person} and {@link Address} entity.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
@DecoratedWith(AccountMapperDecorator.class)
public interface Account_To_AccountDTO_Mapper {

	final Account_To_AccountDTO_Mapper INSTANCE = Mappers.getMapper(Account_To_AccountDTO_Mapper.class);

	// _______________________________________________________________________
	// Mapping for Account to AccountDTO.
	// -----------------------------------------------------------------------
	@Mappings({ 
			@Mapping(target = "account_id", source = "account.id"),
			@Mapping(target = "account_name", source = "account.name"),
			@Mapping(target = "user_id", source = "user.id"),
			@Mapping(target = "user_username", source = "user.username"),
			// @Mapping(target = "legalUser_companyId", source = "legalUser.companyId"),
			// @Mapping(target = "naturalUser_birthDate", source = "naturalUser.birthDate"),
			@Mapping(target = "accountUser_ownerFlag", source = "accountUser.ownerFlag"),
			// NOTE: Is set by the decorator.
			// @Mapping(target = "accountUser_authType", source = "accountUser.authTypes"),
			@Mapping(target = "address_id", source = "address.id"),
			@Mapping(target = "address_city", source = "address.city"),
			@Mapping(target = "address_country", source = "address.country"),
			@Mapping(target = "address_street", source = "address.street"),
			@Mapping(target = "address_zipCode", source = "address.zipCode") })
	AccountDTO map_Account_To_AccountDTO(Account account, AccountUser accountUser, User user, Address address);

	/**
	 * <pre>
	 * Updates the <code>AccountDTO</code> by the given objects.
	 * NOTE: During updating the accountDTO, it is allowed source objects could be null.
	 * </pre>
	 * 
	 * @param account
	 *            account data to update the accountDTO.
	 * @param accountUser
	 *            account user data to update the accountDTO.
	 * @param user
	 *            user data to update the accountDTO.
	 * @param address
	 *            address data to update the accountDTO.
	 * @param accountDTO
	 *            the instance to update.
	 */
	@InheritConfiguration(name = "map_Account_To_AccountDTO")
	void map_Account_Into_AccountDTO(Account account, AccountUser accountUser, User user, Address address,
			@MappingTarget AccountDTO accountDTO);
}
