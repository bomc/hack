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
package de.bomc.poc.order.application.order.mapping;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;

import de.bomc.poc.order.CategoryFastUnitTest;
import de.bomc.poc.order.application.order.dto.OrderLineDTO;
import de.bomc.poc.order.domain.model.item.ItemEntity;
import de.bomc.poc.order.domain.model.order.OrderLineEntity;

/**
 * Tests mapping between {@link OrderLineDTO} and {@link OrderLineEntity}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Category(CategoryFastUnitTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DTOEntityOrderLineMapperTest {

    private static final String LOG_PREFIX = "DTOEntityOrderLineMapperTest#";
    private static final Logger LOGGER = Logger.getLogger(DTOEntityOrderLineMapperTest.class);
    // _______________________________________________
    // Test constants
    // -----------------------------------------------
    private final String ITEM_NAME = "myName";
    private final Double ITEM_PRICE = 2.99d;
    private final Integer ORDERLINE_QUANTITY = 42;
    
    /**
     * <pre>
     *  mvn clean install -Dtest=DTOEntityOrderLineMapperTest#test010_mapEntityToDTO_pass
     *
     * <b><code>test010_mapEntityToDTO_pass</code>:</b><br>
     *  Tests the mapping from OrderLineEntity to a OrderLineDTO.
     *
     * <b>Preconditions:</b><br>
     *  - Create a OrderLineEntity instance.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The mapping is invoked.
     *
     * <b>Postconditions:</b><br>
     *  All attributes of the OrderLineEntity is mapped to OrderLineDTO.
     * </pre>
     */
    @Test
    public void test010_mapEntityToDTO_pass() {
        LOGGER.debug(LOG_PREFIX + "test010_mapEntityToDTO_pass");

        // ___________________________________________
        // Do preparation
        // -------------------------------------------
        final ItemEntity itemEntity = new ItemEntity();
        itemEntity.setName(ITEM_NAME);
        itemEntity.setPrice(ITEM_PRICE);
        
        final OrderLineEntity orderLineEntity = new OrderLineEntity();
        orderLineEntity.setQuantity(ORDERLINE_QUANTITY);
        orderLineEntity.setItem(itemEntity);
        
        final OrderLineDTO orderLineDTO = DTOEntityOrderLineMapper.INSTANCE.mapEntityToDTO(orderLineEntity);
        
        // ___________________________________________
        // Do asserts
        // -------------------------------------------
        assertThat(orderLineDTO.getQuantity(), equalTo(ORDERLINE_QUANTITY));
        assertThat(orderLineDTO.getItem().getName(), equalTo(ITEM_NAME));
        assertThat(orderLineDTO.getItem().getPrice(), equalTo(ITEM_PRICE));
    }
    
    /**
     * <pre>
     *  mvn clean install -Dtest=DTOEntityOrderLineMapperTest#test020_mapEntityToDTOList_pass
     *
     * <b><code>test020_mapEntityToDTOList_pass</code>:</b><br>
     *  Tests the mapping from OrderLineEntity to a OrderLineDTO as a list.
     *
     * <b>Preconditions:</b><br>
     *  - Create two OrderLineEntity instance.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The mapping is invoked.
     *
     * <b>Postconditions:</b><br>
     *  All elements of the OrderLineEntitys are mapped to OrderLineDTOs.
     * </pre>
     */
    @Test
    public void test020_mapEntityToDTOList_pass() {
        LOGGER.debug(LOG_PREFIX + "test020_mapEntityToDTOList_pass");

        // ___________________________________________
        // Do preparation
        // -------------------------------------------
        final ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setName(ITEM_NAME + "1");
        itemEntity1.setPrice(ITEM_PRICE);
        itemEntity1.setId(1L);
        
        final ItemEntity itemEntity2 = new ItemEntity();
        itemEntity2.setName(ITEM_NAME + "2");
        itemEntity2.setPrice(ITEM_PRICE + 1d);
        itemEntity2.setId(2L);
        
        final OrderLineEntity orderLineEntity1 = new OrderLineEntity();
        orderLineEntity1.setQuantity(ORDERLINE_QUANTITY);
        orderLineEntity1.setItem(itemEntity1);
        orderLineEntity1.setId(3L);
        
        final OrderLineEntity orderLineEntity2 = new OrderLineEntity();
        orderLineEntity2.setQuantity(ORDERLINE_QUANTITY);
        orderLineEntity2.setItem(itemEntity2);
        orderLineEntity2.setId(4L);
        
        final Set<OrderLineEntity> orderLineEntityList = new HashSet<>();
        orderLineEntityList.add(orderLineEntity1);
        orderLineEntityList.add(orderLineEntity2);
        
        final Set<OrderLineDTO> orderLineDTOList = DTOEntityOrderLineMapper.INSTANCE.mapEntityToDTO(orderLineEntityList);
        
        // ___________________________________________
        // Do asserts
        // -------------------------------------------
        assertThat(orderLineDTOList.size(), equalTo(2));
        final OrderLineDTO orderLineDTO1 = orderLineDTOList.iterator().next();
        assertThat(orderLineDTO1, notNullValue()); 
        assertThat(orderLineDTO1.getItem(), notNullValue());
        assertThat(orderLineDTO1.getItem().getName(), containsString(ITEM_NAME));
        assertThat(orderLineDTO1.getQuantity(), notNullValue()); 
        final OrderLineDTO orderLineDTO2 = orderLineDTOList.iterator().next();
        assertThat(orderLineDTO2, notNullValue());
        assertThat(orderLineDTO2.getItem(), notNullValue());
        assertThat(orderLineDTO2.getItem().getName(), containsString(ITEM_NAME));
        assertThat(orderLineDTO2.getQuantity(), notNullValue()); 
    }
    
    /**
     * <pre>
     *  mvn clean install -Dtest=DTOEntityOrderLineMapperTest#test030_nullCheck
     *
     * <b><code>test030_nullCheck</code>:</b><br>
     *  Tests the null check of the mapping.
     *
     * <b>Preconditions:</b><br>
     *  - 
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The mapping is invoked.
     *
     * <b>Postconditions:</b><br>
     *  All invocations return null.
     * </pre>
     */
    @Test
    public void test030_nullCheck() {
        LOGGER.debug(LOG_PREFIX + "test030_nullCheck");
     
        final OrderLineEntity orderLineEntity = null;
        final Set<OrderLineEntity> orderLineEntitySet = null;
        final OrderLineEntity orderLineEntity1 = new OrderLineEntity();
        orderLineEntity1.setQuantity(ORDERLINE_QUANTITY);
        orderLineEntity1.setItem(null);
        orderLineEntity1.setId(3L);
        
        assertThat(DTOEntityOrderLineMapper.INSTANCE.mapEntityToDTO(orderLineEntity), equalTo(null));
        assertThat(DTOEntityOrderLineMapper.INSTANCE.mapEntityToDTO(orderLineEntitySet), equalTo(null));
        
        final OrderLineDTO orderLineDTO = DTOEntityOrderLineMapper.INSTANCE.mapEntityToDTO(orderLineEntity1);
        assertThat(orderLineDTO.getItem(), equalTo(null));
    }
}
