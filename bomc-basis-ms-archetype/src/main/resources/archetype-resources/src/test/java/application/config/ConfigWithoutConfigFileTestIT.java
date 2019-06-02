#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: ${symbol_dollar}Author: bomc ${symbol_dollar}
 *
 *  date: ${symbol_dollar}Date: ${symbol_dollar}
 *
 *  revision: ${symbol_dollar}Revision: ${symbol_dollar}
 *
 * </pre>
 */
package ${package}.application.config;

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

import ${package}.ArquillianBase;
import ${package}.CategoryFastIntegrationTestIT;
import ${package}.application.config.qualifier.ConfigQualifier;

/**
 * Tests the {@link ConfigSingletonEJB} and injecting default values.
 * <p>
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=ConfigWithConfigFileTestIT
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 28.11.2018
 */
@RunWith(Arquillian.class)
@Category(CategoryFastIntegrationTestIT.class)
public class ConfigWithoutConfigFileTestIT extends ArquillianBase {

    private static final String LOG_PREFIX = "ConfigWithoutConfigFileTestIT${symbol_pound}";
    private static final String WEB_ARCHIVE_NAME = "bomc-config-without-config-file-war";
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
        webArchive.addClasses(ConfigWithoutConfigFileTestIT.class, CategoryFastIntegrationTestIT.class);
        webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
        webArchive.addPackages(true, ConfigSingletonEJB.class.getPackage()
                                                             .getName());

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
    @Inject
    @ConfigQualifier(key = ConfigKeys.EXCEPTION_LOG_SCHEDULER_DAY_OF_WEEK)
    private String exceptionLogSchedulerDayOfWeek;
    @Inject
    @ConfigQualifier(key = ConfigKeys.EXCEPTION_LOG_SCHEDULER_MINUTE)
    private String exceptionLogSchedulerMinute;
    @Inject
    @ConfigQualifier(key = ConfigKeys.EXCEPTION_LOG_SCHEDULER_HOUR)
    private String exceptionLogSchedulerHour;
    @Inject
    @ConfigQualifier(key = ConfigKeys.EXCEPTION_LOG_SCHEDULER_PERSISTENT)
    private String exceptionLogSchedulerPersistent;
    @Inject
    @ConfigQualifier(key = ConfigKeys.EXCEPTION_LOG_SCHEDULER_TEST_MODUS)
    private String exceptionLogSchedulerTestModus;
    @Inject
    @ConfigQualifier(key = ConfigKeys.EXCEPTION_LOG_SCHEDULER_DAYS_TO_SUBSTRACT)
    private String exceptionLogDaysToSubtract;
    @Inject
    @ConfigQualifier(key = ConfigKeys.SERVICE_NAME)
    private String serviceName;

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=ConfigWithConfigFileTestIT${symbol_pound}test010_readingConfig_pass
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
        // Check if injected values are not not null and the values are the
        // default values.
        // -------------------------------------------
        assertThat(this.exceptionLogSchedulerDayOfWeek, equalTo(ConfigSingletonEJB.DEFAULT_EXCEPTION_LOG_SCHEDULER_DAY_OF_WEEK));
        assertThat(this.exceptionLogSchedulerHour, equalTo(ConfigSingletonEJB.DEFAULT_EXCEPTION_LOG_SCHEDULER_HOUR));
        assertThat(this.exceptionLogSchedulerMinute, equalTo(ConfigSingletonEJB.DEFAULT_EXCEPTION_LOG_SCHEDULER_MINUTE));
        assertThat(this.exceptionLogSchedulerPersistent, equalTo(ConfigSingletonEJB.DEFAULT_EXCEPTION_LOG_SCHEDULER_PERSISTENT));
        assertThat(this.exceptionLogSchedulerTestModus, equalTo(ConfigSingletonEJB.DEFAULT_EXCEPTION_LOG_SCHEDULER_TEST_MODUS));
        assertThat(this.exceptionLogDaysToSubtract, equalTo(ConfigSingletonEJB.DEFAULT_EXCEPTION_LOG_DAYS_TO_SUBSTRACT));
        assertThat(this.serviceName, notNullValue());
    }
}
