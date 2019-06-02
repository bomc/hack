package de.bomc.test;

//import de.bomc.poc.rest.EchoRestResource;
import de.bomc.poc.rest.JaxRsActivator;
//import de.bomc.poc.rest.logger.ResteasyClientLogger;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.Test;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

public class EchoClientSSLWildflyTest {

//	private static final Logger LOGGER = Logger.getLogger(EchoClientSSLTest.class);
//	// ______________________________________________________
//	// Root context parameter --> see jboss-web.xml
//	// ------------------------------------------------------
//	private static final String WEB_ARCHIVE_NAME = "egov";
//	// ------------------------------------------------------
//	private static final String BASE_URI = "https://192.168.4.1:8543/" + WEB_ARCHIVE_NAME + "/";
//	// Keystore and truststore parameter.
//	private static final String KEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword";
//	private static final String KEY_STORE_PATH = "javax.net.ssl.keyStore";
//	private static final String KEY_STORE_TYPE = "javax.net.ssl.keyStoreType";
//	private static final String TRUST_STORE_PASSWORD = "javax.net.ssl.trustStorePassword";
//	private static final String TRUST_STORE_PATH = "javax.net.ssl.trustStore";
//	private static final String TRUST_STORE_TYPE = "javax.net.ssl.trustStoreType";
//
//	/**
//	 * mvn clean install -Dtest=EchoClientSSLWildflyTest#test01_echo
//	 * -Djavax.net.debug=SSL,handshake,data,trustmanager
//	 *
//	 * curl -v -i -H "Accept: application/xml" -H "Content-Type:
//	 * application/xml" -H "egov_userId: my_egov_userId" -b token=my_token GET
//	 * http://192.168.4.1:8180/egov/rest/echo/info
//	 */
//	@Test
//	public void test01_echo() {
//		LOGGER.debug("EchoClientSSLWildflyTest#test01_echo "
//				+ EchoClientSSLWildflyTest.class.getClassLoader().getResource("client.jks").getFile());
//
//		// Setup Keystore und Truststore.
//		System.setProperty(EchoClientSSLWildflyTest.KEY_STORE_PATH,
//				EchoClientSSLWildflyTest.class.getClassLoader().getResource("client.jks").getFile());
//		System.setProperty(EchoClientSSLWildflyTest.KEY_STORE_TYPE, "JKS");
//		System.setProperty(EchoClientSSLWildflyTest.KEY_STORE_PASSWORD, "tzdbmm");
//		System.setProperty(EchoClientSSLWildflyTest.TRUST_STORE_PATH,
//				EchoClientSSLWildflyTest.class.getClassLoader().getResource("client_truststore.jks").getFile());
//		System.setProperty(EchoClientSSLWildflyTest.TRUST_STORE_PASSWORD, "tzdbmm");
//		System.setProperty(EchoClientSSLWildflyTest.TRUST_STORE_TYPE, "JKS");
//
//		// Erstelle Client.
//		final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS)
//				.socketTimeout(2, TimeUnit.SECONDS).build();
//		final ResteasyWebTarget webTarget = client.target(EchoClientSSLWildflyTest.BASE_URI + JaxRsActivator.REST_PATH);
//		webTarget.register(ResteasyClientLogger.class);
//
//		final EchoRestResource proxy = webTarget.proxy(EchoRestResource.class);
//
//		final Response response = proxy.info("the cookie token!", "egov_userId");
//
//		if (response != null) {
//			final NewCookie newCookie = response.getCookies().get(EchoRestResource.TOKEN_PARAM);
//
//			LOGGER.info("EchoClientSSLWildflyTest#test01_echo [status=" + response.getStatus() + ", token="
//					+ newCookie.getValue() + ", header="
//					+ response.getHeaders().get(EchoRestResource.HEADER_PARAM).iterator().next().toString()
//					+ ", payload=" + response.readEntity(String.class) + "]");
//
//			assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
//		} else {
//			fail("Response ist null!");
//		}
//	}
}
