package de.bomc.poc.tls.client;

import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import de.bomc.poc.tls.TlsApplication;
import de.bomc.poc.tls.controller.RestEndpointController;

/**
 * Basic integration tests for demo application.
 * 
 * @author bomc
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
		classes = TlsApplication.class,
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class TlsApplicationTests {

	private static final String LOG_PREFIX = TlsApplicationTests.class.getName() + "#";
	private static final Logger LOGGER = Logger.getLogger(TlsApplicationTests.class.getName());
	
	@LocalServerPort
	private int port;

	@Autowired
    private RestTemplate restTemplate;

	@Test
	public void test010_invokeTls() {
		LOGGER.debug(LOG_PREFIX + "test010_invokeTls");
		
		final String response = restTemplate.getForObject("https://localhost:" + port + "/api/tls", String.class);
	    
		LOGGER.debug(LOG_PREFIX + "test010_invokeTls [response=" + response + "]");
		
	    Assert.assertEquals(RestEndpointController.RESPONSE, response);
	}

}
