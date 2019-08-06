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

import de.bomc.poc.order.application.customer.mapping.DTOEntityCustomerMapper;
import de.bomc.poc.order.application.order.dto.OrderDTO;
import de.bomc.poc.order.domain.model.order.OrderEntity;

/**
 * This mapper maps to/from {@link OrderEntity} to/from {@link OrderDTO}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Mapper(uses = { DTOEntityCustomerMapper.class, DTOEntityOrderLineMapper.class })
public interface DTOEntityOrderMapper {

    DTOEntityOrderMapper INSTANCE = Mappers.getMapper(DTOEntityOrderMapper.class);

    // _______________________________________________________________________
    // Mapping for OrderEntity to OrderDTO.
    // -----------------------------------------------------------------------
    @Mappings({ 
            @Mapping(target = "shippingAddress.street", source = "shippingAddress.street"),
            @Mapping(target = "shippingAddress.zip", source = "shippingAddress.zip"),
            @Mapping(target = "shippingAddress.city", source = "shippingAddress.city"),
            @Mapping(target = "billingAddress.street", source = "billingAddress.street"),
            @Mapping(target = "billingAddress.zip", source = "billingAddress.zip"),
            @Mapping(target = "billingAddress.city", source = "billingAddress.city"),
            @Mapping(target = "customer", source = "customer"),
            @Mapping(target = "orderLineDTOList", source = "orderLineSet"),
            @Mapping(target = "orderId", source = "id") })
    OrderDTO mapEntityToDTO(OrderEntity orderEntity);

    List<OrderDTO> mapEntityToDTO(List<OrderEntity> orderEntityList);

    // _______________________________________________________________________
    // Mapping for OrderDTO to OrderEntity.
    // -----------------------------------------------------------------------
    @Mappings({ 
            @Mapping(target = "shippingAddress.street", source = "shippingAddress.street"),
            @Mapping(target = "shippingAddress.zip", source = "shippingAddress.zip"),
            @Mapping(target = "shippingAddress.city", source = "shippingAddress.city"),
            @Mapping(target = "billingAddress.street", source = "billingAddress.street"),
            @Mapping(target = "billingAddress.zip", source = "billingAddress.zip"),
            @Mapping(target = "billingAddress.city", source = "billingAddress.city"),
            @Mapping(target = "customer", source = "customer"), 
            @Mapping(target = "orderLineSet", ignore = true),
            @Mapping(target = "createDateTime", ignore = true), 
            @Mapping(target = "createUser", ignore = true),
            @Mapping(target = "modifyDateTime", ignore = true), 
            @Mapping(target = "modifyUser", ignore = true),
            @Mapping(target = "version", ignore = true), 
            @Mapping(target = "id", ignore = true) })
    OrderEntity mapDTOToEntity(OrderDTO orderDTO);

}
