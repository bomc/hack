/**
 * Project: bomc-invoice
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.invoice.interfaces.rest.v1.basis;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.JsonObject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.wildfly.swarm.jaxrs.JAXRSArchive;

import de.bomc.poc.invoice.application.internal.AppErrorCodeEnum;
import de.bomc.poc.invoice.application.log.LoggerProducer;
import de.bomc.poc.invoice.application.log.LoggerQualifier;
import de.bomc.poc.invoice.application.util.JaxRsActivator;
import de.bomc.poc.invoice.interfaces.rest.filter.ResteasyClientLogger;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/**
 * Test the {@link HealthRestEndpoint}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 14.02.2018
 */
@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HealthRestEndpointTestIT {

	private static final String LOG_PREFIX = "HealthRestEndpointTestIT#";
	private static final Logger LOGGER = Logger.getLogger(HealthRestEndpointTestIT.class.getName());
	private static final String WAR_ARCHIVE_NAME = "bomc-invoice";

//	@Deployment
//	public static Archive<?> createDeployment() {
//
//		final JavaArchive deployment = ShrinkWrap.create(JavaArchive.class, WAR_ARCHIVE_NAME + ".jar")
//				.addClasses(HealthRestEndpoint.class, HealthRestEndpointImpl.class, JaxRsActivator.class)
//				.addClasses(AppErrorCodeEnum.class, LoggerQualifier.class, LoggerProducer.class,
//						ResteasyClientLogger.class)
//				.addAsResource("META-INF/beans.xml")
//				.addAsResource("project-it.yml", "project-defaults.yml");
////				.addAsResource("META-INF/arquillian.xml");
//
//		// Add dependencies
//		final PomEquippedResolveStage resolver = Maven.configureResolver().withMavenCentralRepo(false)
//				.loadPomFromFile("pom.xml");
//
//		// NOTE@MVN:will be changed during mvn project generating.
//		final JavaArchive exceptionLibArchive = resolver
//				.resolve("de.bomc.poc:exception-lib-ext:jar:?", "org.glassfish:javax.json:?").withoutTransitivity()
//				.as(JavaArchive.class)[0];
//		deployment.merge(exceptionLibArchive);
//
//		System.out.println(LOG_PREFIX + "createDeployment: " + deployment.toString(true));
//
//		return deployment;
//	}

	@Deployment
	public static Archive<?> createDeployment() {

		try {
			final JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class);

			deployment.addClasses(HealthRestEndpoint.class, HealthRestEndpointImpl.class, JaxRsActivator.class);
			deployment.addClasses(AppErrorCodeEnum.class, LoggerQualifier.class, LoggerProducer.class);
			deployment.addClasses(ResteasyClientLogger.class);

			deployment.addAsResource("project-it.yml", "/project-defaults.yml");
			deployment.addAsResource("META-INF/beans.xml");

			deployment.setContextRoot(WAR_ARCHIVE_NAME);

			deployment.addAllDependencies();

			// Add project specific dependencies.
			final PomEquippedResolveStage resolver = Maven.configureResolver().withMavenCentralRepo(false)
					.loadPomFromFile("pom.xml");

			final JavaArchive exceptionLibArchive = resolver.resolve("de.bomc.poc:exception-lib-ext:jar:?")
					.withoutTransitivity().as(JavaArchive.class)[0];
			deployment.addAsLibraries(exceptionLibArchive);

			System.out.println(LOG_PREFIX + "createDeployment: " + deployment.toString(true));

			return deployment;
		} catch (final Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

    /**
     * <pre>
     *  mvn clean test -Parq-wildfly-remote -Dtest=HealthRestEndpointTestIT#test010_invokeEndpoint_pass
     * 
     * <b><code>test010_invokeEndpoint_pass</code>:</b><br>
     *  Tests the invocation of the {@link HealthRestEndpointImpl}.
     *
     * <b>Preconditions:</b><br>
     *  - Archive with endpoint is deployed on Thorntail.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Create rest client proxy.
     *  - Invoke the endpoint.
     *
     * <b>Postconditions:</b><br>
     *  - Get a response with Status 200.
     * </pre>
     */
	@Test
	@InSequence(10)
	public void test010_invokeEndpoint_pass() {
		LOGGER.log(Level.INFO, LOG_PREFIX + "test010_invokeEndpoint_pass");

		// Build URI in form:
		// http://localhost:8080/bomc-invoice/rest/health/liveness

		URI uri = null;
		try {
			uri = new URI("http", null, "localhost", 8080, "/" + "bomc-invoice/rest", null, null);

			final HealthRestEndpoint proxy = RestClientBuilder.newBuilder()
					.register(new ResteasyClientLogger(LOGGER, true)).baseUri(uri).build(HealthRestEndpoint.class);
			// Invoke endpoint.
			final Response response = proxy.getLiveness();

			if (response.getStatus() == Response.Status.OK.getStatusCode()) {
				final JsonObject jsonObject = response.readEntity(JsonObject.class);
				
				assertThat(jsonObject, notNullValue());
				
				LOGGER.log(Level.INFO, LOG_PREFIX + "test010_invokeEndpoint_pass [version=" + jsonObject + "]");
			} else {
				fail("Should not be reached!");
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}