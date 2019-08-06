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
package de.bomc.poc.order.application.order;

import java.time.LocalDateTime;
import java.util.List;

import de.bomc.poc.order.application.order.dto.ItemDTO;
import de.bomc.poc.order.application.order.dto.OrderDTO;
import de.bomc.poc.order.application.order.dto.OrderLineDTO;

/**
 * This controller handles the order domain.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public interface OrderController {

    /**
     * Find the latest modified order.
     * 
     * @param userId
     *            the executed user.
     * @return the last modified date.
     */
    LocalDateTime findLatestModifiedDateTime(String userId);

    /**
     * Find all available items in db.
     * 
     * @param userId
     *            the executed user.
     * @return all available items.
     */
    List<ItemDTO> findAllItems(String userId);

    /**
     * Find all available order in db.
     * 
     * @param userId
     *            the executed user.
     * @return all available order.
     */
    List<OrderDTO> findAllOrder(String userId);

    /**
     * Create a order by the given parameter.
     * 
     * @param orderDTO
     *            the order parameter.
     * @param userId
     *            the executed user.
     * @return the technical id.
     */
    Long createOrder(final OrderDTO orderDTO, final String userId);

    /**
     * Find order by id.
     *
     * @param orderId
     *            the given order id (technical).
     * @param userId
     *            the executed user.
     * @return the orderDTO.
     */
    OrderDTO findOrderById(Long orderId, String userId);

    /**
     * Adds a line to the order.
     * 
     * @param orderLineDTO
     *            the new orderline to add.
     * @param userId
     *            the current user.
     * @return the merged entity.
     */
    void addLine(OrderLineDTO orderLineDTO, String userId);

    /**
     * Delete order by id.
     *
     * @param id
     *            the technical id from db.
     * @param userId
     *            the executed user.
     */
    void deleteOrderById(Long id, String userId);

    /**
     * Find all orders that createDate and modifyDate are older than the given date.
     * 
     * @param modifyDate
     *            the given date.
     * @param userId
     *            the executed user. 
     */
    List<OrderDTO> findByAllOlderThanGivenDate(LocalDateTime modifyDateTime, String userId);

}
