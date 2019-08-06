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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;

import de.bomc.poc.order.CategoryFastUnitTest;
import de.bomc.poc.order.application.customer.dto.CustomerDTO;
import de.bomc.poc.order.application.order.dto.AddressDTO;
import de.bomc.poc.order.application.order.dto.ItemDTO;
import de.bomc.poc.order.application.order.dto.OrderDTO;
import de.bomc.poc.order.application.order.dto.OrderLineDTO;
import de.bomc.poc.order.domain.model.customer.CustomerEntity;
import de.bomc.poc.order.domain.model.item.ItemEntity;
import de.bomc.poc.order.domain.model.order.AddressEntity;
import de.bomc.poc.order.domain.model.order.OrderEntity;

/**
 * Tests mapping between {@link OrderDTO} and {@link OrderEntity}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Category(CategoryFastUnitTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DTOEntityOrderMapperTest {

    private static final String LOG_PREFIX = "DTOEntityOrderMapperTest#";
    private static final Logger LOGGER = Logger.getLogger(DTOEntityOrderMapperTest.class);
    // _______________________________________________
    // Test constants
    // -----------------------------------------------
    private static final String BILLING_ZIP = "4000";
    private static final String BILLING_CITY = "Ninjago-City";
    private static final String BILLING_STREET = "Downing Street";
    private static final String SHIPPING_ZIP = "4001";
    private static final String SHIPPING_CITY = "Chicago-City";
    private static final String SHIPPING_STREET = "Down Street";  
    private static final String CUSTOMER_NAME = "name";
    private static final String CUSTOMER_FIRSTNAME = "firstname";
    private static final String CUSTOMER_USERNAME = "username";
//    private static final String ORDER_USER = "myUser";
    private static final Integer QUANTITY = 42;
    private static final String ITEM_NAME = "itemName";
    private static final Double ITEM_PRICE = 42.42d;
    
    /**
     * <pre>
     *  mvn clean install -Dtest=DTOEntityOrderMapperTest#test010_mapEntityToDTO_pass
     *
     * <b><code>test010_mapEntityToDTO_pass</code>:</b><br>
     *  Tests the mapping from OrderEntity to a OrderDTO.
     *
     * <b>Preconditions:</b><br>
     *  - Create a OrderEntity instance.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The mapping is invoked.
     *
     * <b>Postconditions:</b><br>
     *  All attributes of the OrderEntity is mapped to OrderDTO.
     * </pre>
     */
    @Test
    public void test010_mapEntityToDTO_pass() {
        LOGGER.debug(LOG_PREFIX + "test010_mapEntityToDTO_pass");

        // ___________________________________________
        // Do preparation
        // -------------------------------------------
        final OrderEntity orderEntity = new OrderEntity();
        orderEntity.setBillingAddress(this.createAddressEntity(BILLING_ZIP, BILLING_STREET, BILLING_CITY));
        orderEntity.setShippingAddress(this.createAddressEntity(SHIPPING_ZIP, SHIPPING_STREET, SHIPPING_CITY));
        orderEntity.setCustomer(this.createCustomerEntity(CUSTOMER_FIRSTNAME, CUSTOMER_NAME, CUSTOMER_USERNAME));
        
        final ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setName(ITEM_NAME);
        itemEntity1.setPrice(ITEM_PRICE);
        itemEntity1.setId(42L);
//        orderEntity.addLine(QUANTITY, itemEntity1, ORDER_USER);
        
        // ___________________________________________
        // Do test
        // -------------------------------------------
        final OrderDTO orderDTO = DTOEntityOrderMapper.INSTANCE.mapEntityToDTO(orderEntity);
        
        // ___________________________________________
        // Do asserts
        // -------------------------------------------
        assertThat(orderDTO.getBillingAddress().getCity(), equalTo(BILLING_CITY));
        assertThat(orderDTO.getBillingAddress().getStreet(), equalTo(BILLING_STREET));
        assertThat(orderDTO.getBillingAddress().getZip(), equalTo(BILLING_ZIP));
        assertThat(orderDTO.getShippingAddress().getCity(), equalTo(SHIPPING_CITY));
        assertThat(orderDTO.getShippingAddress().getStreet(), equalTo(SHIPPING_STREET));
        assertThat(orderDTO.getShippingAddress().getZip(), equalTo(SHIPPING_ZIP));
        assertThat(orderDTO.getCustomer().getFirstname(), equalTo(CUSTOMER_FIRSTNAME));
        assertThat(orderDTO.getCustomer().getName(), equalTo(CUSTOMER_NAME));
        assertThat(orderDTO.getCustomer().getUsername(), equalTo(CUSTOMER_USERNAME));
//        assertThat(orderDTO.getOrderLineDTOList().size(), equalTo(1));
//        final OrderLineDTO orderLineDTO = orderDTO.getOrderLineDTOList().iterator().next();
//        assertThat(orderLineDTO, notNullValue());
//        assertThat(orderLineDTO.getQuantity(), equalTo(QUANTITY));
//        assertThat(orderLineDTO.getItem(), notNullValue());
//        assertThat(orderLineDTO.getItem().getName(), equalTo(ITEM_NAME));
//        assertThat(orderLineDTO.getItem().getPrice(), equalTo(ITEM_PRICE));
    }
    
    /**
     * <pre>
     *  mvn clean install -Dtest=DTOEntityOrderMapperTest#test020_mapEntityListToDTOList_pass
     *
     * <b><code>test020_mapEntityListToDTOList_pass</code>:</b><br>
     *  Tests the mapping from a OrderEntity list to a OrderDTO list.
     *
     * <b>Preconditions:</b><br>
     *  - Create two OrderEntity instances.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The mapping is invoked.
     *
     * <b>Postconditions:</b><br>
     *  The list has two DTOs, and all attributes are mapped.
     * </pre>
     */
    @Test
    public void test020_mapEntityListToDTOList_pass() {
        LOGGER.debug(LOG_PREFIX + "test020_mapEntityListToDTOList_pass");

        // ___________________________________________
        // Do preparation
        // -------------------------------------------
        final OrderEntity orderEntity1 = new OrderEntity();
        orderEntity1.setId(42L);
        orderEntity1.setBillingAddress(this.createAddressEntity(BILLING_ZIP, BILLING_STREET, BILLING_CITY));
        orderEntity1.setShippingAddress(this.createAddressEntity(SHIPPING_ZIP, SHIPPING_STREET, SHIPPING_CITY));
        orderEntity1.setCustomer(this.createCustomerEntity(CUSTOMER_FIRSTNAME, CUSTOMER_NAME, CUSTOMER_USERNAME));
        
        final ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setName(ITEM_NAME);
        itemEntity1.setPrice(ITEM_PRICE);
        itemEntity1.setId(42L);
//        orderEntity1.addLine(QUANTITY, itemEntity1, ORDER_USER);
        
        final OrderEntity orderEntity2 = new OrderEntity();
        orderEntity1.setId(24L);
        orderEntity2.setBillingAddress(this.createAddressEntity(BILLING_ZIP, BILLING_STREET, BILLING_CITY));
        orderEntity2.setShippingAddress(this.createAddressEntity(SHIPPING_ZIP, SHIPPING_STREET, SHIPPING_CITY));
        orderEntity2.setCustomer(this.createCustomerEntity(CUSTOMER_FIRSTNAME, CUSTOMER_NAME, CUSTOMER_USERNAME));
        
        final ItemEntity itemEntity2 = new ItemEntity();
        itemEntity2.setName(ITEM_NAME);
        itemEntity2.setPrice(ITEM_PRICE);
        itemEntity2.setId(24L);
//        orderEntity2.addLine(QUANTITY, itemEntity2, ORDER_USER);

//        final ItemEntity itemEntity3 = new ItemEntity();
//        itemEntity3.setName(ITEM_NAME);
//        itemEntity3.setPrice(ITEM_PRICE);
//        itemEntity3.setId(4242L);
//        orderEntity2.addLine(QUANTITY, itemEntity3, ORDER_USER);
        
        final List<OrderEntity> orderEntityList = new ArrayList<>();
        orderEntityList.add(orderEntity1);
        orderEntityList.add(orderEntity2);
        
        // ___________________________________________
        // Do test
        // -------------------------------------------
        final List<OrderDTO> orderDTOList = DTOEntityOrderMapper.INSTANCE.mapEntityToDTO(orderEntityList);
        
        // ___________________________________________
        // Do assertion
        // -------------------------------------------
        assertThat(orderDTOList, notNullValue());
        assertThat(orderDTOList.size(), equalTo(2));
        
        for (final OrderDTO orderDTO : orderDTOList) {
//            assertThat(orderDTO.getOrderLineDTOList(), notNullValue());
//            assertThat(orderDTO.getOrderLineDTOList().size(), equalTo(1));
            assertThat(orderDTO.getShippingAddress(), notNullValue());
            assertThat(orderDTO.getShippingAddress(), notNullValue());
        }
    }
    
    /**
     * <pre>
     *  mvn clean install -Dtest=DTOEntityOrderMapperTest#test030_nullCheckEntityToDTO_pass
     *
     * <b><code>test030_nullCheckEntityToDTO_pass</code>:</b><br>
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
    public void test030_nullCheckEntityToDTO_pass() {
        LOGGER.debug(LOG_PREFIX + "test030_nullCheckEntityToDTO_pass");
        
        final OrderEntity orderEntity = null;
        final List<OrderEntity> orderEntityList = null;
        assertThat(DTOEntityOrderMapper.INSTANCE.mapEntityToDTO(orderEntityList), equalTo(null));
        assertThat(DTOEntityOrderMapper.INSTANCE.mapEntityToDTO(orderEntity), equalTo(null));
        
        final OrderEntity orderEntity1 = new OrderEntity();
        orderEntity1.setBillingAddress(null);
        orderEntity1.setShippingAddress(null);
        orderEntity1.setCustomer(null);
        
        final OrderDTO orderDTO = DTOEntityOrderMapper.INSTANCE.mapEntityToDTO(orderEntity1);
        assertThat(orderDTO.getBillingAddress(), equalTo(null));
        assertThat(orderDTO.getShippingAddress(), equalTo(null));
        assertThat(orderDTO.getCustomer(), equalTo(null));
    }
    
    /**
     * <pre>
     *  mvn clean install -Dtest=DTOEntityOrderMapperTest#test040_mapDTOToEntity_pass
     *
     * <b><code>test040_mapDTOToEntity_pass</code>:</b><br>
     *  Tests the mapping from DTO to Entity.
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
    public void test040_mapDTOToEntity_pass() {
        LOGGER.debug(LOG_PREFIX + "test030_nullCheck_pass");
        
        final OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBillingAddress(this.createAddressDTO(BILLING_ZIP, BILLING_STREET, BILLING_CITY));
        orderDTO.setShippingAddress(this.createAddressDTO(SHIPPING_ZIP, SHIPPING_STREET, SHIPPING_CITY));
        orderDTO.setCustomer(this.createCustomerDTO(CUSTOMER_FIRSTNAME, CUSTOMER_NAME, CUSTOMER_USERNAME));
        orderDTO.setOrderLineDTOList(this.createOrderLineList());
        
        final OrderEntity orderEntity = DTOEntityOrderMapper.INSTANCE.mapDTOToEntity(orderDTO);
        
        assertThat(orderEntity.getBillingAddress(), notNullValue());
        assertThat(orderEntity.getBillingAddress().getCity(), equalTo(BILLING_CITY));
        assertThat(orderEntity.getBillingAddress().getStreet(), equalTo(BILLING_STREET));
        assertThat(orderEntity.getBillingAddress().getZip(), equalTo(BILLING_ZIP));
        assertThat(orderEntity.getShippingAddress(), notNullValue());
        assertThat(orderEntity.getShippingAddress().getCity(), equalTo(SHIPPING_CITY));
        assertThat(orderEntity.getShippingAddress().getStreet(), equalTo(SHIPPING_STREET));
        assertThat(orderEntity.getShippingAddress().getZip(), equalTo(SHIPPING_ZIP));
        assertThat(orderEntity.getCustomer(), notNullValue());
        assertThat(orderEntity.getCustomer().getFirstname(), equalTo(CUSTOMER_FIRSTNAME));
        assertThat(orderEntity.getCustomer().getName(), equalTo(CUSTOMER_NAME));
        assertThat(orderEntity.getCustomer().getUsername(), equalTo(CUSTOMER_USERNAME));
    }
    
    /**
     * <pre>
     *  mvn clean install -Dtest=DTOEntityOrderMapperTest#test050_nullCheckDTOToEntity_pass
     *
     * <b><code>test050_nullCheckDTOToEntity_pass</code>:</b><br>
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
    public void test050_nullCheckDTOToEntity_pass() {
        LOGGER.debug(LOG_PREFIX + "test050_nullCheckDTOToEntity_pass");

        assertThat(DTOEntityOrderMapper.INSTANCE.mapDTOToEntity(null), equalTo(null));
        
        final OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBillingAddress(null);
        orderDTO.setShippingAddress(null);
        orderDTO.setCustomer(null);
        
        // NOTE: orderLineSet is not mapped.

        OrderEntity orderEntity = DTOEntityOrderMapper.INSTANCE.mapDTOToEntity(orderDTO);
        
        assertThat(orderEntity.getBillingAddress(), nullValue());
        assertThat(orderEntity.getShippingAddress(), nullValue());
        assertThat(orderEntity.getCustomer(), nullValue());
        
        orderEntity = DTOEntityOrderMapper.INSTANCE.mapDTOToEntity(null);
        assertThat(orderEntity, nullValue());
    }
    
    @Test
    public void test060_nullCheckEntityToDTO_pass() {
        LOGGER.debug(LOG_PREFIX + "test060_nullCheckEntityToDTO_pass");
        
        final OrderEntity nullOrderEntity = null;
        assertThat(DTOEntityOrderMapper.INSTANCE.mapEntityToDTO(nullOrderEntity), nullValue());
        
        final OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(42L);
        orderEntity.setBillingAddress(null);
        orderEntity.setShippingAddress(null);
        orderEntity.setCustomer(null);
//        orderEntity.addLine(0, null, "createUser");
        final OrderDTO orderDTO = DTOEntityOrderMapper.INSTANCE.mapEntityToDTO(orderEntity);
        
        assertThat(orderDTO, notNullValue());
        assertThat(orderDTO.getBillingAddress(), nullValue());
        assertThat(orderDTO.getShippingAddress(), nullValue());
        assertThat(orderDTO.getCustomer(), nullValue());
//        assertThat(orderDTO.getOrderLineDTOList(), notNullValue());
//        
//        final OrderLineDTO orderLineDTO = orderDTO.getOrderLineDTOList().iterator().next();
//        assertThat(orderLineDTO, notNullValue());
//        assertThat(orderLineDTO.getItem(), nullValue());
    }
    
    
    private AddressEntity createAddressEntity(final String zip, final String street, final String city) {
        
        final AddressEntity addressEntity = new AddressEntity();
        addressEntity.setCity(city);
        addressEntity.setStreet(street);
        addressEntity.setZip(zip);
        
        return addressEntity;
    }
    
    private CustomerEntity createCustomerEntity(final String firstname, final String name, final String username) {
        
        final CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setFirstname(firstname);
        customerEntity.setName(name);
        customerEntity.setUsername(username);
        
        return customerEntity;
    }
    
    private AddressDTO createAddressDTO(final String zip, final String street, final String city) {
        
        final AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCity(city);
        addressDTO.setStreet(street);
        addressDTO.setZip(zip);
        
        return addressDTO;
    }
    
    private CustomerDTO createCustomerDTO(final String firstname, final String name, final String username) {
        
        final CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setFirstname(firstname);
        customerDTO.setName(name);
        customerDTO.setUsername(username);
        
        return customerDTO;
    }
    
    private ItemDTO createItemDTO(final Double price, final String name) {
        
        final ItemDTO itemDTO = new ItemDTO();
        itemDTO.setPrice(price);
        itemDTO.setName(name);
        
        return itemDTO;
    }
    
    private List<OrderLineDTO> createOrderLineList() {
        
        final List<OrderLineDTO> orderLineDTOList = new ArrayList<>();
        
        final OrderLineDTO orderLineDTO1 = new OrderLineDTO();
        orderLineDTO1.setItem(this.createItemDTO(ITEM_PRICE, ITEM_NAME));
        orderLineDTO1.setQuantity(QUANTITY);
        orderLineDTOList.add(orderLineDTO1);
        
        final OrderLineDTO orderLineDTO2 = new OrderLineDTO();
        orderLineDTO2.setItem(this.createItemDTO(ITEM_PRICE + 2d, ITEM_NAME + "_2"));
        orderLineDTO2.setQuantity(QUANTITY + 2);
        orderLineDTOList.add(orderLineDTO2);
        
        return orderLineDTOList;
        
    }
}
