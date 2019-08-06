/**
 * Project: bomc-order
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
package de.bomc.poc.order.interfaces.rest.v1.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.order.application.order.OrderController;
import de.bomc.poc.order.application.order.dto.ItemDTO;
import de.bomc.poc.order.application.order.dto.OrderDTO;
import de.bomc.poc.order.application.order.dto.OrderLineDTO;

/**
 * Mocks the {@link OrderControllerEJB}.
 * <p>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 01.04.2019
 */
@Stateless
@Local(OrderController.class)
public class OrderControllerEJBMock implements OrderController {

    private static final String LOG_PREFIX = "OrderControllerEJBMock#";
    @Inject
    @LoggerQualifier
    private Logger logger;

    @Override
    public List<ItemDTO> findAllItems(String userId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<OrderDTO> findAllOrder(String userId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long createOrder(OrderDTO orderDTO, String userId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OrderDTO findOrderById(Long orderId, String userId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addLine(OrderLineDTO orderLineDTO, String userId) {
        this.logger.debug(LOG_PREFIX + "findLatestModifiedDateTime [orderLineDTO=" + orderLineDTO + ", userId=" + userId + "]");

    }

    @Override
    public void deleteOrderById(Long id, String userId) {
        // TODO Auto-generated method stub

    }

    @Override
    public LocalDateTime findLatestModifiedDateTime(String userId) {
        this.logger.debug(LOG_PREFIX + "findLatestModifiedDateTime [userId=" + userId + "]");

        if (userId != null) {
            return LocalDateTime.now();
        } else {
            return null;
        }

    }

    @Override
    public List<OrderDTO> findByAllOlderThanGivenDate(final LocalDateTime modifyDateTime, final String userId) {
        this.logger.debug(LOG_PREFIX + "findByAllOlderThanGivenDate [modifyDateTime=" + modifyDateTime + ", userId="
                + userId + "]");

        final List<OrderLineDTO> orderLineDTOList = new ArrayList<>();
        final List<OrderDTO> orderDTOList = new ArrayList<>();

        if (userId != null) {
            final OrderDTO orderDTO = OrderDTO.orderId(1L).billingAddress(null).shippingAddress(null).customer(null)
                    .orderLineList(orderLineDTOList).build();

            orderDTOList.add(orderDTO);
        }
        
        return orderDTOList;
    }

}
