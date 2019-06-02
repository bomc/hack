#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: ${symbol_dollar}Author: bomc ${symbol_dollar}
 *
 *  date: ${symbol_dollar}Date: ${symbol_dollar}
 *
 *  revision: ${symbol_dollar}Revision: ${symbol_dollar}
 *
 * </pre>
 */
package ${package}.interfaces.rest.v1.basis.version;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;

import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.rest.logger.client.ResteasyClientLogger;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import ${package}.ArquillianBase;
import ${package}.CategoryBasisIntegrationTestIT;
import ${package}.application.util.JaxRsActivator;
import ${package}.application.basis.version.VersionSingletonEJB;
import ${package}.interfaces.rest.v1.basis.VersionRestEndpoint;

/**
 * Tests the {@link VersionRestEndpointImpl}.
 * <p>
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=VersionRestEndpointTestIT
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
@RunWith(Arquillian.class)
@Category(CategoryBasisIntegrationTestIT.class)
public class VersionRestEndpointTestIT extends ArquillianBase {

    private static final String LOG_PREFIX = "VersionRestEndpointTestIT${symbol_pound}";
    private static final String WEB_ARCHIVE_NAME = "bomc-version-war";
    @Inject
    @LoggerQualifier
    private Logger logger;
    private ResteasyClient resteasyClient;

    // 'testable = true', means all the tests are running inside of the
    // container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = createTestArchiveWithEmptyAssets(WEB_ARCHIVE_NAME);
        webArchive.addClasses(VersionRestEndpointTestIT.class, CategoryBasisIntegrationTestIT.class);
        webArchive.addClasses(VersionRestEndpoint.class, VersionRestEndpointImpl.class);
        webArchive.addClasses(VersionSingletonEJB.class);
        webArchive.addClasses(JaxRsActivator.class, LoggerQualifier.class, LoggerProducer.class);
        webArchive.addAsResource(VersionSingletonEJB.VERSION_PROPERTIES_FILE, VersionSingletonEJB.VERSION_PROPERTIES_FILE);
        webArchive.addClasses(ResteasyClientLogger.class);

        // Add dependencies
        final ConfigurableMavenResolverSystem
            resolver =
            Maven.configureResolver()
                 .withMavenCentralRepo(false);

        // NOTE@MVN:will be changed during mvn project generating.
        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
                                          .resolve("de.bomc.poc:exception-lib-ext:jar:?")
                                          .withTransitivity()
                                          .asFile());

        System.out.println(LOG_PREFIX + "createDeployment: " + webArchive.toString(true));

        return webArchive;
    }

    /**
     * Setup.
     */
    @Before
    @SuppressWarnings("deprecation")
    public void setupClass() {
        // Create rest client with Resteasy Client Framework.
        resteasyClient = new ResteasyClientBuilder().connectionTTL(DEFAULT_REST_CLIENT_CONNECTION_TTL, TimeUnit.MILLISECONDS)
                                                    .establishConnectionTimeout(DEFAULT_REST_CLIENT_ESTABLISHED_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                                                    .socketTimeout(DEFAULT_REST_CLIENT_SOCKET_TIMEOUT, TimeUnit.MILLISECONDS)
                                                    .register(new ResteasyClientLogger(logger, true))
                                                    .build();
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=VersionRestEndpointTestIT${symbol_pound}test010_v1_readVersion_Pass
     *
     * <b><code>test010_v1_readVersion_Pass</code>:</b><br>
     *  This test read the current version of this artefact from the 'version.properties'.
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *  - version.properties file is available on classpath.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The endpoint is invoked.
     *  - The VersionSingletonEJB reads the current version from 'version.properties' file.
     *
     * <b>Postconditions:</b><br>
     *  Returns the current version as json.
     * </pre>
     * @throws URISyntaxException is thrown during URI creation, is not expected.
     */
    @Test
    @InSequence(10)
    public void test010_v1_readVersion_Pass() throws URISyntaxException {
        this.logger.info(LOG_PREFIX + "test010_v1_readVersion_Pass [uri=" + this.buildUri(WEB_ARCHIVE_NAME) + "]");

        Response response = null;

        try {
            final VersionRestEndpoint proxy = resteasyClient.target(this.buildUri(WEB_ARCHIVE_NAME))
                                                            .proxy(VersionRestEndpoint.class);

            response = proxy.getVersion();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final JsonObject jsonObject = response.readEntity(JsonObject.class);
                this.logger.info(LOG_PREFIX + "test010_v1_readVersion_Pass [version=" + jsonObject + "]");
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }

        assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
    }
}
