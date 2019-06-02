package ch.bs.zid.egov.test.unit;

import de.bomc.poc.rest.endpoints.impl.HealthRESTResourceImpl;
import de.bomc.poc.rest.endpoints.v1.HealthRESTResource;
import de.bomc.poc.service.impl.ServerStatisticsSingletonEJB;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests the {@link HealthRESTResource} as unit test.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
@RunWith(MockitoJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HealthRESTResourceTest {

    private static final String LOG_PREFIX = "HealthRESTResourceTest";
    private static final Logger LOGGER = Logger.getLogger(HealthRESTResourceTest.class);
    private static final String BASE_URI = "http://localhost:";
    private static int port;
    private static TJWSEmbeddedJaxrsServer server;
    @Mock
    private Logger logger;
    @Mock
    private ServerStatisticsSingletonEJB serverStatisticsEJB;
    @InjectMocks
    private static final HealthRESTResourceImpl sut = new HealthRESTResourceImpl();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeClass
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void beforeClass() throws Exception {
        port = findPort();

        server = new TJWSEmbeddedJaxrsServer();
        server.setPort(port);
        server.getDeployment()
              .setResources((List)Collections.singletonList(sut));
        server.start();
    }

    @AfterClass
    public static void cleanup() throws Exception {
        server.stop();
    }

    @Test
    public void test01_osInfo_Pass() throws Exception {
        LOGGER.debug(LOG_PREFIX + "#test01_osInfo_Pass");

        when(this.serverStatisticsEJB.osInfo()).thenReturn(this.osInfoJsonObjectAsMockData());

        // Create clients.
        final ResteasyClient resteasyClient = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS)
                                                                         .socketTimeout(10, TimeUnit.SECONDS)
                                                                         .build();
        final ResteasyWebTarget webTarget = resteasyClient.target(HealthRESTResourceTest.BASE_URI + port);
        final HealthRESTResource proxy = webTarget.proxy(HealthRESTResource.class);

        Response response = null;

        try {
            // Start request.
            response = proxy.osInfo();

            assertThat("Response must be != null", response, notNullValue());
            assertThat("Expected status OK", response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final String jsonString = response.readEntity(String.class);
                LOGGER.info("HealthRestResourceTestIT#test01_osInfo_Pass [response=" + jsonString + "]");
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * Find a free port on machine.
     * @return a free port.
     * @throws IOException if ServerSOcket failed.
     */
    public static int findPort() throws IOException {
        final ServerSocket server = new ServerSocket(0);
        final int port = server.getLocalPort();
        server.close();

        return port;
    }

    /**
     * @return a <code>JsonObject</code> with os informations.
     */
    public JsonObject osInfoJsonObjectAsMockData() {
        final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

        final JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("System Load Average", osBean.getSystemLoadAverage())
               .add("Available CPUs", osBean.getAvailableProcessors())
               .add("Architecture", osBean.getArch())
               .add("OS Name", osBean.getName())
               .add("Version", osBean.getVersion());

        return builder.build();
    }
    
}
