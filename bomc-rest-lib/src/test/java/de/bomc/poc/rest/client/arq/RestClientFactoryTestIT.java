package de.bomc.poc.rest.client.arq;

import de.bomc.poc.rest.client.factory.RestClientFactory;
import de.bomc.poc.rest.logger.client.ResteasyClientLogger;
import de.bomc.poc.rest.logger.server.ResteasyServerLogger;
import de.bomc.poc.rest.mock.JaxRsActivator;
import de.bomc.poc.rest.mock.MockDTO;
import de.bomc.poc.rest.mock.MockResource;
import de.bomc.poc.rest.mock.MockResourceInterface;
import de.bomc.poc.rest.mock.SimpleClientRequestFilter;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.fail;

/**
 * Tests rest client factory.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: 6816 $ $Author: tzdbmm $ $Date: 2016-07-20 13:22:42 +0200 (Mi, 20 Jul 2016) $
 * @since 14.07.2016
 */
@RunWith(Arquillian.class)
public class RestClientFactoryTestIT {

    private static final String LOG_PREFIX = "RestClientFactoryTestIT#";
    private static final String WEB_ARCHIVE_NAME = "rest-factory-test";
    private static final Logger LOGGER = Logger.getLogger(RestClientFactoryTestIT.class);
    // The default scheme.
    final String DEFAULT_SCHEME = "http";
    // A systemProperty that is used, to get the host name of a running wildfly instance, during arquillian tests at runtime.
    final String WILDFLY_BIND_ADDRESS_SYSTEM_PROPERTY_KEY = "jboss.bind.address";
    // A systemProperty that is used, to get the port-offset of a running wildfly instance, during arquillian tests at runtime.
    final String WILDFLY_PORT_OFFSET_SYSTEM_PROPERTY_KEY = "jboss.socket.binding.port-offset";
    final String HOST_ADDRESS = "192.168.4.1";

    // 'testable = true', means all the tests are running inside the container.
    @Deployment(testable = true)
    public static WebArchive createDeployment() {
        final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, WEB_ARCHIVE_NAME + ".war");
        webArchive.addPackage(MockResource.class.getPackage()
                                                .getName());
        webArchive.addClasses(ResteasyClientLogger.class, ResteasyServerLogger.class);
        webArchive.addClasses(RestClientFactory.class);

        webArchive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        System.out.println("archiveContent: " + webArchive.toString(true));

