package de.bomc.poc.business.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import de.bomc.poc.api.dto.AccountDTO;
import de.bomc.poc.model.account.User;

/**
 * Maps the account data of the {@link AccountDTO} to the {@link Person} entity.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
@Mapper(uses = { UserFactory.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountDTO_To_User_Mapper {
	
	final AccountDTO_To_User_Mapper INSTANCE = Mappers.getMapper(AccountDTO_To_User_Mapper.class);	
	
    // _______________________________________________________________________
    // Mapping for AccountDTO to Person.
    // -----------------------------------------------------------------------
    @Mappings({
        @Mapping(target = "id", source = "user_id"),
        @Mapping(target = "username", source = "user_username"),
//        @Mapping(target = "companyId", source = "legalUser_companyId"),
//        @Mapping(target = "birthDate", source = "naturalUser_birthDate")
    })
    User map_AccountDTO_To_User(AccountDTO accountDTO);
    
	/**
	 * <pre>
	 * Updates the <code>AccountDTO</code> by the given objects.
	 * </pre>
	 * 
	 * @param accountDTO
	 *            account data to update the account.
	 * @param user
	 *            the instance to update.
	 */
	@InheritConfiguration(name = "map_AccountDTO_To_User")
	void map_AccountDTO_Into_User(AccountDTO accountDTO, @MappingTarget User user);
}
