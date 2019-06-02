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
package de.bomc.poc.order.infrastructure.persistence.order;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
import de.bomc.poc.order.application.internal.ApplicationUserEnum;
import de.bomc.poc.order.domain.model.basis.AbstractEntity;
import de.bomc.poc.order.domain.model.basis.AbstractMetadataEntity;
import de.bomc.poc.order.domain.model.basis.DomainObject;
import de.bomc.poc.order.domain.model.customer.CustomerEntity;
import de.bomc.poc.order.domain.model.customer.JpaCustomerDao;
import de.bomc.poc.order.domain.model.item.ItemEntity;
import de.bomc.poc.order.domain.model.order.AddressEntity;
import de.bomc.poc.order.domain.model.order.JpaOrderDao;
import de.bomc.poc.order.domain.model.order.OrderEntity;
import de.bomc.poc.order.domain.model.order.OrderLineEntity;
import de.bomc.poc.order.infrastructure.persistence.basis.JpaGenericDao;
import de.bomc.poc.order.infrastructure.persistence.basis.impl.AbstractJpaDao;
import de.bomc.poc.order.infrastructure.persistence.basis.producer.DatabaseProducer;
import de.bomc.poc.order.infrastructure.persistence.basis.qualifier.JpaDao;
import de.bomc.poc.order.infrastructure.persistence.customer.JpaCustomerDaoImpl;

/**
 * Tests the dao layer for {@link OrderEntity} and {@link JpaOrderDaoImplMock}.
 * <p>
 * 
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=JpaOrderDaoTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 23.11.2018
 */
@RunWith(Arquillian.class)
@Category(CategoryFastIntegrationTestIT.class)
public class JpaOrderDaoTestIT extends ArquillianBase {

    private static final String LOG_PREFIX = "JpaOrderDaoTestIT#";
    private static final String WEB_ARCHIVE_NAME = "bomc-order-dao-war";
    // _______________________________________________
    // Constants
    // -----------------------------------------------
    private static final String E_MAIL = "bomc@bomc.org";
    private static final String NAME = "Name";
    private static final String FIRST_NAME = "Firstname";
    private static final String CREATE_USER = "createUser";

    // _______________________________________________
    // Membervariables
    // -----------------------------------------------
    @Inject
    @LoggerQualifier
    private Logger logger;
    @Inject
    private UserTransaction utx;
    @Inject
    @JpaDao
    private JpaOrderDao jpaOrderDao;
    @Inject
    @JpaDao
    private JpaCustomerDao jpaCustomerDao;

    // 'testable = true', means all the tests are running inside of the
    // container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
        webArchive.addClasses(JpaOrderDaoTestIT.class, CategoryFastIntegrationTestIT.class);
        webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
        webArchive.addClasses(JpaOrderDao.class, JpaOrderDaoImpl.class);
        webArchive.addClasses(JpaCustomerDao.class, JpaCustomerDaoImpl.class);
        webArchive.addClasses(OrderLineEntity.class, AddressEntity.class, ItemEntity.class, CustomerEntity.class,
                OrderEntity.class, AbstractEntity.class, AbstractMetadataEntity.class, DomainObject.class);
        webArchive.addClasses(AbstractJpaDao.class, JpaGenericDao.class, DatabaseProducer.class, JpaDao.class);
        webArchive.addClasses(ApplicationUserEnum.class);

        // Add initial data.
        //
        // Add dependencies
        final ConfigurableMavenResolverSystem resolver = Maven.configureResolver().withMavenCentralRepo(false);

