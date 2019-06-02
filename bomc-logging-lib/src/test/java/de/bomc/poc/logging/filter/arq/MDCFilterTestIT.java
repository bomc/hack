package de.bomc.poc.logging.filter.arq;

import de.bomc.poc.logging.filter.MDCFilter;
import de.bomc.poc.logging.filter.UIDHeaderRequestFilter;
import de.bomc.poc.logging.filter.mock.JaxRsActivator;
import de.bomc.poc.logging.filter.mock.MockHeaderRequestFilter;
import de.bomc.poc.logging.filter.mock.MockResource;
import de.bomc.poc.logging.filter.mock.MockResourceInterface;
import de.bomc.poc.logging.filter.mock.ResteasyClientLogger;
import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.logging.MDC;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Tests setting the requestId.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: 7096 $ $Author: tzdbmm $ $Date: 2016-08-03 14:59:36 +0200 (Mi, 03 Aug 2016) $
 * @since 19.07.2016
 */
@RunWith(Arquillian.class)
public class MDCFilterTestIT {

    private static final String WEB_ARCHIVE_NAME = "logging-filter-test";
    /** Logger */
    @Inject
    @LoggerQualifier
    private Logger logger;
    private static final Logger LOGGER = Logger.getLogger(MDCFilterTestIT.class.getName());
    private static final String LOG_PREFIX = "MDCFilterTestIT#";
    private static String uuid;

    // NOTE:
    // __________________________________________________________________
    // 'testable = false', means all the tests are running outside of the
    // container.
    @Deployment(testable = true, order = 1)
    public static WebArchive createTestArchive() {
        final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, WEB_ARCHIVE_NAME + ".war");
        webArchive.addClass(MDCFilterTestIT.class)
                  .addClasses(LoggerProducer.class, LoggerQualifier.class, ResteasyClientLogger.class);
        webArchive.addClasses(JaxRsActivator.class, MockResource.class, MockResourceInterface.class, MockHeaderRequestFilter.class);
        webArchive.addClasses(MDCFilter.class, UIDHeaderRequestFilter.class);
        webArchive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        System.out.println(LOG_PREFIX + "archiveContent: " + webArchive.toString(true));

