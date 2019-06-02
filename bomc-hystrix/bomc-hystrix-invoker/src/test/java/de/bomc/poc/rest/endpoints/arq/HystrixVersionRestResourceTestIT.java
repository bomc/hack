package de.bomc.poc.rest.endpoints.arq;

import de.bomc.poc.hystrix.boot.HystrixBootstrapSingletonEJB;
import de.bomc.poc.hystrix.boot.strategy.WildflyHystrixConcurrencyStrategy;
import de.bomc.poc.hystrix.command.HystrixVersionCommand;
import de.bomc.poc.hystrix.web.VersionHystrixMetricsStreamServlet;
import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.rest.JaxRsActivator;
import de.bomc.poc.rest.api.FallbackDTO;
import de.bomc.poc.rest.api.HystrixDTO;
import de.bomc.poc.rest.endpoints.impl.HystrixVersionRESTResourceImpl;
import de.bomc.poc.rest.endpoints.v1.HystrixVersionRESTResource;
import de.bomc.poc.rest.logger.client.ResteasyClientLogger;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the {@link HystrixVersionRESTResource}.
 * 
 * <pre>
 *     mvn clean install -Parq-wildfly-remote -Dtest=HystrixVersionRestResourceTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
@RunWith(Arquillian.class)
public class HystrixVersionRestResourceTestIT extends ArquillianBase {
	private static final String LOG_PREFIX = "HystrixVersionRestResourceTestIT#";
	private static final String WEB_ARCHIVE_NAME = "bomc-hystrix-invoker";
	private static final String BASE_URI = BASE_URL + WEB_ARCHIVE_NAME;
	private static ResteasyWebTarget webTarget;
	@Inject
	@LoggerQualifier
	private Logger logger;

	// 'testable = true', means all the tests are running inside of the
	// container.
	@Deployment(testable = true)
	public static Archive<?> createDeployment() {
		final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
		webArchive.addClass(HystrixVersionRestResourceTestIT.class);
		webArchive.addClass(VersionHystrixMetricsStreamServlet.class);
		webArchive.addClasses(HystrixDTO.class, FallbackDTO.class);
		webArchive.addClasses(HystrixVersionRESTResource.class, HystrixVersionRESTResourceImpl.class);
		webArchive.addClasses(HystrixBootstrapSingletonEJB.class, HystrixVersionCommand.class, WildflyHystrixConcurrencyStrategy.class);
		webArchive.addClasses(JaxRsActivator.class, LoggerQualifier.class, LoggerProducer.class);
		webArchive.addClass(ResteasyClientLogger.class);
		webArchive.addAsWebInfResource("META-INF/beans.xml", "beans.xml");
		webArchive.addAsWebInfResource("META-INF/jboss-deployment-structure.xml", "jboss-deployment-structure.xml");
		webArchive.addAsWebInfResource("META-INF/jboss-web.xml", "jboss-web.xml");
		webArchive.addAsWebInfResource("META-INF/web.xml", "web.xml");
		
		// Add dependencies
		final MavenResolverSystem resolver = Maven.resolver();
		
//		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("com.netflix.archaius:archaius-core:jar:?")
//				.withMavenCentralRepo(false).withTransitivity().asFile());

//		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.jboss.resteasy:resteasy-jackson-provider:jar:?")
//				.withMavenCentralRepo(false).withTransitivity().asFile());
		
		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("com.netflix.hystrix:hystrix-core:jar:?")
				.withMavenCentralRepo(false).withTransitivity().asFile());

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("com.netflix.hystrix:hystrix-metrics-event-stream:jar:?")
				.withMavenCentralRepo(false).withTransitivity().asFile());

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("io.reactivex:rxjava:jar:?")
				.withMavenCentralRepo(false).withTransitivity().asFile());
		
		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("com.fasterxml.jackson.core:jackson-annotations:jar:?")
				.withMavenCentralRepo(false).withTransitivity().asFile());
		
		System.out.println(LOG_PREFIX + "createDeployment: " + webArchive.toString(true));

		return webArchive;
	}

	/**
	 * Setup.
	 */
	@Before
	public void setupClass() {
		// Create rest client with Resteasy Client Framework.
		final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS)
				.socketTimeout(10, TimeUnit.MINUTES).register(new ResteasyClientLogger(this.logger, true)).build();
		webTarget = client.target(HystrixVersionRestResourceTestIT.BASE_URI + "/" + JaxRsActivator.APPLICATION_PATH);
	}

	/**
	 * Test reading available heap from app server.
	 * 
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=HystrixVersionRestResourceTestIT#test01_v1_readVersion_Pass
	 * </pre>
	 */
	@Test
	@InSequence(1)
	public void test01_v1_readVersion_Pass() {
		this.logger.info(LOG_PREFIX + "test01_v1_readVersion_Pass");

		Response response = null;

		try {
			response = webTarget.proxy(HystrixVersionRESTResource.class).getVersion();

			if (response.getStatus() == Response.Status.OK.getStatusCode()) {
				final HystrixDTO hystrixDTO = response.readEntity(HystrixDTO.class);
				this.logger.info(LOG_PREFIX + "test01_v1_readVersion_Pass [hystrixDTO=" + hystrixDTO + "]");
			}
		} finally {
			if (response != null) {
				response.close();
			}
		}

		assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
	}
}
