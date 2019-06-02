package de.bomc.poc.exception.test.arq;

import de.bomc.poc.exception.RootRuntimeException;
import de.bomc.poc.exception.cdi.interceptor.ExceptionHandlerInterceptor;
import de.bomc.poc.exception.cdi.qualifier.ExceptionHandlerQualifier;
import de.bomc.poc.exception.core.ErrorCode;
import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.exception.core.handler.ExceptionHandler;
import de.bomc.poc.exception.core.web.WebRuntimeException;
import de.bomc.poc.exception.test.TestApiError;
import de.bomc.poc.exception.test.TestErrorCode;
import de.bomc.poc.exception.test.arq.mock.application.JaxRsActivator;
import de.bomc.poc.exception.test.arq.mock.application.MockExceptionHandler;
import de.bomc.poc.exception.test.arq.mock.application.MockResponseObject;
import de.bomc.poc.exception.test.arq.mock.application.MockWebAppExResource;
import de.bomc.poc.exception.test.arq.mock.application.MockWebAppExResourceInterface;
import de.bomc.poc.exception.test.arq.mock.application.MockWebAppplicationException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.exception.Nestable;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Kommentar.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 */
@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WebApplicationExceptionTestIT {

	private static final String LOG_PREFIX = "WebApplicationExceptionTestIT#";
	private static final String WEB_ARCHIVE_NAME = "web-app-runtime-exception-test";
	private static final Logger LOGGER = Logger.getLogger(WebApplicationExceptionTestIT.class);
	/**
	 * The resource target identified by the resource URI.
	 */
	private static ResteasyWebTarget webTarget;

	// 'testable = true', means all the tests are running inside the container.
	@Deployment(testable = true)
	public static WebArchive createDeployment() {
		final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, WEB_ARCHIVE_NAME + ".war");
		webArchive.addClass(WebApplicationExceptionTestIT.class);
		webArchive.addClasses(ErrorCode.class, RootRuntimeException.class);
		webArchive.addClasses(TestApiError.class, TestErrorCode.class);
		webArchive.addPackage(AppRuntimeException.class.getPackage().getName());
		webArchive.addPackage(WebRuntimeException.class.getPackage().getName());
		webArchive.addClasses(ExceptionHandlerInterceptor.class, ExceptionHandlerQualifier.class);
		webArchive.addClasses(JaxRsActivator.class, MockWebAppplicationException.class, MockWebAppExResource.class,
				MockWebAppExResourceInterface.class, MockResponseObject.class, MockExceptionHandler.class);
		webArchive.addClasses(ExceptionUtils.class, NullArgumentException.class, Nestable.class, ArrayUtils.class,
				SystemUtils.class, StringUtils.class);
		webArchive.addClasses(ExceptionHandler.class);
		webArchive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

		System.out.println("archiveContent: " + webArchive.toString(true));

		return webArchive;
	}

	/**
	 * Setup.
	 */
	@Before
	public void setupClass() {
		// Create rest client with Resteasy Client Framework.
		final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS)
				.socketTimeout(10, TimeUnit.SECONDS).build();
		webTarget = client.target(
				UriBuilder.fromPath(this.buildBaseUrl(WEB_ARCHIVE_NAME) + "/" + JaxRsActivator.APPLICATION_PATH));
	}

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=WebApplicationExceptionTestIT#test010_ResponseObject_pass
	 * </pre>
	 */
	@Test
	@InSequence(10)
	public void test010_ResponseObject_pass() {
		LOGGER.debug(LOG_PREFIX + "test010_ResponseObject_pass");

		Response response = null;

		try {
			final MockWebAppExResourceInterface proxy = webTarget.proxy(MockWebAppExResourceInterface.class);
			response = proxy.checkMockResponseObject(1L);

			assertThat(response, notNullValue());
			assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

			final MockResponseObject responseObject = response.readEntity(MockResponseObject.class);

			assertThat(responseObject.getErrorCode(), nullValue());
			assertThat(responseObject.getShortErrorCodeDescription(), nullValue());
			assertThat(responseObject.getStatus(), nullValue());
			assertThat(responseObject.getUuid(), nullValue());
			assertThat(responseObject.getResponseValue(), equalTo("1"));
		} catch (final Exception ex) {
			ex.printStackTrace();
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=WebApplicationExceptionTestIT#test020_ResponseObject_pass
	 * </pre>
	 */
	@Test
	@InSequence(20)
	public void test020_ResponseObject_pass() {
		LOGGER.debug(LOG_PREFIX + "test020_ResponseObject_pass");

		Response response = null;

		try {
			final MockWebAppExResourceInterface proxy = webTarget.proxy(MockWebAppExResourceInterface.class);
			response = proxy.checkMockResponseObject(2L);

			assertThat(response, notNullValue());
			assertThat(response.getStatus(), equalTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));

			final MockResponseObject responseObject = response.readEntity(MockResponseObject.class);

			assertThat(responseObject.getErrorCode(), equalTo("TEST_API_00101"));
			assertThat(responseObject.getShortErrorCodeDescription(), equalTo("Invalid arguments"));
			assertThat(responseObject.getStatus().name(), containsString("INTERNAL_SERVER_ERROR"));
			assertThat(responseObject.getUuid(), notNullValue());
			assertThat(responseObject.getResponseValue(), nullValue());
		} catch (final Exception ex) {
			ex.printStackTrace();
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=WebApplicationExceptionTestIT#test030_ResponseObject_pass
	 * </pre>
	 */
	@Test
	@InSequence(30)
	public void test030_ResponseObject_pass() {
		LOGGER.debug(LOG_PREFIX + "test030_ResponseObject_pass");

		Response response = null;

		try {
			final MockWebAppExResourceInterface proxy = webTarget.proxy(MockWebAppExResourceInterface.class);
			response = proxy.checkMockResponseObject(3L);

			assertThat(response, notNullValue());
			assertThat(response.getStatus(), equalTo(Response.Status.BAD_REQUEST.getStatusCode()));

			final MockResponseObject responseObject = response.readEntity(MockResponseObject.class);

			assertThat(responseObject.getErrorCode(), containsString("TEST_API_00101"));
			assertThat(responseObject.getShortErrorCodeDescription(), equalTo("Invalid arguments"));
			assertThat(responseObject.getStatus().name(), containsString("BAD_GATEWAY"));
			assertThat(responseObject.getUuid(), containsString("uuid"));
			assertThat(responseObject.getResponseValue(), equalTo("3"));
		} catch (final Exception ex) {
			ex.printStackTrace();
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	/**
	 * The arquillian war is already deployed to wildfly, read now the
	 * connection properties from global system properties, to build the base
	 * url.
	 * 
	 * @param webArchiveName
	 *            part of the base url, 'http://localhost:8080/webArchiveName/'.
	 * @return the base url, created from system properties.
	 */
	protected String buildBaseUrl(final String webArchiveName) {
		// A systemProperty that is used, to get the host name of a running
		// wildfly instance, during arquillian tests at runtime.
		final String WILDFLY_BIND_ADDRESS_SYSTEM_PROPERTY_KEY = "jboss.bind.address";
		final String bindAddressProperty = System.getProperty(WILDFLY_BIND_ADDRESS_SYSTEM_PROPERTY_KEY);
		// A systemProperty that is used, to get the port-offset of a running
		// wildfly instance, during arquillian tests at runtime.
		final String WILDFLY_PORT_OFFSET_SYSTEM_PROPERTY_KEY = "jboss.socket.binding.port-offset";
		// The wildfly default port for http requests.
		final int WILDFLY_DEFAULT_HTTP_PORT = 8080;
		// Get port of the running wildfly instance.
		final int port = WILDFLY_DEFAULT_HTTP_PORT
				+ Integer.parseInt(System.getProperty(WILDFLY_PORT_OFFSET_SYSTEM_PROPERTY_KEY));

		// Build the base Url.
		return "http://" + bindAddressProperty + ":" + port + "/" + webArchiveName;
	}

}