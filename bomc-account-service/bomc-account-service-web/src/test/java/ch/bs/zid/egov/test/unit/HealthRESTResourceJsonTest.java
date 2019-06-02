package ch.bs.zid.egov.test.unit;

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
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.bomc.poc.rest.endpoints.impl.HealthRESTResourceImpl;
import de.bomc.poc.rest.endpoints.v1.HealthRESTResource;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Tests the {@link HealthRESTResource} as unit test with the TJWS-server.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HealthRESTResourceJsonTest {

    private static final String LOG_PREFIX = "HealthRESTResourceJsonTest";
    private static final Logger LOGGER = Logger.getLogger(HealthRESTResourceJsonTest.class);
    private static final String BASE_URI = "http://localhost:";
    private static int port;
    private static TJWSEmbeddedJaxrsServer server;
    @Mock
    private Logger logger;
    @InjectMocks
    private static final HealthRESTResourceImpl sut = new HealthRESTResourceImpl();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeClass
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void beforeClass() throws Exception {
        // Initialize the server.
        port = findPort();

        server = new TJWSEmbeddedJaxrsServer();
        server.setPort(port);
        server.getDeployment()
              .setResources((List)Collections.singletonList(sut));
        server.start();
    }

    @AfterClass
    public static void cleanup() throws Exception {
        // Cleanup resources.
        server.stop();
    }

//    @Test
//    public void test01_readFile_Pass() throws Exception {
//        LOGGER.debug(LOG_PREFIX + "#test01_readFile_Pass");
//
//        // Create clients.
//        final ResteasyClient resteasyClient = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS)
//                                                                         .socketTimeout(10, TimeUnit.SECONDS)
//                                                                         .build();
//        final ResteasyWebTarget webTarget = resteasyClient.target(HealthRESTResourceJsonTest.BASE_URI + port);
//        final HealthRESTResource proxy = webTarget.proxy(HealthRESTResource.class);
//
//        Response response = null;
//
//        try {
//            // Start request.
//            response = proxy.readFile();
//
//            assertThat("Response must be != null", response, notNullValue());
//            assertThat("Expected status OK", response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
//
//            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
//                final String jsonString = response.readEntity(String.class);
//                LOGGER.info("HealthRestResourceTestIT#test01_readFile_Pass [response=" + jsonString + "]");
//            }
//        } finally {
//            if (response != null) {
//                response.close();
//            }
//        }
//    }

    /**
     * Find a free port on machine.
     * @return a free port.
     * @throws IOException if ServerSOcket failed.
     */
    private static int findPort() throws IOException {
        final ServerSocket server = new ServerSocket(0);
        final int port = server.getLocalPort();
        server.close();

        return port;
    }
}
