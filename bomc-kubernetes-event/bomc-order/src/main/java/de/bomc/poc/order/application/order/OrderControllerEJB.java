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
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.bomc.poc.order.application.basis.log.qualifier.AuditLogQualifier;
import de.bomc.poc.order.application.order.dto.ItemDTO;
import de.bomc.poc.order.application.order.dto.OrderDTO;
import de.bomc.poc.order.application.order.dto.OrderLineDTO;
import de.bomc.poc.order.application.order.mapping.DTOEntityItemMapper;
import de.bomc.poc.order.application.order.mapping.DTOEntityOrderMapper;
import de.bomc.poc.order.domain.model.customer.CustomerEntity;
import de.bomc.poc.order.domain.model.customer.JpaCustomerDao;
import de.bomc.poc.order.domain.model.item.ItemEntity;
import de.bomc.poc.order.domain.model.item.JpaItemDao;
import de.bomc.poc.order.domain.model.order.JpaOrderDao;
import de.bomc.poc.order.domain.model.order.OrderEntity;
import de.bomc.poc.order.infrastructure.persistence.basis.qualifier.JpaDao;

/**
 * This controller handles the order domain.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Stateless
@AuditLogQualifier
@Local(OrderController.class)
public class OrderControllerEJB implements OrderController {

    @Inject
    @JpaDao
    private JpaItemDao jpaItemDao;
    @Inject
    @JpaDao
    private JpaOrderDao jpaOrderDao;
    @Inject
    @JpaDao
    private JpaCustomerDao jpaCustomerDao;

    @Override
    public LocalDateTime findLatestModifiedDateTime(final String userId) {

        return this.jpaOrderDao.findLatestModifiedDateTime(userId);
    }
    
    @Override
    public List<ItemDTO> findAllItems(final String userId) {

        final List<ItemEntity> itemEntityList = this.jpaItemDao.findAll();
        final List<ItemDTO> itemDTOList = DTOEntityItemMapper.INSTANCE.mapEntityListToDTOList(itemEntityList);

        return itemDTOList;
    }

    @Override
    public List<OrderDTO> findAllOrder(final String userId) {

        final List<OrderEntity> orderEntityList = this.jpaOrderDao.findAll();
        final List<OrderDTO> orderDTOList = DTOEntityOrderMapper.INSTANCE.mapEntityToDTO(orderEntityList);

        return orderDTOList;
    }

    /**
     * NOTE: used from orderTo: ItemDTO.getName(),
     * orderDTO.getCustomer().getUsername(), userId).
     */
    @Override
    public Long createOrder(final OrderDTO orderDTO, final String userId) {

        final OrderEntity orderEntity = DTOEntityOrderMapper.INSTANCE.mapDTOToEntity(orderDTO);

        orderDTO.getOrderLineDTOSet().forEach(orderLineDTO -> {
            final ItemDTO itemDTO = orderLineDTO.getItem();
            final ItemEntity itemEntity = this.jpaItemDao.findByName(itemDTO.getName(), userId);

            orderEntity.addLine(orderLineDTO.getQuantity(), itemEntity, userId);
        });

        final CustomerEntity customerEntity = this.jpaCustomerDao.findByUsername(orderDTO.getCustomer().getUsername(),
                userId);
        orderEntity.setCustomer(customerEntity);

        this.jpaOrderDao.persist(orderEntity, userId);

        return orderEntity.getId();
    }

    @Override
    public OrderDTO findOrderById(final Long orderId, final String userId) {

        final OrderEntity orderEntity = this.jpaOrderDao.findById(orderId);
        final OrderDTO orderDTO = DTOEntityOrderMapper.INSTANCE.mapEntityToDTO(orderEntity);

        return orderDTO;
    }

    @Override
    public void addLine(final OrderDTO orderDTO, final String userId) {

        // Read order from db by given id.
        final OrderEntity orderEntity = this.jpaOrderDao.findById(orderDTO.getOrderId());

        // Add line to entity.
        final Set<OrderLineDTO> orderLineDTOSet = orderDTO.getOrderLineDTOSet();
        orderLineDTOSet.forEach(orderLineDTO -> {
            // Read desired item with given name from db.
            final ItemEntity itemEntity = this.jpaItemDao.findByName(orderLineDTO.getItem().getName(), userId);
            // Add new line to order.
            orderEntity.addLine(orderLineDTO.getQuantity(), itemEntity, userId);
        });
    }

    @Override
    public void deleteOrderById(final Long id, final String userId) {
        
        final OrderEntity orderEntity = this.jpaOrderDao.findById(id);
        orderEntity.setCustomer(null);
        
        this.jpaOrderDao.remove(orderEntity);
    }

    @Override
    public List<OrderDTO> findByAllOlderThanGivenDate(final LocalDateTime modifyDateTime, final String userId) {
        
        final List<OrderEntity> orderEntityList = this.jpaOrderDao.findByAllOlderThanGivenDate(modifyDateTime, userId);
        final List<OrderDTO> orderDTOList = DTOEntityOrderMapper.INSTANCE.mapEntityToDTO(orderEntityList);
        
        return orderDTOList;
    }
}