        // NOTE@MVN:will be changed during mvn project generating.
        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("de.bomc.poc:exception-lib-ext:jar:?")
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
     *  mvn clean install -Parq-wildfly-remote -Dtest=JpaOrderDaoTestIT#test010_createOrderEntity_pass
     *
     * <b><code>test010_createOrderEntity_pass</code>:</b><br>
     *
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Create a new instance of OrderEntity and persist it by the jpaOrderDao.
     *
     * <b>Postconditions:</b><br>
     *  - Entity is persisted in db.
     * </pre>
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    @InSequence(10)
    public void test010_createOrderEntity_pass() throws Exception {
        this.logger.debug(LOG_PREFIX + "test010_createOrderEntity_pass");

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        final String username = UUID.randomUUID().toString();

        this.utx.begin();
        final OrderEntity orderEntity = this.createOrderEntity(username);

        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        this.jpaOrderDao.persist(orderEntity, ApplicationUserEnum.SYSTEM_USER.name());

        this.utx.commit();

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(orderEntity.getId(), notNullValue());

        final Long assertId = orderEntity.getId();
        assertThat(this.jpaOrderDao.findById(assertId).getId(), notNullValue());
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=JpaOrderDaoTestIT#test010_createOrderEntity_pass+test020_findLatestModifiedDate_pass
     *
     * <b><code>test020_findLatestModifiedDate_pass</code>:</b><br>
     *  Tests finding the last modified date for OrderEntities. First a modifyDate is not set, second a modifyDate is set.
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *  - The previous test must be successful finished.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - 1. Check against a not modifyDate setting
     *  - 2. Update entity, to set modifyDate.
     *  - 3. Check against a modifyDate setting.
     *
     * <b>Postconditions:</b><br>
     *  - In case 1., null is returned.
     *  - In case 3., a LocalDate instance is returned.
     * </pre>
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    @InSequence(20)
    public void test020_findLatestModifiedDate_pass() throws Exception {
        this.logger.debug(LOG_PREFIX + "test020_findLatestModifiedDate_pass");

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        final String userId = UUID.randomUUID().toString();

        final List<OrderEntity> orderEntityList = this.jpaOrderDao.findAll();
        // There is a order in db from test010_createOrderEntity_pass.
        final OrderEntity orderEntity = orderEntityList.iterator().next();
        assertThat(orderEntity, notNullValue());

        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        //
        // There is no entity in db that is modified.
        LocalDateTime modifiedDateTime = this.jpaOrderDao.findLatestModifiedDateTime(userId);
        assertThat(modifiedDateTime, nullValue());

        // Update db entry, to create a modifyDate.
        this.utx.begin();

        final CustomerEntity customerEntity = this.createCustomerEntity(E_MAIL + "_020");
        this.jpaCustomerDao.persist(customerEntity, ApplicationUserEnum.TEST_USER.name());

        orderEntity.setCustomer(customerEntity);
        this.jpaOrderDao.merge(orderEntity, userId);

        this.utx.commit();

        modifiedDateTime = this.jpaOrderDao.findLatestModifiedDateTime(userId);

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(modifiedDateTime, notNullValue());

        final Long assertId = orderEntity.getId();
        assertThat(this.jpaOrderDao.findById(assertId).getId(), notNullValue());

        final OrderEntity modifiedOrderEntity = this.jpaOrderDao.findById(assertId);
        this.logger.info(LOG_PREFIX + "test020_findLatestModifiedDate_pass [modifiedOrderEntity.id"
                + modifiedOrderEntity.getId() + ", modifyDate=" + modifiedOrderEntity.getModifyDateTime() + "]");
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=JpaOrderDaoTestIT#test010_createOrderEntity_pass+test030_findByAllOlderThanGivenDate_pass
     *
     * <b><code>test030_findByAllOlderThanGivenDate_pass</code>:</b><br>
     *  Tests 
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *  - The previous test must be successful finished.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - 1. Run query with a date not older than the create date.
     *  - 2. Run query with a date older than the create date.
     *
     * <b>Postconditions:</b><br>
     *  - 1. A order entity is returned.
     *  - 2. A empty list is returned.
     * </pre>
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    @InSequence(30)
    public void test030_findByAllOlderThanGivenDate_pass() throws Exception {
        this.logger.debug(LOG_PREFIX + "test030_findByAllOlderThanGivenDate_pass");

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        final String userId = UUID.randomUUID().toString();

        final List<OrderEntity> orderEntityListPrepare = this.jpaOrderDao.findAll();
        // There is a order in db from test010_createOrderEntity_pass.
        final OrderEntity orderEntity = orderEntityListPrepare.iterator().next();
        assertThat(orderEntity, notNullValue());

        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        //
        // There is no entity in db that is modified.
        final List<OrderEntity> orderEntityList = this.jpaOrderDao
                .findByAllOlderThanGivenDate(LocalDateTime.now().plusSeconds(1L), userId);
        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(orderEntityList, notNullValue());
        assertThat(orderEntityList.size(), greaterThanOrEqualTo(1));

        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        //
        // There is no entity in db that is modified.
        final List<OrderEntity> orderEntityEmptyList = this.jpaOrderDao
                .findByAllOlderThanGivenDate(LocalDateTime.now().minusSeconds(2L), userId);
        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(orderEntityEmptyList, notNullValue());
        assertThat(orderEntityEmptyList.size(), equalTo(0));
    }

    private OrderEntity createOrderEntity(final String username) {
        final CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setUsername(username);
        customerEntity.setFirstname(FIRST_NAME);
        customerEntity.setName(NAME);
        customerEntity.setCreateUser(CREATE_USER);

        final OrderEntity orderEntity = new OrderEntity();
        orderEntity.setCustomer(customerEntity);
        orderEntity.setCreateUser(CREATE_USER);

        return orderEntity;
    }

    private CustomerEntity createCustomerEntity(final String username) {
        final CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setUsername(username);
        customerEntity.setFirstname(FIRST_NAME);
        customerEntity.setName(NAME);
        customerEntity.setCreateUser(CREATE_USER);

        return customerEntity;
    }
}