        return webArchive;
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=RestClientFactoryTestIT#test010_createSimpleRestClient_Pass
     * </pre>
     * Tests using the <code>RestClientFactory</code>.
     * @throws URISyntaxException is thrown to indicate that a string could not be parsed as a URI reference.
     */
    @Test
    @InSequence(10)
    public void test010_createSimpleRestClient_Pass() throws URISyntaxException {
        LOGGER.debug(LOG_PREFIX + "test010_createSimpleRestClient_Pass");

        final Long REQUEST_PARAM = 2L;

        Response response = null;

        try {
            final MockResourceInterface proxy = RestClientFactory.createSimpleRestClient(MockResourceInterface.class, this.buildUri(RestClientFactoryTestIT.WEB_ARCHIVE_NAME), LOGGER, null);
            response = proxy.logToMe(REQUEST_PARAM);

            assertThat(response, notNullValue());
            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final String responseStr = response.readEntity(String.class);

                assertThat(REQUEST_PARAM, equalTo(Long.parseLong(responseStr)));

                LOGGER.debug(LOG_PREFIX + "test010_createSimpleRestClient_Pass [responseStr=" + responseStr + "]");
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
     *  mvn clean install -Parq-wildfly-remote -Dtest=RestClientFactoryTestIT#test020_createRestClientFactoryWithJson_Pass
     * </pre>
     * Tests using the <code>RestClientFactory</code>and returns a json object.
     * @throws URISyntaxException is thrown to indicate that a string could not be parsed as a URI reference.
     */
    @Test
    @InSequence(20)
    public void test020_createRestClientFactoryWithJson_Pass() throws URISyntaxException {
        LOGGER.debug(LOG_PREFIX + "test020_createRestClientFactoryWithJson_Pass");

        final MockDTO mockDTO = new MockDTO("myString", 10L, 15);

        Response response = null;

        try {
            final MockResourceInterface proxy = RestClientFactory.createSimpleRestClient(MockResourceInterface.class, this.buildUri(RestClientFactoryTestIT.WEB_ARCHIVE_NAME), LOGGER, null);
            response = proxy.getMockDTO(mockDTO);

            assertThat(response, notNullValue());
            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final MockDTO retMockDTO = response.readEntity(MockDTO.class);

                assertThat(mockDTO, is(equalTo(retMockDTO)));
                LOGGER.debug(LOG_PREFIX + "test020_createRestClientFactoryWithJson_Pass [retMockDTO=" + retMockDTO + "]");
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
     *  mvn clean install -Parq-wildfly-remote -Dtest=RestClientFactoryTestIT#test030_createRestClientFactoryAsClientWithJson_Pass
     * </pre>
     * Tests using the <code>RestClientFactory</code>and returns a json object. This client is running outside the container. So System.properties could nt be determined.
     * @throws URISyntaxException is thrown to indicate that a string could not be parsed as a URI reference.
     */
    @Test
    @RunAsClient
    @InSequence(30)
    public void test030_createRestClientFactoryAsClientWithJson_Pass() throws URISyntaxException {
        LOGGER.debug(LOG_PREFIX + "test030_createRestClientFactoryAsClientWithJson_Pass");

        final MockDTO mockDTO = new MockDTO("myString", 10L, 15);

        Response response = null;

        try {
            final URI uri = new URI(this.DEFAULT_SCHEME, null, HOST_ADDRESS, 8180, "/" + RestClientFactoryTestIT.WEB_ARCHIVE_NAME + "/" + JaxRsActivator.APPLICATION_PATH, null, null);
            final MockResourceInterface proxy = RestClientFactory.createSimpleRestClient(MockResourceInterface.class, uri, LOGGER, null);
            response = proxy.getMockDTO(mockDTO);

            assertThat(response, notNullValue());
            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final MockDTO retMockDTO = response.readEntity(MockDTO.class);

                assertThat(mockDTO, is(equalTo(retMockDTO)));
                LOGGER.debug(LOG_PREFIX + "test030_createRestClientFactoryAsClientWithJson_Pass [retMockDTO=" + retMockDTO + "]");
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
            fail(LOG_PREFIX + "test030_createRestClientFactoryAsClientWithJson_Pass - Should not happen,check the running wildfly instance against host and port, must be host=" + HOST_ADDRESS + " and port=8080.");
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=RestClientFactoryTestIT#test040_createRestClientFactoryWithJsonAsList_Pass
     * </pre>
     * Tests using the <code>RestClientFactory</code>and returns a json object.
     * @throws URISyntaxException is thrown to indicate that a string could not be parsed as a URI reference.
     */
    @Test
    @InSequence(40)
    public void test040_createRestClientFactoryWithJsonAsList_Pass() throws URISyntaxException {
        LOGGER.debug(LOG_PREFIX + "test040_createRestClientFactoryWithJsonAsList_Pass");

        final List<MockDTO> mockDTOList = new ArrayList<>();
        final MockDTO mockDTO_1 = new MockDTO("myString_1", 10L, 15);
        mockDTOList.add(mockDTO_1);
        final MockDTO mockDTO_2 = new MockDTO("myString_2", 10L, 15);
        mockDTOList.add(mockDTO_2);
        final MockDTO mockDTO_3 = new MockDTO("myString_3", 10L, 15);
        mockDTOList.add(mockDTO_3);
        final MockDTO mockDTO_4 = new MockDTO("myString_4", 10L, 15);
        mockDTOList.add(mockDTO_4);
        final MockDTO mockDTO_5 = new MockDTO("myString_5", 10L, 15);
        mockDTOList.add(mockDTO_5);

        Response response = null;

        try {
            final MockResourceInterface proxy = RestClientFactory.createSimpleRestClient(MockResourceInterface.class, this.buildUri(RestClientFactoryTestIT.WEB_ARCHIVE_NAME), LOGGER, null);
            response = proxy.getMockDTOAsList(mockDTOList);

            assertThat(response, notNullValue());
            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final List<MockDTO> retMockDTOList = response.readEntity(new GenericType<List<MockDTO>>() {});

                assertThat(mockDTOList.size(), is(equalTo(retMockDTOList.size())));
                LOGGER.debug(LOG_PREFIX + "test040_createRestClientFactoryWithJsonAsList_Pass [retMockDTOList.size=" + retMockDTOList.size() + "]");
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
     *  mvn clean install -Parq-wildfly-remote -Dtest=RestClientFactoryTestIT#test050_createRestClientFactoryWithJsonAsListAndRunAsClient_Pass
     * </pre>
     * Tests using the <code>RestClientFactory</code>and returns a json object. This client is running outside the container. So System.properties could nt be determined.
     * @throws URISyntaxException is thrown to indicate that a string could not be parsed as a URI reference.
     */
    @Test
    @RunAsClient
    @InSequence(50)
    public void test050_createRestClientFactoryWithJsonAsListAndRunAsClient_Pass() throws URISyntaxException {
        LOGGER.debug(LOG_PREFIX + "test050_createRestClientFactoryWithJsonAsListAndRunAsClient_Pass");

        final List<MockDTO> mockDTOList = new ArrayList<>();
        final MockDTO mockDTO_1 = new MockDTO("myString_1", 10L, 15);
        mockDTOList.add(mockDTO_1);
        final MockDTO mockDTO_2 = new MockDTO("myString_2", 10L, 15);
        mockDTOList.add(mockDTO_2);
        final MockDTO mockDTO_3 = new MockDTO("myString_3", 10L, 15);
        mockDTOList.add(mockDTO_3);
        final MockDTO mockDTO_4 = new MockDTO("myString_4", 10L, 15);
        mockDTOList.add(mockDTO_4);
        final MockDTO mockDTO_5 = new MockDTO("myString_5", 10L, 15);
        mockDTOList.add(mockDTO_5);

        Response response = null;

        try {
            final URI uri = new URI(this.DEFAULT_SCHEME, null, HOST_ADDRESS, 8180, "/" + RestClientFactoryTestIT.WEB_ARCHIVE_NAME + "/" + JaxRsActivator.APPLICATION_PATH, null, null);
            final MockResourceInterface proxy = RestClientFactory.createSimpleRestClient(MockResourceInterface.class, uri, LOGGER, null);
            response = proxy.getMockDTOAsList(mockDTOList);

            assertThat(response, notNullValue());
            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final List<MockDTO> retMockDTOList = response.readEntity(new GenericType<List<MockDTO>>() {});

                assertThat(mockDTOList.size(), is(equalTo(retMockDTOList.size())));
                LOGGER.debug(LOG_PREFIX + "test050_createRestClientFactoryWithJsonAsListAndRunAsClient_Pass [retMockDTOList.size=" + retMockDTOList.size() + "]");
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
            fail(LOG_PREFIX + "test050_createRestClientFactoryWithJsonAsListAndRunAsClient_Pass - Should not happen,check the running wildfly instance against host and port, must be host=" + HOST_ADDRESS + " and port=8080.");
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * <pre>
     *  ____________________________________________________________________________________________
     *  NOTE:
     *  Test validation has to be made in logfile: see for 'I'm here... and do the whole enchilada'.
     *
     *  mvn clean install -Parq-wildfly-remote -Dtest=RestClientFactoryTestIT#test060_createRestClientFactoryWithClientFilter_Pass
     * </pre>
     * Tests using the <code>RestClientFactory</code>and returns a json object.
     * @throws URISyntaxException is thrown to indicate that a string could not be parsed as a URI reference.
     */
    @Test
    @InSequence(60)
    public void test060_createRestClientFactoryWithClientFilter_Pass() throws URISyntaxException {
        LOGGER.debug(LOG_PREFIX + "test060_createRestClientFactoryWithClientFilter_Pass");

        final List<MockDTO> mockDTOList = new ArrayList<>();
        final MockDTO mockDTO_1 = new MockDTO("myString_1", 10L, 15);
        mockDTOList.add(mockDTO_1);

        Response response = null;

        try {
            final List<Object> clientFilterList = new ArrayList<>();
            clientFilterList.add(new SimpleClientRequestFilter());
            final MockResourceInterface proxy = RestClientFactory.createSimpleRestClient(MockResourceInterface.class, this.buildUri(RestClientFactoryTestIT.WEB_ARCHIVE_NAME), LOGGER, clientFilterList);
            response = proxy.getMockDTOAsList(mockDTOList);

            assertThat(response, notNullValue());
            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final List<MockDTO> retMockDTOList = response.readEntity(new GenericType<List<MockDTO>>() {});

                assertThat(mockDTOList.size(), is(equalTo(retMockDTOList.size())));
                LOGGER.debug(LOG_PREFIX + "test060_createRestClientFactoryWithClientFilter_Pass [retMockDTOList.size=" + retMockDTOList.size() + "]");
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
     * @throws URISyntaxException is thrown to indicate that a string could not be parsed as a URI reference.
     */
    private URI buildUri(final String webArchiveName) throws URISyntaxException {
        // The wildfly default port for http requests.
        final int WILDFLY_DEFAULT_HTTP_PORT = 8080;
        // Get port of the running wildfly instance.
        final int port = WILDFLY_DEFAULT_HTTP_PORT + Integer.parseInt(System.getProperty(this.WILDFLY_PORT_OFFSET_SYSTEM_PROPERTY_KEY));

        // Build the base Url.
        return new URI(this.DEFAULT_SCHEME, null, System.getProperty(this.WILDFLY_BIND_ADDRESS_SYSTEM_PROPERTY_KEY), port, "/" + webArchiveName + "/" + JaxRsActivator.APPLICATION_PATH, null, null);
    }
}

