/**
 * Project: bomc-upload
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: tzdbmm $
 *
 *  date: $Date: 2016-12-23 14:20:47 +0100 (Fr, 23 Dez 2016) $
 *
 *  revision: $Revision: 9598 $
 *
 * </pre>
 */
package de.bomc.poc.upload.test.arq;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Providers;

import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bomc.poc.rest.logger.client.ResteasyClientLogger;
import de.bomc.poc.upload.api.UploadResponseObject;
import de.bomc.poc.upload.rest.JaxRsActivator;
import de.bomc.poc.upload.rest.endpoint.v1.UploadEndpoint;
import de.bomc.poc.upload.rest.endpoint.v1.impl.UploadEndpointImpl;
import de.bomc.poc.upload.test.arq.mock.TransferMockEJB;
import de.bomc.poc.upload.transfer.TransferLocal;

/**
 * Tests the rest upload endpoint.
 * 
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=UploadApacheHttpClient4EngineTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 14.03.2016
 */
@RunWith(Arquillian.class)
public class UploadApacheHttpClient4EngineTestIT extends ArquillianBase {
	// _______________________________________________
	// Test parameters
	//
	// Constants
	private static final String LOG_PREFIX = "UploadApacheHttpClient4EngineTestIT#";
	private static final String WEB_ARCHIVE_NAME = "upload-service";
	private static final Logger LOGGER = Logger.getLogger(UploadApacheHttpClient4EngineTestIT.class);
	private static final String RESOURCE_PATH = "src/test/resources";
	private static final String CSV_FILE_NAME = "upload.csv";
	private static final String QUERY_PARAM_REQUEST_ID = UUID.randomUUID().toString();
	private static final int DEFAULT_FILE_UPLOAD_IN_MEMORY_THRESHOLD_LIMIT = 1024;
	// Members
	private String userDir;
	private ResteasyWebTarget webTarget;

