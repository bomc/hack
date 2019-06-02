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
package de.bomc.poc.test.exception.arq;

import de.bomc.poc.auth.rest.application.JaxRsActivator;
import de.bomc.poc.auth.rest.endpoint.v2.usermanagement.AuthUserManagementRestEndpoint;
import de.bomc.poc.exception.app.AppAuthRuntimeException;
import de.bomc.poc.exception.app.AppInvalidArgumentException;
import de.bomc.poc.exception.handling.ApiError;
import de.bomc.poc.exception.handling.ApiErrorResponse;
import de.bomc.poc.exception.handling.ApiUsermanagementError;
import de.bomc.poc.exception.handling.ResponseStatusAdapter;
import de.bomc.poc.exception.interceptor.ApiExceptionInterceptor;
import de.bomc.poc.exception.qualifier.ApiExceptionQualifier;
import de.bomc.poc.exception.web.WebUsermanagementException;
import de.bomc.poc.exception.web.WebAuthRuntimeException;
import de.bomc.poc.logger.producer.LoggerProducer;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.rest.filter.accept.AcceptHeaderRequestFilter;
import de.bomc.poc.rest.logger.ResteasyClientLogger;
import de.bomc.poc.test.exception.arq.mock.MockAuthUserManagementRestEndpointImpl;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
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

import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Tests exception handling by REST requests.
 * <pre>
 *     mvn clean install -Parq-wildfly-remote -Dtest=WebApiExceptionTestIT
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@PerformanceTest(resultsThreshold = 10)
@RunWith(Arquillian.class)
public class WebApiExceptionTestIT extends ArquillianExceptionBase {

    private static final String WEB_ARCHIVE_NAME = "auth-exception";
    private static final String LOG_PREFIX = "WebApiExceptionTestIT#";
    private static final Logger LOGGER = Logger.getLogger(WebApiExceptionTestIT.class);
    private static ResteasyWebTarget webTarget;

    @RunAsClient
    // 'testable = true', means all the tests are running inside of the container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
        webArchive.addClass(WebApiExceptionTestIT.class);
        webArchive.addClasses(LoggerProducer.class, LoggerQualifier.class);
        webArchive.addClasses(ApiError.class, ApiErrorResponse.class, ApiUsermanagementError.class, ResponseStatusAdapter.class);
        webArchive.addClass(ApiExceptionInterceptor.class);
        webArchive.addClass(ApiExceptionQualifier.class);
        webArchive.addClasses(WebAuthRuntimeException.class, AppAuthRuntimeException.class, AppInvalidArgumentException.class);
        webArchive.addClasses(JaxRsActivator.class, AuthUserManagementRestEndpoint.class, MockAuthUserManagementRestEndpointImpl.class);
        webArchive.addClass(ResteasyClientLogger.class);
        webArchive.addClass(AcceptHeaderRequestFilter.class);

        webArchive.addClass(WebUsermanagementException.class);

        webArchive.addAsResource("ApiUsermanagementError.properties");
        webArchive.addAsResource("ApiUsermanagementError_en.properties");

        System.out.println("WebApiExceptionTestIT#createDeployment: " + webArchive.toString(true));

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
                                                                 .socketTimeout(10, TimeUnit.SECONDS)
                                                                 .register(new ResteasyClientLogger(LOGGER, true))
                                                                 .build();

        WebApiExceptionTestIT.webTarget = client.target(buildBaseUrl(WEB_ARCHIVE_NAME) + "/" + JaxRsActivator.APPLICATION_PATH);
    }

    /**
     * Test the handling of WebAPIException.
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=WebApiExceptionTestIT#test01_throwWebApiException_Pass
     * </pre>
     * @throws Exception
     */
    @Test
    @InSequence(1)
    @Performance(time = 1500)
    public void test01_throwWebApiException_Pass() {
        LOGGER.debug(LOG_PREFIX + "test01_throwWebApiException_Pass");

        final AuthUserManagementRestEndpoint proxy = WebApiExceptionTestIT.webTarget.proxy(AuthUserManagementRestEndpoint.class);
        final Response response = proxy.findAllRolesByUsername("test");

        assertThat(response.getStatus(), is(equalTo(400)));

        LOGGER.info(LOG_PREFIX + "test01_throwWebApiException_Pass [response=" + response.toString() + "]");

        final ApiErrorResponse apiErrorResponse = response.readEntity(ApiErrorResponse.class);

        assertThat(apiErrorResponse.getErrorCode(), is(equalTo(ApiUsermanagementError.U10001.getErrorCode())));
    }
}
