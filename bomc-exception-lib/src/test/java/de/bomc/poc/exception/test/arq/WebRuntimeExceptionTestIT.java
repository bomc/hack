package de.bomc.poc.exception.test.arq;

import de.bomc.poc.exception.RootRuntimeException;
import de.bomc.poc.exception.cdi.interceptor.ExceptionHandlerInterceptor;
import de.bomc.poc.exception.cdi.qualifier.ExceptionHandlerQualifier;
import de.bomc.poc.exception.core.ErrorCode;
import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.exception.core.web.ApiErrorResponseObject;
import de.bomc.poc.exception.core.web.WebRuntimeException;
import de.bomc.poc.exception.test.TestApiError;
import de.bomc.poc.exception.test.TestErrorCode;
import de.bomc.poc.exception.test.arq.mock.application.JaxRsActivator;
import de.bomc.poc.exception.test.arq.mock.application.MockResourceInterface;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Kommentar.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: 6814 $ $Author: tzdbmm $ $Date: 2016-07-20 13:22:21 +0200 (Mi, 20 Jul 2016) $
 * @since 12.07.2016
 */
@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WebRuntimeExceptionTestIT {

    private static final String LOG_PREFIX = "WebRuntimeExceptionTestIT#";
    private static final String WEB_ARCHIVE_NAME = "web-runtime-exception-test";
    private static final Logger LOGGER = Logger.getLogger(WebRuntimeExceptionTestIT.class);
    /**
     * The resource target identified by the resource URI.
     */
    private static ResteasyWebTarget webTarget;

    // 'testable = true', means all the tests are running inside the container.
    @Deployment(testable = true)
    public static WebArchive createDeployment() {
        final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, WEB_ARCHIVE_NAME + ".war");
        webArchive.addClass(WebRuntimeExceptionTestIT.class);
        webArchive.addClasses(ErrorCode.class, RootRuntimeException.class);
        webArchive.addClasses(TestApiError.class, TestErrorCode.class);
        webArchive.addPackage(AppRuntimeException.class.getPackage()
                                                       .getName());
        webArchive.addPackage(WebRuntimeException.class.getPackage()
                                                       .getName());
        webArchive.addClasses(ExceptionHandlerInterceptor.class, ExceptionHandlerQualifier.class);
        webArchive.addPackage(JaxRsActivator.class.getPackage()
                                                  .getName());
        webArchive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        System.out.println("archiveContent: " + webArchive.toString(true));

        return webArchive;
    }

