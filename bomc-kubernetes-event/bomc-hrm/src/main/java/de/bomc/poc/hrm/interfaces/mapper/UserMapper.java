/**
 * Project: hrm
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

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import de.bomc.poc.hrm.domain.model.UserEntity;

/**
 * A mapper that maps the {@link UserEntity} to {@link UserDto}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

	@Mappings({ 
		@Mapping(target = "id", source = "id"),
		@Mapping(target = "username", source = "username"),
		@Mapping(target = "password", source = "password"),
		@Mapping(target = "fullname", source = "fullname"),
		@Mapping(target = "comment", source = "userDetails.comment"),
		@Mapping(target = "phoneNo", source = "userDetails.phoneNo"),
		@Mapping(target = "image", source = "userDetails.image"),
		@Mapping(target = "sex", source = "userDetails.sex")
	})
	UserDto mapEntityToDto(UserEntity userEntity);
	List<UserDto> mapEntitiesToDtos(List<UserEntity> userEntityList);
	
	@InheritInverseConfiguration
	UserEntity mapDtoToEntity(UserDto userDto);
	
	@InheritInverseConfiguration
	List<UserEntity> mapDtosToEntities(List<UserDto> userDtoList);
}
