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

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.order.application.basis.log.qualifier.AuditLogQualifier;
import de.bomc.poc.order.application.internal.AppErrorCodeEnum;
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
import de.bomc.poc.order.domain.model.order.OrderLineEntity;
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

    private static final String LOG_PREFIX = "OrderControllerEJB#";
    @Inject
    @LoggerQualifier
    private Logger logger;
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

        final CustomerEntity customerEntity = this.jpaCustomerDao.findByUsername(orderDTO.getCustomer().getUsername(),
                userId);
        orderEntity.setCustomer(customerEntity);

        this.jpaOrderDao.persist(orderEntity, userId);

        // Add lines to order.
        orderDTO.getOrderLineDTOList().forEach(orderLineDTO -> {
            final ItemDTO itemDTO = orderLineDTO.getItem();
            final ItemEntity itemEntity = this.jpaItemDao.findByName(itemDTO.getName(), userId);

            final OrderLineEntity orderLineEntity = new OrderLineEntity();
            orderLineEntity.setCreateUser(userId);
            orderLineEntity.setQuantity(orderLineDTO.getQuantity());
            orderLineEntity.setItem(itemEntity);
            orderLineEntity.setOrderEntity(orderEntity);

            orderEntity.addLine(orderLineEntity, userId);

            this.jpaOrderDao.merge(orderEntity, userId);
        });

        return orderEntity.getId();
    }

    @Override
    public OrderDTO findOrderById(final Long orderId, final String userId) {

        final OrderEntity orderEntity = this.jpaOrderDao.findById(orderId);
        final OrderDTO orderDTO = DTOEntityOrderMapper.INSTANCE.mapEntityToDTO(orderEntity);

        return orderDTO;
    }

    @Override
    public void addLine(final OrderLineDTO orderLineDTO, final String userId) {

        // Read order from db by given id.
        final OrderEntity orderEntity = this.jpaOrderDao.findById(orderLineDTO.getOrderId());
        final ItemEntity itemEntity = this.jpaItemDao.findByName(orderLineDTO.getItem().getName(), userId);

        if (itemEntity != null) {
            // Add line to order entity.
            final OrderLineEntity orderLineEntity = new OrderLineEntity();
            orderLineEntity.setCreateUser(userId);
            orderLineEntity.setItem(itemEntity);
            orderLineEntity.setQuantity(orderLineDTO.getQuantity());
            orderLineEntity.setOrderEntity(orderEntity);

            orderEntity.addLine(orderLineEntity, userId);

            this.jpaOrderDao.merge(orderEntity, userId);
        } else {
            // Log exception.
            final String errMsg = LOG_PREFIX + "addLine - no item with given name in db available [name=" + orderLineDTO.getItem().getName() + "]";
            final AppRuntimeException appRuntimeException = new AppRuntimeException(errMsg, AppErrorCodeEnum.JPA_PERSISTENCE_10401);
            this.logger.error(appRuntimeException.stackTraceToString());
            
            throw appRuntimeException;
        }
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
