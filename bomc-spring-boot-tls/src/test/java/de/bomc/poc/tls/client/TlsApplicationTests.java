package de.bomc.poc.tls.client;

import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import de.bomc.poc.tls.TlsApplication;
import de.bomc.poc.tls.controller.RestEndpointController;

/**
 * Basic integration tests for demo application.
 * 
 * @author bomc
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TlsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TlsApplicationTests {

	private static final String LOG_PREFIX = TlsApplicationTests.class.getName() + "#";
	private static final Logger LOGGER = Logger.getLogger(TlsApplicationTests.class.getName());

	@LocalServerPort
	private int port;

	@Autowired
	private RestTemplate restTemplate;

	@Test
	public void test010_invokeTlsBasicUser_pass() {
		LOGGER.debug(LOG_PREFIX + "test010_invokeTlsBasicUser_pass");

		final String response = restTemplate
				.getForObject("https://localhost:" + port + RestEndpointController.BASIC_USER_TLS_PATH, String.class);

		LOGGER.debug(LOG_PREFIX + "test010_invokeTlsBasicUser_pass [response=" + response + "]");

		Assert.assertEquals(RestEndpointController.BASIC_USER_RESPONSE, response);
	}

	@Test(expected = HttpClientErrorException.class)
	public void test020_invokeTlsAdminUser_fail() {
		LOGGER.debug(LOG_PREFIX + "test020_invokeTlsAdminUser_fail");

		restTemplate.getForObject("https://localhost:" + port + RestEndpointController.ADMIN_USER_TLS_PATH,
				String.class);
	}

}
