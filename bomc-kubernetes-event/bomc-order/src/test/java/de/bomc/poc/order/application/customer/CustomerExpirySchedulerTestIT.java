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
package de.bomc.poc.order.application.customer;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.inject.Inject;

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
import de.bomc.poc.order.CategorySlowIntegrationTestIT;
import de.bomc.poc.order.application.config.ConfigKeys;
import de.bomc.poc.order.application.config.ConfigSingletonEJB;
import de.bomc.poc.order.application.config.producer.ConfigProducer;
import de.bomc.poc.order.application.config.qualifier.ConfigQualifier;
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
import de.bomc.poc.order.infrastructure.persistence.customer.JpaCustomerDaoImpl;

/**
 * Tests the {@link CustomerExpirySchedulerSingletonEJB}.
 * <p>
 * 
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=CustomerExpirySchedulerTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 23.11.2018
 */
@RunWith(Arquillian.class)
@Category(CategorySlowIntegrationTestIT.class)
public class CustomerExpirySchedulerTestIT extends ArquillianBase {

    private static final String LOG_PREFIX = "CustomerExpirySchedulerTestIT#";
    private static final String WEB_ARCHIVE_NAME = "bomc-customer-expiry-war";
    @Inject
    @LoggerQualifier
    private Logger logger;

    // 'testable = true', means all the tests are running inside of the
    // container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
        webArchive.addClasses(CustomerExpirySchedulerTestIT.class, CategorySlowIntegrationTestIT.class);
        webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
        webArchive.addClasses(ConfigKeys.class, ConfigSingletonEJB.class, ConfigProducer.class, ConfigQualifier.class);
        webArchive.addClasses(CustomerExpirySchedulerSingletonEJB.class);
        webArchive.addClasses(JpaCustomerDao.class, JpaCustomerDaoImpl.class);
        webArchive.addClasses(CustomerEntity.class, AbstractEntity.class, AbstractMetadataEntity.class, DomainObject.class);
        webArchive.addClasses(AbstractJpaDao.class, JpaGenericDao.class, DatabaseProducer.class, JpaDao.class);
        webArchive.addClasses(ApplicationUserEnum.class);
        
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

    @EJB
    private CustomerExpirySchedulerSingletonEJB customerExpirySchedulerSingletonEJB;

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=CustomerExpirySchedulerTestIT#test010_schedule_pass
     *
     * <b><code>test010_schedule_pass</code>:</b><br>
     *  Tests the scheduler. Note the implementation supports a test mode in default cases.
     *  This means the case runs without the 'configuration.properties' file.
     *  The scheduler expires 1 minute after test has started.
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *  - Expiry time is set to 1 minute after test is started.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - In test mode, the timer has to be created manually by invoking 'initScheduler' on 'customerExpirySchedulerSingletonEJB'.
     *  - Printout the next expiry.
     *
     * <b>Postconditions:</b><br>
     *  The scheduler expires 1 minute after test case started and in log-file a entry with 42 ist printed out. See log-file for validation of asserts.
     * </pre>
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    @InSequence(10)
    public void test010_schedule_pass() throws Exception {
        this.logger.info(LOG_PREFIX + "test010_schedule_pass");

        final LocalDateTime localDateTime = LocalDateTime.now();
        final int customerExpirySchedulerHour = localDateTime.getHour();
        int customerExpirySchedulerMinute = localDateTime.getMinute();
        
        if (customerExpirySchedulerMinute == 60) {
            // 60 is for timer not allowed.
            customerExpirySchedulerMinute = customerExpirySchedulerMinute + 1;
        }
        
        final boolean customerExpirySchedulerPersistent = false;
        final DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();

        this.customerExpirySchedulerSingletonEJB.initScheduler(dayOfWeek.name(), customerExpirySchedulerMinute,
                customerExpirySchedulerHour, customerExpirySchedulerPersistent, ApplicationUserEnum.SYSTEM_USER.name());

        this.customerExpirySchedulerSingletonEJB.printoutNextTimeout(ApplicationUserEnum.SYSTEM_USER.name());

        // Waits for seconds, in worst case 1 minute has to be wait, +10 seconds
        // for a buffer to ensure timer expiration.
        TimeUnit.SECONDS.sleep(70);
    }
}
