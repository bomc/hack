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

import de.bomc.poc.hrm.domain.model.UserEntity;

/**
 * A mapper that maps the {@link UserEntity} to {@link UserDto}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {

	@Mappings({ 
		@Mapping(target = "id", source = "userEntity.id"),
		@Mapping(target = "username", source = "userEntity.username")
	})
	public abstract UserDto mapEntityToDto(UserEntity userEntity);
	
	public abstract List<UserDto> mapEntitiesToDtos(List<UserEntity> userEntityList);
	
	@Mappings({ 
		@Mapping(target = "id", source = "userDto.id"),
		@Mapping(target = "username", source = "userDto.username"),

	})
	public abstract UserEntity mapDtoToEntity(UserDto userDto);
	
}
