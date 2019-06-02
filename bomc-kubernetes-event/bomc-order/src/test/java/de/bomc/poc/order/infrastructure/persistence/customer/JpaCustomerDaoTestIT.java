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
package de.bomc.poc.order.infrastructure.persistence.customer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.transaction.NotSupportedException;
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
import de.bomc.poc.order.infrastructure.persistence.basis.JpaGenericDao;
import de.bomc.poc.order.infrastructure.persistence.basis.impl.AbstractJpaDao;
import de.bomc.poc.order.infrastructure.persistence.basis.producer.DatabaseProducer;
import de.bomc.poc.order.infrastructure.persistence.basis.qualifier.JpaDao;

/**
 * Tests the dao layer for {@link CustomerEntity} and
 * {@link JpaCustomerDaoImplMock}.
 * <p>
 * 
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=JpaCustomerDaoTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 23.11.2018
 */
@RunWith(Arquillian.class)
@Category(CategoryFastIntegrationTestIT.class)
public class JpaCustomerDaoTestIT extends ArquillianBase {

    private static final String LOG_PREFIX = "JpaCustomerDaoTestIT#";
    private static final String WEB_ARCHIVE_NAME = "bomc-customer-dao-war";
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
    private JpaCustomerDao jpaCustomerDao;

    // 'testable = true', means all the tests are running inside of the
    // container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
        webArchive.addClasses(JpaCustomerDaoTestIT.class, CategoryFastIntegrationTestIT.class);
        webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
        webArchive.addClasses(JpaCustomerDao.class, JpaCustomerDaoImpl.class);
        webArchive.addClasses(CustomerEntity.class, AbstractEntity.class, AbstractMetadataEntity.class,
                DomainObject.class);
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
     *  mvn clean install -Parq-wildfly-remote -Dtest=JpaCustomerDaoTestIT#test010_createCustomerEntity_pass
     *
     * <b><code>test010_createCustomerEntity_pass</code>:</b><br>
     *
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Create a new instance of CustomerEntity and persist it by the jpaCustomerDao.
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
    public void test010_createCustomerEntity_pass() throws Exception {
        this.logger.debug(LOG_PREFIX + "test010_createCustomerEntity_pass");

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        final String username = UUID.randomUUID().toString();

        this.utx.begin();
        final CustomerEntity customerEntity = this.createCustomerEntity(username);

        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        this.jpaCustomerDao.persist(customerEntity, ApplicationUserEnum.SYSTEM_USER.name());

        this.utx.commit();

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(customerEntity.getId(), notNullValue());

        final Long assertId = customerEntity.getId();
        assertThat(this.jpaCustomerDao.findById(assertId).getId(), notNullValue());
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=JpaCustomerDaoTestIT#test010_createCustomerEntity_pass+test020_findLatestModifiedDate_pass
     *
     * <b><code>test020_findLatestModifiedDate_pass</code>:</b><br>
     *  Tests finding the last modified date for CustomerEntities. First a modifyDate is not set, second a modifyDate is set.
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

        final List<CustomerEntity> customerEntityList = this.jpaCustomerDao.findAll();
        // There is a customer in db from test010_createCustomerEntity_pass.
        final CustomerEntity customerEntity = customerEntityList.iterator().next();
        assertThat(customerEntity, notNullValue());

        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        //
        // There is no entity in db that is modified.
        LocalDateTime modifiedDateTime = this.jpaCustomerDao.findLatestModifiedDateTime(userId);
        assertThat(modifiedDateTime, nullValue());

        // Update db entry, to create a modifyDate.
        this.utx.begin();

        customerEntity.setUsername(E_MAIL + "a");
        this.jpaCustomerDao.merge(customerEntity, userId);

        this.utx.commit();

        modifiedDateTime = this.jpaCustomerDao.findLatestModifiedDateTime(userId);

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(modifiedDateTime, notNullValue());

        final Long assertId = customerEntity.getId();
        assertThat(this.jpaCustomerDao.findById(assertId).getId(), notNullValue());

        final CustomerEntity modifiedCustomerEntity = this.jpaCustomerDao.findById(assertId);
        this.logger.info(LOG_PREFIX + "test020_findLatestModifiedDate_pass [" + modifiedCustomerEntity + ", modifyDate="
                + modifiedCustomerEntity.getModifyDateTime() + "]");
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=JpaCustomerDaoTestIT#test030_findCustomerByUsername_pass
     *
     * <b><code>test030_findCustomerByUsername_pass</code>:</b><br>
     *  Tests the NamedQuery 'findByUsername'.
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *  - A entity with the username with a UUID must be persisted in db.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Read the created entity with the UUID back from db.
     *
     * <b>Postconditions:</b><br>
     *  - The entity with the given UUId is returned.
     * </pre>
     * 
     * @throws Exception
     *             should not occured.
     */
    @Test
    @InSequence(30)
    public void test030_findCustomerByUsername_pass() throws NotSupportedException, Exception {
        this.logger.debug(LOG_PREFIX + "test030_findCustomerByUsername_pass");

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        final String username = UUID.randomUUID().toString();

        this.utx.begin();
        final CustomerEntity customerEntity = this.createCustomerEntity(username);
        this.jpaCustomerDao.persist(customerEntity, ApplicationUserEnum.SYSTEM_USER.name());

        this.utx.commit();

        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        final CustomerEntity retCustomerEntity = this.jpaCustomerDao.findByUsername(username,
                ApplicationUserEnum.TEST_USER.name());

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(retCustomerEntity, notNullValue());
        assertThat(retCustomerEntity.getUsername(), equalTo(username));
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
