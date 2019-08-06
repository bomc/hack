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
package de.bomc.poc.invoice.application.controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.transaction.Transactional;

import de.bomc.poc.invoice.application.internal.ApplicationUserEnum;
import de.bomc.poc.invoice.application.log.LoggerQualifier;
import de.bomc.poc.invoice.application.mapper.OrderDtoToInvoiceAggregateEntityMapper;
import de.bomc.poc.invoice.application.mapper.OrderLineDtoToOrderLineEntityMapper;
import de.bomc.poc.invoice.application.order.OrderDTO;
import de.bomc.poc.invoice.application.order.OrderLineDTO;
import de.bomc.poc.invoice.domain.model.core.InvoiceAggregateEntity;
import de.bomc.poc.invoice.domain.model.core.JpaInvoiceAggregateRepository;
import de.bomc.poc.invoice.domain.model.core.OrderLineEntity;
import de.bomc.poc.invoice.infrastructure.persistence.basis.qualifier.JpaRepository;

/**
 * This service is used as controller for handling invoices.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class InvoiceCompositionService implements InvoiceComposition {

	private static final String LOG_PREFIX = "InvoiceCompositionService#";
	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	@JpaRepository
	private JpaInvoiceAggregateRepository jpaInvoiceAggregateRepository;

	@Override
	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public void createInvoice(List<OrderDTO> oderDtoList, final String userId) {
		this.logger.log(Level.FINE,
				LOG_PREFIX + "createInvoice [orderDtoList.size=" + oderDtoList.size() + ", userId=" + userId + "]");

		InvoiceAggregateEntity invoiceAggregateEntity = null;

		for (final OrderDTO orderDTO : oderDtoList) {
			invoiceAggregateEntity = OrderDtoToInvoiceAggregateEntityMapper.INSTANCE
					.mapOrderDtoToInvoiceAggregateEntity(userId, orderDTO);

			jpaInvoiceAggregateRepository.persist(invoiceAggregateEntity, ApplicationUserEnum.SYSTEM_USER.name());

			final List<OrderLineDTO> orderLineDTOList = orderDTO.getOrderLineDTOList();
			for (final OrderLineDTO orderLineDTO : orderLineDTOList) {
				final OrderLineEntity orderLineEntity = OrderLineDtoToOrderLineEntityMapper.INSTANCE
						.mapOrderLineDtoToOrderLineEntity(userId, orderLineDTO);
				invoiceAggregateEntity.addLine(orderLineDTO.getQuantity(), orderLineEntity.getItem(), userId);
				jpaInvoiceAggregateRepository.merge(invoiceAggregateEntity, userId);
			} // end for
		} // end for

		if (invoiceAggregateEntity != null) {
			this.logger.log(Level.INFO, 
					LOG_PREFIX + "createInvoice [___total=" + invoiceAggregateEntity.totalPrice() + ", numberOfLines=" + invoiceAggregateEntity.getNumberOfLines() + "___]");
		}
	}

}
