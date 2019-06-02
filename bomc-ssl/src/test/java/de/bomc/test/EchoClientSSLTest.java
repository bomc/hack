package de.bomc.test;

//import de.bomc.poc.rest.EchoRestResource;
//import de.bomc.poc.rest.endpoints.impl.EchoRESTResourceImpl;
//import de.bomc.poc.rest.logger.ResteasyClientLogger;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

/**
 * -Djavax.net.debug=SSL,handshake,data,trustmanager </p>
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 */
public class EchoClientSSLTest {

//    /** Logger. */
//    private static final Logger LOGGER = Logger.getLogger(EchoClientSSLTest.class);
//    private static final String BASE_URI = "https://192.168.4.1:";
//    // Keystore and truststore parameter.
//    private static final String KEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword";
//    private static final String KEY_STORE_PATH = "javax.net.ssl.keyStore";
//    private static final String KEY_STORE_TYPE = "javax.net.ssl.keyStoreType";
//    private static final String TRUST_STORE_PASSWORD = "javax.net.ssl.trustStorePassword";
//    private static final String TRUST_STORE_PATH = "javax.net.ssl.trustStore";
//    private static final String TRUST_STORE_TYPE = "javax.net.ssl.trustStoreType";
//    @InjectMocks
//    private static final EchoRESTResourceImpl sut = new EchoRESTResourceImpl();
//    private static int port = 8443;
//    private static TJWSEmbeddedJaxrsServer server;
//
//    @BeforeClass
//    @SuppressWarnings({"rawtypes", "unchecked"})
//    public static void beforeClass() throws Exception {
//        server = new TJWSEmbeddedJaxrsServer();
//
//        // Set up SSL connections on server.
//        // Setup Keystore und Truststore.
//        server.setSSLPort(8443);
//        server.setSSLKeyStoreFile(EchoClientSSLTest.class.getClassLoader()
//                                                         .getResource("server.jks")
//                                                         .getFile());
//        server.setSSLKeyStorePass("tzdbmm");
//        server.setSSLKeyStoreType("JKS");
//
//        server.getDeployment()
//              .setResources((List)Arrays.asList(sut));
//
//        //server.getDeployment().getActualProviderClasses().addAll(Arrays.asList(ResteasyServerLogger.class));
//
//        server.start();
//    }
//
//    @AfterClass
//    public static void afterClass() throws Exception {
//        server.stop();
//    }
//
//    /**
//     * mvn clean install -Dtest=EchoClientSSLTest#test01_echo -Djavax.net.debug=SSL,handshake,data,trustmanager
//     *
//     * curl -v -i -H "Accept: application/xml" -H "Content-Type: application/xml" -H "egov_userId: my_egov_userId" -b token=my_token GET http://192.168.4.1:8180/egov/rest/echo/info
//     */
//    @Test
//    public void test01_echo() {
//        LOGGER.debug("EchoClientSSLTest#test01_echo");
//
//        // Setup Keystore und Truststore.
//        System.setProperty(EchoClientSSLTest.KEY_STORE_PATH, EchoClientSSLTest.class.getClassLoader()
//                                                                                    .getResource("client.jks")
//                                                                                    .getFile());
//        System.setProperty(EchoClientSSLTest.KEY_STORE_TYPE, "JKS");
//        System.setProperty(EchoClientSSLTest.KEY_STORE_PASSWORD, "tzdbmm");
//        System.setProperty(EchoClientSSLTest.TRUST_STORE_PATH, EchoClientSSLTest.class.getClassLoader()
//                                                                                      .getResource("client_truststore.jks")
//                                                                                      .getFile());
//        System.setProperty(EchoClientSSLTest.TRUST_STORE_PASSWORD, "tzdbmm");
//        System.setProperty(EchoClientSSLTest.TRUST_STORE_TYPE, "JKS");
//
//        // Erstelle Client.
//        final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS)
//                                                                 .socketTimeout(2, TimeUnit.SECONDS)
//                                                                 .build();
//        final ResteasyWebTarget webTarget = client.target(EchoClientSSLTest.BASE_URI + port);
//        webTarget.register(ResteasyClientLogger.class);
//
//        final EchoRestResource proxy = webTarget.proxy(EchoRestResource.class);
//
//        final Response response = proxy.info("the cookie token!", "egov_userId");
//
//        if (response != null) {
//            final NewCookie
//                newCookie =
//                response.getCookies()
//                        .get(EchoRestResource.TOKEN_PARAM);
//
//            LOGGER.info("EchoClientSSLTest#test01_echo [status="
//                        + response.getStatus()
//                        + ", token="
//                        + newCookie.getValue()
//                        + ", header="
//                        + response.getHeaders()
//                                  .get(EchoRestResource.HEADER_PARAM)
//                                  .iterator()
//                                  .next()
//                                  .toString()
//                        + ", payload="
//                        + response.readEntity(String.class)
//                        + "]");
//
//            assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
//        } else {
//            fail("Response ist null!");
//        }
//    }
}
