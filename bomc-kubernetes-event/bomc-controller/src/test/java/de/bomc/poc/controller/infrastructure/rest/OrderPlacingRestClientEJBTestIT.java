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
package de.bomc.poc.controller.infrastructure.rest;

import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import de.bomc.poc.controller.ArquillianBase;
import de.bomc.poc.controller.CategorySlowIntegrationTestIT;
import de.bomc.poc.controller.infrastructure.rest.order.OrderPlacingRestClientEJB;
import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.rest.logger.client.ResteasyClientLogger;

/**
 * Tests the {@link OrderPlacingRestClientEJBTestIT}.
 * <p>
 * 
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=OrderPlacingRestClientEJBTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
@RunWith(Arquillian.class)
@Category(CategorySlowIntegrationTestIT.class)
public class OrderPlacingRestClientEJBTestIT extends ArquillianBase {
    
    private static final String LOG_PREFIX = "OrderPlacingRestClientEJBTestIT#";
    private static final String WEB_ARCHIVE_NAME = "bomc-order-war";
    @Inject
    @LoggerQualifier
    private Logger logger;
    
    // 'testable = true', means all the tests are running inside of the
    // container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = createTestArchiveWithEmptyAssets(WEB_ARCHIVE_NAME);
        webArchive.addClasses(OrderPlacingRestClientEJBTestIT.class, CategorySlowIntegrationTestIT.class);
        webArchive.addClasses(OrderPlacingRestClientEJB.class);
        webArchive.addClasses(LoggerQualifier.class, LoggerProducer.class);
        webArchive.addClasses(ResteasyClientLogger.class);
        
        // // Add dependencies
        // final ConfigurableMavenResolverSystem
        // resolver =
        // Maven.configureResolver()
        // .withMavenCentralRepo(false);
        //
        // // NOTE@MVN:will be changed during mvn project generating.
        // webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
        // .resolve("de.bomc.poc:exception-lib-ext:jar:?")
        // .withTransitivity()
        // .asFile());
        
        System.out.println(LOG_PREFIX + "createDeployment: " + webArchive.toString(true));
        
        return webArchive;
    }
    
    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=OrderPlacingRestClientEJBTestIT#test010_v1_order_pass
     *
     * <b><code>test010_v1_overview_pass</code>:</b><br>
     *
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The endpoint is invoked.
     *
     * <b>Postconditions:</b><br>
     *
     * </pre>
     * 
     * @throws URISyntaxException
     *             is thrown during URI creation, is not expected.
     */
    @Test
    @InSequence(10)
    public void test010_v1_order_pass() throws URISyntaxException {
        this.logger.debug(LOG_PREFIX + "test010_v1_overview_pass [uri=" + this.buildUri(WEB_ARCHIVE_NAME) + "]");
        
        
        try {
            TimeUnit.SECONDS.sleep(10L);
        } catch (InterruptedException e) {
            // Ignore
        }
        
//        Response response = null;
//        
//        try {
//            response = resteasyClient.target(this.buildUri(WEB_ARCHIVE_NAME)).path("/overview/endpoints")
//                    .request(ApiOverviewRestEndpoint.MEDIA_TYPE_JSON_V1).get();
//            
//            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
//                final JsonArray jsonArray = response.readEntity(JsonArray.class);
//                
//                this.logger.info(LOG_PREFIX + "test010_v1_overview_pass [rest.overview=" + jsonArray + "]");
//            }
//        } finally {
//            // Cleanup resources.
//            if (response != null) {
//                response.close();
//            }
//        }
//        
//        assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
    }
}
