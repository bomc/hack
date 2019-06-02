/**
 * Project: Egov-Integrationsplattform
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: tzdbmm $
 *
 *  date: $Date: 2017-11-09 11:18:40 +0100 (Do, 09 Nov 2017) $
 *
 *  revision: $Revision: 17624 $
 *
 * </pre>
 */
package de.bomc.poc.rest.endpoint;

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
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.rest.JaxRsActivator;
import de.bomc.poc.rest.endpoints.impl.HealthRESTResourceImpl;
import de.bomc.poc.rest.endpoints.v1.HealthRESTResource;
import de.bomc.poc.service.ServerStatisticsSingletonEJB;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the {@link HealthRESTResource}.
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=HealthRestResourceTestIT
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @since 09.12.2017
 */
@PerformanceTest(resultsThreshold = 10)
@RunWith(Arquillian.class)
public class HealthRestResourceTestIT extends ArquillianBase {

    private static final String LOG_PREFIX = "HealthRestResourceTestIT#";
    private static final String WEB_ARCHIVE_NAME = "bomc-health";
    @Inject
    @LoggerQualifier
    private Logger logger;

    // 'testable = true', means all the tests are running inside of the container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
        webArchive.addClass(HealthRestResourceTestIT.class);
        webArchive.addClasses(HealthRESTResource.class, HealthRESTResourceImpl.class);
        webArchive.addClasses(ServerStatisticsSingletonEJB.class, JaxRsActivator.class);
        webArchive.addAsWebInfResource("META-INF/beans.xml", "beans.xml");

        // Add dependencies
        final ConfigurableMavenResolverSystem resolver = Maven.configureResolver().withMavenCentralRepo(false);

        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
                                          .resolve("de.bomc.poc:logging-lib:jar:?")
                                          .withTransitivity()
                                          .asFile());
		
        System.out.println(LOG_PREFIX + "createDeployment: " + webArchive.toString(true));

        return webArchive;
    }

    /**
     * Setup.
     */
    @Before
    public void setupClass() {
        //
    }

    /**
     * Test reading available heap from app server.
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=HealthRestResourceTestIT#test01_v1_readAvailableHeap_Pass
     * </pre>
     * @throws URISyntaxException is thrown during URI creation, is not expected.
     */
    @Test
    @InSequence(1)
    @Performance(time = 1500)
    public void test01_v1_readAvailableHeap_Pass() throws URISyntaxException {
        this.logger.debug(LOG_PREFIX + "test01_v1_readAvailableHeap_Pass [uri=" + this.buildUri(WEB_ARCHIVE_NAME) + "]");

        Response response = null;

        try {
        	final ResteasyClient client = new ResteasyClientBuilder().build();
        	final ResteasyWebTarget target = client.target(this.buildUri(WEB_ARCHIVE_NAME));
        	final HealthRESTResource proxy = target.proxy(HealthRESTResource.class);
        	
            response = proxy.availableHeap();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final JsonObject jsonObject = response.readEntity(JsonObject.class);
                this.logger.info(LOG_PREFIX + "test01_v1_readAvailableHeap_Pass [response=" + jsonObject + "]");
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }

        assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
    }

    /**
     * Test reading os info from app server.
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=HealthRestResourceTestIT#test10_v1_readOsInfo
     * </pre>
     * @throws URISyntaxException is thrown during URI creation, is not expected.
     */
    @Test
    @InSequence(10)
    @Performance(time = 1500)
    public void test10_v1_readOsInfo() throws URISyntaxException {
        this.logger.debug(LOG_PREFIX + "test10_v1_readOsInfo [uri=" + this.buildUri(WEB_ARCHIVE_NAME) + "]");

        Response response = null;

        try {
        	final ResteasyClient client = new ResteasyClientBuilder().build();
        	final ResteasyWebTarget target = client.target(this.buildUri(WEB_ARCHIVE_NAME));
        	final HealthRESTResource proxy = target.proxy(HealthRESTResource.class);
        	
            response = proxy.osInfo();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final String jsonString = response.readEntity(String.class);
                this.logger.info(LOG_PREFIX + "test10_v1_readOsInfo [response=" + jsonString + "]");
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

}
