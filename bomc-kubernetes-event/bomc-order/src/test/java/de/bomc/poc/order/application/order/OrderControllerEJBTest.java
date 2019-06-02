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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import de.bomc.poc.order.CategoryFastUnitTest;
import de.bomc.poc.order.application.customer.dto.CustomerDTO;
import de.bomc.poc.order.application.order.dto.AddressDTO;
import de.bomc.poc.order.application.order.dto.ItemDTO;
import de.bomc.poc.order.application.order.dto.OrderDTO;
import de.bomc.poc.order.application.order.dto.OrderLineDTO;
import de.bomc.poc.order.domain.model.customer.CustomerEntity;
import de.bomc.poc.order.domain.model.customer.JpaCustomerDao;
import de.bomc.poc.order.domain.model.item.ItemEntity;
import de.bomc.poc.order.domain.model.item.JpaItemDao;
import de.bomc.poc.order.domain.model.order.AddressEntity;
import de.bomc.poc.order.domain.model.order.JpaOrderDao;
import de.bomc.poc.order.domain.model.order.OrderEntity;

/**
 * Test the controller {@link OrderControllerEJB}.
 * <p>
 * 
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=OrderControllerEJBTest
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 28.02.2019
 */
@Category(CategoryFastUnitTest.class)
@RunWith(MockitoJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrderControllerEJBTest {

    private static final String LOG_PREFIX = OrderControllerEJBTest.class.getSimpleName() + "#";
    private static final Logger LOGGER = Logger.getLogger(OrderControllerEJBTest.class);
    // _______________________________________________
    // Test constants.
    // -----------------------------------------------
    private static final String USER_ID = UUID.randomUUID().toString();
    private static final String NAME = "myItem";
    private static final Double PRICE = 2.99d;
    private static final String ADDRESS_CITY = "myCity";
    private static final String ADDRESS_STREET = "myStreet";
    private static final String ADDRESS_ZIP = "myZip";
    private static final String CUSTOMER_FIRSTNAME = "myFirstName";
    private static final String CUSTOMER_NAME = "myName";
    private static final String CUSTOMER_USERNAME = "myUsername";
    private static final Long CUSTOMER_ID = 42L;
    // _______________________________________________
    // Mocks
    // -----------------------------------------------
    @Mock
    private Logger logger;
    @Mock
    private JpaItemDao jpaItemDao;
    @Mock
    private JpaCustomerDao jpaCustomerDao;
    @Mock
    private JpaOrderDao jpaOrderDao;
    @InjectMocks
    private OrderControllerEJB sut = new OrderControllerEJB();

    /**
     * <pre>
     *  mvn clean install -Dtest=OrderControllerEJBTest#test010_findAllItems_pass
     *
     * <b><code>test010_findAllItems_pass</code>:</b><br>
     *  Tests the OrderControllerEJB method findAllItems.
     *
     * <b>Preconditions:</b><br>
     *  - Create a list with 3 entities.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Invoke 'findAll' on OrderControllerEJB.
     *
     * <b>Postconditions:</b><br>
     *  Returned list has 3 DTOs.
     * </pre>
     */
    @Test
    public void test010_findAllItems_pass() {
        LOGGER.debug(LOG_PREFIX + "test010_findAllItems_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        final String USER_ID = UUID.randomUUID().toString();
        final List<ItemEntity> itemEntityList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            itemEntityList.add(this.getItemEntity(new Long(i), NAME + i, PRICE + i));
        }

        when(this.jpaItemDao.findAll()).thenReturn(itemEntityList);

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        final List<ItemDTO> itemDTOList = this.sut.findAllItems(USER_ID);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        assertThat(itemDTOList.size(), equalTo(3));
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=OrderControllerEJBTest#test020_findAllItemsWithEmptyList_pass
     *
     * <b><code>test020_findAllItemsWithEmptyList_pass</code>:</b><br>
     *  Tests the OrderControllerEJB method findAllItems and no items are available in db.
     *
     * <b>Preconditions:</b><br>
     *  - 
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Invoke 'findAllItems' on OrderControllerEJB and a empty list is returned..
     *
     * <b>Postconditions:</b><br>
     *  A empty DTO list is returned.
     * </pre>
     */
    @Test
    public void test020_findAllItemsWithEmptyList_pass() {
        LOGGER.debug(LOG_PREFIX + "test020_findAllItemsWithEmptyList_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        when(this.jpaItemDao.findAll()).thenReturn(Collections.emptyList());

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        final List<ItemDTO> itemDTOList = this.sut.findAllItems(USER_ID);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        assertThat(itemDTOList, notNullValue());
        assertThat(true, equalTo(itemDTOList.isEmpty()));
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=OrderControllerEJBTest#test030_findAllOrder_pass
     *
     * <b><code>test030_findAllOrder_pass</code>:</b><br>
     *  Tests the OrderControllerEJB method findAllOrder.
     *
     * <b>Preconditions:</b><br>
     *  - Create a list with 3 entities.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Invoke 'findAll' on OrderControllerEJB and a empty list is returned.
     *
     * <b>Postconditions:</b><br>
     *  A empty DTO list is returned.
     * </pre>
     */
    @Test
    public void test030_findAllOrder_pass() {
        LOGGER.debug(LOG_PREFIX + "test030_findAllOrder_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        when(this.jpaOrderDao.findAll()).thenReturn(this.getOrderEntityList());

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        final List<OrderDTO> orderDTOList = this.sut.findAllOrder(USER_ID);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        assertThat(orderDTOList, notNullValue());
        assertThat(orderDTOList.size(), equalTo(1));
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=OrderControllerEJBTest#test040_createOrder_pass
     *
     * <b><code>test040_createOrder_pass</code>:</b><br>
     *  Tests the OrderControllerEJB method createOrder.
     *
     * <b>Preconditions:</b><br>
     *  - mock invocations:
     *      - jpaItemDao.findByName
     *      - jpaCustomerDao.findByUsername
     *      - jpaOrderDao.persist
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Invoke 'createOrder' on OrderControllerEJB and the technical id is returned.
     *
     * <b>Postconditions:</b><br>
     *  The technical id is returned.
     * </pre>
     */
    @Test
    public void test040_createOrder_pass() {
        this.logger.debug(LOG_PREFIX + "createOrder");

        final Long orderId = 42L;

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        when(this.jpaItemDao.findByName(any(String.class), any(String.class)))
                .thenReturn(this.getItemEntity(42L, NAME, PRICE));
        when(this.jpaCustomerDao.findByUsername(any(String.class), any(String.class)))
                .thenReturn(this.getCustomerEntity());
        doAnswer(new Answer<Long>() {
            @Override
            public Long answer(final InvocationOnMock invocation) throws Throwable {
                // Get method parameter and manipulate the CustomerEntity.
                final Object[] args = invocation.getArguments();
                // Set id to allow assertion.
                final OrderEntity orderEntity = (OrderEntity) args[0];
                orderEntity.setId(orderId);

                return null;
            }
        }).when(this.jpaOrderDao).persist(any(OrderEntity.class), any(String.class));

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        final Long retId = this.sut.createOrder(this.getOrderDTO(), USER_ID);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        assertThat(retId, equalTo(orderId));
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=OrderControllerEJBTest#test050_findOrderById_pass
     *
     * <b><code>test050_findOrderById_pass</code>:</b><br>
     *  Tests the OrderControllerEJB method findOrderById.
     *
     * <b>Preconditions:</b><br>
     *  - mock: this.jpaOrderDao.findById.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Invoke 'findOrderById' on OrderControllerEJB and a OrderEntity is returned.
     *  - The entity is mapped to a dto.
     *
     * <b>Postconditions:</b><br>
     *  A OrderDTO is returned.
     * </pre>
     */
    @Test
    public void test050_findOrderById_pass() {
        LOGGER.debug(LOG_PREFIX + "test050_findOrderById_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        final Long orderId = 42L;
        final String userId = UUID.randomUUID().toString();
        final OrderEntity orderEntity = this.getOrderEntityList().get(0);

        when(this.jpaOrderDao.findById(orderId)).thenReturn(orderEntity);

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        final OrderDTO orderDTO = this.sut.findOrderById(orderId, userId);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        assertThat(orderDTO, notNullValue());
        assertThat(orderDTO.getBillingAddress(), notNullValue());
        assertThat(orderDTO.getShippingAddress(), notNullValue());
        assertThat(orderDTO.getCustomer(), notNullValue());
        assertThat(orderDTO.getOrderLineDTOSet(), notNullValue());
        assertThat(orderDTO.getOrderLineDTOSet().size(), equalTo(1));
        assertThat(orderDTO.getOrderLineDTOSet().iterator().next().getItem(), notNullValue());
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=OrderControllerEJBTest#test060_nullValue_pass
     *
     * <b><code>test060_nullValue_pass</code>:</b><br>
     *  Tests the return value in case the modified date is null.
     *
     * <b>Preconditions:</b><br>
     *  -
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The method under test is invoked.
     *  - The jpa return value is null.
     *
     * <b>Postconditions:</b><br>
     *  A null value is returned.
     * </pre>
     */
    @Test
    public void test060_nullValue_pass() {
        LOGGER.debug(LOG_PREFIX + "test060_nullValue_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        when(this.jpaOrderDao.findLatestModifiedDateTime(USER_ID)).thenReturn(null);

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        final LocalDateTime modifyDateTime = sut.findLatestModifiedDateTime(USER_ID);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        assertThat(modifyDateTime, nullValue());
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=OrderControllerEJBTest#test070_modifyDate_pass
     *
     * <b><code>test070_modifyDate_pass</code>:</b><br>
     *  Tests the 'findLatestModifiedDateTime'-method in 'OrderControllerEJB'.
     *
     * <b>Preconditions:</b><br>
     *  -
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - sut is invoked.
     *  - jpaOrderDao returns current LocalDateTime instance.
     *
     * <b>Postconditions:</b><br>
     *  The sut returned date/time is equals the date/time from jpaOrderDao.
     * </pre>
     */
    @Test
    public void test070_modifyDate_pass() {
        LOGGER.debug(LOG_PREFIX + "test070_modifyDate_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        final LocalDateTime localDateTime = LocalDateTime.now();
        when(this.jpaOrderDao.findLatestModifiedDateTime(USER_ID)).thenReturn(localDateTime);

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        final LocalDateTime modifyDateTime = sut.findLatestModifiedDateTime(USER_ID);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        assertThat(modifyDateTime, equalTo(localDateTime));
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=OrderControllerEJBTest#test080_findByAllOlderThanGivenDate_pass
     *
     * <b><code>test080_findByAllOlderThanGivenDate_pass</code>:</b><br>
     *  Tests the 'findByAllOlderThanGivenDate'-method in 'OrderControllerEJB'.
     *
     * <b>Preconditions:</b><br>
     *  -
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - sut is invoked.
     *  - jpaOrderDao returns one OrderEntity.
     *
     * <b>Postconditions:</b><br>
     *  The sut returned one OrderDTO.
     * </pre>
     */
    @Test
    public void test080_findByAllOlderThanGivenDate_pass() {
        LOGGER.debug(LOG_PREFIX + "test080_findByAllOlderThanGivenDate_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        final LocalDateTime modifyDateTime = LocalDateTime.now();
        final List<OrderEntity> orderEntityList = new ArrayList<>();
        final OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(42L);
        orderEntityList.add(orderEntity);

        when(this.jpaOrderDao.findByAllOlderThanGivenDate(modifyDateTime, USER_ID)).thenReturn(orderEntityList);

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        final List<OrderDTO> retOrderDTOList = sut.findByAllOlderThanGivenDate(modifyDateTime, USER_ID);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        assertThat(retOrderDTOList, notNullValue());
        assertThat(retOrderDTOList.size(), equalTo(1));
    }

    // _______________________________________________
    // Helper methods
    // -----------------------------------------------
    private List<OrderEntity> getOrderEntityList() {
        final OrderEntity orderEntity = new OrderEntity();
        orderEntity.setBillingAddress(this.getAddressEntity());
        orderEntity.setShippingAddress(this.getAddressEntity());
        orderEntity.setCustomer(this.getCustomerEntity());
        orderEntity.addLine(42, this.getItemEntity(42L, NAME, PRICE), USER_ID);

        final List<OrderEntity> orderEntityList = new ArrayList<>();
        orderEntityList.add(orderEntity);

        return orderEntityList;
    }

    private ItemEntity getItemEntity(final Long id, final String name, final double price) {
        final ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(id);
        itemEntity.setName(name);
        itemEntity.setPrice(price);

        return itemEntity;
    }

    private AddressEntity getAddressEntity() {
        final AddressEntity addressEntity = new AddressEntity();
        addressEntity.setCity(ADDRESS_CITY);
        addressEntity.setStreet(ADDRESS_STREET);
        addressEntity.setZip(ADDRESS_ZIP);

        return addressEntity;
    }

    private CustomerEntity getCustomerEntity() {
        final CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setFirstname(CUSTOMER_FIRSTNAME);
        customerEntity.setName(CUSTOMER_NAME);
        customerEntity.setUsername(CUSTOMER_USERNAME);
        customerEntity.setId(CUSTOMER_ID);

        return customerEntity;
    }

    private OrderDTO getOrderDTO() {
        final OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBillingAddress(this.getAddressDTO());
        orderDTO.setShippingAddress(this.getAddressDTO());
        orderDTO.setCustomer(this.getCustomerDTO());

        final Set<OrderLineDTO> orderLineDTOSet = new HashSet<>();
        final OrderLineDTO orderLineDTO = new OrderLineDTO();
        orderLineDTO.setQuantity(42);
        orderLineDTO.setItem(ItemDTO.name(NAME).price(PRICE).build());
        orderLineDTOSet.add(orderLineDTO);
        orderDTO.setOrderLineDTOSet(orderLineDTOSet);

        return orderDTO;
    }

    private AddressDTO getAddressDTO() {
        final AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCity(ADDRESS_CITY);
        addressDTO.setStreet(ADDRESS_STREET);
        addressDTO.setZip(ADDRESS_ZIP);

        return addressDTO;
    }

    private CustomerDTO getCustomerDTO() {
        final CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setFirstname(CUSTOMER_FIRSTNAME);
        customerDTO.setName(CUSTOMER_NAME);
        customerDTO.setUsername(CUSTOMER_USERNAME);

        return customerDTO;
    }
}