//    private static StringAsset getBeansXml() {
//        return new StringAsset("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
//                               + "<!-- Marker file indicating CDI should be enabled -->\n"
//                               + "<beans xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
//                               + "\txsi:schemaLocation=\"\n"
//                               + "        http://xmlns.jcp.org/xml/ns/javaee\n"
//                               + "        http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd\"\n"
//                               + "\tbean-discovery-mode=\"all\">\n"
//                               + "\t<interceptors>\n"
//                               + "\t\t<class>de.bomc.poc.exception.cdi.interceptor.ExceptionHandlerInterceptor</class>\n"
//                               + "\t</interceptors>\n"
//                               + "</beans>");
//    }

    /**
     * Setup.
     */
    @Before
    public void setupClass() {
        // Create rest client with Resteasy Client Framework.
        final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS)
                                                                 .socketTimeout(10, TimeUnit.SECONDS)
                                                                 .build();
        webTarget = client.target(UriBuilder.fromPath(this.buildBaseUrl(WEB_ARCHIVE_NAME) + "/" + JaxRsActivator.APPLICATION_PATH));
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=WebRuntimeExceptionTestIT#test010_ExceptionWithApiError_pass
     * </pre>
     */
    @Test
    public void test010_ExceptionWithApiError_pass() {
        LOGGER.debug(LOG_PREFIX + "test010_ExceptionWithApiError_pass");

        Response response = null;

        try {
            final MockResourceInterface proxy = webTarget.proxy(MockResourceInterface.class);
            response = proxy.getExceptionWithApiError(2L);

            assertThat(response, notNullValue());
            assertThat(response.getStatus(), equalTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));

            if (response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
                final ApiErrorResponseObject responseObject = response.readEntity(ApiErrorResponseObject.class);

                assertThat(responseObject.getErrorCode(), equalTo(TestApiError.TEST_API_00101.name()));

                LOGGER.debug(LOG_PREFIX + "test010_ExceptionWithApiError_pass [response=" + responseObject + "]");
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=WebRuntimeExceptionTestIT#test020_ExceptionWithStatusAndErrorCode_pass
     * </pre>
     */
    @Test
    public void test020_ExceptionWithStatusAndErrorCode_pass() {
        LOGGER.debug(LOG_PREFIX + "test020_ExceptionWithStatusAndErrorCode_pass");

        Response response = null;

        try {
            final MockResourceInterface proxy = webTarget.proxy(MockResourceInterface.class);
            response = proxy.getExceptionWithStatusAndErrorCode(2L);

            assertThat(response, notNullValue());
            assertThat(response.getStatus(), equalTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));

            if (response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
                final ApiErrorResponseObject responseObject = response.readEntity(ApiErrorResponseObject.class);

                assertThat(responseObject.getErrorCode(), equalTo(TestApiError.TEST_API_00102.name()));

                LOGGER.debug("WebRuntimeExceptionTestIT#test020_ExceptionWithStatusAndErrorCode_pass [response=" + responseObject + "]");
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=WebRuntimeExceptionTestIT#test030_ExceptionWithWrappedException_pass
     * </pre>
     */
    @Test
    public void test030_ExceptionWithWrappedException_pass() {
        LOGGER.debug(LOG_PREFIX + "test030_ExceptionWithWrappedException_pass");

        Response response = null;

        try {
            final MockResourceInterface proxy = webTarget.proxy(MockResourceInterface.class);
            response = proxy.getExceptionWithWrappedException(2L);

            assertThat(response, notNullValue());
            assertThat(response.getStatus(), equalTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));

            if (response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
                final ApiErrorResponseObject responseObject = response.readEntity(ApiErrorResponseObject.class);

                assertThat(responseObject.getErrorCode(), equalTo(TestApiError.TEST_API_00101.name()));

                LOGGER.debug("WebRuntimeExceptionTestIT#test030_ExceptionWithWrappedException_pass [response=" + responseObject + "]");
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=WebRuntimeExceptionTestIT#test040_WrappedExceptionInARuntimeException_pass
     * </pre>
     */
    @Test
    public void test040_WrappedExceptionInARuntimeException_pass() {
        LOGGER.debug(LOG_PREFIX + "test040_WrappedExceptionInARuntimeException_pass");

        Response response = null;

        try {
            final MockResourceInterface proxy = webTarget.proxy(MockResourceInterface.class);
            response = proxy.getWrappedExceptionInARuntimeException(2L);

            assertThat(response, notNullValue());
            assertThat(response.getStatus(), equalTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));

            if (response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
                final ApiErrorResponseObject responseObject = response.readEntity(ApiErrorResponseObject.class);

                assertThat(responseObject.getErrorCode(), equalTo(TestApiError.TEST_API_00101.name()));

                LOGGER.debug("WebRuntimeExceptionTestIT#test040_WrappedExceptionInARuntimeException_pass [response=" + responseObject + "]");
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=WebRuntimeExceptionTestIT#test050_NotAWebRuntimeException_pass
     * </pre>
     */
    @Test
    public void test050_NotAWebRuntimeException_pass() {
        LOGGER.debug(LOG_PREFIX + "test050_NotAWebRuntimeException_pass");

        Response response = null;

        try {
            final MockResourceInterface proxy = webTarget.proxy(MockResourceInterface.class);
            response = proxy.getNotAWebRuntimeException(2L);

            assertThat(response, notNullValue());
            assertThat(response.getStatus(), equalTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));

            if (response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
                final ApiErrorResponseObject responseObject = response.readEntity(ApiErrorResponseObject.class);

                assertThat(responseObject.getErrorCode(), equalTo("0"));

                LOGGER.debug("WebRuntimeExceptionTestIT#test050_NotAWebRuntimeException_pass [response=" + responseObject + "]");
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=WebRuntimeExceptionTestIT#test060_ConstraintValidationException_pass
     * </pre>
     */
    @Test
    public void test060_ConstraintValidationException_pass() {
        LOGGER.debug(LOG_PREFIX + "test060_ConstraintValidationException_pass");

        Response response = null;

        try {
            final MockResourceInterface proxy = webTarget.proxy(MockResourceInterface.class);
            response = proxy.getNotAWebRuntimeException(2L);

            assertThat(response, notNullValue());
            assertThat(response.getStatus(), equalTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));

            if (response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
                final ApiErrorResponseObject responseObject = response.readEntity(ApiErrorResponseObject.class);

                assertThat(responseObject.getErrorCode(), equalTo("0"));

                LOGGER.debug("WebRuntimeExceptionTestIT#test060_ConstraintValidationException_pass [response=" + responseObject + "]");
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=WebRuntimeExceptionTestIT#test070_AppRuntimeException_pass
     * </pre>
     */
    @Test
    public void test070_AppRuntimeException_pass() {
        LOGGER.debug(LOG_PREFIX + "test070_AppRuntimeException_pass");

        Response response = null;

        try {
            final MockResourceInterface proxy = webTarget.proxy(MockResourceInterface.class);
            response = proxy.getAppRuntimeException(2L);

            assertThat(response, notNullValue());
            assertThat(response.getStatus(), equalTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));

            if (response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
                final ApiErrorResponseObject responseObject = response.readEntity(ApiErrorResponseObject.class);

                assertThat(responseObject.getErrorCode(), equalTo(TestErrorCode.TEST_00101.name()));

                LOGGER.debug("WebRuntimeExceptionTestIT#test070_AppRuntimeException_pass [response=" + responseObject + "]");
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * The arquillian war is already deployed to wildfly, read now the connection properties from global system properties, to build the base url.
     * @param webArchiveName part of the base url, 'http://localhost:8080/webArchiveName/'.
     * @return the base url, created from system properties.
     */
    protected String buildBaseUrl(final String webArchiveName) {
        // A systemProperty that is used, to get the host name of a running wildfly instance, during arquillian tests at runtime.
        final String WILDFLY_BIND_ADDRESS_SYSTEM_PROPERTY_KEY = "jboss.bind.address";
        final String bindAddressProperty = System.getProperty(WILDFLY_BIND_ADDRESS_SYSTEM_PROPERTY_KEY);
        // A systemProperty that is used, to get the port-offset of a running wildfly instance, during arquillian tests at runtime.
        final String WILDFLY_PORT_OFFSET_SYSTEM_PROPERTY_KEY = "jboss.socket.binding.port-offset";
        // The wildfly default port for http requests.
        final int WILDFLY_DEFAULT_HTTP_PORT = 8080;
        // Get port of the running wildfly instance.
        final int port = WILDFLY_DEFAULT_HTTP_PORT + Integer.parseInt(System.getProperty(WILDFLY_PORT_OFFSET_SYSTEM_PROPERTY_KEY));

        // Build the base Url.
        return "http://" + bindAddressProperty + ":" + port + "/" + webArchiveName;
    }
}
