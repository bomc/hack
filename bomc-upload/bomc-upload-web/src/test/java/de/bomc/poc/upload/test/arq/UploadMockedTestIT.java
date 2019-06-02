/**
 * Project: Poc-upload
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
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

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
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
 * Tests the rest upload endpoint, whereby the {@link UoloadEJB} is mocked.
 * 
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=UploadMockedTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 14.03.2016
 */
@RunWith(Arquillian.class)
public class UploadMockedTestIT extends ArquillianBase {
	// _______________________________________________
	// Test parameters
	//
	// Constants
	private static final String LOG_PREFIX = "UploadMockedTestIT#";
	private static final String WEB_ARCHIVE_NAME = "upload-service";
	private static final String CSV_FILE_NAME = "upload.csv";
	private static final String RESOURCE_PATH = "src/test/resources";
	private static final Logger LOGGER = Logger.getLogger(UploadMockedTestIT.class);
	private static final String QUERY_PARAM_REQUEST_ID = UUID.randomUUID().toString();
	private ResteasyWebTarget webTarget;
	private String userDir;

	/**
	 * Create deployment artifact. NOTE: 'testable=false' means restClient is
	 * running outside the container.
	 * 
	 * @return the created artifact.
	 */
	@Deployment(testable = false, name = "service")
	public static Archive<?> createDeployment() {
		final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
		webArchive.addClass(UploadMockedTestIT.class);
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

	/**
	 * Setup creates a rest client with path to the upload service.
	 */
	@Before
	public void setupClass() throws URISyntaxException {

		this.userDir = System.getProperty("user.dir");

		// Create rest client with Resteasy Client Framework.
		final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS)
				.socketTimeout(10, TimeUnit.SECONDS).register(new ResteasyClientLogger(LOGGER, true)).build();

		final String uri = buildUri(WEB_ARCHIVE_NAME).toString().concat("/").concat(JaxRsActivator.APPLICATION_PATH)
				.concat("/").concat(UploadEndpoint.BOMC_UPLOAD_REST_RESOURCE_PATH).concat("/upload");

		LOGGER.info(LOG_PREFIX + "setupClass [uri=" + uri + ", user.dir=" + this.userDir + "]");

		webTarget = client.target(uri);
	}

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=UploadMockedTestIT#test010_v1_doUpload_Pass
	 *
	 * <b><code>test010_v1_doUpload_Pass</code>:</b><br>
	 *  NOTE: The arquillian rest client is not deployed in the wildfly container (so CDI container is not available).
	 *
	 * <b>Preconditions:</b><br>
	 *  - Artifact must be successful deployed in Wildfly.
	 *  - {@link ResteasyClient} must be successful created.
	 *
	 * <b>Scenario:</b><br>
	 *  The following steps are executed:
	 *
	 * <b>Postconditions:</b><br>
	 *  The file is uploaded and the client gets a successful status 200 and a responseObject.
	 * </pre>
	 * 
	 * @throws URISyntaxException
	 *             is thrown during URI creation, is not expected.
	 */
	@Test
	@InSequence(10)
	public void test010_v1_doUpload_Pass() throws Exception {
		LOGGER.debug(LOG_PREFIX + "test010_v1_doUpload_Pass [uri=" + this.webTarget.getUri() + "]");

		Response response = null;

		try {
			// Fill up the query parameter.
			final MultivaluedMap<String, Object> multivaluedQueryMap = new MultivaluedHashMap<>(2);
			multivaluedQueryMap.add(UploadEndpoint.QUERY_PARAM_REQUEST_ID, UploadMockedTestIT.QUERY_PARAM_REQUEST_ID);

			// Read 'upload.csv' from filesystem.
			final byte[] content = this.readFile(CSV_FILE_NAME);

			// Build the multipart payload.
			final MultipartFormDataOutput multipartFormDataOutput = new MultipartFormDataOutput();
			multipartFormDataOutput.addFormData("attachment", content, MediaType.MULTIPART_FORM_DATA_TYPE,
					CSV_FILE_NAME);
			final GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(
					multipartFormDataOutput) {
			};

			response = this.webTarget.queryParams(multivaluedQueryMap).request().accept(UploadEndpoint.BOMC_UPLOAD_MEDIA_TYPE)
					.post(Entity.entity(entity, MediaType.MULTIPART_FORM_DATA_TYPE));

			// Get return and evaluate the response.
			if (response.getStatus() == Response.Status.OK.getStatusCode()) {
				final String strResponse = response.readEntity(String.class);
				//
				// Do asserts, get the comparison data from api-spec.
				LOGGER.info(LOG_PREFIX + "test010_v1_doUpload_Pass [response=" + strResponse + "]");
			} else {
				fail(LOG_PREFIX
						+ "test010_v1_doUpload_Pass - should not happen, the testcase should finished with status 200.");
			}
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	/**
	 * Read the upload file from filesystem.
	 * 
	 * @param fileName
	 *            the given filename.
	 * @return the file content in bytes.
	 * @throws IOException
	 */
	private byte[] readFile(final String fileName) throws IOException {
		LOGGER.debug(LOG_PREFIX + "readFile [fileName=" + fileName + "]");

		final Path path = Paths.get("D:\\trunk\\projects\\bomc-upload\\bomc-upload-web", RESOURCE_PATH, fileName);

		return Files.readAllBytes(path);
	}
}
