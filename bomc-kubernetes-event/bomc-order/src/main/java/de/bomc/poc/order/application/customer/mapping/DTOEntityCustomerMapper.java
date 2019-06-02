/**
 * Project: bomc-onion-architecture
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
package de.bomc.poc.order.application.customer.mapping;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import de.bomc.poc.order.application.customer.dto.CustomerDTO;
import de.bomc.poc.order.domain.model.customer.CustomerEntity;

/**
 * This mapper maps to/from {@link CustomerEntity} to/from {@link CustomerDTO}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Mapper
public interface DTOEntityCustomerMapper {
    DTOEntityCustomerMapper INSTANCE = Mappers.getMapper(DTOEntityCustomerMapper.class);

    // _______________________________________________________________________
    // Mapping for CustomerEntity to CustomerDTO.
    // -----------------------------------------------------------------------
    @Mappings({ 
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "firstname", source = "firstname"),
            @Mapping(target = "username", source = "username") })
    CustomerDTO mapEntityToDTO(CustomerEntity customerEntity);
    
    //
    // Map from to list
    List<CustomerDTO> mapEntityToDTO(List<CustomerEntity> customerEntity);
    
    // _______________________________________________________________________
    // Mapping for CustomerDTO to CustomerEntity.
    // -----------------------------------------------------------------------
    @Mappings({ 
            @Mapping(target = "version", ignore = true), 
            @Mapping(target = "createUser", ignore = true), 
            @Mapping(target = "createDateTime", ignore = true), 
            @Mapping(target = "modifyUser", ignore = true), 
            @Mapping(target = "modifyDateTime", ignore = true), 
            @Mapping(target = "id", ignore = true), 
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "firstname", source = "firstname"),
            @Mapping(target = "username", source = "username") })
    CustomerEntity mapDTOToEntity(CustomerDTO customerDTO);
}
