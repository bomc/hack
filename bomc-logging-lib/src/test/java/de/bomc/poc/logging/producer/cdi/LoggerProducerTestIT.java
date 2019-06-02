package de.bomc.poc.logging.producer.cdi;

import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * <pre>
 *  Um diesen Test im Arquillian-Cdi-Container laufen zulassen, muss dieser Test nach 'LoggerProducerTestCdiIT' umbennant werden,
 *  oder im Profile die auszufuehrenden Testnamen angepasst werden (*TestIT zu/von *TestCdiIT).
 *  Problem: im Jenkins koennen im MOMENT die Profile 'arq-cdi-embedded' und 'arq-wildfly-managed-dist' nicht in einem Job ausgefuehrt werden.
 *  Damit koennen keine Testabdeckungsdaten gesammelt werden.
 *
 * 	mvn clean install -Parq-cdi-embedded -Dtest=LoggerProducerTestIT
 *
 *  Performs cdi tests for the {@link LoggerProducer}.
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: 7096 $ $Author: tzdbmm $ $Date: 2016-08-03 14:59:36 +0200 (Mi, 03 Aug 2016) $
 * @since 19.07.2016
 */
@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoggerProducerTestIT {

    private static final String LOG_PREFIX = "LoggerProducerTestIT#";
    private static final String WEB_ARCHIVE_NAME = "logging-producer-test";
    private static final String MY_CUSTOM_PREFIX = "my-custom-prefix";
    @Inject
    @LoggerQualifier
    Logger logger_WithoutLogPrefix;
    @Inject
    @LoggerQualifier(logPrefix = MY_CUSTOM_PREFIX)
    Logger logger_WithLogPrefix;

    @Deployment
    public static WebArchive createDeployment() {
        final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, WEB_ARCHIVE_NAME + ".war");
        webArchive.addClasses(LoggerProducer.class, LoggerQualifier.class);
        webArchive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        System.out.println("archiveContent: " + webArchive.toString(true));

        return webArchive;
    }

    @Before
    public void init() {
        assertThat(this.logger_WithoutLogPrefix, notNullValue());
        assertThat(this.logger_WithLogPrefix, notNullValue());
    }

    /**
     * mvn clean install -Parq-cdi-embedded -Dtest=LoggerProducerTestIT#test01_loggerWithoutLogPrefix_Pass
     */
    @Test
    public void test01_loggerWithoutLogPrefix_Pass() {

        this.logger_WithoutLogPrefix.debug(LOG_PREFIX + "test01_loggerWithoutLogPrefix_Pass");

        // ___________________________________
        // See console for ouput verification.
    }

    /**
     * mvn clean install -Parq-cdi-embedded -Dtest=LoggerProducerTestIT#test01_loggerWithoutLogPrefix_Pass
     */
    @Test
    public void test02_loggerWithLogPrefix_Pass() {

        this.logger_WithLogPrefix.debug(LOG_PREFIX + "test02_loggerWithLogPrefix_Pass");

        // ___________________________________
        // See console for ouput verification.
    }
}

