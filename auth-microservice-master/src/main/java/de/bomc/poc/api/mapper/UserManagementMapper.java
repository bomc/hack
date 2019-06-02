/**
 * Project: MY_POC
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.api.mapper;

import de.bomc.poc.api.mapper.transfer.GrantDTO;
import de.bomc.poc.api.mapper.transfer.RoleDTO;
import de.bomc.poc.api.mapper.transfer.UserDTO;
import de.bomc.poc.api.mapper.transfer.UserManagementDTO;
import de.bomc.poc.auth.model.usermanagement.Grant;
import de.bomc.poc.auth.model.usermanagement.Role;
import de.bomc.poc.auth.model.usermanagement.SecurityObject;
import de.bomc.poc.auth.model.usermanagement.User;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * <pre>
 * A mapper class for mapping user management data with Mapstruct.
 *
 * NOTE:
 * Damit eine Klasse fuer <code>InheritInverseConfiguration</code> verwendet werden kann,
 * muss diese einen public-Default-Konstruktor zur Verfuegung stellen. Sowie alle Attribute dieser Klasse,
 * die 'gemappt' werden sollen, muessen die setter- und getter Methoden zur Verfuegung stellen.
 * </pre>
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@Mapper(
    uses = {
        LocalDateTimeMapper.class, UserMappingFactory.class, RoleMappingFactory.class, GrantMappingFactory.class
    }
)
public interface UserManagementMapper {

    UserManagementMapper INSTANCE = Mappers.getMapper(UserManagementMapper.class);

    // _______________________________________________________________________
    // -----------------------------------------------------------------------
    // Mapping for User, UserDTO.
    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "username", target = "username"),
        @Mapping(source = "expirationDate", target = "expirationDate")
    })
    UserDTO map_User_To_DTO(User user);
    // Mapping for User to UserDTO collection.
    List<UserDTO> map_UserList_To_DTOList(List<User> userList);


    // _______________________________________________________________________
    // -----------------------------------------------------------------------
    // 'mapstruct' uses the default constructor for instance creation, but the default constructor is 'protected' and mapstruct is not using reflection.
    // So a factory class 'see de.bomc.poc.api.mapper.UserMappingFactory.class' is necessary to get this running.
    // &#064;InheritInverseConfiguration("map_User_To_DTO") could not be used here, because UserDTO extends not a super class. User extends AbstractEntity.
    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "expirationDate", target = "expirationDate"),
        @Mapping(source = "username", target = "username"),
        @Mapping(target = "locked", ignore = true),
        @Mapping(target = "externalUser", ignore = true),
        @Mapping(target = "roles", ignore = true),
        @Mapping(target = "enabled", ignore = true),
        @Mapping(target = "fullname", ignore = true),
        @Mapping(target = "userDetails", ignore = true),
        @Mapping(target = "newPassword", ignore = true),
        @Mapping(target = "grants", ignore = true),
        @Mapping(target = "passwords", ignore = true)
    })
    User map_DTO_To_User(UserDTO userDTO);
    // Mapping for UserDTO to User collection.
    List<User> map_UserDTOList_To_List(List<UserDTO> userDTOList);
    
    
    // _______________________________________________________________________
    // -----------------------------------------------------------------------
    // Mapping for Role, RoleDTO.
    @Mappings({
        @Mapping(source = "name", target = "name"),
        @Mapping(source = "description", target = "description"),
        @Mapping(source = "grants", target = "grantDTOList")
    })
    RoleDTO map_Role_To_DTO(Role role);
    // Mapping for Role to RoleDTO collection.
    List<RoleDTO> map_RoleList_To_DTOList(List<Role> roleList);

    
    // _______________________________________________________________________
    // -----------------------------------------------------------------------
    // 'mapstruct' uses the default constructor for instance creation, but the default constructor is 'protected' and mapstruct is not using reflection.
    // So a factory class 'see de.bomc.poc.api.mapper.RoleMappingFactory.class' is necessary to get this running.
    // &#064;InheritInverseConfiguration("map_Role_To_DTO") could not be used here, because RoleDTO extends not a super class. Role extends AbstractEntity.
    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "name", target = "name"),
        @Mapping(source = "description", target = "description"),
        @Mapping(source = "grantDTOList", target = "grants"),
        @Mapping(target = "users", ignore = true)
    })
    Role map_DTO_To_Role(RoleDTO roleDTO);
    // Mapping for RoleDTO to Role collection.
    List<Role> map_RoleDTOList_To_List(List<RoleDTO> roleDTOList);

    
    // _______________________________________________________________________
    // -----------------------------------------------------------------------
    // Mapping for Grant, GrantDTO.
    @Mappings({
        @Mapping(source = "name", target = "name"),
        @Mapping(source = "description", target = "description")
    })
    GrantDTO map_Grant_To_DTO(SecurityObject grantObject);
    // Mapping for Grant to GrantDTO collection.
    List<GrantDTO> map_GrantList_To_DTOList(List<SecurityObject> grantList);


    // _______________________________________________________________________
    // -----------------------------------------------------------------------
    // 'mapstruct' uses the default constructor for instance creation, but the default constructor is 'protected' and mapstruct is not using reflection.
    // So a factory class 'see de.bomc.poc.api.mapper.GrantMappingFactory.class' is necessary to get this running.
    // &#064;InheritInverseConfiguration("map_Grant_To_DTO") could not be used here, because GrantDTO extends not a super class. Grant extends AbstractEntity.
    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "name", target = "name"),
        @Mapping(source = "description", target = "description")
    })
    Grant map_DTO_To_Grant(GrantDTO grantDTO);
    // Mapping for GrantDTO to Grant collection.
    List<Grant> map_GrantDTOList_To_List(List<GrantDTO> grantDTOList);


    // _______________________________________________________________________
    // -----------------------------------------------------------------------
	@Mappings({
		@Mapping(source = "user", target = "userDTO"),
		@Mapping(source = "expirationDate", target = "userExpirationDate"),
		@Mapping(source = "roles", target = "roleListDTO")
	})
	UserManagementDTO map_User_To_UserManagementDTO(User user);
}
