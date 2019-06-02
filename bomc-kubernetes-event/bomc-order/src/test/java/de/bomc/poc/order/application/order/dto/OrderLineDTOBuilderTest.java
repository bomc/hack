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
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;

import de.bomc.poc.order.CategoryFastUnitTest;
import de.bomc.poc.order.application.order.dto.ItemDTO;
import de.bomc.poc.order.application.order.dto.OrderLineDTO;

/**
 * Test the builder of {@link OrderLineDTO}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 23.11.2018
 */
@Category(CategoryFastUnitTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrderLineDTOBuilderTest {
    
    private static final String LOG_PREFIX = OrderLineDTOBuilderTest.class.getSimpleName() + "#";
    private static final Logger LOGGER = Logger.getLogger(OrderLineDTOBuilderTest.class);
    // _______________________________________________
    // Constants for testing.
    // -----------------------------------------------
    private static final String ITEM_NAME = "myItem";
    private static final Double ITEM_PRICE = 2.99d;
    private static final Integer ORDERLINE_QUANTITY = 42;
    
    @Test
    public void test010_builder_pass() {
        LOGGER.debug(LOG_PREFIX + "test010_builder_pass");
        
        // ___________________________________________
        // Perform test.
        // -------------------------------------------
        final ItemDTO itemDTO = ItemDTO.name(ITEM_NAME).price(ITEM_PRICE).build();

        final OrderLineDTO orderLineDTO = OrderLineDTO.quantity(ORDERLINE_QUANTITY).item(itemDTO).build();
        
        LOGGER.debug(LOG_PREFIX + "test010_builder_pass [" + orderLineDTO + "]");
        
        // ___________________________________________
        // Do asserts
        // -------------------------------------------
        assertThat(orderLineDTO.getItem(), notNullValue());
        assertThat(orderLineDTO.getItem(), equalTo(itemDTO));
        assertThat(orderLineDTO.getQuantity(), equalTo(ORDERLINE_QUANTITY));
    }
    
    @Test
    public void test020_getterSetter() {
        LOGGER.debug(LOG_PREFIX + "test020_getterSetter");
        
        
    }
    
    @Test
    public void test030_equalsHashcode_pass() {
        LOGGER.debug(LOG_PREFIX + "test030_equalsHashcode_pass");

        // ___________________________________________
        // Do test preparation-
        // -------------------------------------------
        final Set<OrderLineDTO> set = new HashSet<>();
        
        // ___________________________________________
        // Perform test.
        // -------------------------------------------
        final ItemDTO itemDTO1 = ItemDTO.name(ITEM_NAME).price(ITEM_PRICE).build();
        final OrderLineDTO orderLineDTO1 = OrderLineDTO.quantity(ORDERLINE_QUANTITY).item(itemDTO1).build();
        
        final ItemDTO itemDTO2 = ItemDTO.name(ITEM_NAME).price(ITEM_PRICE).build();
        final OrderLineDTO orderLineDTO2 = OrderLineDTO.quantity(ORDERLINE_QUANTITY).item(itemDTO2).build();
        
        final ItemDTO itemDTO3 = ItemDTO.name(ITEM_NAME + "_3").price(ITEM_PRICE + 1).build();
        final OrderLineDTO orderLineDTO3 = OrderLineDTO.quantity(ORDERLINE_QUANTITY + 1).item(itemDTO3).build();
        
        set.add(orderLineDTO1);
        set.add(orderLineDTO2);
        set.add(orderLineDTO3);
        
        final OrderLineDTO orderLineDTO4 = OrderLineDTO.quantity(null).item(null).build();
        final OrderLineDTO orderLineDTO5 = null;
        
        // ___________________________________________
        // Do asserts
        // -------------------------------------------
        // NOTE: OrderLineDTO1 and OrderLineDTO2 are equal, so only two elements are added. 
        assertThat(set.size(), equalTo(2));
        assertThat(set.contains(orderLineDTO3), equalTo(true));
        assertThat(set.contains(new OrderLineDTO()), equalTo(false));
        assertThat(orderLineDTO1.equals(orderLineDTO1), equalTo(true));
        assertThat(orderLineDTO1.equals(orderLineDTO2), equalTo(true));
        assertThat(orderLineDTO1.equals(orderLineDTO3), equalTo(false));
        assertThat(orderLineDTO1.equals(orderLineDTO4), equalTo(false));
        assertThat(orderLineDTO1.equals(orderLineDTO5), equalTo(false));
        assertThat(orderLineDTO1.hashCode(), equalTo(orderLineDTO2.hashCode()));
        assertThat(orderLineDTO1.hashCode(), not(equalTo(orderLineDTO3.hashCode())));
        assertThat(orderLineDTO1.hashCode(), not(equalTo(orderLineDTO4.hashCode())));
    }
    
}