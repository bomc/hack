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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.rest.logger.client.ResteasyClientLogger;
import de.bomc.poc.upload.api.UploadResponseObject;
import de.bomc.poc.upload.configuration.ConfigKeys;
import de.bomc.poc.upload.configuration.ConfigSingletonEJB;
import de.bomc.poc.upload.configuration.producer.ConfigProducer;
import de.bomc.poc.upload.configuration.qualifier.ConfigQualifier;
import de.bomc.poc.upload.rest.JaxRsActivator;
import de.bomc.poc.upload.rest.api.v1.LagacyUploadResource;
import de.bomc.poc.upload.rest.api.v1.LagacyUploadServiceResponseCodesEnum;
import de.bomc.poc.upload.rest.client.RestClientBuilder;
import de.bomc.poc.upload.rest.endpoint.v1.UploadEndpoint;
import de.bomc.poc.upload.rest.endpoint.v1.impl.UploadEndpointImpl;
import de.bomc.poc.upload.test.arq.mock.UploadEndpointMockImpl;
import de.bomc.poc.upload.transfer.TransferLocal;
import de.bomc.poc.upload.transfer.impl.TransferEJB;

/**
 * Tests the upload, whereby the lagacy-upload-service is deployed in a second
 * artifact.
 * 
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=UploadUnmockedTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 14.03.2016
 */
@RunWith(Arquillian.class)
public class UploadUnmockedTestIT extends ArquillianBase {

	// _______________________________________________
	// Test parameters
	//
	// Constants
	private static final String LOG_PREFIX = "UploadUnmockedTestIT#";
	private static final String WEB_ARCHIVE_NAME_SERVICE = "upload-unmocked-service";
	private static final String WEB_ARCHIVE_NAME_LAGACY = "bomc-upload-lagacy";
	private static final String CSV_FILE_NAME = "upload.csv";
	private static final String CONFIGURATION_FILE_NAME = "configuration.properties";
	private static final String QUERY_PARAM_REQUEST_ID = UUID.randomUUID().toString();
	private static final Logger LOGGER = Logger.getLogger(UploadUnmockedTestIT.class);

	/**
	 * Creates the deployment artifact. The tests run full inside the container.
	 * The 'configuration.properties' file is part of the deployment artifact.
	 * 
	 * @return a created artifact for deployment.
	 */
	@Deployment(testable = true, name = "service", order = 1)
	public static Archive<?> createDeployment() {
		final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME_SERVICE);
		webArchive.addClass(UploadUnmockedTestIT.class);
		webArchive.addClasses(ConfigKeys.class, ConfigSingletonEJB.class, ConfigProducer.class, ConfigQualifier.class);
		webArchive.addClasses(TransferEJB.class, TransferLocal.class, UploadResponseObject.class);
		webArchive.addClasses(RestClientBuilder.class);
		webArchive.addClasses(JaxRsActivator.class, UploadEndpointImpl.class, UploadEndpoint.class);
		webArchive.addAsWebInfResource(getBeansXml(), "beans.xml");
		webArchive.addAsWebInfResource("jboss-deployment-structure.xml", "jboss-deployment-structure.xml");
		webArchive.addAsResource(CONFIGURATION_FILE_NAME);
		webArchive.addAsResource(CSV_FILE_NAME);

