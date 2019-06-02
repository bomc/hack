/**
 * Project: Poc-upload
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: 2017-01-18 08:28:21 +0100 (Mi, 18 Jan 2017) $
 *
 *  revision: $Revision: 9696 $
 *
 * </pre>
 */
package de.bomc.poc.upload.test.pact;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRule;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactFragment;
import de.bomc.poc.rest.logger.client.ResteasyClientLogger;
import de.bomc.poc.upload.rest.JaxRsActivator;
import de.bomc.poc.upload.rest.endpoint.v1.UploadEndpoint;

/**
 * Creates the consumer contract for {@link UploadEndpoint}}
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 14.03.2016
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UploadPactTest {

	private static final String LOG_PREFIX = "UploadPactTest#";
	private static final Logger LOGGER = Logger.getLogger(UploadPactTest.class);
	private static final int DEFAULT_CONNECTION_TTL = 5000;
	private static final int DEFAUL_CONNECT_TIMEOUT = 3000;
	private static final int DEFAULT_SO_TIMEOUT = 20000;
	// Context-root, see jboss-web.xml for configuration.
	private static final String BASE_URI = "/bomc-upload" + "/" + JaxRsActivator.APPLICATION_PATH + "/"
			+ UploadEndpoint.BOMC_UPLOAD_REST_RESOURCE_PATH + "/upload";
	private static final String SCHEME = "http";
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 8080;
	private static final String URI = SCHEME + "://" + HOST + ":" + PORT + BASE_URI;
	private static final String FILE_UPLOAD_V1_PROVIDER = "UploadEndpointProvider_V1";
	private static final String CSV_FILE_NAME = "upload.csv";
	private static final String QUERY_PARAM_REQUEST_ID = "28b81fea-03b4-4bf2-8a5f-ae3077cb6122";
	private static final String BOUNDARY = "5b385fba-f60a-4ea9-9863-ad56ba933bee";
	@Rule
	public PactProviderRule rule_FileUploadV1Provider = new PactProviderRule(FILE_UPLOAD_V1_PROVIDER, HOST, PORT, this);

	/**
	 * Creates the interaction that the test is using for request and response.
	 * 
	 * @param builder
	 *            the given dsl builder.
	 * @return a PactFragment with defined interaction configuration.
	 */
	@Pact(provider = FILE_UPLOAD_V1_PROVIDER, consumer = "UploadEndpoint")
	public PactFragment createFileUploadV1Fragment(final PactDslWithProvider builder) {

		final String queryParamRequestId = UploadEndpoint.QUERY_PARAM_REQUEST_ID + "=" + UploadPactTest.QUERY_PARAM_REQUEST_ID;

		final DslPart responseBody = new PactDslJsonBody();
		responseBody.asBody().stringValue("statusCode", "10100").stringValue("statusText", "UPLOAD_SUCCESS")
				.stringValue("requestId", "28b81fea-03b4-4bf2-8a5f-ae3077cb6122").numberType("duration");
		
		return builder.given("Describe the state the provider needs to be in for the pact test to be verified")
				.uponReceiving("A upload request with requestId as query parameter.")
				.path(BASE_URI)
				.method("POST")
				.headers(this.requestHeaders())
				.query(queryParamRequestId)
				.body("--" + BOUNDARY + "\n"
						+ "Content-Disposition: form-data; name=\"attachment\"; filename=\"upload.csv\"\n"
						+ "Content-Type: multipart/form-data\n" 
						+ "\n" 
						+ "Season,Episode,Character,Line\n"
						+ "10,1,Stan,\"You guys, you guys! Chef is going away.\n" + "\n" 
						+ "--" + BOUNDARY 
						// Special handling for header definition.
						+ "--", "multipart/form-data; boundary=" + BOUNDARY)
				.willRespondWith()
				.headers(this.responseHeaders())
				.status(Response.Status.OK.getStatusCode())
				.body(responseBody)
				.toFragment();
	}

	/**
	 * mvn clean install -Dtest=UploadPactTest#test01_uploadFile_Pass
	 */
	@org.junit.Ignore
	@Test
	@PactVerification(FILE_UPLOAD_V1_PROVIDER)
	public void test01_uploadFile_Pass() {
		LOGGER.debug(LOG_PREFIX + "test01_uploadFile_Pass");

		Response response = null;

		try {
			final ResteasyClientBuilder resteasyClientBuilder = new ResteasyClientBuilder()
					.connectionTTL(DEFAULT_CONNECTION_TTL, TimeUnit.MILLISECONDS)
					.establishConnectionTimeout(DEFAUL_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
					.socketTimeout(DEFAULT_SO_TIMEOUT, TimeUnit.MILLISECONDS);
			final ResteasyClient resteasyClient = resteasyClientBuilder.build();
			resteasyClient.register(new ResteasyClientLogger(LOGGER, true));

			final ResteasyWebTarget webTarget = resteasyClient.target(URI);

			LOGGER.debug(LOG_PREFIX + "test01_uploadFile_Pass - " + webTarget.getUri().toASCIIString());

			final MultivaluedMap<String, Object> multivaluedQueryMap = new MultivaluedHashMap<>(2);
			multivaluedQueryMap.add(UploadEndpoint.QUERY_PARAM_REQUEST_ID, UploadPactTest.QUERY_PARAM_REQUEST_ID);

			final MultipartFormDataOutput multipartFormDataOutput = new MultipartFormDataOutput();
			final byte[] content = this.getBodyAsBytes();
			multipartFormDataOutput.addFormData("attachment", content, MediaType.MULTIPART_FORM_DATA_TYPE,
					CSV_FILE_NAME);
			multipartFormDataOutput.setBoundary(BOUNDARY);
			final GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(
					multipartFormDataOutput) {
			};

			response = webTarget.queryParams(multivaluedQueryMap).request()
					.accept(UploadEndpoint.BOMC_UPLOAD_MEDIA_TYPE)
					.post(Entity.entity(entity, MediaType.MULTIPART_FORM_DATA));

			if (response.getStatus() == Response.Status.OK.getStatusCode()) {
				final String responseObject = response.readEntity(String.class);

				LOGGER.info(LOG_PREFIX + "test01_uploadFile_Pass [response=" + responseObject + "]");
			} else {
				fail(LOG_PREFIX
						+ "test01_uploadFile_Pass - should not happen, the testcase should finished with status 200.");
			}
		} finally {
			if (response != null) {
				response.close();
			}
		}

		assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
	}

	private Map<String, String> requestHeaders() {
		final Map<String, String> requestHeaders = new HashMap<>();
		//
		// The request header is not set here. Because the necessary header
		// informations are set by the resteasy proxy framework.
		//
		requestHeaders.put("Accept", UploadEndpoint.BOMC_UPLOAD_MEDIA_TYPE);
		requestHeaders.put("Accept-Encoding", "gzip, deflate");
		
		return Collections.unmodifiableMap(requestHeaders);
	}

	private Map<String, String> responseHeaders() {
		final Map<String, String> requestHeaders = new HashMap<>();
		//
		// The request header is not set here. Because the necessary header
		// informations are set by the resteasy proxy framework.
		//
		requestHeaders.put("Content-Type", UploadEndpoint.BOMC_UPLOAD_MEDIA_TYPE);
		
		return Collections.unmodifiableMap(requestHeaders);
	}

	private byte[] getBodyAsBytes() {

		final String body = "Season,Episode,Character,Line\n"
				+ "10,1,Stan,\"You guys, you guys! Chef is going away.\n";

		return body.getBytes();

	}
}