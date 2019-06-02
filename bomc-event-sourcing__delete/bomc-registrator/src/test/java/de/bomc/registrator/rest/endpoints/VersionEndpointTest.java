package de.bomc.registrator.rest.endpoints;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.bomc.registrator.config.ConfigurationPropertiesConstants;

/**
 * Tests the {@link VersionEndpoint}.
 * 
 * mvn clean install -Dtest=VersionEndpointTest
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 25.07.2018
 */
@SuppressWarnings("deprecation")
@RunWith(MockitoJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VersionEndpointTest {

	// _______________________________________________
	// Member constants.
	// -----------------------------------------------
	private static final String LOG_PREFIX = "VersionEndpointTest#";
	private static final Logger LOGGER = Logger.getLogger(VersionEndpointTest.class);
	private static int port;
	private static TJWSEmbeddedJaxrsServer server;
	// _______________________________________________
	// Simple test data.
	// -----------------------------------------------
	private static final String RESPONSE_VERSION = "RETURN_VERION";
	// _______________________________________________
	// Member variables.
	// -----------------------------------------------
	@Mock
	private Logger logger;
	@Mock
	private Properties versionProperties;
	@InjectMocks
	private static VersionEndpoint cut = new VersionEndpoint();

	@BeforeClass
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void beforeClass() throws Exception {
		port = PortFinder.findPort();

		server = new TJWSEmbeddedJaxrsServer();
		server.setPort(port);
		server.getDeployment().setResources((List) Collections.singletonList(cut));

		// NOTE: All providers introduced by the Application.class will be
		// invoked and has to be mocked.
		// In this case the AuthorizationTokenFilter has to be mocked and the
		// injected Logger inside the AuthorizationTokenFilter has to be mocked
		// too.
		// Problem, the logger inside the AuthorizationTokenFilter is always
		// null, how to mock this?
		// server.getDeployment().setApplicationClass(de.bomc.poc.rest.management.application.ManagementRESTApplication.class.getCanonicalName());

		// server.getDeployment().getActualProviderClasses().addAll((List<Class<?>>)
		// Arrays
		// .asList(DataTransferMessageBodyReader.class,
		// DataTransferMessageBodyWriter.class));

		server.start();
	}

	@AfterClass
	public static void stop() throws Exception {
		LOGGER.debug(LOG_PREFIX + "stop");

		if (server != null) {
			server.stop();
			server = null;
		}
	}

	/**
	 * <pre>
	 * 	mvn clean install -Dtest=VersionEndpointTest#test010_readVersion_pass
	 *
	 * <b><code>test010_readVersion_pass</code>:</b><br>
	 *     - Test the successful invocation for reading the version.
	 *
	 * <b>Preconditions:</b><br>
	 *     - versionProperties.getProperty must be mocked.
	 *
	 * <b>Scenario:</b><br>
	 *     The following steps are executed:
	 *     - The cut reads the version by the mocked versionProperties.getProperty method.
	 *
	 * <b>Postconditions:</b><br>
	 *     - Cut returns the version
	 * </pre>
	 */
	@Test
	public void test010_readVersion_pass() throws Exception {
		LOGGER.debug(LOG_PREFIX + "test010_readVersion_pass");

		// ___________________________________________
		// Given
		// -------------------------------------------

		// ___________________________________________
		// When
		// -------------------------------------------
		when(this.versionProperties.getProperty(ConfigurationPropertiesConstants.VERSION_VERSION_PROPERTY_KEY))
				.thenReturn(RESPONSE_VERSION);

		// ___________________________________________
		// Run test.
		// -------------------------------------------
		final Response response = cut.getVersion();

		// ___________________________________________
		// Do asserts
		// -------------------------------------------
		assertThat(response, notNullValue());

		if (response.getStatus() == Status.OK.getStatusCode()) {
			LOGGER.debug(LOG_PREFIX + "test010_readVersion_pass");

			final JsonObject jsonObject = (JsonObject) response.getEntity();

			LOGGER.debug(LOG_PREFIX + "test010_readVersion_pass - [version="
					+ jsonObject.get(ConfigurationPropertiesConstants.VERSION_VERSION_PROPERTY_KEY) + "]");
		}
	}

	// _______________________________________________
	// Inner classes
	// -----------------------------------------------

}
