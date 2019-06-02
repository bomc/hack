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

import de.bomc.poc.order.application.order.dto.OrderLineDTO;
import de.bomc.poc.order.domain.model.order.OrderLineEntity;

/**
 * Customize the {@link OrderLineEntity}, that maps the {@link ItemEntity} to
 * {@link OrderLineDTO}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.03.2019
 */
public abstract class OrderLineItemDecorator implements DTOEntityOrderLineMapper {
    private DTOEntityOrderLineMapper delegate;

    public OrderLineItemDecorator(final DTOEntityOrderLineMapper delegate) {
        this.delegate = delegate;
    }

    @Override
    public OrderLineDTO mapEntityToDTO(final OrderLineEntity orderLineEntity) {
        final OrderLineDTO orderLineDTO = this.delegate.mapEntityToDTO(orderLineEntity);

        if (orderLineDTO != null) {
            orderLineDTO.setItem(DTOEntityItemMapper.INSTANCE.mapEntityToDTO(orderLineEntity.getItem()));
        }
        
        return orderLineDTO;
    }
}
