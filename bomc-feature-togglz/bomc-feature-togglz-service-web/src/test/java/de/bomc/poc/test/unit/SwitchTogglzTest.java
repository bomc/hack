package de.bomc.poc.test.unit;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.Test;

import de.bomc.poc.rest.JaxRsActivator;
import de.bomc.poc.rest.endpoints.v1.TogglzRESTResource;
import de.bomc.poc.rest.logger.client.ResteasyClientLogger;

public class SwitchTogglzTest {
	private static final String LOG_PREFIX = "SwitchTogglzTest#";
	private static final Logger LOGGER = Logger.getLogger(SwitchTogglzTest.class);

	@Test
	public void run() {
		Response response = null;

		final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS)
				.socketTimeout(10, TimeUnit.MINUTES).connectionPoolSize(3).build();
		client.register(new ResteasyClientLogger(LOGGER, true));

		// http://192.168.4.1:8180/bomc-tooglz-war/rest/togglz/current-version
		// docker-host: http://192.168.99.100
		ResteasyWebTarget webTarget = client
				.target("http://127.0.0.1:8180/bomc-tooglz-war/" + JaxRsActivator.APPLICATION_PATH);

		for (int i = 0; i < 25; i++) {
			try {
				response = webTarget.proxy(TogglzRESTResource.class).getVersion();

				if (response.getStatus() == Response.Status.OK.getStatusCode()) {
					final String version = response.readEntity(String.class);
					System.out.println(LOG_PREFIX + "test010_v1_readVersion_Pass [version=" + version + "]");
				}

				TimeUnit.MILLISECONDS.toMillis(100);
			} finally {
				if (response != null) {
					response.close();
				}
			}
		}
	}
}
