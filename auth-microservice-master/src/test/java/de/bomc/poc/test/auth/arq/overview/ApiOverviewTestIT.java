/**
 * Project: MY_POC_MICROSERVICE
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
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.auth.arq.overview;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import de.bomc.poc.exception.handling.ApiError;
import de.bomc.poc.exception.interceptor.ApiExceptionInterceptor;
import de.bomc.poc.exception.qualifier.ApiExceptionQualifier;
import de.bomc.poc.exception.web.WebAuthRuntimeException;
import de.bomc.poc.validation.ConstraintViolationMapper;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.performance.annotation.Performance;
import org.jboss.arquillian.performance.annotation.PerformanceTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bomc.poc.auth.rest.application.JaxRsActivator;
import de.bomc.poc.auth.rest.endpoint.v1.overview.ApiOverviewRestEndpoint;
import de.bomc.poc.auth.rest.endpoint.v1.overview.impl.ApiOverviewRestEndpointImpl;
import de.bomc.poc.logger.producer.LoggerProducer;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.rest.logger.ResteasyClientLogger;
import de.bomc.poc.test.auth.arq.ArquillianAuthBase;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * <pre>
 * Performs integration tests for listing available endpoints.
 * mvn clean install -Parq-wildfly-remote -Dtest=ApiOverviewTestIT
 * _______________________________________________________________________________
 * NOTE: BeforeClass / AfterClass are ALWAYS and ONLY executed on the Client side.
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@RunWith(Arquillian.class)
@PerformanceTest(resultsThreshold = 10)
public class ApiOverviewTestIT extends ArquillianAuthBase {
    private static final String WEB_ARCHIVE_NAME = "auth-overview-endpoints";
    private static final String LOGGER_PREFIX = "ApiOverviewTestIT";

    @Inject
    @LoggerQualifier(logPrefix = LOGGER_PREFIX)
    private Logger logger;

    // 'testable = true', means all the tests are running inside of the container.
    @Deployment
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = ArquillianAuthBase.createTestArchive(WEB_ARCHIVE_NAME);
        webArchive.addClass(ApiOverviewTestIT.class);
        webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
        webArchive.addClasses(ApiOverviewRestEndpointImpl.class, ApiOverviewRestEndpoint.class);
        webArchive.addClasses(ResteasyClientLogger.class);
        webArchive.addClasses(JaxRsActivator.class);

        webArchive.addPackages(true, ApiError.class.getPackage().getName());
        webArchive.addPackages(true, ApiExceptionInterceptor.class.getPackage().getName());
        webArchive.addPackages(true, ApiExceptionQualifier.class.getPackage().getName());
        webArchive.addPackages(true, WebAuthRuntimeException.class.getPackage().getName());
        webArchive.addPackages(true, ConstraintViolationMapper.class.getPackage().getName());

        System.out.println("archiveContent: " + webArchive.toString(true));

        return webArchive;
    }

    @Before
    public void setup() throws Exception {
        //
    }

    @After
    public void cleanup() {
        //
    }

    /**
     * <pre>
     * 	mvn clean install -Parq-wildfly-remote -Dtest=ApiOverviewTestIT#test01_readAvailableEndpoints_Pass
     * </pre>
     * @throws Exception
     */
    @Test
    @InSequence(1)
    @Performance(time = 1000)
    public void test01_readAvailableEndpoints_Pass() {
        this.logger.debug(LOGGER_PREFIX + "test01_readAvailableEndpoints_Pass");

        final String baseUri = buildBaseUrl(WEB_ARCHIVE_NAME) + JaxRsActivator.APPLICATION_PATH + JaxRsActivator.OVERVIEW_ENDPOINT_PATH + "/endpoints";
        final Client client = ClientBuilder.newClient();
        client.register(new ResteasyClientLogger(this.logger, true));
        final Response response = client.target(baseUri).request().accept(ApiOverviewRestEndpoint.MEDIA_TYPE_JSON_V1).get();
        
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            final JsonObject jsonObject = response.readEntity(JsonObject.class);
            
            final String responseBasePath = jsonObject.get("basePath").toString();
            final String responseFullPath = jsonObject.get("fullPath").toString();
            
            assertThat(responseBasePath, is(equalTo("\"/overview\"")));
            assertThat(responseFullPath, is(equalTo("\"/overview/endpoints\"")));
            
            this.logger.info(LOGGER_PREFIX + "test01_readAvailableEndpoints_Pass [response=" + jsonObject.toString() + "]");
        }
    }
}