	@Deployment
	public static Archive<?> createDeployment() {
		final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
		webArchive.addClass(UploadApacheHttpClient4EngineTestIT.class);
		webArchive.addClasses(UploadEndpoint.class, UploadEndpointImpl.class, UploadResponseObject.class);
		webArchive.addClasses(TransferLocal.class, TransferMockEJB.class);
		webArchive.addClasses(JaxRsActivator.class);
		webArchive.addClass(ResteasyClientLogger.class);
		webArchive.addAsWebInfResource(getBeansXml(), "beans.xml");

		// Add dependencies
		final MavenResolverSystem resolver = Maven.resolver();

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("commons-lang:commons-lang:jar:?")
				.withMavenCentralRepo(false).withTransitivity().asFile());

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("de.bomc.poc:logging-lib:jar:?")
				.withMavenCentralRepo(false).withTransitivity().asFile());

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("de.bomc.poc:exception-lib:jar:?")
				.withMavenCentralRepo(false).withTransitivity().asFile());

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("de.bomc.poc:bomc-upload-lib:jar:?")
				.withMavenCentralRepo(false).withTransitivity().asFile());

		webArchive
				.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("de.bomc.poc:bomc-upload-lagacy-api:jar:?")
						.withMavenCentralRepo(false).withTransitivity().asFile());

		webArchive.addAsLibraries(
				resolver.loadPomFromFile("pom.xml").resolve("org.jboss.resteasy:resteasy-jackson-provider:jar:?")
						.withMavenCentralRepo(false).withTransitivity().asFile());

		System.out.println(LOG_PREFIX + "createDeployment: " + webArchive.toString(true));

		return webArchive;
	}

	@ArquillianResource
	private URL baseUrl;

	/**
	 * Setup creates a rest client with path to the hoko-zid upload service.
	 */
	@Before
	public void setupClass() throws URISyntaxException {
		LOGGER.info(
				LOG_PREFIX + "setupClass [host=" + this.baseUrl.getHost() + ", port=" + this.baseUrl.getPort() + "]");

		this.userDir = System.getProperty("user.dir");

		final PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
		cm.setMaxTotal(10);

		final HttpClient httpClient = new DefaultHttpClient(cm);
		final HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 5000);
		HttpConnectionParams.setSoTimeout(params, 5000);
		HttpConnectionParams.setStaleCheckingEnabled(params, true);

		// Create rest client with Resteasy Client Framework.
		final ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
		// This line is only needed if you run this as a java console app or
		// outside the application server.
		ResteasyProviderFactory.pushContext(Providers.class, factory);
		// final ApacheHttpClient4Engine httpEngine = new
		// ApacheHttpClient4Engine(HttpClientBuilder.create().build(),
		// true);
		final ApacheHttpClient4Engine httpEngine = new ApacheHttpClient4Engine(httpClient, true);
		// Set memory threshold lower than the actual file size so that
		// streaming to temp file is used.
		httpEngine.setFileUploadInMemoryThresholdLimit(DEFAULT_FILE_UPLOAD_IN_MEMORY_THRESHOLD_LIMIT);
		httpEngine.setFileUploadMemoryUnit(ApacheHttpClient4Engine.MemoryUnit.KB);
		final ResteasyClient client = new ResteasyClientBuilder().providerFactory(factory).httpEngine(httpEngine)
				// .socketTimeout(3L, TimeUnit.SECONDS)
				.establishConnectionTimeout(3000L, TimeUnit.MILLISECONDS).connectionTTL(5000L, TimeUnit.MILLISECONDS)
				.build();

		final URIBuilder uriBuilder = new URIBuilder();
		final String uriStr = uriBuilder.setScheme(this.DEFAULT_SCHEME).setHost(this.baseUrl.getHost())
				.setPort(this.baseUrl.getPort()).setPath("/" + WEB_ARCHIVE_NAME + "/" + JaxRsActivator.APPLICATION_PATH
						+ "/" + UploadEndpoint.BOMC_UPLOAD_REST_RESOURCE_PATH + "/upload")
				.build().toString();

		LOGGER.debug(LOG_PREFIX + "setupClass [uriStr=" + uriStr + ", userDir=" + this.userDir + "]");

		this.webTarget = client.target(uriStr);
	}

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=UploadApacheHttpClient4EngineTestIT#test010_v1_doUploadSuccess_Pass
	 *
	 * <b><code>test010_v1_doUploadSuccess_Pass</code>:</b><br>
	 *  Test uploading file.
	 *  NOTE: The arquillian rest client is not deployed in the wildfly container (so CDI container is not available).
	 *
	 * <b>Preconditions:</b><br>
	 *  - Artifact must be successful deployed in Wildfly.
	 *  - {@link ResteasyClient} must be successful created.
	 *
	 * <b>Scenario:</b><br>
	 *  The following steps are executed:
	 *  - Rest client uploads 'upload.csv' to upload service.
	 *
	 * <b>Postconditions:</b><br>
	 *  The file is uploaded and the client gets a successful status 200 and a responseObject.
	 * </pre>
	 * 
	 * @throws URISyntaxException
	 *             is thrown during URI creation, is not expected.
	 */
	@Test
	@RunAsClient
	@InSequence(10)
	public void test010_v1_doUploadSuccess_Pass() throws Exception {
		LOGGER.debug(LOG_PREFIX + "test010_v1_doUploadSuccess_Pass [uri=" + this.webTarget.getUri() + "]");

		Response response = null;

		try {
			// Fill up the query parameter.
			final MultivaluedMap<String, Object> multivaluedQueryMap = new MultivaluedHashMap<>(2);
			multivaluedQueryMap.add(UploadEndpoint.QUERY_PARAM_REQUEST_ID,
					UploadApacheHttpClient4EngineTestIT.QUERY_PARAM_REQUEST_ID);

			// Read 'upload.csv' from filesystem.
			final byte[] content = this.readFile(CSV_FILE_NAME);

			// Build the multipart payload.
			final MultipartFormDataOutput multipartFormDataOutput = new MultipartFormDataOutput();
			multipartFormDataOutput.addFormData("attachment", content, MediaType.MULTIPART_FORM_DATA_TYPE,
					CSV_FILE_NAME);
			final GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(
					multipartFormDataOutput) {
			};

			// Upload the file to hoko-zid service.
			response = this.webTarget.queryParams(multivaluedQueryMap).request()
					.accept(UploadEndpoint.BOMC_UPLOAD_MEDIA_TYPE)
					.post(Entity.entity(entity, MediaType.MULTIPART_FORM_DATA_TYPE));

			// Get return and evaluate the response.
			if (response.getStatus() == Response.Status.OK.getStatusCode()) {
				final String strResponse = response.readEntity(String.class);
				//
				// Do asserts, get the comparison data from api-spec.
				LOGGER.info(LOG_PREFIX + "test010_v1_doUploadSuccess_Pass [response=" + strResponse + "]");
			} else {
				fail(LOG_PREFIX
						+ "test010_v1_doUploadSuccess_Pass - should not happen, the testcase should finished with status 200.");
			}
		} catch (final IOException ioEx) {
			LOGGER.error(LOG_PREFIX + "test010_v1_doUploadSuccess_Pass - failed" + ioEx);
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	private byte[] readFile(final String fileName) throws IOException {
		LOGGER.debug(LOG_PREFIX + "readFile [fileName=" + fileName + "]");

		final Path path = Paths.get(this.userDir, RESOURCE_PATH, fileName);

		return Files.readAllBytes(path);
	}
}
