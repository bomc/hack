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

/**
 * Test the builder of {@link ItemDTO}.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 23.11.2018
 */
@Category(CategoryFastUnitTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ItemDTOBuilderTest {
    
    private static final String LOG_PREFIX = ItemDTOBuilderTest.class.getSimpleName() + "#";
    private static final Logger LOGGER = Logger.getLogger(ItemDTOBuilderTest.class);
    // _______________________________________________
    // Constants for testing.
    // -----------------------------------------------
    private static final String ITEM_NAME = "myItem";
    private static final Double ITEM_PRICE = 2.99d;
    
    @Test
    public void test010_builder_pass() {
        LOGGER.debug(LOG_PREFIX + "test010_builder_pass");
        
        // ___________________________________________
        // Perform test.
        // -------------------------------------------
        final ItemDTO itemDTO = ItemDTO.name(ITEM_NAME).price(ITEM_PRICE).build();
        
        LOGGER.debug(LOG_PREFIX + "test010_builder_pass [" + itemDTO + "]");
        
        // ___________________________________________
        // Do asserts
        // -------------------------------------------
        assertThat(itemDTO, notNullValue());
        assertThat(itemDTO.getName(), equalTo(ITEM_NAME));
        assertThat(itemDTO.getPrice(), equalTo(ITEM_PRICE));
    }

    @Test
    public void test020_getterSetter_pass() {
        LOGGER.debug(LOG_PREFIX + "test020_getterSetter_pass");
        
        // ___________________________________________
        // Perform test.
        // -------------------------------------------
        final ItemDTO itemDTO = new ItemDTO();
        itemDTO.setName(ITEM_NAME);
        itemDTO.setPrice(ITEM_PRICE);
        
        // ___________________________________________
        // Do asserts
        // -------------------------------------------
        assertThat(itemDTO, notNullValue());
        assertThat(itemDTO.getName(), equalTo(ITEM_NAME));
        assertThat(itemDTO.getPrice(), equalTo(ITEM_PRICE));
    }
 
    @Test
    @SuppressWarnings("unlikely-arg-type")
    public void test030_equalsHashcode_pass() {
        LOGGER.debug(LOG_PREFIX + "test030_equalsHashcode_pass");

        // ___________________________________________
        // Do test preparation-
        // -------------------------------------------
        final Set<ItemDTO> set = new HashSet<>();
        
        // ___________________________________________
        // Perform test.
        // -------------------------------------------
        final ItemDTO itemDTO1 = new ItemDTO();
        itemDTO1.setName(ITEM_NAME);
        itemDTO1.setPrice(ITEM_PRICE);

        final ItemDTO itemDTO2 = new ItemDTO();
        itemDTO2.setName(ITEM_NAME);
        itemDTO2.setPrice(ITEM_PRICE);

        final ItemDTO itemDTO3 = new ItemDTO();
        itemDTO3.setName(ITEM_NAME + "!");
        itemDTO3.setPrice(null);
        
        set.add(itemDTO1);
        set.add(itemDTO2);
        set.add(itemDTO3);
        
        final ItemDTO itemDTO4 = ItemDTO.name(null).price(null).build();
        final ItemDTO itemDTO5 = null;
        
        // ___________________________________________
        // Do asserts
        // -------------------------------------------
        //
        // Must be 2, because itemDTO1/itemDTO2 are equal.
        assertThat(set.size(), equalTo(2));
        assertThat(true, equalTo(set.contains(itemDTO1)));
        assertThat(set.iterator().next(), notNullValue());
        assertThat(itemDTO1.equals(itemDTO1), equalTo(true));
        assertThat(itemDTO1.equals(itemDTO2), equalTo(true));
        assertThat(itemDTO1.equals(itemDTO3), equalTo(false));
        assertThat(itemDTO1.equals(itemDTO4), equalTo(false));
        assertThat(itemDTO1.equals(itemDTO5), equalTo(false));
        assertThat(itemDTO1.equals(null), equalTo(false));
        assertThat(itemDTO1.equals(new ItemDTO()), equalTo(false));
        assertThat(itemDTO1.equals(new String()), equalTo(false));
        assertThat(itemDTO1.hashCode(), equalTo(itemDTO2.hashCode()));
        assertThat(itemDTO1.hashCode(), not(equalTo(itemDTO3.hashCode())));
        assertThat(itemDTO1.hashCode(), not(equalTo(itemDTO4.hashCode())));
    }
}
