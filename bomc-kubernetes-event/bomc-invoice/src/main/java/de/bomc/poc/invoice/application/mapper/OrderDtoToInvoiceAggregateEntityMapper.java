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

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import de.bomc.poc.invoice.application.order.OrderDTO;
import de.bomc.poc.invoice.domain.model.core.InvoiceAggregateEntity;

/**
 * Maps the {@link OrderDTO} to {@link InvoiceAggregateEntity}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderDtoToInvoiceAggregateEntityMapper {

	OrderDtoToInvoiceAggregateEntityMapper INSTANCE = Mappers.getMapper(OrderDtoToInvoiceAggregateEntityMapper.class);

    @Mappings({
            @Mapping(source = "orderDTO.orderId", target = "orderId"),
            @Mapping(source = "orderDTO.shippingAddress.street", target = "shippingAddress.street"),
            @Mapping(source = "orderDTO.shippingAddress.zip", target = "shippingAddress.zip"),
            @Mapping(source = "orderDTO.shippingAddress.city", target = "shippingAddress.city"),
            @Mapping(source = "orderDTO.billingAddress.street", target = "billingAddress.street"),
            @Mapping(source = "orderDTO.billingAddress.zip", target = "billingAddress.zip"),
            @Mapping(source = "orderDTO.billingAddress.city", target = "billingAddress.city"),
            @Mapping(source = "orderDTO.customer.name", target = "customer.name"),
            @Mapping(source = "orderDTO.customer.firstname", target = "customer.firstname"),
            @Mapping(source = "createUser", target = "customer.createUser"),
            @Mapping(source = "createUser", target = "createUser"),
    })
    InvoiceAggregateEntity mapOrderDtoToInvoiceAggregateEntity(String createUser, OrderDTO orderDTO);
}