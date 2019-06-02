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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the {@link ${package}.application.basis.log.ExceptionLogControllerEJB}.
 * <p>
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=ConfigTestIT
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 28.11.2018
 */
@RunWith(Arquillian.class)
@Category(CategoryFastIntegrationTestIT.class)
public class ConfigTestIT extends ArquillianBase {

    private static final String LOG_PREFIX = "ConfigTestIT${symbol_pound}";
    private static final String WEB_ARCHIVE_NAME = "bomc-config-war";
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
        webArchive.addClasses(ConfigTestIT.class, CategoryFastIntegrationTestIT.class);
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
     *  mvn clean install -Parq-wildfly-remote -Dtest=ConfigTestIT${symbol_pound}test010_readingConfig_pass
     *
     * <b><code>test010_readingConfig_pass</code>:</b><br>
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
     *
     * </pre>
     * @throws Exception not expected.
     */
    @Test
    @InSequence(10)
    public void test010_readingConfig_pass() throws Exception {
        this.logger.info(LOG_PREFIX + "test010_readingConfig_pass");

        assertThat(this.configSingletonEJB.getExceptionSchedulerDayOfWeek(), notNullValue());
        assertThat(this.configSingletonEJB.getExceptionSchedulerHour(), notNullValue());
        assertThat(this.configSingletonEJB.getExceptionSchedulerMinute(), notNullValue());
        assertThat(this.configSingletonEJB.getExceptionSchedulerPersistent(), notNullValue());
        assertThat(this.configSingletonEJB.getExceptionSchedulerTestModus(), notNullValue());
        assertThat(this.configSingletonEJB.getServiceName(), notNullValue());
    }
}
