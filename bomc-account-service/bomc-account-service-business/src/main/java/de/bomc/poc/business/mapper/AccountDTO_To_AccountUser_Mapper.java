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
import de.bomc.poc.model.account.AccountUser;

/**
 * Maps the account data of the {@link AccountDTO} to the {@link AccountUser} entity.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
@Mapper(uses = { AccountUserFactory.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
@DecoratedWith(AccountUserMapperDecorator.class)
public interface AccountDTO_To_AccountUser_Mapper {

	final AccountDTO_To_AccountUser_Mapper INSTANCE = Mappers.getMapper(AccountDTO_To_AccountUser_Mapper.class);	
	
    // _______________________________________________________________________
    // Mapping for Account to AccountDTO.
    // -----------------------------------------------------------------------
    @Mappings({
        @Mapping(target = "ownerFlag", source = "accountUser_ownerFlag")
    })
    AccountUser map_AccountDTO_To_AccountUser(AccountDTO accountDTO);
    
	/**
	 * <pre>
	 * Updates the <code>AccountDTO</code> by the given objects.
	 * </pre>
	 * 
	 * @param accountDTO
	 *            account data to update the account.
	 * @param accountUser
	 *            the instance to update.
	 */
	@InheritConfiguration(name = "map_AccountDTO_To_AccountUser")
	void map_AccountDTO_Into_AccountUser(AccountDTO accountDTO, @MappingTarget AccountUser accountUser);
}
