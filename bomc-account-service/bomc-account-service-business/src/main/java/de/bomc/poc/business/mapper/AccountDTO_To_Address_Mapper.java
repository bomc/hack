package de.bomc.poc.business.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import de.bomc.poc.api.dto.AccountDTO;
import de.bomc.poc.model.account.Address;

/**
 * Maps the account data of the {@link AccountDTO} to the {@link Address}
 * entity.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 21.08.2016
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountDTO_To_Address_Mapper {

	final AccountDTO_To_Address_Mapper INSTANCE = Mappers.getMapper(AccountDTO_To_Address_Mapper.class);

	// _______________________________________________________________________
	// Mapping for AccountDTO to Address.
	// -----------------------------------------------------------------------
	@Mappings({ 
			@Mapping(target = "id", source = "address_id"), 
			@Mapping(target = "city", source = "address_city"),
			@Mapping(target = "country", source = "address_country"),
			@Mapping(target = "street", source = "address_street"),
			@Mapping(target = "zipCode", source = "address_zipCode") 
	})
	Address map_AccountDTO_To_Address(AccountDTO accountDTO);
	
	/**
	 * <pre>
	 * Updates the <code>AccountDTO</code> by the given objects.
	 * </pre>
	 * 
	 * @param accountDTO
	 *            account data to update the account.
	 * @param address
	 *            the instance to update.
	 */
	@InheritConfiguration(name = "map_AccountDTO_To_Address")
	void map_AccountDTO_Into_Address(AccountDTO accountDTO, @MappingTarget Address address);
}
