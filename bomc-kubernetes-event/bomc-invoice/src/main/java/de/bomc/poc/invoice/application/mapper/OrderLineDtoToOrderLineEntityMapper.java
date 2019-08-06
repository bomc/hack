/**
 * Project: bomc-invoice
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
package de.bomc.poc.invoice.application.mapper;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import de.bomc.poc.invoice.application.order.OrderLineDTO;
import de.bomc.poc.invoice.domain.model.core.OrderLineEntity;

/**
 * Maps the {@link OrderLineDTO} to {@link OrderLineEntity}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderLineDtoToOrderLineEntityMapper {
	
	OrderLineDtoToOrderLineEntityMapper INSTANCE = Mappers.getMapper(OrderLineDtoToOrderLineEntityMapper.class);

    @Mappings({
        @Mapping(source = "orderLineDTO.quantity", target = "quantity"),
        @Mapping(source = "orderLineDTO.item.price", target = "item.price"),
        @Mapping(source = "orderLineDTO.item.name", target = "item.name"),
        @Mapping(source = "createUser", target = "createUser"),
        @Mapping(source = "createUser", target = "item.createUser"),
    })
	OrderLineEntity mapOrderLineDtoToOrderLineEntity(String createUser, OrderLineDTO orderLineDTO);
    
    Set<OrderLineEntity>mapOrderLineDtoSetToOrderLineEntitySet(Set<OrderLineDTO> orderLineDTO);
}
