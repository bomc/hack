package de.bomc.poc.business.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import de.bomc.poc.api.dto.AccountDTO;
import de.bomc.poc.model.account.Account;

/**
 * Maps the account data of the {@link AccountDTO} to the {@link Account}
 * entity.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
@Mapper(uses = { AccountFactory.class, AccountUserFactory.class, UserFactory.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountDTO_To_Account_Mapper {

	final AccountDTO_To_Account_Mapper INSTANCE = Mappers.getMapper(AccountDTO_To_Account_Mapper.class);

	// _______________________________________________________________________
	// Mapping for Account to AccountDTO.
	// -----------------------------------------------------------------------
	@Mappings({ 
		@Mapping(target = "id", source = "account_id"), 
		@Mapping(target = "name", source = "account_name") 
	})
	Account map_AccountDTO_To_Account(AccountDTO accountDTO);
	
	/**
	 * <pre>
	 * Updates the <code>AccountDTO</code> by the given objects.
	 * </pre>
	 * 
	 * @param accountDTO
	 *            account data to update the account.
	 * @param account
	 *            the instance to update.
	 */
	@InheritConfiguration(name = "map_AccountDTO_To_Account")
	void map_AccountDTO_Into_Account(AccountDTO accountDTO, @MappingTarget Account account);
}
