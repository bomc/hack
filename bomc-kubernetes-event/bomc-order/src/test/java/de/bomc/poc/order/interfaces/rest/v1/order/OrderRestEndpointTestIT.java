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
package de.bomc.poc.order.interfaces.rest.v1.order;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

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

import de.bomc.poc.logging.producer.LoggerProducer;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.order.ArquillianBase;
import de.bomc.poc.order.CategoryFastIntegrationTestIT;
import de.bomc.poc.order.application.basis.log.interceptor.AuditLogInterceptor;
import de.bomc.poc.order.application.basis.log.qualifier.AuditLogQualifier;
import de.bomc.poc.order.application.customer.dto.CustomerDTO;
import de.bomc.poc.order.application.internal.AppErrorCodeEnum;
import de.bomc.poc.order.application.internal.ApplicationUserEnum;
import de.bomc.poc.order.application.order.OrderController;
import de.bomc.poc.order.application.order.dto.AddressDTO;
import de.bomc.poc.order.application.order.dto.ItemDTO;
import de.bomc.poc.order.application.order.dto.OrderDTO;
import de.bomc.poc.order.application.order.dto.OrderLineDTO;
import de.bomc.poc.order.application.order.mapping.DTOEntityOrderMapper;
import de.bomc.poc.order.application.util.JaxRsActivator;
import de.bomc.poc.order.domain.shared.DomainObjectUtils;
import de.bomc.poc.order.infrastructure.rest.cache.client.RestClientLastModifiedFilter;
import de.bomc.poc.order.infrastructure.rest.cache.producer.CacheControlProducer;
import de.bomc.poc.order.infrastructure.rest.cache.qualifier.CacheControlConfigQualifier;
import de.bomc.poc.rest.logger.client.ResteasyClientLogger;

/**
 * Tests the {@link OrderRestEndpointTestIT}.
 * 
 * <pre>
 *     mvn clean install -Dtest=OrderRestEndpointTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.02.2019
 */
@RunWith(Arquillian.class)
@Category(CategoryFastIntegrationTestIT.class)
public class OrderRestEndpointTestIT extends ArquillianBase {

    private static final String LOG_PREFIX = "OrderRestEndpointTestIT#";
    private static final String WEB_ARCHIVE_NAME = "bomc-order-war";
    @Inject
    @LoggerQualifier
    private Logger logger;
    @EJB
    private OrderController orderController;
    private ResteasyClient resteasyClient;

    // 'testable = true', means all the tests are running inside of the
    // container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
        webArchive.addClasses(OrderRestEndpointTestIT.class, CategoryFastIntegrationTestIT.class);
        webArchive.addClasses(ResteasyClientLogger.class, LoggerQualifier.class, LoggerProducer.class);
        webArchive.addClasses(OrderController.class, OrderControllerEJBMock.class);
        webArchive.addClasses(ItemDTO.class, OrderDTO.class, AddressDTO.class, CustomerDTO.class, OrderLineDTO.class,
                DTOEntityOrderMapper.class);
        webArchive.addClasses(CacheControlConfigQualifier.class, CacheControlProducer.class,
                RestClientLastModifiedFilter.class);
        webArchive.addClasses(JaxRsActivator.class, OrderRestEndpoint.class, OrderRestEndpointImpl.class);
        webArchive.addClasses(ApplicationUserEnum.class, DomainObjectUtils.class, AppErrorCodeEnum.class);
        webArchive.addClasses(AuditLogQualifier.class, AuditLogInterceptor.class);

        //
        // Add dependencies
        final ConfigurableMavenResolverSystem resolver = Maven.configureResolver().withMavenCentralRepo(false);

