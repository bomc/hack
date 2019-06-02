/**
 * Project: MY_POC
 * <p/>
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * <p/>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.cdi.logger.arq;

import de.bomc.poc.logger.producer.LoggerProducer;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * <pre>
 *	mvn clean install -Parq-cdi-embedded -Dtest=LoggerTestCdiIT
 *
 *  Performs cdi tests for the {@link de.bomc.poc.logger.producer.LoggerProducer}.
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@RunWith(Arquillian.class)
public class LoggerTestCdiIT {

    private static final String LOG_PREFIX = "test#cdi#logger";
    private static final String WEB_ARCHIVE_NAME = "poc-cdi-logger-test";
    @Inject
    @LoggerQualifier
    Logger logger_WithoutLogPrefix;
    @Inject
    @LoggerQualifier(logPrefix = LOG_PREFIX)
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
     *	mvn clean install -Parq-cdi-embedded -Dtest=LoggerTestCdiIT#test01_loggerWithoutLogPrefix_Pass
     */
    @Test
    public void test01_loggerWithoutLogPrefix_Pass() {

        this.logger_WithoutLogPrefix.debug("LoggerTestIT#test01_loggerWithoutLogPrefix_Pass");
    }

    /**
     *	mvn clean install -Parq-cdi-embedded -Dtest=LoggerTestCdiIT#test01_loggerWithoutLogPrefix_Pass
     */
    @Test
    public void test02_loggerWithLogPrefix_Pass() {

        this.logger_WithLogPrefix.debug("LoggerTestIT#test02_loggerWithLogPrefix_Pass");
    }
}
