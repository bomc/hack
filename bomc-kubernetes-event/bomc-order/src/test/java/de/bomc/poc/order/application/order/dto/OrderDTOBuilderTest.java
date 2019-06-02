
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
package de.bomc.poc.order.application.order.dto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.order.CategoryFastUnitTest;
import de.bomc.poc.order.application.customer.dto.CustomerDTO;
import de.bomc.poc.order.application.order.dto.AddressDTO;
import de.bomc.poc.order.application.order.dto.ItemDTO;
import de.bomc.poc.order.application.order.dto.OrderDTO;
import de.bomc.poc.order.application.order.dto.OrderLineDTO;

/**
 * Tests the {@link OrderDTO}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 13.02.2019
 */
@Category(CategoryFastUnitTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrderDTOBuilderTest {

    private static final String LOG_PREFIX = OrderDTOBuilderTest.class.getSimpleName() + "#";
    private static final Logger LOGGER = Logger.getLogger(OrderDTOBuilderTest.class);
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    // _______________________________________________
    // Test constants
    // -----------------------------------------------
    private static final Long ORDER_ID = 42L;
    private static final String SHIPPING_ZIP = "shippingZip";
    private static final String SHIPPING_CITY = "shippingCity";
    private static final String SHIPPING_STREET = "shippingStreet";
    private static final String BILLING_ZIP = "billingZip";
    private static final String BILLING_CITY = "billingCity";
    private static final String BILLING_STREET = "billingStreet";
    private static final String ITEM_NAME = "itemName";
    private static final Double ITEM_PRICE = 42.99d;
    private static final Integer ORDERLINE_QUANTITY = 42;
    private static final String CUSTOMER_EMAIL = "bomc@bomc.org";
    private static final String CUSTOMER_NAME = "myName";
    private static final String CUSTOMER_FIRST_NAME = "myFirstName";

    @Test
    public void test010_build_pass() {
        LOGGER.debug(LOG_PREFIX + "test010_build_pass");

        final AddressDTO shippingAddressDTO = AddressDTO.zip(SHIPPING_ZIP).street(SHIPPING_STREET).city(SHIPPING_CITY)
                .build();
        final AddressDTO billingAddressDTO = AddressDTO.zip(BILLING_ZIP).street(BILLING_STREET).city(BILLING_CITY)
                .build();
        final ItemDTO itemDTO1 = ItemDTO.name(ITEM_NAME).price(ITEM_PRICE).build();
        final ItemDTO itemDTO2 = ItemDTO.name(ITEM_NAME + "_2").price(ITEM_PRICE + 1).build();
        final OrderLineDTO orderLineDTO1 = OrderLineDTO.quantity(ORDERLINE_QUANTITY).item(itemDTO1).build();
        final OrderLineDTO orderLineDTO2 = OrderLineDTO.quantity(ORDERLINE_QUANTITY).item(itemDTO2).build();
        final CustomerDTO customerDTO = CustomerDTO.username(CUSTOMER_EMAIL).name(CUSTOMER_NAME)
                .firstname(CUSTOMER_FIRST_NAME).build();

        final Set<OrderLineDTO> orderLineDTOSet = new HashSet<>();
        orderLineDTOSet.add(orderLineDTO1);
        orderLineDTOSet.add(orderLineDTO2);

        final OrderDTO orderDTO = OrderDTO.orderId(ORDER_ID).billingAddress(billingAddressDTO).shippingAddress(shippingAddressDTO)
                .customer(customerDTO).orderLineSet(orderLineDTOSet).build();

        LOGGER.debug(LOG_PREFIX + "test010_build_pass " + orderDTO);

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------

        assertThat(orderDTO.getBillingAddress(), equalTo(billingAddressDTO));
        assertThat(orderDTO.getShippingAddress(), equalTo(shippingAddressDTO));
        assertThat(orderDTO.getCustomer(), equalTo(customerDTO));
        assertThat(orderDTO.getOrderLineDTOSet(), notNullValue());
        assertThat(orderDTO.getOrderLineDTOSet().size(), equalTo(2));
        assertThat(orderDTO.getOrderId(), equalTo(ORDER_ID));
    }

    @Test
    public void test020_build_fail() {
        LOGGER.debug(LOG_PREFIX + "test020_build_fail");

        this.thrown.expect(AppRuntimeException.class);

        final OrderDTO orderDTO = this.createOrderDTO("");

        assertThat(orderDTO.getBillingAddress().getZip(), equalTo(BILLING_ZIP));
        assertThat(orderDTO.getBillingAddress().getCity(), equalTo(BILLING_CITY));
        assertThat(orderDTO.getBillingAddress().getStreet(), equalTo(BILLING_STREET));
        assertThat(orderDTO.getShippingAddress().getZip(), equalTo(SHIPPING_ZIP));
        assertThat(orderDTO.getShippingAddress().getCity(), equalTo(SHIPPING_CITY));
        assertThat(orderDTO.getShippingAddress().getStreet(), equalTo(SHIPPING_STREET));
        assertThat(orderDTO.getCustomer().getFirstname(), equalTo(CUSTOMER_FIRST_NAME));
        assertThat(orderDTO.getCustomer().getName(), equalTo(CUSTOMER_NAME));
        assertThat(orderDTO.getCustomer().getUsername(), equalTo(CUSTOMER_EMAIL));
        assertThat(orderDTO.getOrderId(), equalTo(ORDER_ID));

        // Set 'orderLineSet' null. A AppRuntimeException will be thrown.
        orderDTO.setOrderLineDTOSet(null);

        fail("Should no be invoked.");
    }

    @Test
    @SuppressWarnings("unlikely-arg-type")
    public void test030_equalsHashcode() {
        LOGGER.debug(LOG_PREFIX + "test030_equalsHashcode");

        final OrderDTO orderDTO1 = this.createOrderDTO("_1");
        final OrderDTO orderDTO2 = this.createOrderDTO("_1");
        final OrderDTO orderDTO3 = this.createOrderDTO("_3");
        final OrderDTO orderDTO4 = OrderDTO.orderId(null).billingAddress(null).shippingAddress(null).customer(null).orderLineSet(Collections.emptySet()).build();
        final OrderDTO orderDTO5 = null;
        
        assertThat(orderDTO1.equals(orderDTO1), equalTo(true));
        assertThat(orderDTO1.equals(orderDTO2), equalTo(true));
        assertThat(orderDTO1.equals(orderDTO3), equalTo(false));
        assertThat(orderDTO1.equals(orderDTO4), equalTo(false));
        assertThat(orderDTO1.equals(orderDTO5), equalTo(false));
        assertThat(orderDTO1.equals(null), equalTo(false));
        assertThat(orderDTO1.equals(new OrderDTO()), equalTo(false));
        assertThat(orderDTO1.equals(new String()), equalTo(false));
        assertThat(orderDTO1.hashCode(), equalTo(orderDTO2.hashCode()));
        assertThat(orderDTO1.hashCode(), not(equalTo(orderDTO3.hashCode())));
        assertThat(orderDTO1.hashCode(), not(equalTo(orderDTO4.hashCode())));
        
    }

    private OrderDTO createOrderDTO(final String prefix) {

        final AddressDTO shippingAddressDTO = AddressDTO.zip(SHIPPING_ZIP + prefix).street(SHIPPING_STREET + prefix)
                .city(SHIPPING_CITY + prefix).build();
        final AddressDTO billingAddressDTO = AddressDTO.zip(BILLING_ZIP + prefix).street(BILLING_STREET + prefix)
                .city(BILLING_CITY + prefix).build();
        final CustomerDTO customerDTO = CustomerDTO.username(CUSTOMER_EMAIL + prefix).name(CUSTOMER_NAME + prefix)
                .firstname(CUSTOMER_FIRST_NAME + prefix).build();

        // Test getter/setter.
        final OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBillingAddress(billingAddressDTO);
        orderDTO.setShippingAddress(shippingAddressDTO);
        orderDTO.setCustomer(customerDTO);
        orderDTO.setOrderId(ORDER_ID);

        return orderDTO;
    }
}