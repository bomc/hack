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

import java.util.Set;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import de.bomc.poc.order.application.order.dto.OrderLineDTO;
import de.bomc.poc.order.domain.model.order.OrderLineEntity;

/**
 * This mapper maps to/from {@link OrderLineEntity} to/from {@link OrderLineDTO}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Mapper
@DecoratedWith(OrderLineItemDecorator.class)
public interface DTOEntityOrderLineMapper {

    DTOEntityOrderLineMapper INSTANCE = Mappers.getMapper(DTOEntityOrderLineMapper.class);
    
    // _______________________________________________________________________
    // Mapping for OrderLineEntity to OrderLineDTO.
    // -----------------------------------------------------------------------
    @Mappings({ 
        @Mapping(target = "quantity", source = "quantity")
     })
    OrderLineDTO mapEntityToDTO(OrderLineEntity orderLineEntity);
    
    Set<OrderLineDTO> mapEntityToDTO(Set<OrderLineEntity> orderLineEntityList);
}
