package de.bomc.poc.rest.logger.arq;

import de.bomc.poc.rest.logger.client.ResteasyClientLogger;
import de.bomc.poc.rest.logger.server.ResteasyServerLogger;
import de.bomc.poc.rest.mock.JaxRsActivator;
import de.bomc.poc.rest.mock.MockDTO;
import de.bomc.poc.rest.mock.MockResource;
import de.bomc.poc.rest.mock.MockResourceInterface;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Tests client(from inside the container) and server logging.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: 6816 $ $Author: tzdbmm $ $Date: 2016-07-20 13:22:42 +0200 (Mi, 20 Jul 2016) $
 * @since 14.07.2016
 */
@RunWith(Arquillian.class)
public class RestLoggerTestIT {
    private static final String LOG_PREFIX = "RestLoggerTestIT#";
    private static final String WEB_ARCHIVE_NAME = "rest-logger-test";
    private static final Logger LOGGER = Logger.getLogger(RestLoggerTestIT.class);
    /**
     * The resource target identified by the resource URI.
     */
    private static ResteasyWebTarget webTarget;

    // 'testable = true', means all the tests are running inside the container.
    @Deployment(testable = true)
    public static WebArchive createDeployment() {
        final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, WEB_ARCHIVE_NAME + ".war");
        webArchive.addPackage(MockResource.class.getPackage()
                                                .getName());
        webArchive.addClasses(ResteasyClientLogger.class, ResteasyServerLogger.class);

        webArchive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        System.out.println(LOG_PREFIX + "archiveContent: " + webArchive.toString(true));

        return webArchive;
    }

    /**
     * Setup.
     */
    @Before
    public void setupClass() {
        // Create rest client with Resteasy Client Framework.
        final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS)
                                                                 .socketTimeout(10, TimeUnit.SECONDS)
                                                                 .build();
        // Add the client logger.
        client.register(new ResteasyClientLogger(LOGGER, true));
        webTarget = client.target(UriBuilder.fromPath(this.buildBaseUrl(WEB_ARCHIVE_NAME) + "/" + JaxRsActivator.APPLICATION_PATH));
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=RestLoggerTestIT#test010_clientLogging
     * </pre>
     * Tests client logging from inside the container.
     */
    @Test
    @InSequence(10)
    public void test010_clientLogging() {
        LOGGER.debug(LOG_PREFIX + "test010_clientLogging");

        final Long REQUEST_PARAM = 2L;

        Response response = null;

        try {
            final MockResourceInterface proxy = webTarget.proxy(MockResourceInterface.class);
            response = proxy.logToMe(REQUEST_PARAM);

            assertThat(response, notNullValue());
            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final String responseStr = response.readEntity(String.class);

                assertThat(REQUEST_PARAM, equalTo(Long.parseLong(responseStr)));

                LOGGER.debug(LOG_PREFIX + "test010_clientLogging [responseStr=" + responseStr + "]");
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
     *  mvn clean install -Parq-wildfly-remote -Dtest=RestLoggerTestIT#test020_largeMessageLogging
     * </pre>
     * Tests client logging from inside the container.
     */
    @Test
    @InSequence(20)
    public void test020_largeMessageLogging() {
        LOGGER.debug(LOG_PREFIX + "test020_largeMessageLogging");

        Response response = null;

        try {
            final List<MockDTO> mockDTOList = new ArrayList<>();

            for (int i = 0; i < 100000; i++) {
                final MockDTO mockDTO = new MockDTO(("mockStr" + i), 1L, i);
                mockDTOList.add(mockDTO);
            }

            final MockResourceInterface proxy = webTarget.proxy(MockResourceInterface.class);
            response = proxy.getMockDTOAsList(mockDTOList);

            assertThat(response, notNullValue());
            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final List<MockDTO> retMockDTOList = response.readEntity(new GenericType<List<MockDTO>>() {});

                assertThat(mockDTOList.size(), is(equalTo(retMockDTOList.size())));
                LOGGER.debug(LOG_PREFIX + "test050_createRestClientFactoryWithJsonAsListAndRunAsClient [retMockDTOList.size=" + retMockDTOList.size() + "]");

                // ______________________________________
                // Check log file for '...more...' entry.
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
