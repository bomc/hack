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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.bomc.poc.invoice.application.customer.CustomerDTO;
import de.bomc.poc.invoice.application.order.AddressDTO;
import de.bomc.poc.invoice.application.order.ItemDTO;
import de.bomc.poc.invoice.application.order.OrderDTO;
import de.bomc.poc.invoice.application.order.OrderLineDTO;
import de.bomc.poc.invoice.domain.model.core.InvoiceAggregateEntity;
import de.bomc.poc.invoice.domain.model.core.OrderLineEntity;

/**
 * Tests the {@link OrderDtoToInvoiceAggregateEntityMapper}
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 17.07.2019
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrderDtoToInvoiceAggregateEntityMapperTest {

	private static final String LOG_PREFIX = "OrderDtoToInvoiceAggregateEntityMapperTest#";

    /**
     * <pre>
     * <b><code>test010_createOrderDtoToInvoiceAggregateEntityMapper_pass</code>:</b><br>
     *  Tests the {@link OrderDtoToInvoiceAggregateEntityMapper}.
     *
     * <b>Preconditions:</b><br>
     *  - Create a full initialized OrderDTO
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Invoke the OrderDtoToInvoiceAggregateEntityMapper INSTANCE to perform the mapping.
     *
     * <b>Postconditions:</b><br>
     *  - The Mapper maps the dto to the entity.
     * </pre>
     */
	@Test
	public void test010_createOrderDtoToInvoiceAggregateEntityMapper_pass() {
		System.out.println(LOG_PREFIX + "test010_createOrderDtoToInvoiceAggregateEntityMapper_pass");

		final OrderDTO orderDTO = new OrderDTO();
		orderDTO.setOrderId(1L);

		final AddressDTO shippingAddress = new AddressDTO();
		shippingAddress.setCity("addressCity");
		shippingAddress.setZip("addressZip");
		shippingAddress.setStreet("addressStreet");
		orderDTO.setShippingAddress(shippingAddress);

		final AddressDTO billingAddress = new AddressDTO();
		billingAddress.setCity("billingCity");
		billingAddress.setZip("billingZip");
		billingAddress.setStreet("billingStreet");
		orderDTO.setBillingAddress(billingAddress);

		final CustomerDTO customer = new CustomerDTO();
		customer.setFirstname("firstname");
		customer.setName("name");
		customer.setUsername("username");
		orderDTO.setCustomer(customer);

		final List<OrderLineDTO> orderLineDTOList = new ArrayList<>();

		final OrderLineDTO orderLine1 = new OrderLineDTO();
		final ItemDTO item1 = new ItemDTO();
		item1.setName("name1");
		item1.setPrice(1.2d);
		orderLine1.setItem(item1);
		orderLine1.setQuantity(2);
		orderLineDTOList.add(orderLine1);

		final OrderLineDTO orderLine2 = new OrderLineDTO();
		final ItemDTO item2 = new ItemDTO();
		item2.setName("name2");
		item2.setPrice(1.2d);
		orderLine2.setItem(item2);
		orderLine2.setQuantity(22);
		orderLineDTOList.add(orderLine2);

		final OrderLineDTO orderLine3 = new OrderLineDTO();
		final ItemDTO item3 = new ItemDTO();
		item3.setName("name3");
		item3.setPrice(1.2d);
		orderLine3.setItem(item3);
		orderLine3.setQuantity(22);
		orderLineDTOList.add(orderLine3);

		orderDTO.setOrderLineDTOList(orderLineDTOList);

		final Long orderId = 7L;
		orderDTO.setOrderId(orderId);

		final InvoiceAggregateEntity invoiceAggregateEntity = OrderDtoToInvoiceAggregateEntityMapper.INSTANCE.mapOrderDtoToInvoiceAggregateEntity("userId", orderDTO);
		
		assertThat(invoiceAggregateEntity.getBillingAddress(), notNullValue());
		assertThat(invoiceAggregateEntity.getShippingAddress(), notNullValue());
		assertThat(invoiceAggregateEntity.getCustomer(), notNullValue());
		
		final OrderLineEntity mappedOrderLineEntity1 = OrderLineDtoToOrderLineEntityMapper.INSTANCE.mapOrderLineDtoToOrderLineEntity("userId", orderLine1);
		invoiceAggregateEntity.addLine(mappedOrderLineEntity1.getQuantity(), mappedOrderLineEntity1.getItem(), "userId");
		
		final OrderLineEntity mappedOrderLineEntity2 = OrderLineDtoToOrderLineEntityMapper.INSTANCE.mapOrderLineDtoToOrderLineEntity("userId", orderLine2);
		invoiceAggregateEntity.addLine(mappedOrderLineEntity2.getQuantity(), mappedOrderLineEntity2.getItem(), "userId");
		
		final OrderLineEntity mappedOrderLineEntity3 = OrderLineDtoToOrderLineEntityMapper.INSTANCE.mapOrderLineDtoToOrderLineEntity("userId", orderLine3);
		invoiceAggregateEntity.addLine(mappedOrderLineEntity3.getQuantity(), mappedOrderLineEntity3.getItem(), "userId");
		
		// ___________________________________________
		// NOTE: could only be one, because hashCode/equals handling with a a java.util.Set.
		assertThat(invoiceAggregateEntity.getOrderLineSet().isEmpty(), equalTo(false));
	}
}
