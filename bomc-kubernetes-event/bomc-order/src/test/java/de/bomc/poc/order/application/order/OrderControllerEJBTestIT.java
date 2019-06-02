/**
 * Project: bomc-order
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.order.ArquillianBase;
import de.bomc.poc.order.CategoryFastIntegrationTestIT;
import de.bomc.poc.order.application.customer.dto.CustomerDTO;
import de.bomc.poc.order.application.customer.mapping.DTOEntityCustomerMapper;
import de.bomc.poc.order.application.customer.mapping.DTOEntityCustomerMapperImpl;
import de.bomc.poc.order.application.internal.ApplicationUserEnum;
import de.bomc.poc.order.application.order.dto.AddressDTO;
import de.bomc.poc.order.application.order.dto.ItemDTO;
import de.bomc.poc.order.application.order.dto.OrderDTO;
import de.bomc.poc.order.application.order.dto.OrderLineDTO;
import de.bomc.poc.order.application.order.mapping.DTOEntityItemMapper;
import de.bomc.poc.order.application.order.mapping.DTOEntityItemMapperImpl;
import de.bomc.poc.order.application.order.mapping.DTOEntityOrderMapper;
import de.bomc.poc.order.application.order.mapping.DTOEntityOrderMapperImpl;
import de.bomc.poc.order.domain.model.basis.AbstractEntity;
import de.bomc.poc.order.domain.model.basis.AbstractMetadataEntity;
import de.bomc.poc.order.domain.model.basis.DomainObject;
import de.bomc.poc.order.domain.model.customer.CustomerEntity;
import de.bomc.poc.order.domain.model.customer.JpaCustomerDao;
import de.bomc.poc.order.domain.model.item.ItemEntity;
import de.bomc.poc.order.domain.model.item.JpaItemDao;
import de.bomc.poc.order.domain.model.order.AddressEntity;
import de.bomc.poc.order.domain.model.order.JpaOrderDao;
import de.bomc.poc.order.domain.model.order.OrderEntity;
import de.bomc.poc.order.domain.model.order.OrderLineEntity;
import de.bomc.poc.order.infrastructure.persistence.basis.JpaGenericDao;
import de.bomc.poc.order.infrastructure.persistence.basis.impl.AbstractJpaDao;
import de.bomc.poc.order.infrastructure.persistence.basis.producer.DatabaseProducer;
import de.bomc.poc.order.infrastructure.persistence.basis.qualifier.JpaDao;
import de.bomc.poc.order.infrastructure.persistence.customer.JpaCustomerDaoImpl;
import de.bomc.poc.order.infrastructure.persistence.item.JpaItemDaoImpl;
import de.bomc.poc.order.infrastructure.persistence.order.JpaOrderDaoImpl;

/**
 * Tests the {@link OrderControllerEJB}.
 * <p>
 * 
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=OrderControllerEJBTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 15.03.2019
 */
@RunWith(Arquillian.class)
@Category(CategoryFastIntegrationTestIT.class)
public class OrderControllerEJBTestIT extends ArquillianBase {

    private static final String LOG_PREFIX = "OrderControllerEJBTestIT#";
    private static final String WEB_ARCHIVE_NAME = "bomc-order-ejb-war";
    // _______________________________________________
    // Test constants
    // -----------------------------------------------
    private static final String USER_ID = UUID.randomUUID().toString();
    private static final String NAME = "iPhone";
    private static final String ADDRESS_CITY = "myCity";
    private static final String ADDRESS_STREET = "myStreet";
    private static final String ADDRESS_ZIP = "myZip";
    private static final String BILLING_CITY = "myCity";
    private static final String BILLING_STREET = "myStreet";
    private static final String BILLING_ZIP = "myZip";
    private static final String CUSTOMER_USERNAME = "bomc@bomc.org";
    private static final Long ORDER_ID = 42L;
    private static final Integer ORDERLINE_QUANTITY = 42;

    // _______________________________________________
    // Membervariables
    // -----------------------------------------------
    @Inject
    @LoggerQualifier
    private Logger logger;
    @Inject
    private UserTransaction utx;
    @EJB
    private OrderController orderController;
    @Inject
    @JpaDao
    private JpaCustomerDao jpaCustomerDao;
    @Inject
    @JpaDao
    private JpaOrderDao jpaOrderDao;

    // 'testable = true', means all the tests are running inside of the
    // container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
        webArchive.addClasses(OrderControllerEJBTestIT.class, CategoryFastIntegrationTestIT.class);
        webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
        webArchive.addClasses(JpaItemDao.class, JpaItemDaoImpl.class, JpaCustomerDao.class, JpaCustomerDaoImpl.class);
        webArchive.addClasses(JpaOrderDao.class, JpaOrderDaoImpl.class);
        webArchive.addClasses(CustomerEntity.class, ItemEntity.class, OrderEntity.class, OrderLineEntity.class,
                AddressEntity.class, AbstractEntity.class, AbstractMetadataEntity.class, DomainObject.class);
        webArchive.addClasses(AbstractJpaDao.class, JpaGenericDao.class, DatabaseProducer.class, JpaDao.class);
        webArchive.addClasses(ApplicationUserEnum.class);
        webArchive.addClasses(ItemDTO.class, OrderDTO.class, OrderLineDTO.class, AddressDTO.class, CustomerDTO.class);
        webArchive.addClasses(DTOEntityItemMapper.class, DTOEntityItemMapperImpl.class, DTOEntityOrderMapper.class,
                DTOEntityOrderMapperImpl.class, DTOEntityCustomerMapper.class, DTOEntityCustomerMapperImpl.class);
        webArchive.addClasses(OrderController.class, OrderControllerEJB.class);
        //
        // Add initial data.
        webArchive.addAsResource("test.scripts/customer_item_import.sql", "import.sql");
        //
        // Add dependencies
        final ConfigurableMavenResolverSystem resolver = Maven.configureResolver().withMavenCentralRepo(false);