        return webArchive;
    }

    @Before
    public void setup() {
        uuid =
            UUID.randomUUID()
                .toString();
    }

    /**
     * Tests {@link UIDHeaderRequestFilter}, {@link MDCFilter} without given requestId (using DefaultConstructor), so the requestId has to be created by the {@link UIDHeaderRequestFilter}.
     * <pre>
     * 	mvn clean install -Parq-wildfly-remote -Dtest=MDCFilterTestIT#test010_MDCWithUIDHeaderRequestFilterWithoutOwnUID_Pass
     * </pre>
     */
    @Test
    @InSequence(10)
    public void test010_MDCWithUIDHeaderRequestFilterWithoutOwnUID_Pass() {
        LOGGER.debug(LOG_PREFIX + "test010_MDCWithUIDHeaderRequestFilterWithoutOwnUID_Pass");

        final Long REQUEST_PARAM = 2L;

        MDC.put(MDCFilter.HEADER_REQUEST_ID_ATTR, "myRequestId");
        Map<String, Object> map = MDC.getMap();
        map.entrySet().forEach(entry -> {
        	LOGGER.debug(LOG_PREFIX + "test010_MDCWithUIDHeaderRequestFilterWithoutOwnUID_Pass 1 [key=" + entry.getKey() + ", value=" + entry.getValue() + "]");

        }); 
        
        // Create rest client with Resteasy Client Framework.
        final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS)
                                                                 .socketTimeout(10, TimeUnit.SECONDS)
                                                                 .register(new UIDHeaderRequestFilter())
                                                                 .register(new ResteasyClientLogger(this.logger, true))
                                                                 .build();
        final ResteasyWebTarget webTarget = client.target(UriBuilder.fromPath(this.buildBaseUrl(WEB_ARCHIVE_NAME) + "/" + JaxRsActivator.APPLICATION_PATH));

        Response response = null;

        try {
            final MockResourceInterface proxy = webTarget.proxy(MockResourceInterface.class);
            response = proxy.logToMe(REQUEST_PARAM);

            assertThat(response, notNullValue());
            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final String responseStr = response.readEntity(String.class);

                assertThat(responseStr, notNullValue());
                assertThat(responseStr, containsString("-"));

                Map<String, Object> responseMap = MDC.getMap();
                responseMap.entrySet().forEach(entry -> {
                	LOGGER.debug(LOG_PREFIX + "test010_MDCWithUIDHeaderRequestFilterWithoutOwnUID_Pass 2 [key=" + entry.getKey() + ", value=" + entry.getValue() + "]");
                }); 
                
                LOGGER.debug(LOG_PREFIX + "test010_MDCWithUIDHeaderRequestFilterWithoutOwnUID_Pass 3 [value=" + MDC.get(MDCFilter.HEADER_REQUEST_ID_ATTR) + "]");

                LOGGER.debug(LOG_PREFIX + "test010_MDCWithUIDHeaderRequestFilterWithoutOwnUID_Pass [responseStr=" + responseStr + "]");
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
     * Tests {@link UIDHeaderRequestFilter} with given requestId.
     * <pre>
     * 	mvn clean install -Parq-wildfly-remote -Dtest=MDCFilterTestIT#test020_MDCWithUIDHeaderRequestFilterWithUID_Pass
     * </pre>
     */
    @Test
    @InSequence(20)
    public void test020_MDCWithUIDHeaderRequestFilterWithUID_Pass() {
        LOGGER.debug(LOG_PREFIX + "test020_MDCWithUIDHeaderRequestFilterWithUID_Pass");

        final Long REQUEST_PARAM = 2L;

        // Create rest client with Resteasy Client Framework.
        final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS)
                                                                 .socketTimeout(10, TimeUnit.SECONDS)
                                                                 .register(new UIDHeaderRequestFilter(uuid))
                                                                 .build();
        final ResteasyWebTarget webTarget = client.target(UriBuilder.fromPath(this.buildBaseUrl(WEB_ARCHIVE_NAME) + "/" + JaxRsActivator.APPLICATION_PATH));

        Response response = null;

        try {
            final MockResourceInterface proxy = webTarget.proxy(MockResourceInterface.class);
            response = proxy.logToMe(REQUEST_PARAM);

            assertThat(response, notNullValue());
            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final String responseStr = response.readEntity(String.class);

                assertThat(responseStr, equalTo(uuid));

                LOGGER.debug(LOG_PREFIX + "test020_MDCWithUIDHeaderRequestFilterWithUID_Pass [responseStr=" + responseStr + "]");
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
     * Tests {@link MDCFilter}, no uuid is set by client request, so the {@link MDCFilter} has to set this.
     * <pre>
     * 	mvn clean install -Parq-wildfly-remote -Dtest=MDCFilterTestIT#test030_MDC_Pass
     * </pre>
     */
    @Test
    @InSequence(30)
    public void test030_MDC_Pass() {
        LOGGER.debug(LOG_PREFIX + "test030_MDC_Pass");

        final Long REQUEST_PARAM = 2L;

        // Create rest client with Resteasy Client Framework.
        final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS)
                                                                 .socketTimeout(10, TimeUnit.SECONDS)
                                                                 .build();
        final ResteasyWebTarget webTarget = client.target(UriBuilder.fromPath(this.buildBaseUrl(WEB_ARCHIVE_NAME) + "/" + JaxRsActivator.APPLICATION_PATH));

        Response response = null;

        try {
            final MockResourceInterface proxy = webTarget.proxy(MockResourceInterface.class);
            response = proxy.logToMe(REQUEST_PARAM);

            assertThat(response, notNullValue());
            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final String responseStr = response.readEntity(String.class);

                assertThat(responseStr, notNullValue());
                assertThat(responseStr, containsString("-"));

                LOGGER.debug(LOG_PREFIX + "test030_MDC_Pass [responseStr=" + responseStr + "]");
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
     * Tests {@link MDCFilter} with incomplete header, the value is not set.
     * <pre>
     * 	mvn clean install -Parq-wildfly-remote -Dtest=MDCFilterTestIT#test040_MDCWithIncompleteHeader_Pass
     * </pre>
     */
    @Test
    @InSequence(40)
    public void test040_MDCWithIncompleteHeader_Pass() {
        LOGGER.debug(LOG_PREFIX + "test040_MDCWithIncompleteHeader_Pass");

        final Long REQUEST_PARAM = 2L;

        // Create rest client with Resteasy Client Framework.
        final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS)
                                                                 .socketTimeout(10, TimeUnit.SECONDS)
                                                                 .register(new MockHeaderRequestFilter())
                                                                 .build();
        final ResteasyWebTarget webTarget = client.target(UriBuilder.fromPath(this.buildBaseUrl(WEB_ARCHIVE_NAME) + "/" + JaxRsActivator.APPLICATION_PATH));

        Response response = null;

        try {
            final MockResourceInterface proxy = webTarget.proxy(MockResourceInterface.class);
            response = proxy.logToMe(REQUEST_PARAM);

            assertThat(response, notNullValue());
            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final String responseStr = response.readEntity(String.class);

                assertThat(responseStr, notNullValue());
                assertThat(responseStr, containsString("-"));

                LOGGER.debug(LOG_PREFIX + "test040_MDCWithIncompleteHeader_Pass [responseStr=" + responseStr + "]");
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
    private String buildBaseUrl(final String webArchiveName) {
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
