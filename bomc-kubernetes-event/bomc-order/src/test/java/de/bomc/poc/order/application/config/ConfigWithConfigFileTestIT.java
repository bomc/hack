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
package de.bomc.poc.order.application.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.ejb.EJB;
import javax.inject.Inject;

import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import de.bomc.poc.order.ArquillianBase;
import de.bomc.poc.order.CategoryFastIntegrationTestIT;

/**
 * Tests the {@link ConfigSingletonEJB} by using the 'configuration-file'.
 * <p>
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=ConfigWithConfigFileTestIT
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 28.11.2018
 */
@RunWith(Arquillian.class)
@Category(CategoryFastIntegrationTestIT.class)
public class ConfigWithConfigFileTestIT extends ArquillianBase {

    private static final String LOG_PREFIX = "ConfigWithConfigFileTestIT#";
    private static final String WEB_ARCHIVE_NAME = "bomc-config-with-config-file-war";
    // _______________________________________________
    // Member variables.
    // -----------------------------------------------
    @Inject
    @LoggerQualifier
    private Logger logger;

    // 'testable = true', means all the tests are running inside of the
    // container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = createTestArchiveWithEmptyAssets(WEB_ARCHIVE_NAME);
        webArchive.addClasses(ConfigWithConfigFileTestIT.class, CategoryFastIntegrationTestIT.class);
        webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
        webArchive.addPackages(true, ConfigSingletonEJB.class.getPackage()
                                                             .getName());
        webArchive.addAsResource("configuration.properties");

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
    private ConfigSingletonEJB configSingletonEJB;

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=ConfigWithConfigFileTestIT#test010_readingConfig_pass
     *
     * <b><code>test010_readingConfig_pass</code>:</b><br>
     *
     *
     * <b>Preconditions:</b><br>
     *  - 'configuration.properties' must be part of the deployed archive.
     *  - Artifact must be successful deployed in Wildfly.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Check read values against null value.
     *  - Set new values by using the setter methods.
     *  - Check new values.
     *
     * <b>Postconditions:</b><br>
     *  - All asserts are successful ended.
     * </pre>
     * @throws Exception not expected.
     */
    @Test
    @InSequence(10)
    public void test010_readingConfig_pass() throws Exception {
        this.logger.info(LOG_PREFIX + "test010_readingConfig_pass");

        // ___________________________________________
        // Check data from config file.
        // -------------------------------------------
        assertThat(this.configSingletonEJB.getExceptionSchedulerDayOfWeek(), notNullValue());
        assertThat(this.configSingletonEJB.getExceptionSchedulerHour(), notNullValue());
        assertThat(this.configSingletonEJB.getExceptionSchedulerMinute(), notNullValue());
        assertThat(this.configSingletonEJB.getExceptionSchedulerPersistent(), notNullValue());
        assertThat(this.configSingletonEJB.getExceptionSchedulerTestModus(), notNullValue());
        assertThat(this.configSingletonEJB.getExceptionSchedulerDaysToSubstract(), notNullValue());
        
        assertThat(this.configSingletonEJB.getCustomerExpirySchedulerDayOfWeek(), notNullValue());
        assertThat(this.configSingletonEJB.getCustomerExpirySchedulerHour(), notNullValue());
        assertThat(this.configSingletonEJB.getCustomerExpirySchedulerMinute(), notNullValue());
        assertThat(this.configSingletonEJB.getCustomerExpirySchedulerPersistent(), notNullValue());
        assertThat(this.configSingletonEJB.getCustomerExpirySchedulerTestModus(), notNullValue());
        
        assertThat(this.configSingletonEJB.getServiceName(), notNullValue());
        // ___________________________________________
        // Some new test data.
        // -------------------------------------------
        final String schedulerDayOfWeek = "Mon-Fri";
        final String schedulerHour = "22";
        final String scheduleMinute = "58";
        final String schedulerPersistent = "true";
        final String schedulerTestModus = "false";
        final String serviceName = WEB_ARCHIVE_NAME;
        final String schedulerDaysToSubstract = "10";

        this.configSingletonEJB.setExceptionSchedulerDayOfWeek(schedulerDayOfWeek);
        this.configSingletonEJB.setExceptionSchedulerHour(schedulerHour);
        this.configSingletonEJB.setExceptionSchedulerMinute(scheduleMinute);
        this.configSingletonEJB.setExceptionSchedulerPersistent(schedulerPersistent);
        this.configSingletonEJB.setExceptionSchedulerTestModus(schedulerTestModus);
        this.configSingletonEJB.setExceptionSchedulerDaysToSubstract(schedulerDaysToSubstract);
        
        this.configSingletonEJB.setCustomerExpirySchedulerDayOfWeek(schedulerDayOfWeek);
        this.configSingletonEJB.setCustomerExpirySchedulerHour(schedulerHour);
        this.configSingletonEJB.setCustomerExpirySchedulerMinute(scheduleMinute);
        this.configSingletonEJB.setCustomerExpirySchedulerPersistent(schedulerPersistent);
        this.configSingletonEJB.setCustomerExpirySchedulerTestModus(schedulerTestModus);
        
        this.configSingletonEJB.setServiceName(serviceName);

        // ___________________________________________
        // Check new test data.
        // -------------------------------------------
        assertThat(this.configSingletonEJB.getCustomerExpirySchedulerDayOfWeek(), equalTo(schedulerDayOfWeek));
        assertThat(this.configSingletonEJB.getExceptionSchedulerHour(), equalTo(schedulerHour));
        assertThat(this.configSingletonEJB.getExceptionSchedulerMinute(), equalTo(scheduleMinute));
        assertThat(this.configSingletonEJB.getExceptionSchedulerPersistent(), equalTo(schedulerPersistent));
        assertThat(this.configSingletonEJB.getExceptionSchedulerTestModus(), equalTo(schedulerTestModus));
        assertThat(this.configSingletonEJB.getExceptionSchedulerDaysToSubstract(), equalTo(schedulerDaysToSubstract));
        
        assertThat(this.configSingletonEJB.getCustomerExpirySchedulerDayOfWeek(), equalTo(schedulerDayOfWeek));
        assertThat(this.configSingletonEJB.getCustomerExpirySchedulerHour(), equalTo(schedulerHour));
        assertThat(this.configSingletonEJB.getCustomerExpirySchedulerMinute(), equalTo(scheduleMinute));
        assertThat(this.configSingletonEJB.getCustomerExpirySchedulerPersistent(), equalTo(schedulerPersistent));
        assertThat(this.configSingletonEJB.getCustomerExpirySchedulerTestModus(), equalTo(schedulerTestModus));
        
        assertThat(this.configSingletonEJB.getServiceName(), equalTo(serviceName));
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=ConfigWithConfigFileTestIT#test020_overrideConfigProperties_pass
     *
     * <b><code>test020_overrideConfigProperties_pass</code>:</b><br>
     *
     *
     * <b>Preconditions:</b><br>
     *  - 'configuration.properties' must be part of the deployed archive.
     *  - Artifact must be successful deployed in Wildfly.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Config properties will be overrides, with null value.
     *
     * <b>Postconditions:</b><br>
     *  - Config properties will be overrides by default values.
     * </pre>
     * @thCustomerExpiryption not expected.
     */
    @Test
    @InSequence(20)
    public void test020_overrideConfigProperties_pass() throws Exception {
        this.logger.info(LOG_PREFIX + "test020_overrideConfigProperties_pass");

        // ___________________________________________
        // Check data from config file.
        // -------------------------------------------
        assertThat(this.configSingletonEJB.getExceptionSchedulerDayOfWeek(), notNullValue());
        assertThat(this.configSingletonEJB.getExceptionSchedulerHour(), notNullValue());
        assertThat(this.configSingletonEJB.getExceptionSchedulerMinute(), notNullValue());
        assertThat(this.configSingletonEJB.getExceptionSchedulerPersistent(), notNullValue());
        assertThat(this.configSingletonEJB.getExceptionSchedulerTestModus(), notNullValue());
        assertThat(this.configSingletonEJB.getExceptionSchedulerDaysToSubstract(), notNullValue());
        
        assertThat(this.configSingletonEJB.getCustomerExpirySchedulerDayOfWeek(), notNullValue());
        assertThat(this.configSingletonEJB.getCustomerExpirySchedulerHour(), notNullValue());
        assertThat(this.configSingletonEJB.getCustomerExpirySchedulerMinute(), notNullValue());
        assertThat(this.configSingletonEJB.getCustomerExpirySchedulerPersistent(), notNullValue());
        assertThat(this.configSingletonEJB.getCustomerExpirySchedulerTestModus(), notNullValue());
        
        assertThat(this.configSingletonEJB.getServiceName(), notNullValue());

        this.configSingletonEJB.setExceptionSchedulerDayOfWeek(null);
        this.configSingletonEJB.setExceptionSchedulerHour(null);
        this.configSingletonEJB.setExceptionSchedulerMinute(null);
        this.configSingletonEJB.setExceptionSchedulerPersistent(null);
        this.configSingletonEJB.setExceptionSchedulerTestModus(null);
        this.configSingletonEJB.setExceptionSchedulerDaysToSubstract(null);
        
        this.configSingletonEJB.setCustomerExpirySchedulerDayOfWeek(null);
        this.configSingletonEJB.setCustomerExpirySchedulerHour(null);
        this.configSingletonEJB.setCustomerExpirySchedulerMinute(null);
        this.configSingletonEJB.setCustomerExpirySchedulerPersistent(null);
        this.configSingletonEJB.setCustomerExpirySchedulerTestModus(null);
        
        this.configSingletonEJB.setServiceName(null);

        // ___________________________________________
        // Check new test data.
        // -------------------------------------------
        // NOTE: If the 'test010_readingConfig_pass' is running first, than the values are set by the value.
        // If not, than the values are the default values.
        assertThat(this.configSingletonEJB.getExceptionSchedulerDayOfWeek(), notNullValue());
        assertThat(this.configSingletonEJB.getExceptionSchedulerHour(), notNullValue());
        assertThat(this.configSingletonEJB.getExceptionSchedulerMinute(), notNullValue());
        assertThat(this.configSingletonEJB.getExceptionSchedulerPersistent(), notNullValue());
        assertThat(this.configSingletonEJB.getExceptionSchedulerTestModus(), notNullValue());
        assertThat(this.configSingletonEJB.getExceptionSchedulerDaysToSubstract(), notNullValue());
       
        assertThat(this.configSingletonEJB.getCustomerExpirySchedulerDayOfWeek(), notNullValue());
        assertThat(this.configSingletonEJB.getCustomerExpirySchedulerHour(), notNullValue());
        assertThat(this.configSingletonEJB.getCustomerExpirySchedulerMinute(), notNullValue());
        assertThat(this.configSingletonEJB.getCustomerExpirySchedulerPersistent(), notNullValue());
        assertThat(this.configSingletonEJB.getCustomerExpirySchedulerTestModus(), notNullValue());
        
        assertThat(this.configSingletonEJB.getServiceName(), notNullValue());
    }
}