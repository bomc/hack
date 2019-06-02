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
package de.bomc.poc.test.os.arq.runtime;

import de.bomc.poc.exception.handling.ApiError;
import de.bomc.poc.exception.interceptor.ApiExceptionInterceptor;
import de.bomc.poc.exception.qualifier.ApiExceptionQualifier;
import de.bomc.poc.exception.web.WebAuthRuntimeException;
import de.bomc.poc.os.runtime.RuntimeInfoSingletonEJB;
import de.bomc.poc.test.os.arq.ArquillianOsBase;
import de.bomc.poc.auth.rest.application.JaxRsActivator;
import de.bomc.poc.auth.rest.endpoint.v1.runtime.RuntimeRestEndpoint;
import de.bomc.poc.auth.rest.endpoint.v1.runtime.impl.RuntimeRestEndpointImpl;
import de.bomc.poc.auth.rest.endpoint.v1.version.VersionRestEndpoint;
import de.bomc.poc.logger.producer.LoggerProducer;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.validation.ConstraintViolationMapper;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.performance.annotation.Performance;
import org.jboss.arquillian.performance.annotation.PerformanceTest;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resources;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the {@link VersionRestEndpoint}.
 * <pre>
 *     mvn clean install -Parq-wildfly-remote -Dtest=RuntimeRestResourceTestIT
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@PerformanceTest(resultsThreshold = 10)
@RunWith(Arquillian.class)
public class RuntimeRestResourceTestIT extends ArquillianOsBase {

    private static final String WEB_ARCHIVE_NAME = "auth-runtime";
    private static ResteasyWebTarget webTarget;
    @Inject
    @LoggerQualifier
    private Logger logger;

    // 'testable = true', means all the tests are running inside the container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
        webArchive.addClass(RuntimeRestResourceTestIT.class);
        webArchive.addClasses(LoggerProducer.class, LoggerQualifier.class);
        webArchive.addClasses(RuntimeRestEndpoint.class, RuntimeRestEndpointImpl.class);
        webArchive.addClasses(RuntimeInfoSingletonEJB.class, JaxRsActivator.class, Resources.class);

        webArchive.addPackages(true, ApiError.class.getPackage().getName());
        webArchive.addPackages(true, ApiExceptionInterceptor.class.getPackage().getName());
        webArchive.addPackages(true, ApiExceptionQualifier.class.getPackage().getName());
        webArchive.addPackages(true, WebAuthRuntimeException.class.getPackage().getName());
        webArchive.addPackages(true, ConstraintViolationMapper.class.getPackage().getName());

        System.out.println("RuntimeRestResourceTestIT#createDeployment: " + webArchive.toString(true));

        return webArchive;
    }

    /**
     * Setup.
     * @throws MalformedURLException
     */
    @Before
    public void setupClass() throws MalformedURLException {
        // Create rest client with Resteasy Client Framework.
        final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS)
                                                                 .socketTimeout(10, TimeUnit.MINUTES)
                                                                 .build();
        webTarget = client.target(buildBaseUrl(WEB_ARCHIVE_NAME) + "/" + JaxRsActivator.APPLICATION_PATH);
    }

    @EJB
    RuntimeInfoSingletonEJB ejb;

    /**
     * Test reading start time from app server.
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=RuntimeRestResourceTestIT#test01_v1_readStartTime_Pass
     * </pre>
     * @throws Exception
     */
    @Test
    @InSequence(1)
    @Performance(time = 1000)
    public void test01_v1_readStartTime_Pass() {
        this.logger.info("RuntimeRestResourceTestIT#test01_v1_readStartTime_Pass");

        final String startDateTime = this.ejb.getDateTimeAsString();

        this.logger.info("RuntimeRestResourceTestIT#test01_v1_readStartTime_Pass [startDateTime=" + startDateTime + "]");

        assertThat(startDateTime, is(notNullValue()));
    }

    /**
     * Test reading available heap from app server.
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=RuntimeRestResourceTestIT#test10_v1_readAvailableHeap_Pass
     * </pre>
     * @throws Exception
     */
    @Test
    @InSequence(1)
    @Performance(time = 1000)
    public void test10_v1_readAvailableHeap_Pass() {
        this.logger.info("RuntimeRestResourceTestIT#test10_v1_readAvailableHeap_Pass [uri=" + webTarget.getUri() + "]");

        Response response = null;

        try {
            response = webTarget.proxy(RuntimeRestEndpoint.class)
                                .availableHeap();

            assertThat(response, notNullValue());
            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final JsonObject jsonObject = response.readEntity(JsonObject.class);
                this.logger.info("RuntimeRestResourceTestIT#test10_v1_readAvailableHeap_Pass [response=" + jsonObject.toString() + "]");
            }
        } catch(Exception ex) {
        	this.logger.error("RuntimeRestResourceTestIT#test10_v1_readAvailableHeap_Pass - failed! " + ex);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * Test reading os info from app server.
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=RuntimeRestResourceTestIT#test20_v1_readOsInfo
     * </pre>
     * @throws Exception
     */
    @Test
    @InSequence(10)
    @Performance(time = 1000)
    public void test20_v1_readOsInfo() {
        this.logger.info("RuntimeRestResourceTestIT#test20_v1_readOsInfo [uri=" + webTarget.getUri() + "]");

        Response response = null;

        try {
            response = webTarget.proxy(RuntimeRestEndpoint.class)
                                .osInfo();

            assertThat(response, notNullValue());
            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final JsonObject jsonObject = response.readEntity(JsonObject.class);
                this.logger.info("RuntimeRestResourceTestIT#test20_v1_readOsInfo [response=" + jsonObject.toString() + "]");
            }
        } catch(Exception ex) {
        	this.logger.error("RuntimeRestResourceTestIT#test20_v1_readOsInfo - failed! " + ex);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * Test reading os info from app server.
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=RuntimeRestResourceTestIT#test30_v1_readNodeName
     * </pre>
     * @throws Exception
     */
    @Test
    @InSequence(10)
    @Performance(time = 1000)
    public void test30_v1_readNodeName() {
        this.logger.info("RuntimeRestResourceTestIT#test30_v1_readNodeName [uri=" + webTarget.getUri() + "]");

        Response response = null;

        try {
            response = webTarget.proxy(RuntimeRestEndpoint.class)
                                .nodeName();

            assertThat(response, notNullValue());
            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final JsonObject jsonObject = response.readEntity(JsonObject.class);
                this.logger.info("RuntimeRestResourceTestIT#test30_v1_readNodeName [response=" + jsonObject.toString() + "]");
            }
        } catch(Exception ex) {
        	this.logger.error("RuntimeRestResourceTestIT#test30_v1_readNodeName - failed! " + ex);
        } finally {
            if (response != null) {
                response.close();
            }
        }

    }
}
