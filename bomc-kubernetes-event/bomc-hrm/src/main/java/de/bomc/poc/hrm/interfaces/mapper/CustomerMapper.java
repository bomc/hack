/**
 * Project: POC PaaS
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.hrm.interfaces.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import de.bomc.poc.hrm.domain.model.CustomerEntity;

/**
 * A mapper that maps the {@link CustomerEntity} to {@link CustomerDto}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@Component
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CustomerMapper {

	@Mappings({ 
		@Mapping(target = "id", source = "customerEntity.id"),
		@Mapping(target = "emailAddress", source = "customerEntity.emailAddress"),
		@Mapping(target = "phoneNumber", source = "customerEntity.phoneNumber"),
		@Mapping(target = "firstName", source = "customerEntity.firstName"),
		@Mapping(target = "lastName", source = "customerEntity.lastName"),
		@Mapping(target = "dateOfBirth", source = "customerEntity.dateOfBirth", dateFormat = "yyyy-MM-dd"),
		@Mapping(target = "city", source = "customerEntity.city"),
		@Mapping(target = "postalCode", source = "customerEntity.postalCode"),
		@Mapping(target = "street", source = "customerEntity.street"),
		@Mapping(target = "houseNumber", source = "customerEntity.houseNumber"),
		@Mapping(target = "country", source = "customerEntity.country")
	})
	public abstract CustomerDto mapEntityToDto(CustomerEntity customerEntity);
	
	public abstract List<CustomerDto> mapEntitiesToDtos(List<CustomerEntity> customerEntityList);

	@Mappings({ 
		@Mapping(target = "id", source = "customerDto.id"),
		@Mapping(target = "emailAddress", source = "customerDto.emailAddress"),
		@Mapping(target = "phoneNumber", source = "customerDto.phoneNumber"),
		@Mapping(target = "firstName", source = "customerDto.firstName"),
		@Mapping(target = "lastName", source = "customerDto.lastName"),
		@Mapping(target = "dateOfBirth", source = "customerDto.dateOfBirth", dateFormat = "yyyy-MM-dd"),
		@Mapping(target = "city", source = "customerDto.city"),
		@Mapping(target = "postalCode", source = "customerDto.postalCode"),
		@Mapping(target = "street", source = "customerDto.street"),
		@Mapping(target = "houseNumber", source = "customerDto.houseNumber"),
		@Mapping(target = "country", source = "customerDto.country")
	})
	public abstract CustomerEntity mapDtoToEntity(CustomerDto customerDto);
	
}