        // NOTE@MVN:will be changed during mvn project generating.
        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("de.bomc.poc:exception-lib-ext:jar:?")
                .withTransitivity().asFile());
        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.mapstruct:mapstruct:jar:?")
                .withTransitivity().asFile());

        System.out.println(LOG_PREFIX + "createDeployment: " + webArchive.toString(true));

        return webArchive;
    }

    /**
     * Setup.
     */
    @Before
    public void setupClass() {
        //
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=OrderControllerEJBTestIT#test010_addOrderLine_pass
     *
     * <b><code>test010_addOrderLine_pass</code>:</b><br>
     *
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - 
     *
     * <b>Postconditions:</b><br>
     *  -
     * </pre>
     * 
     * @throws Exception
     *             is not expected.
     */
    @Test
    @InSequence(10)
    public void test010_addOrderLine_pass() throws Exception {
        this.logger.debug(LOG_PREFIX + "test010_addOrderLine_pass");

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        this.utx.begin();

        // Read order from db, see customer_item_import.sql.
        final CustomerEntity customerEntity = this.jpaCustomerDao.findByUsername(CUSTOMER_USERNAME, USER_ID);
        final OrderDTO orderDTO = this.getOrderDTO();
        // Create a order instance.
        final OrderEntity orderEntity = new OrderEntity();
        orderEntity.setCustomer(customerEntity);
        orderEntity.setBillingAddress(this.getAddressEntity(ADDRESS_CITY, ADDRESS_STREET, ADDRESS_ZIP));
        orderEntity.setShippingAddress(this.getAddressEntity(BILLING_CITY, BILLING_STREET, BILLING_ZIP));
        this.jpaOrderDao.persist(orderEntity, USER_ID);

        this.utx.commit();

        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        orderDTO.setOrderId(orderEntity.getId());
        this.orderController.addLine(orderDTO, USER_ID);

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        this.utx.begin();

        this.jpaOrderDao.clearEntityManagerCache();

        this.utx.commit();

        final OrderDTO retOrderDTO = this.orderController.findOrderById(orderEntity.getId(), USER_ID);

        assertThat(retOrderDTO, notNullValue());
        assertThat(retOrderDTO.getOrderLineDTOSet().size(), equalTo(1));
        final OrderLineDTO retOrderLine = orderDTO.getOrderLineDTOSet().iterator().next();
        assertThat(retOrderLine.getQuantity(), equalTo(ORDERLINE_QUANTITY));
        assertThat(retOrderLine.getItem().getName(), equalTo(NAME));
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=OrderControllerEJBTestIT#test020_removeOrder_pass
     *
     * <b><code>OrderControllerEJBTestIT</code>:</b><br>
     *
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - 
     *
     * <b>Postconditions:</b><br>
     *  -
     * </pre>
     * 
     * @throws Exception
     *             is not expected.
     */
    @Test
    @InSequence(20)
    public void test020_removeOrder_pass() throws Exception {
        this.logger.debug(LOG_PREFIX + "test020_removeOrder_pass");

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        this.utx.begin();

        // Read order from db, see customer_item_import.sql.
        final CustomerEntity customerEntity = this.jpaCustomerDao.findByUsername(CUSTOMER_USERNAME, USER_ID);
        final OrderDTO orderDTO = this.getOrderDTO();
        // Create a order instance.
        final OrderEntity orderEntity = new OrderEntity();
        orderEntity.setCustomer(customerEntity);
        orderEntity.setBillingAddress(this.getAddressEntity(ADDRESS_CITY, ADDRESS_STREET, ADDRESS_ZIP));
        orderEntity.setShippingAddress(this.getAddressEntity(BILLING_CITY, BILLING_STREET, BILLING_ZIP));
        this.jpaOrderDao.persist(orderEntity, USER_ID);

        this.utx.commit();

        orderDTO.setOrderId(orderEntity.getId());
        this.orderController.addLine(orderDTO, USER_ID);
        
        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        this.orderController.deleteOrderById(orderEntity.getId(), USER_ID);

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        final OrderDTO assertOrderDTO = this.orderController.findOrderById(orderEntity.getId(), USER_ID);
        assertThat(assertOrderDTO, nullValue());
    }
    
    private OrderDTO getOrderDTO() {
        final OrderDTO orderDTO = new OrderDTO();

        final Set<OrderLineDTO> orderLineDTOSet = new HashSet<>();
        final OrderLineDTO orderLineDTO = new OrderLineDTO();
        orderLineDTO.setQuantity(ORDERLINE_QUANTITY);
        orderLineDTO.setItem(ItemDTO.name(NAME).price(null).build());
        orderLineDTOSet.add(orderLineDTO);
        orderDTO.setOrderLineDTOSet(orderLineDTOSet);
        orderDTO.setOrderId(ORDER_ID);
        return orderDTO;
    }

    private AddressEntity getAddressEntity(final String city, final String street, final String address) {
        final AddressEntity addressEntity = new AddressEntity();
        addressEntity.setCity(ADDRESS_CITY);
        addressEntity.setStreet(ADDRESS_STREET);
        addressEntity.setZip(ADDRESS_ZIP);

        return addressEntity;
    }
}
