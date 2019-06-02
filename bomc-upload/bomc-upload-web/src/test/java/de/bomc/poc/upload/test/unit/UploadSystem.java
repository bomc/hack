package de.bomc.poc.upload.test.unit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.util.UUID;
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
import org.junit.Test;

import de.bomc.poc.logging.filter.UIDHeaderRequestFilter;
import de.bomc.poc.rest.logger.client.ResteasyClientLogger;
import de.bomc.poc.upload.rest.JaxRsActivator;
import de.bomc.poc.upload.rest.endpoint.v1.UploadEndpoint;

public class UploadSystem {
	
	private static final String LOG_PREFIX = "UploadSystem#";
	private static final Logger LOGGER = Logger.getLogger(UploadSystem.class);
	private static final int DEFAULT_CONNECTION_TTL = 5000;
	private static final int DEFAUL_CONNECT_TIMEOUT = 3000;
	private static final int DEFAULT_SO_TIMEOUT = 20000;
	// Context-root, see jboss-web.xml for configuration.
	private static final String BASE_URI = "/bomc-upload" + "/" + JaxRsActivator.APPLICATION_PATH + "/"
			+ UploadEndpoint.BOMC_UPLOAD_REST_RESOURCE_PATH + "/upload";
	private static final String SCHEME = "http";
	private static final String HOST = "192.168.99.100";
	private static final int PORT = 8080;
	private static final String URI = SCHEME + "://" + HOST + ":" + PORT + BASE_URI;
	private static final String CSV_FILE_NAME = "upload.csv";
	private static final String QUERY_PARAM_REQUEST_ID = "28b81fea-03b4-4bf2-8a5f-ae3077cb6122";
	private static final String BOUNDARY = "5b385fba-f60a-4ea9-9863-ad56ba933bee";
	
	@Test
	public void test01_uploadFile_Pass() {
		LOGGER.debug(LOG_PREFIX + "test01_uploadFile_Pass");

		Response response = null;

		try {
			final ResteasyClientBuilder resteasyClientBuilder = new ResteasyClientBuilder()
					.connectionTTL(DEFAULT_CONNECTION_TTL, TimeUnit.MILLISECONDS)
					.establishConnectionTimeout(DEFAUL_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
					.socketTimeout(DEFAULT_SO_TIMEOUT, TimeUnit.MILLISECONDS)
					.register(new ResteasyClientLogger(LOGGER, true))
					.register(new UIDHeaderRequestFilter(UUID.randomUUID().toString()));
			final ResteasyClient resteasyClient = resteasyClientBuilder.build();

			final ResteasyWebTarget webTarget = resteasyClient.target(URI);

			LOGGER.debug(LOG_PREFIX + "test01_uploadFile_Pass - " + webTarget.getUri().toASCIIString());

			final MultivaluedMap<String, Object> multivaluedQueryMap = new MultivaluedHashMap<>(2);
			multivaluedQueryMap.add(UploadEndpoint.QUERY_PARAM_REQUEST_ID, UploadSystem.QUERY_PARAM_REQUEST_ID);

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
	
	private byte[] getBodyAsBytes() {

		final String body = "Season,Episode,Character,Line\n"
				+ "10,1,Stan,\"You guys, you guys! Chef is going away.\n";

		return body.getBytes();

	}
}