        // NOTE@MVN:will be changed during mvn project generating.
        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("de.bomc.poc:exception-lib-ext:jar:?")
                .withTransitivity().asFile());

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
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=OrderRestEndpointTestIT#test010_v1_cacheModified_Pass
     *
     * <b><code>test010_v1_cacheModified_Pass</code>:</b><br>
     *  Tests the handling of the CacheControl between client and server. 
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *  - Initial data must be imported.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - see steps in test case 1., 2. and 3.
     *
     * <b>Postconditions:</b><br>
     *  - see steps in test case.
     * </pre>
     * 
     * @throws URISyntaxException
     *             is thrown during URI creation, is not expected.
     */
    @Test
    @InSequence(10)
    public void test010_v1_cacheModified_Pass() throws URISyntaxException {
        this.logger.debug(LOG_PREFIX + "test010_v1_cacheModified_Pass [uri=" + this.buildUri(WEB_ARCHIVE_NAME) + "]");

        Response response = null;
        Date modifiedDate = null;

        // ___________________________________________
        // 1. Start a initial invocation. No If-Modified-Since will be set. The
        // response has
        // status '200' and returns a entity.
        try {
            // Setup resteasy client.
            resteasyClient = new ResteasyClientBuilder()
                    .connectionTTL(DEFAULT_REST_CLIENT_CONNECTION_TTL, TimeUnit.MILLISECONDS).build();
            this.resteasyClient.register(new ResteasyClientLogger(logger, true));

            // Invoke endpoint by proxy.
            final OrderRestEndpoint proxy = this.resteasyClient.target(this.buildUri(WEB_ARCHIVE_NAME))
                    .proxy(OrderRestEndpoint.class);
            response = proxy.getLatestModifiedDate(ApplicationUserEnum.TEST_USER.name());
            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

            modifiedDate = response.getLastModified();
            assertThat(modifiedDate, notNullValue());

            // Read response.
            final List<OrderDTO> orderDTOList = response.readEntity(new GenericType<List<OrderDTO>>() { });
            assertThat(orderDTOList, notNullValue());
            this.logger.info(LOG_PREFIX + "test010_v1_cacheModified_Pass [orderDTOList.size=" + orderDTOList.size() + "]");
        } finally {
            if (response != null) {
                response.close();
            }
        }

        // ___________________________________________
        // 2. A response with http-status '200' and the entities should be
        // returned. Ensure this by manipulate the date, for 1 sec to the past.
        try {
            final String strLastModifiedDate = DomainObjectUtils
                    .formatRfc1123DateTime(new Date(System.currentTimeMillis() - 2000L), TimeZone.getDefault());
            final RestClientLastModifiedFilter restClientLastModifiedFilter = new RestClientLastModifiedFilter(
                    strLastModifiedDate);

            // Invoke endpoint by proxy.
            final OrderRestEndpoint proxy = this.invokeProxy(restClientLastModifiedFilter);
            response = proxy.getLatestModifiedDate(ApplicationUserEnum.TEST_USER.name());
            // Returns 304, that means noting has change on server.
            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
        } finally {
            if (response != null) {
                response.close();
            }
        }

        // ___________________________________________
        // 3. A response with http-status '304' and no entity should be
        // returned. Ensure this by manipulate the date, for 1 sec to the future.
        try {
            final Date newModifiedDate = new Date(System.currentTimeMillis() + 2000L);
            final String strLastModifiedDate = DomainObjectUtils.formatRfc1123DateTime(newModifiedDate,
                    TimeZone.getDefault());
            final RestClientLastModifiedFilter restClientLastModifiedFilter = new RestClientLastModifiedFilter(
                    strLastModifiedDate);

            // Invoke endpoint by proxy.
            final OrderRestEndpoint proxy = this.invokeProxy(restClientLastModifiedFilter);
            response = proxy.getLatestModifiedDate(ApplicationUserEnum.TEST_USER.name());
            // Returns 304, that means noting has change on server.
            assertThat(response.getStatus(), equalTo(Response.Status.NOT_MODIFIED.getStatusCode()));
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=OrderRestEndpointTestIT#test020_v1_cacheModified_Fail
     *
     * <b><code>test020_v1_cacheModified_Fail</code>:</b><br>
     *  Tests the handling if no data is available in db and null is returned from jpaDao. 
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *  - Initial data must be imported.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Invoke method on erver.
     *
     * <b>Postconditions:</b><br>
     *  - Not-Modified is returned.
     * </pre>
     * 
     * @throws URISyntaxException
     *             is thrown during URI creation, is not expected.
     */
    @Test
    @InSequence(20)
    public void test020_v1_cacheModified_Fail() throws URISyntaxException {
        this.logger.debug(LOG_PREFIX + "test020_v1_cacheModified_Fail [uri=" + this.buildUri(WEB_ARCHIVE_NAME) + "]");
        
        Response response = null;

        // ___________________________________________
        // 1. Start a initial invocation. No If-Modified-Since will be set. The
        // response has status '304' and returns a entity.
        try {
            // Setup resteasy client.
            resteasyClient = new ResteasyClientBuilder()
                    .connectionTTL(DEFAULT_REST_CLIENT_CONNECTION_TTL, TimeUnit.MILLISECONDS).build();
            this.resteasyClient.register(new ResteasyClientLogger(logger, true));

            // Invoke endpoint by proxy.
            final OrderRestEndpoint proxy = this.resteasyClient.target(this.buildUri(WEB_ARCHIVE_NAME))
                    .proxy(OrderRestEndpoint.class);
            
            // 'null'-parameter indicates the special test case.
            response = proxy.getLatestModifiedDate(null);
            
            assertThat(response.getStatus(), equalTo(Response.Status.NOT_MODIFIED.getStatusCode()));
        } finally {
            if (response != null) {
                response.close();
            }
        }

    }
    
    private OrderRestEndpoint invokeProxy(final RestClientLastModifiedFilter restClientLastModifiedFilter)
            throws NullPointerException, URISyntaxException {
        // Setup resteasy client.
        resteasyClient = new ResteasyClientBuilder()
                .connectionTTL(DEFAULT_REST_CLIENT_CONNECTION_TTL, TimeUnit.MILLISECONDS).build();
        this.resteasyClient.register(restClientLastModifiedFilter);
        this.resteasyClient.register(new ResteasyClientLogger(logger, true));
        // Invoke endpoint by proxy.
        final OrderRestEndpoint proxy = this.resteasyClient.target(this.buildUri(WEB_ARCHIVE_NAME))
                .proxy(OrderRestEndpoint.class);

        return proxy;
    }
}