package ch.bs.zid.egov.test.rest.endpoints.arq;

import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.rest.JaxRsActivator;
import de.bomc.poc.rest.endpoints.impl.VersionRESTResourceImpl;
import de.bomc.poc.rest.endpoints.v1.VersionRESTResource;
import de.bomc.poc.service.impl.VersionSingletonEJB;

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
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the {@link VersionRESTResource}.
 * <pre>
 *     mvn clean install -Parq-wildfly-remote -Dtest=VersionRestResourceTestIT
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
@PerformanceTest(resultsThreshold = 10)
@RunWith(Arquillian.class)
public class VersionRestResourceTestIT extends ArquillianBase {
    private static final String LOG_PREFIX = "VersionRestResourceTestIT#";
    private static final String WEB_ARCHIVE_NAME = "egov-version";
    private static final String BASE_URI = BASE_URL + WEB_ARCHIVE_NAME;
    private static ResteasyWebTarget webTarget;
    @Inject
    @LoggerQualifier
    private Logger logger;

    // 'testable = true', means all the tests are running inside of the container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
        webArchive.addClass(VersionRestResourceTestIT.class);
        webArchive.addClasses(VersionRESTResource.class, VersionRESTResourceImpl.class);
        webArchive.addClasses(VersionSingletonEJB.class, JaxRsActivator.class, LoggerQualifier.class, LoggerProducer.class);
        webArchive.addAsWebInfResource("META-INF/beans.xml", "beans.xml");
        webArchive.addAsResource(VersionSingletonEJB.VERSION_PROPERTIES_FILE, VersionSingletonEJB.VERSION_PROPERTIES_FILE);

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
                                                                 .socketTimeout(10, TimeUnit.MINUTES)
                                                                 .build();
        webTarget = client.target(VersionRestResourceTestIT.BASE_URI + JaxRsActivator.APPLICATION_PATH);
    }

    /**
     * Test reading available heap from app server.
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=VersionRestResourceTestIT#test01_v1_readVersion_Pass
     * </pre>
     */
    @Test
    @InSequence(1)
    @Performance(time = 1500)
    public void test01_v1_readVersion_Pass() {
        this.logger.info(LOG_PREFIX + "test01_v1_readVersion_Pass");

        Response response = null;

        try {
            response = webTarget.proxy(VersionRESTResource.class)
                                .getVersion();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                final JsonObject jsonObject = response.readEntity(JsonObject.class);
                this.logger.info(LOG_PREFIX + "test01_v1_readVersion_Pass [version=" + jsonObject + "]");
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }

        assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
    }
}
