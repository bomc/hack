package de.bomc.test;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.junit.Test;

import de.bomc.poc.rest.JaxRsActivator;
import de.bomc.poc.rest.client.ResteasyTrustAllWebTargetFactory;
import de.bomc.poc.rest.endpoints.v1.SslRESTResource;
import de.bomc.poc.rest.logger.client.ResteasyClientLogger;

public class VersionTest {

	private static final Logger LOGGER = Logger.getLogger(VersionTest.class);
	private static final String LOG_PREFIX = "VersionTest#";
	private static final String BASE_URI = "https://192.168.4.1:8543/ssl/" + JaxRsActivator.APPLICATION_PATH;
	// Keystore and truststore parameter.
	private static final String KEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword";
	private static final String KEY_STORE_PATH = "javax.net.ssl.keyStore";
	private static final String KEY_STORE_TYPE = "javax.net.ssl.keyStoreType";
	private static final String TRUST_STORE_PASSWORD = "javax.net.ssl.trustStorePassword";
	private static final String TRUST_STORE_PATH = "javax.net.ssl.trustStore";
	private static final String TRUST_STORE_TYPE = "javax.net.ssl.trustStoreType";
	
	@Test
	public void test010_trustAll() {
		LOGGER.debug(LOG_PREFIX + "test010_trustAll");
		Response response = null;

		final ResteasyWebTarget resteasyWebtarget = ResteasyTrustAllWebTargetFactory.buildWebTarget();
		
		final ResteasyClient client = resteasyWebtarget.getResteasyClient();
		client.register(new ResteasyClientLogger(LOGGER, true));

		final ResteasyWebTarget webTarget = client.target(BASE_URI);

		try {
			response = webTarget.proxy(SslRESTResource.class).getVersion();

			if (response.getStatus() == Response.Status.OK.getStatusCode()) {
				final String version = response.readEntity(String.class);
				LOGGER.debug(LOG_PREFIX + "test010_trustAll [version=" + version + "]");
			}
		} catch(Exception ex) {
			LOGGER.error(LOG_PREFIX + "test010_trustAll \n");
			
			ex.printStackTrace();
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}
	
	/**
	 * mvn clean install -Dtest=VersionTest#test020_oneWayTrustServerCertificate -Djavax.net.debug=SSL,handshake,data,trustmanager
	 *
	 * curl -v -i -H "Accept: application/vnd.version-v1+json" -H "Content-Type: application/vnd.version-v1+json" GET http://192.168.4.1:8180/ssl/rest/version/current-version
	 */
	@Test
	public void test020_oneWayTrustServerCertificate() {
		LOGGER.debug(LOG_PREFIX + "test020_trustServerCertificate");

		// Setup Truststore.
		System.setProperty(VersionTest.TRUST_STORE_PATH, VersionTest.class.getClassLoader().getResource("one-way-wildfly-client.truststore").getFile());
		System.setProperty(VersionTest.TRUST_STORE_PASSWORD, "bomc1234");
		System.setProperty(VersionTest.TRUST_STORE_TYPE, "JKS");

		// Erstelle Client.
		final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS)
				.socketTimeout(2, TimeUnit.SECONDS).build();
		final ResteasyWebTarget webTarget = client.target(BASE_URI);
		webTarget.register(ResteasyClientLogger.class);

		Response response = null;
		
		try {
			response = webTarget.proxy(SslRESTResource.class).getVersion();

			if (response.getStatus() == Response.Status.OK.getStatusCode()) {
				final String version = response.readEntity(String.class);
				LOGGER.debug(LOG_PREFIX + "test020_trustServerCertificate [version=" + version + "]");
			}
		} catch(Exception ex) {
			LOGGER.error(LOG_PREFIX + "test020_trustServerCertificate \n");
			
			ex.printStackTrace();
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}
	
	/**
	 * mvn clean install -Dtest=VersionTest#test030_twoWaytrustServerCertificate -Djavax.net.debug=SSL,handshake,data,trustmanager
	 *
	 * curl -v -i -H "Accept: application/xml" -H "Content-Type: application/xml" GET http://192.168.4.1:8180/egov/rest/echo/info
	 */
	@Test
	public void test030_twoWayTrustServerCertificate() {
		LOGGER.debug(LOG_PREFIX + "test030_twoWayTrustServerCertificate");

		// Setup Keystore und Truststore.
		System.setProperty(VersionTest.KEY_STORE_PATH, VersionTest.class.getClassLoader().getResource("two-way-wildfly-client.keystore").getFile());
		System.setProperty(VersionTest.KEY_STORE_TYPE, "JKS");
		System.setProperty(VersionTest.KEY_STORE_PASSWORD, "bomc1234");
		System.setProperty(VersionTest.TRUST_STORE_PATH, VersionTest.class.getClassLoader().getResource("one-way-wildfly-client.truststore").getFile());
		System.setProperty(VersionTest.TRUST_STORE_PASSWORD, "bomc1234");
		System.setProperty(VersionTest.TRUST_STORE_TYPE, "JKS");

		// Erstelle Client.
		final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS)
				.socketTimeout(2, TimeUnit.SECONDS).build();
		final ResteasyWebTarget webTarget = client.target(BASE_URI);
		webTarget.register(ResteasyClientLogger.class);

		Response response = null;
		
		try {
			response = webTarget.proxy(SslRESTResource.class).getVersion();

			if (response.getStatus() == Response.Status.OK.getStatusCode()) {
				final String version = response.readEntity(String.class);
				LOGGER.debug(LOG_PREFIX + "test030_twoWayTrustServerCertificate [version=" + version + "]");
			}
		} catch(Exception ex) {
			LOGGER.error(LOG_PREFIX + "test030_twoWayTrustServerCertificate \n");
			
			ex.printStackTrace();
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}
	
	/**
	 * mvn clean install -Dtest=VersionTest#test030_twoWaytrustServerCertificate -Djavax.net.debug=SSL,handshake,data,trustmanager
	 *
	 * curl -v -i -H "Accept: application/xml" -H "Content-Type: application/xml" GET http://192.168.4.1:8180/egov/rest/echo/info
	 */
	@Test
	public void test040_unsecureWay() {
		LOGGER.debug(LOG_PREFIX + "test040_unsecureWay");

		// Erstelle Client.
		final ResteasyClient client = new ResteasyClientBuilder()
                .establishConnectionTimeout(10, TimeUnit.SECONDS)
				.socketTimeout(10, TimeUnit.MINUTES).connectionPoolSize(3)
                .build();
		
		final ResteasyWebTarget webTarget = client.target("http://192.168.4.1:8180/ssl/" + JaxRsActivator.APPLICATION_PATH);
		webTarget.register(ResteasyClientLogger.class);

		Response response = null;
		
		try {
			response = webTarget.proxy(SslRESTResource.class).getVersion();

			if (response.getStatus() == Response.Status.OK.getStatusCode()) {
				final String version = response.readEntity(String.class);
				LOGGER.debug(LOG_PREFIX + "test030_twoWayTrustServerCertificate [version=" + version + "]");
			}
		} catch(Exception ex) {
			LOGGER.error(LOG_PREFIX + "test030_twoWayTrustServerCertificate \n");
			
			ex.printStackTrace();
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}
}