		// Add dependencies
		final MavenResolverSystem resolver = Maven.resolver();

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("commons-lang:commons-lang:jar:?")
				.withMavenCentralRepo(false).withTransitivity().asFile());

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("de.bomc.poc:logging-lib:jar:?")
				.withMavenCentralRepo(false).withTransitivity().asFile());

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("de.bomc.poc:exception-lib:jar:?")
				.withMavenCentralRepo(false).withTransitivity().asFile());

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("de.bomc.poc:rest-lib:jar:?")
				.withMavenCentralRepo(false).withTransitivity().asFile());

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("de.bomc.poc:bomc-upload-lib:jar:?")
				.withMavenCentralRepo(false).withTransitivity().asFile());

		webArchive
				.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("de.bomc.poc:bomc-upload-lagacy-api:jar:?")
						.withMavenCentralRepo(false).withTransitivity().asFile());

		// Necessary for client here in test-file.
		webArchive.addAsLibraries(
				resolver.loadPomFromFile("pom.xml").resolve("org.jboss.resteasy:resteasy-jackson-provider:jar:?")
						.withMavenCentralRepo(false).withTransitivity().asFile());

		System.out.println(LOG_PREFIX + "createDeployment: " + webArchive.toString(true));

		return webArchive;
	}

	@Deployment(testable = true, name = "lagacy", order = 2)
	public static Archive<?> createServiceDeployment() {
		final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME_LAGACY);
		webArchive.addClasses(UploadEndpointMockImpl.class, JaxRsActivator.class);
		webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
		webArchive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
		webArchive.addAsWebInfResource("jboss-deployment-structure.xml", "jboss-deployment-structure.xml");

		// Add dependencies
		final MavenResolverSystem resolver = Maven.resolver();

		webArchive
				.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("de.bomc.poc:bomc-upload-lagacy-api:jar:?")
						.withMavenCentralRepo(false).withTransitivity().asFile());

		System.out.println(LOG_PREFIX + "createLagacyDeployment: " + webArchive.toString(true));

		return webArchive;
	}

	@EJB
	private TransferLocal transferEJB;

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=UploadUnmockedTestIT#test010_v1_upload_Pass
	 *
	 * <b><code>test010_v1_upload_Pass</code>:</b><br>
	 *	Uploads the file to the upload service.
	 *   
	 * <b>Preconditions:</b><br>
	 *  - Artifact must be successful deployed in Wildfly.
	 *  
	 *
	 * <b>Scenario:</b><br>
	 *  The following steps are executed:
	 *  - The restClient uploads the file to the upload-service.
	 *  - The upload-service uploads the file to the lagacy-upload-service.
	 *  - The lagacy-upload-service send a response with status '10600' for a successful upload.   
	 * 
	 * <b>Postconditions:</b><br>
	 * - restClient receives responseObject that conatins the applicationstatus '10600'.
	 * </pre>
	 */
	@Test
	@InSequence(10)
	public void test010_v1_upload_Pass() {
		LOGGER.debug(LOG_PREFIX + "test010_v1_upload_Pass");

		Response response = null;

		try {
			// Fill up the query parameter.
			final MultivaluedMap<String, Object> multivaluedQueryMap = new MultivaluedHashMap<>(2);
			multivaluedQueryMap.add(LagacyUploadResource.QUERY_PARAM_REQUEST_ID,
					UploadUnmockedTestIT.QUERY_PARAM_REQUEST_ID);

			// Read 'upload.csv' from archive.
			final InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream(CSV_FILE_NAME);
			final byte[] content = IOUtils.toByteArray(fileInputStream);

			// Build the multipart payload.
			final MultipartFormDataOutput multipartFormDataOutput = new MultipartFormDataOutput();
			multipartFormDataOutput.addFormData("attachment", content, MediaType.MULTIPART_FORM_DATA_TYPE,
					CSV_FILE_NAME);
			final GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(
					multipartFormDataOutput) {
			};

			// Upload the file to upload-lagacy service.
			final ResteasyClientBuilder resteasyClientBuilder = new ResteasyClientBuilder()
					.register(new ResteasyClientLogger(LOGGER, true));
			final ResteasyClient resteasyClient = resteasyClientBuilder.build();
			final String url = this.buildUri(WEB_ARCHIVE_NAME_SERVICE) + "/" + JaxRsActivator.APPLICATION_PATH + "/"
					+ UploadEndpoint.BOMC_UPLOAD_REST_RESOURCE_PATH + "/upload";

			LOGGER.debug(LOG_PREFIX + "test010_v1_upload_Pass [lagacy-url=" + url + "]");

			final ResteasyWebTarget resteasyWebTarget = resteasyClient.target(url);

			response = resteasyWebTarget.queryParams(multivaluedQueryMap).request()
					.accept(UploadEndpoint.BOMC_UPLOAD_MEDIA_TYPE)
					.post(Entity.entity(entity, MediaType.MULTIPART_FORM_DATA_TYPE));

			// Get return and evaluate the response.
			if (response.getStatus() == Response.Status.OK.getStatusCode()) {
				final UploadResponseObject uploadResponseObject = response.readEntity(UploadResponseObject.class);
				//
				// Do asserts, get the comparison data from api-spec.
				LOGGER.info(LOG_PREFIX + "test010_v1_upload_Pass [response=" + uploadResponseObject + "]");

				assertThat(uploadResponseObject.getRequestId(), equalTo(UploadUnmockedTestIT.QUERY_PARAM_REQUEST_ID));
				assertThat(Long.parseLong(uploadResponseObject.getStatusCode()),
						equalTo(LagacyUploadServiceResponseCodesEnum.UPLOAD_SUCCESS.getCode()));
			} else {
				fail(LOG_PREFIX
						+ "test010_v1_upload_Pass - should not happen, the testcase should finished with status 200.");
			}
		} catch (final IOException | URISyntaxException ex) {
			LOGGER.error(LOG_PREFIX + "test010_v1_upload_Pass - failed! " + ex);
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}
}
