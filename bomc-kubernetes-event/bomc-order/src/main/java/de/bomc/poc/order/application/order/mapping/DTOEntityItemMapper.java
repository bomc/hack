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
package de.bomc.poc.order.application.order.mapping;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import de.bomc.poc.order.application.order.dto.ItemDTO;
import de.bomc.poc.order.domain.model.item.ItemEntity;

/**
 * This mapper maps to/from {@link ItemEntity} to/from {@link ItemDTO}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Mapper
public interface DTOEntityItemMapper {

    DTOEntityItemMapper INSTANCE = Mappers.getMapper(DTOEntityItemMapper.class);

    // _______________________________________________________________________
    // Mapping for ItemEntity to ItemDTO.
    // -----------------------------------------------------------------------
    @Mappings({ 
        @Mapping(target = "name", source = "name"), 
        @Mapping(target = "price", source = "price") 
     })
    ItemDTO mapEntityToDTO(ItemEntity itemEntity);

    //
    // Map from to list
    List<ItemDTO> mapEntityListToDTOList(List<ItemEntity> itemEntity);

    // _______________________________________________________________________
    // Mapping for ItemDTO to ItemEntity.
    // -----------------------------------------------------------------------
    @Mappings({
            @Mapping(target = "version", ignore = true), 
            @Mapping(target = "createUser", ignore = true),
            @Mapping(target = "createDateTime", ignore = true), 
            @Mapping(target = "modifyUser", ignore = true),
            @Mapping(target = "modifyDateTime", ignore = true), 
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "name", source = "name"), 
            @Mapping(target = "price", source = "price") 
    })
    ItemEntity mapDTOToEntity(ItemDTO itemDTO);
    
    //
    // Map from to list
    List<ItemEntity> mapDTOListToEntityList(List<ItemDTO> itemDTO);
}