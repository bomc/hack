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
package de.bomc.poc.order.domain.model.order;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.FixMethodOrder;
//import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
//import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import de.bomc.poc.order.AbstractUnitTest;
import de.bomc.poc.order.CategoryFastUnitTest;
import de.bomc.poc.order.domain.model.customer.CustomerEntity;
import de.bomc.poc.order.domain.model.item.ItemEntity;

/**
 * Test the {@link OrderEntity}, {@link OrderLineEntity} and
 * {@link AddressEntity}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 14.02.2018
 */
@Category(CategoryFastUnitTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrderTest extends AbstractUnitTest {

    private static final Logger LOGGER = Logger.getLogger(OrderTest.class);
    private static final String LOG_PREFIX = "OrderTest#";
//    @Rule
//    public final ExpectedException thrown = ExpectedException.none();
    private EntityTransaction utx;
    // _______________________________________________
    // Constants
    // -----------------------------------------------
    private static final String CREATE_USER = "createUser";
    private static final String ITEM1_NAME = "itemName1";
    private static final Double ITEM1_PRICE = 2.99d;
    private static final Integer COUNT_ITEM1 = 42;
    private static final String ITEM2_NAME = "itemName2";
    private static final Double ITEM2_PRICE = 5.99d;
    private static final int COUNT_ITEM2 = 24;
    private static final String ITEM3_NAME = "itemName3";
    private static final Double ITEM3_PRICE = 7.99d;
    private static final int COUNT_ITEM3 = 4;
    private static final String FIRST_NAME = "firstName";
    private static final String NAME = "name";
    private static final String USER_NAME = "userName";

    @Before
    public void setup() {
        this.entityManager = this.emProvider.getEntityManager();
        assertThat(this.entityManager, notNullValue());

        this.utx = this.entityManager.getTransaction();
        assertThat(this.utx, notNullValue());
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=OrderTest#test010_createOrder_pass
     *
     * <b><code>test010_createOrder_pass</code>:</b><br>
     *  Tests the Cascading.PERSIST relationship between OrderEntity and OrderLineEntity.
     *  
     * <b>Preconditions:</b><br>
     *  - Create three ItemEntity instances.
     *  - Create a OrderEntity instance with a billing- and a shippingAddress.
     *  - Add three lines by invoking the addLine method.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Persist OrderEntity instance to db.
     *  - Remove OrderEntity from db.
     *
     * <b>Postconditions:</b><br>
     *  - Check asserts below.
     * </pre>
     */
    @Test
    public void test010_createRemoverOrder_pass() {
        LOGGER.debug(LOG_PREFIX + "test010_createRemoverOrder_pass");

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        final ItemEntity itemEntity1 = new ItemEntity(ITEM1_NAME, ITEM1_PRICE);
        itemEntity1.setCreateUser(CREATE_USER);

        final ItemEntity itemEntity2 = new ItemEntity(ITEM2_NAME, ITEM2_PRICE);
        itemEntity2.setCreateUser(CREATE_USER);

        final ItemEntity itemEntity3 = new ItemEntity(ITEM3_NAME, ITEM3_PRICE);
        itemEntity3.setCreateUser(CREATE_USER);

        final CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setCreateUser(CREATE_USER);
        customerEntity.setFirstname(FIRST_NAME);
        customerEntity.setName(NAME);
        customerEntity.setUsername(USER_NAME);

        final OrderEntity orderEntity = new OrderEntity();
        orderEntity.setCreateUser(CREATE_USER);

        orderEntity.setBillingAddress(this.createAddress("billingAddress"));
        orderEntity.setShippingAddress(this.createAddress("shippingAddress"));

        orderEntity.setCustomer(customerEntity);

        // ___________________________________________
        // Perform actual test - create.
        // -------------------------------------------
        this.utx.begin();

        orderEntity.addLine(COUNT_ITEM1, itemEntity1, CREATE_USER);
        this.entityManager.persist(orderEntity);

        this.utx.commit();

        this.utx.begin();
        // A new transaction set the modify date on the orderEntity.
        orderEntity.addLine(COUNT_ITEM2, itemEntity2, CREATE_USER);
        this.entityManager.persist(orderEntity);

        orderEntity.addLine(COUNT_ITEM3, itemEntity3, CREATE_USER);
        this.entityManager.persist(orderEntity);

        this.utx.commit();

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(orderEntity.getId(), notNullValue());
        assertThat(orderEntity.getBillingAddress(), notNullValue());
        assertThat(orderEntity.getBillingAddress().getCity(), notNullValue());
        assertThat(orderEntity.getBillingAddress().getStreet(), notNullValue());
        assertThat(orderEntity.getBillingAddress().getZip(), notNullValue());
        assertThat(orderEntity.getShippingAddress(), notNullValue());
        assertThat(orderEntity.getCreateUser(), equalTo(CREATE_USER));
        assertThat(true, equalTo(orderEntity.getCreateDateTime().isBefore(LocalDateTime.now())));
        assertThat(orderEntity.getModifyUser(), nullValue());
        assertThat(orderEntity.getModifyDateTime(), notNullValue());
        assertThat(orderEntity.getNumberOfLines(), equalTo(3));
        assertThat(orderEntity.getCustomer(), notNullValue());
        assertThat(orderEntity.getCustomer().getCreateUser(), notNullValue());
        assertThat(orderEntity.getCustomer().getFirstname(), equalTo(FIRST_NAME));
        assertThat(orderEntity.getCustomer().getName(), equalTo(NAME));
        assertThat(orderEntity.getCustomer().getUsername(), equalTo(USER_NAME));

        LOGGER.debug(
                LOG_PREFIX + "test010_createOrder_pass - [orderEntity=" + orderEntity + ", orderEntity.totalPrice=" + orderEntity.totalPrice() + "]");

        // ___________________________________________
        // Perform actual test - remove.
        // -------------------------------------------

        this.utx.begin();

        this.entityManager.remove(orderEntity);
        this.entityManager.flush();

        this.utx.commit();

        assertThat(this.entityManager.find(ItemEntity.class, itemEntity1.getId()), notNullValue());
        assertThat(this.entityManager.find(ItemEntity.class, itemEntity2.getId()), notNullValue());
        assertThat(this.entityManager.find(ItemEntity.class, itemEntity3.getId()), notNullValue());

        assertThat(this.entityManager.find(CustomerEntity.class, customerEntity.getId()), notNullValue());

        final Set<OrderLineEntity> orderLineEntitySet = orderEntity.getOrderLineSet();
        orderLineEntitySet.forEach(o -> assertThat(this.entityManager.find(OrderLineEntity.class, o.getId()), nullValue()));
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=OrderTest#test020_removeOrder_pass
     *
     * <b><code>test020_removeOrder_pass</code>:</b><br>
     *  Tests the Cascading.REMOVE relationship between OrderEntity and OrderLineEntity. Further...
     *  
     * <b>Preconditions:</b><br>
     *  - Create two ItemEntity instances.
     *  - Create a OrderEntity instance with a billing- and a shippingAddress.
     *  - Add two lines by invoking the addLine method.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  -
     *
     * <b>Postconditions:</b><br>
     *  - Check asserts below.
     * </pre>
     */
    @Test
    public void test020_removeOrder_pass() {
        LOGGER.debug(LOG_PREFIX + "test020_removeOrder_pass");

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        final ItemEntity itemEntity1 = new ItemEntity(ITEM1_NAME, ITEM1_PRICE);
        itemEntity1.setCreateUser(CREATE_USER);

        final ItemEntity itemEntity2 = new ItemEntity(ITEM2_NAME, ITEM2_PRICE);
        itemEntity2.setCreateUser(CREATE_USER);

        final CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setCreateUser(CREATE_USER);
        customerEntity.setFirstname(FIRST_NAME);
        customerEntity.setName(NAME);
        customerEntity.setUsername(USER_NAME);

        final OrderEntity orderEntity = new OrderEntity();
        orderEntity.setCreateUser(CREATE_USER);

        final AddressEntity billingAddress = this.createAddress("billingAddress");
        orderEntity.setBillingAddress(billingAddress);
        final AddressEntity shippingAddress = this.createAddress("shippingAddress");
        orderEntity.setShippingAddress(shippingAddress);

        orderEntity.addLine(COUNT_ITEM1, itemEntity1, CREATE_USER);
        orderEntity.addLine(COUNT_ITEM2, itemEntity2, CREATE_USER);

        orderEntity.setCustomer(customerEntity);

        this.utx.begin();
        this.entityManager.persist(orderEntity);
        this.entityManager.flush();
        this.utx.commit();

        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        this.utx.begin();
        this.entityManager.remove(orderEntity);
        this.entityManager.flush();
        this.utx.commit();

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        final OrderEntity rOrderEntity = this.entityManager.find(OrderEntity.class, orderEntity.getId());
        final CustomerEntity rCustomerEntity = this.entityManager.find(CustomerEntity.class, customerEntity.getId());
        final ItemEntity rItemEntity = this.entityManager.find(ItemEntity.class, itemEntity1.getId());

        assertThat(rOrderEntity, nullValue());
        assertThat(rCustomerEntity, notNullValue());
        assertThat(rItemEntity, notNullValue());
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=OrderTest#test030_createOrderLine_pass
     *
     * <b><code>test030_createOrderLine_pass</code>:</b><br>
     *  Tests the Cascading.PERSIST relationship between OrderLineEntity and ItemEntity.
     *  
     * <b>Preconditions:</b><br>
     *  - Create two OrderLineEntity instances.
     *  - Create one ItemEntity.
     *  - Add the created ItemEntity to the OrderLineEntity instances.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Persist both OrderLineEntity instances.
     *
     * <b>Postconditions:</b><br>
     *  - Check asserts below.
     * </pre>
     */
    @Test
    public void test030_createOrderLine_pass() {
        LOGGER.debug(LOG_PREFIX + "test030_createOrderLine_pass");

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        final ItemEntity itemEntity = new ItemEntity(ITEM1_NAME, ITEM1_PRICE);
        itemEntity.setCreateUser(CREATE_USER);

        final OrderLineEntity orderLineEntity1 = new OrderLineEntity();
        orderLineEntity1.setCreateUser(CREATE_USER + "_1");

        orderLineEntity1.setItem(itemEntity);
        orderLineEntity1.setQuantity(COUNT_ITEM1);

        final OrderLineEntity orderLineEntity2 = new OrderLineEntity();
        orderLineEntity2.setCreateUser(CREATE_USER + "_2");

        orderLineEntity2.setItem(itemEntity);
        orderLineEntity2.setQuantity(COUNT_ITEM2);

        // ___________________________________________
        // Perform persist test.
        // -------------------------------------------
        this.utx.begin();

        this.entityManager.persist(orderLineEntity1);
        this.entityManager.persist(orderLineEntity2);

        this.utx.commit();

        LOGGER.debug(LOG_PREFIX + "test020_createOrderLine_pass - "
                + this.entityManager.find(OrderLineEntity.class, orderLineEntity1.getId()));
        LOGGER.debug(LOG_PREFIX + "test020_createOrderLine_pass - "
                + this.entityManager.find(OrderLineEntity.class, orderLineEntity2.getId()));

        LOGGER.debug(LOG_PREFIX + "test020_createOrderLine_pass - "
                + this.entityManager.find(ItemEntity.class, itemEntity.getId()));

        final Long itemId = itemEntity.getId();

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(orderLineEntity1.getId(), notNullValue());
        assertThat(orderLineEntity1.getCreateUser(), equalTo(CREATE_USER + "_1"));
        
        System.out.println(orderLineEntity1.getCreateDateTime() + ", " + LocalDateTime.now());
        
        assertThat(true, equalTo(orderLineEntity1.getCreateDateTime().isBefore(LocalDateTime.now().plusSeconds(1L))));
        assertThat(orderLineEntity1.getModifyUser(), nullValue());
        assertThat(orderLineEntity1.getModifyDateTime(), notNullValue());
        assertThat(orderLineEntity1.getQuantity(), equalTo(COUNT_ITEM1));

        assertThat(orderLineEntity1.getItem().getId(), equalTo(itemId));
        assertThat(orderLineEntity1.getItem().getName(), equalTo(ITEM1_NAME));
        assertThat(orderLineEntity1.getItem().getPrice(), equalTo(ITEM1_PRICE));
        assertThat(orderLineEntity1.getItem().getCreateUser(), equalTo(CREATE_USER));
        assertThat(orderLineEntity1.getItem().getCreateDateTime(), notNullValue());
        assertThat(orderLineEntity1.getItem().getModifyUser(), nullValue());
        assertThat(orderLineEntity1.getItem().getModifyDateTime(), notNullValue()) ;

        assertThat(orderLineEntity2.getId(), notNullValue());
        assertThat(orderLineEntity2.getCreateUser(), equalTo(CREATE_USER + "_2"));
        assertThat(true, equalTo(orderLineEntity2.getCreateDateTime().isBefore(LocalDateTime.now().plusSeconds(1L))));
        assertThat(orderLineEntity2.getModifyUser(), nullValue());
        assertThat(orderLineEntity2.getModifyDateTime(), notNullValue());
        assertThat(orderLineEntity2.getQuantity(), equalTo(COUNT_ITEM2));

        assertThat(orderLineEntity2.getItem().getId(), equalTo(itemId));
        assertThat(orderLineEntity2.getItem().getName(), equalTo(ITEM1_NAME));
        assertThat(orderLineEntity2.getItem().getPrice(), equalTo(ITEM1_PRICE));
        assertThat(orderLineEntity2.getItem().getCreateUser(), equalTo(CREATE_USER));
        assertThat(orderLineEntity2.getItem().getCreateDateTime(), notNullValue());
        assertThat(orderLineEntity2.getItem().getModifyUser(), nullValue());
        assertThat(orderLineEntity2.getItem().getModifyDateTime(), notNullValue());
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=OrderTest#test040_removeOrderLine_pass
     *
     * <b><code>test040_removeOrderLine_pass</code>:</b><br>
     *  Tests... 
     *  
     * <b>Preconditions:</b><br>
     *  - Create one OrderLineEntity instances.
     *  - Create one ItemEntity.
     *  - Add the created ItemEntity to the OrderLineEntity instances.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Persist both OrderLineEntity instances, cascade.Persist persists both entities.
     *  - Remove the OrderLineEntity instance.
     *
     * <b>Postconditions:</b><br>
     *  - Both instances are removed, OrderLineEntity and ItemEntity.
     * </pre>
     */
    @Test
    public void test040_removeOrderLine_pass() {
        LOGGER.debug(LOG_PREFIX + "test040_removeOrderLine_pass");

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        final ItemEntity itemEntity = new ItemEntity(ITEM1_NAME, ITEM1_PRICE);
        itemEntity.setCreateUser(CREATE_USER);

        final OrderLineEntity orderLineEntity1 = new OrderLineEntity();
        orderLineEntity1.setCreateUser(CREATE_USER + "_1");

        orderLineEntity1.setItem(itemEntity);
        orderLineEntity1.setQuantity(COUNT_ITEM1);

        this.utx.begin();
        this.entityManager.persist(orderLineEntity1);
        this.utx.commit();

        // ___________________________________________
        // Perform persist test.
        // -------------------------------------------
        this.utx.begin();
        this.entityManager.remove(orderLineEntity1);
        this.entityManager.flush();
        this.utx.commit();

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(this.entityManager.find(OrderLineEntity.class, orderLineEntity1.getId()), nullValue());
        assertThat(this.entityManager.find(ItemEntity.class, itemEntity.getId()), notNullValue());
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=OrderTest#test050_findByAllOlderThanGivenDate_pass
     *
     * <b><code>test050_findByAllOlderThanGivenDate_pass</code>:</b><br>
     *  
     *  
     * <b>Preconditions:</b><br>
     *  - Create three ItemEntity instances.
     *  - Create a OrderEntity instance with a billing- and a shippingAddress.
     *  - Add three lines by invoking the addLine method.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Persist OrderEntity instance to db.
     *
     * <b>Postconditions:</b><br>
     *  - Check asserts below.
     * </pre>
     */
    @Test
    public void test050_findByAllOlderThanGivenDate_pass() {
        LOGGER.debug(LOG_PREFIX + "test050_findByAllOlderThanGivenDate_pass");

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        final ItemEntity itemEntity1 = new ItemEntity(ITEM1_NAME, ITEM1_PRICE);
        itemEntity1.setCreateUser(CREATE_USER);

        final ItemEntity itemEntity2 = new ItemEntity(ITEM2_NAME, ITEM2_PRICE);
        itemEntity2.setCreateUser(CREATE_USER);

        final ItemEntity itemEntity3 = new ItemEntity(ITEM3_NAME, ITEM3_PRICE);
        itemEntity3.setCreateUser(CREATE_USER);

        final CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setCreateUser(CREATE_USER);
        customerEntity.setFirstname(FIRST_NAME);
        customerEntity.setName(NAME);
        customerEntity.setUsername(USER_NAME);

        final OrderEntity orderEntity = new OrderEntity();
        orderEntity.setCreateUser(CREATE_USER);

        orderEntity.setBillingAddress(this.createAddress("billingAddress"));
        orderEntity.setShippingAddress(this.createAddress("shippingAddress"));

        orderEntity.setCustomer(customerEntity);

        // Persist order and the lines.
        this.utx.begin();

        orderEntity.addLine(COUNT_ITEM1, itemEntity1, CREATE_USER);
        this.entityManager.persist(orderEntity);

        this.utx.commit();

        this.utx.begin();
        // A new transaction set the modify date on the orderEntity.
        orderEntity.addLine(COUNT_ITEM2, itemEntity2, CREATE_USER);
        this.entityManager.persist(orderEntity);

        orderEntity.addLine(COUNT_ITEM3, itemEntity3, CREATE_USER);
        this.entityManager.persist(orderEntity);

        this.utx.commit();

        LOGGER.debug(LOG_PREFIX + "test050_findByAllOlderThanGivenDate_pass [createDate=" + orderEntity.getCreateDateTime() + ", modifyDate=" + orderEntity.getModifyDateTime() + ", comparableDate=" + LocalDateTime.now().minusMinutes(1L) + "]");
        
        // ___________________________________________
        // Perform actual test, run the namedQuery.
        // -------------------------------------------
        final TypedQuery<OrderEntity> query = this.entityManager.createNamedQuery(OrderEntity.NQ_FIND_ALL_MODIFIED_ORDERS, OrderEntity.class);
        query.setParameter("createDateTime", LocalDateTime.now().minusMinutes(1L));
        query.setParameter("modifyDateTime", LocalDateTime.now().minusMinutes(1L));
        
        final List<OrderEntity> resultList = query.getResultList();
        
        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(resultList.size(), equalTo(1));
    }
    
    private AddressEntity createAddress(final String prefix) {

        final AddressEntity addressEntity = new AddressEntity(prefix + "_street", prefix + "_zip", prefix + "_city");

        return addressEntity;
    }
}
