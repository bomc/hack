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
package de.bomc.poc.invoice.domain.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.bomc.poc.invoice.application.customer.CustomerDTO;
import de.bomc.poc.invoice.application.mapper.OrderDtoToInvoiceAggregateEntityMapper;
import de.bomc.poc.invoice.application.mapper.OrderLineDtoToOrderLineEntityMapper;
import de.bomc.poc.invoice.application.order.AddressDTO;
import de.bomc.poc.invoice.application.order.ItemDTO;
import de.bomc.poc.invoice.application.order.OrderDTO;
import de.bomc.poc.invoice.application.order.OrderLineDTO;
import de.bomc.poc.invoice.domain.AbstractUnitTest;
import de.bomc.poc.invoice.domain.model.core.InvoiceAggregateEntity;
import de.bomc.poc.invoice.domain.model.core.ItemEntity;
import de.bomc.poc.invoice.domain.model.core.OrderLineEntity;

/**
 * Test the {@link InvoiceAggregateEntity}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 14.02.2018
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InvoiceAggregateEntityTest extends AbstractUnitTest {

	private static final String LOG_PREFIX = InvoiceAggregateEntityTest.class.getName() + "#";
	// Member variables
	
	// Constants
	private static final String CREATE_USER = "bomc";
	private static final String ITEM_NAME = "item_name";
	private static final Integer COUNT = 9;
	private static final Double PRICE = 1.5d;
	private static final Double TOTAL_PRICE = 13.5d;
	private static final Long ORDER_ID = 42l;
	
    @Before
    public void setup() {
        this.entityManager = this.emProvider.getEntityManager();
        assertThat(this.entityManager, notNullValue());

        this.utx = this.entityManager.getTransaction();
        assertThat(this.utx, notNullValue());
    }
    
    /**
     * <pre>
     * <b><code>test010_createInvoiceAggregateEntity_pass</code>:</b><br>
     *  Tests the creating of the {@link InvoiceAggregateEntity}.
     *
     * <b>Preconditions:</b><br>
     *  - Create two ItemEntities.
     *  - Add them via 'addLine' to the InvoiceAggregateEntity.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Persist the InvoiceAggregateEntity with the entityManager.
     *
     * <b>Postconditions:</b><br>
     *  - After commit the technical ids of the entities are not null.
     * </pre>
     */
	@Test
	public void test010_createInvoiceAggregateEntity_pass() {
		System.out.println(LOG_PREFIX + "test010_createInvoiceAggregateEntity_pass");
		
		// ___________________________________________
		// GIVEN
		// -------------------------------------------
		this.utx.begin();
		
		final ItemEntity itemEntity1 = new ItemEntity();
		itemEntity1.setCreateUser(CREATE_USER);
		itemEntity1.setName(ITEM_NAME);
		itemEntity1.setPrice(PRICE);

		final ItemEntity itemEntity2 = new ItemEntity();
		itemEntity2.setCreateUser(CREATE_USER + "_1");
		itemEntity2.setName(ITEM_NAME + "_1");
		itemEntity2.setPrice(PRICE);
		
		final InvoiceAggregateEntity invoiceAggregateEntity = new InvoiceAggregateEntity();
		invoiceAggregateEntity.setCreateUser(CREATE_USER);
		invoiceAggregateEntity.setOrderId(ORDER_ID);
		
		invoiceAggregateEntity.addLine(COUNT, itemEntity1, CREATE_USER);
		invoiceAggregateEntity.addLine(COUNT, itemEntity2, CREATE_USER);
		
		// ___________________________________________
		// WHEN
		// -------------------------------------------
		this.entityManager.persist(invoiceAggregateEntity);
		
		this.utx.commit();
		
		// ___________________________________________
		// THEN
		// -------------------------------------------
		assertThat(itemEntity1.getId(), notNullValue());
		assertThat(invoiceAggregateEntity.getId(), notNullValue());
		assertThat(invoiceAggregateEntity.totalPrice(), equalTo(TOTAL_PRICE));
	}
	
	/**
     * <pre>
     * <b><code>test020_createInvoiceAggregateEntityFromDto_pass</code>:</b><br>
     *  Tests the creating of the {@link InvoiceAggregateEntity} with the {@link OrderDtoToInvoiceAggregateEntityMapper}.
     *
     * <b>Preconditions:</b><br>
     *  - Create a full blown OrderDTO with three items.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Map the dto to the entity
     *  - Persist the InvoiceAggregateEntity with the entityManager.
     *
     * <b>Postconditions:</b><br>
     *  - After commit three items are indb available.
     * </pre>
     */
	@Test
	public void test020_createInvoiceAggregateEntityFromDto_pass() {
		System.out.println(LOG_PREFIX + "test020_createInvoiceAggregateEntityFromDto_pass");
		
		// ___________________________________________
		// GIVEN
		// -------------------------------------------
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
		
		// ___________________________________________
		// WHEN
		// -------------------------------------------
		final InvoiceAggregateEntity invoiceAggregateEntity = OrderDtoToInvoiceAggregateEntityMapper.INSTANCE
				.mapOrderDtoToInvoiceAggregateEntity("userId", orderDTO);

		this.utx.begin();
		
		this.entityManager.persist(invoiceAggregateEntity);

		for (final OrderLineDTO orderLineDTO : orderLineDTOList) {
			final OrderLineEntity orderLineEntity = OrderLineDtoToOrderLineEntityMapper.INSTANCE
					.mapOrderLineDtoToOrderLineEntity("userId", orderLineDTO);
			invoiceAggregateEntity.addLine(orderLineDTO.getQuantity(), orderLineEntity.getItem(), "userId");
			this.entityManager.merge(invoiceAggregateEntity);
		} // end for
		
		this.utx.commit();
		
		final InvoiceAggregateEntity retInvoiceAggregateEntity = this.entityManager.find(InvoiceAggregateEntity.class, 1L);
		
		// ___________________________________________
		// THEN
		// -------------------------------------------
		assertThat(retInvoiceAggregateEntity.getNumberOfLines(), equalTo(3));
		
		System.out.println("test010_createInvoiceAggregateEntity_pass" + retInvoiceAggregateEntity);
	}
}
