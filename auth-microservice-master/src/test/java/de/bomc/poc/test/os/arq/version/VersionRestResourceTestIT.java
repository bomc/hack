/**
 * Project: MY_POC
 * <p/>
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * <p/>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.os.arq.version;

import de.bomc.poc.auth.rest.application.JaxRsActivator;
import de.bomc.poc.auth.rest.endpoint.v1.version.VersionRestEndpoint;
import de.bomc.poc.auth.rest.endpoint.v1.version.impl.VersionRestEndpointImpl;
import de.bomc.poc.exception.app.AppAuthRuntimeException;
import de.bomc.poc.exception.app.AppInitializationException;
import de.bomc.poc.exception.app.AppInvalidPasswordException;
import de.bomc.poc.exception.handling.ApiError;
import de.bomc.poc.exception.interceptor.ApiExceptionInterceptor;
import de.bomc.poc.exception.qualifier.ApiExceptionQualifier;
import de.bomc.poc.exception.web.WebAuthRuntimeException;
import de.bomc.poc.logger.producer.LoggerProducer;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.os.version.VersionSingletonEJB;
import de.bomc.poc.test.os.arq.ArquillianOsBase;
import de.bomc.poc.validation.ConstraintViolationMapper;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.performance.annotation.Performance;
import org.jboss.arquillian.performance.annotation.PerformanceTest;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the {@link VersionRestEndpoint}.
 * 
 * <pre>
 *     mvn clean install -Parq-wildfly-remote -Dtest=VersionRestResourceTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@PerformanceTest(resultsThreshold = 10)
@RunWith(Arquillian.class)
public class VersionRestResourceTestIT extends ArquillianOsBase {
	private static final String LOG_PREFIX = "VersionRestResourceTestIT#";
	private static final String WEB_ARCHIVE_NAME = "auth-version";
	private static ResteasyWebTarget webTarget;
	@Inject
	@LoggerQualifier
	private Logger logger;

	// 'testable = true', means all the tests are running inside of the
	// container.
	@Deployment(testable = true)
	public static Archive<?> createDeployment() {
		final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
		webArchive.addClass(VersionRestResourceTestIT.class);
		webArchive.addClasses(LoggerProducer.class, LoggerQualifier.class);
		webArchive.addClasses(VersionRestEndpoint.class, VersionRestEndpointImpl.class);
		webArchive.addClasses(VersionSingletonEJB.class, JaxRsActivator.class);
		webArchive.addClasses(AppInitializationException.class, AppAuthRuntimeException.class,
				AppInvalidPasswordException.class);
		webArchive.addAsResource(VersionSingletonEJB.VERSION_PROPERTIES_FILE);

		webArchive.addPackages(true, ApiError.class.getPackage().getName());
		webArchive.addPackages(true, ApiExceptionInterceptor.class.getPackage().getName());
		webArchive.addPackages(true, ApiExceptionQualifier.class.getPackage().getName());
		webArchive.addPackages(true, WebAuthRuntimeException.class.getPackage().getName());
		webArchive.addPackages(true, ConstraintViolationMapper.class.getPackage().getName());

		System.out.println("VersionRestResourceTestIT#createDeployment: " + webArchive.toString(true));

		return webArchive;
	}

	/**
	 * Setup.
	 * 
	 * @throws MalformedURLException
	 */
	@Before
	public void setupClass() throws MalformedURLException {
		// Create rest client with Resteasy Client Framework.
		final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS)
				.socketTimeout(10, TimeUnit.MINUTES).build();
		webTarget = client.target(buildBaseUrl(WEB_ARCHIVE_NAME) + "/" + JaxRsActivator.APPLICATION_PATH);
	}

	/**
	 * Test reading available heap from app server.
	 * 
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=VersionRestResourceTestIT#test01_v1_readVersion_Pass
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	@InSequence(1)
	@Performance(time = 1000)
	public void test01_v1_readVersion_Pass() {
		this.logger.info(LOG_PREFIX + "test01_v1_readVersion_Pass");

		Response response = null;

		try {
			response = webTarget.proxy(VersionRestEndpoint.class).getVersion();

			if (response.getStatus() == Response.Status.OK.getStatusCode()) {
				final JsonObject jsonObject = response.readEntity(JsonObject.class);
				this.logger.info(LOG_PREFIX + "test01_v1_readVersion_Pass [version=" + jsonObject.toString() + "]");
			}

			assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
		} catch (Exception ex) {
			this.logger.error(LOG_PREFIX + "test01_v1_readVersion_Pass - failed! " + ex);
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}
}
