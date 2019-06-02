/**
 * Project: bomc-onion-architecture
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
package de.bomc.poc.order.interfaces.rest.v1.basis.version;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.bomc.poc.exception.core.web.ApiErrorResponseObject;
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

import de.bomc.poc.order.ArquillianBase;
import de.bomc.poc.order.CategoryBasisIntegrationTestIT;
import de.bomc.poc.order.application.internal.AppErrorCodeEnum;
import de.bomc.poc.order.application.util.JaxRsActivator;
import de.bomc.poc.order.application.basis.version.VersionSingletonEJB;
import de.bomc.poc.order.interfaces.rest.v1.basis.VersionRestEndpoint;

/**
 * Tests the {@link VersionRestEndpointFailTestIT}.
 * <p>
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=VersionRestEndpointFailTestIT
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
@RunWith(Arquillian.class)
@Category(CategoryBasisIntegrationTestIT.class)
public class VersionRestEndpointFailTestIT extends ArquillianBase {

    private static final String LOG_PREFIX = "VersionRestEndpointFailTestIT#";
    private static final String WEB_ARCHIVE_NAME = "bomc-version-fail-war";
    @Inject
    @LoggerQualifier
    private Logger logger;
    private ResteasyClient resteasyClient;

    // 'testable = true', means all the tests are running inside of the
    // container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = createTestArchiveWithEmptyAssets(WEB_ARCHIVE_NAME);
        webArchive.addClasses(VersionRestEndpointFailTestIT.class, CategoryBasisIntegrationTestIT.class);
        webArchive.addClasses(VersionRestEndpoint.class, VersionRestEndpointImpl.class);
        webArchive.addClasses(VersionSingletonEJB.class);
        webArchive.addClasses(AppErrorCodeEnum.class);
        webArchive.addClasses(JaxRsActivator.class, LoggerQualifier.class, LoggerProducer.class);
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
     *  mvn clean install -Parq-wildfly-remote -Dtest=VersionRestEndpointFailTestIT#test010_v1_readVersion_Fail
     *
     * <b><code>test010_v1_readVersion_Fail</code>:</b><br>
     *  This test checks the behavior if the 'version.properties'-file does not exist.
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *  - version.properties file is not added to archive.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The endpoint is invoked.
     *  - The VersionSingletonEJB fails, and a exception is thrown and will be catched.
     *
     * <b>Postconditions:</b><br>
     *  A ApiErrorResponseObject is received, with AppErrorCodeEnum.APP_READ_VERSION_FAILED_10602.
     * </pre>
     * @throws URISyntaxException is thrown during URI creation, is not expected.
     */
    @Test
    @InSequence(10)
    public void test010_v1_readVersion_Fail() throws URISyntaxException {
        this.logger.info(LOG_PREFIX + " test010_v1_readVersion_Fail [uri=" + this.buildUri(WEB_ARCHIVE_NAME) + "]");

        Response response = null;

        try {
            final VersionRestEndpoint proxy = resteasyClient.target(this.buildUri(WEB_ARCHIVE_NAME))
                                                            .proxy(VersionRestEndpoint.class);

            response = proxy.getVersion();

            assertThat(response.getStatus(), equalTo(Status.INTERNAL_SERVER_ERROR.getStatusCode()));

            final ApiErrorResponseObject apiErrorResponseObject = response.readEntity(ApiErrorResponseObject.class);

            assertThat(apiErrorResponseObject.getUuid(), notNullValue());
            assertThat(apiErrorResponseObject.getErrorCode(), equalTo(AppErrorCodeEnum.APP_READ_VERSION_FAILED_10602.name()));
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}
