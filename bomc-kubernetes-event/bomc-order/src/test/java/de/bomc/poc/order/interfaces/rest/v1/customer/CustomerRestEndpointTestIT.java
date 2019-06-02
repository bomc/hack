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
package de.bomc.poc.order.interfaces.rest.v1.customer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.transaction.UserTransaction;
import javax.ws.rs.core.EntityTag;
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
import de.bomc.poc.order.application.customer.CustomerController;
import de.bomc.poc.order.application.customer.CustomerControllerEJB;
import de.bomc.poc.order.application.customer.dto.CustomerDTO;
import de.bomc.poc.order.application.customer.mapping.DTOEntityCustomerMapper;
import de.bomc.poc.order.application.internal.ApplicationUserEnum;
import de.bomc.poc.order.application.util.JaxRsActivator;
import de.bomc.poc.order.domain.model.basis.AbstractEntity;
import de.bomc.poc.order.domain.model.basis.AbstractMetadataEntity;
import de.bomc.poc.order.domain.model.basis.DomainObject;
import de.bomc.poc.order.domain.model.customer.CustomerEntity;
import de.bomc.poc.order.domain.model.customer.JpaCustomerDao;
import de.bomc.poc.order.infrastructure.persistence.basis.JpaGenericDao;
import de.bomc.poc.order.infrastructure.persistence.basis.impl.AbstractJpaDao;
import de.bomc.poc.order.infrastructure.persistence.basis.producer.DatabaseProducer;
import de.bomc.poc.order.infrastructure.persistence.basis.qualifier.JpaDao;
import de.bomc.poc.order.infrastructure.persistence.customer.JpaCustomerDaoImpl;
import de.bomc.poc.order.infrastructure.rest.cache.client.RestClientIfNotMatchFilter;
import de.bomc.poc.order.infrastructure.rest.cache.producer.CacheControlProducer;
import de.bomc.poc.order.infrastructure.rest.cache.qualifier.CacheControlConfigQualifier;
import de.bomc.poc.rest.logger.client.ResteasyClientLogger;

/**
 * Tests the {@link CustomerRestEndpointTestIT}.
 * 
 * <pre>
 *     mvn clean install -Dtest=CustomerRestEndpointTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.02.2019
 */
@RunWith(Arquillian.class)
@Category(CategoryFastIntegrationTestIT.class)
public class CustomerRestEndpointTestIT extends ArquillianBase {

    private static final String LOG_PREFIX = "CustomerRestEndpointTestIT#";
    private static final String WEB_ARCHIVE_NAME = "bomc-customer-war";
    @Inject
    @LoggerQualifier
    private Logger logger;
    @Inject
    @JpaDao
    private JpaCustomerDao jpaCustomerDao;
    @Inject
    private UserTransaction utx;
    private ResteasyClient resteasyClient;

    // 'testable = true', means all the tests are running inside of the
    // container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
        webArchive.addClasses(CustomerRestEndpointTestIT.class, CategoryFastIntegrationTestIT.class);
        webArchive.addClasses(ResteasyClientLogger.class, LoggerQualifier.class, LoggerProducer.class);
        webArchive.addClasses(JpaCustomerDao.class, JpaCustomerDaoImpl.class);
        webArchive.addClasses(CustomerEntity.class, AbstractEntity.class, AbstractMetadataEntity.class,
                DomainObject.class);
        webArchive.addClasses(AbstractJpaDao.class, JpaGenericDao.class, DatabaseProducer.class, JpaDao.class);
        webArchive.addClasses(CustomerController.class, CustomerControllerEJB.class, CustomerDTO.class, DTOEntityCustomerMapper.class);
        webArchive.addClasses(CacheControlConfigQualifier.class, CacheControlProducer.class);
        webArchive.addClasses(JaxRsActivator.class, CustomerRestEndpoint.class, CustomerRestEndpointImpl.class);
        webArchive.addClasses(ApplicationUserEnum.class);
        webArchive.addClasses(RestClientIfNotMatchFilter.class);
        // Add initial data.
        webArchive.addAsResource("test.scripts/customer_log_import.sql", "import.sql");

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
     *  mvn clean install -Parq-wildfly-remote -Dtest=CustomerRestEndpointTestIT#test010_v1_cacheModified_Pass
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
     *  - Start a initial invocation. No eTag will be set. The response has status '200' and returns a entity. 
     *    The eTag has to be cached.
     *  - Set eTag to header from previous invocation and invoke endpoint again. 
     *    A response with http-status '304' and no entity will be returned.
     *  - Change modified date for one item in db to current date.
     *  - Next invocation: with header 'If-No-Match' and eTag from last invocation. 
     *    The db has changed a entity and status '200' will be returned. 
     *    The eTag from response has to be cached for next invocation.
     *  - Next invocation: with header 'If-No-Match' eTag from last invocation. 
     *    A empty response and status '304' is returned.
     *  - Next invocation: with header 'If-No-Match' eTag from last invocation. 
     *    A empty response and status '304' is returned.
     *
     * <b>Postconditions:</b><br>
     * 
     * </pre>
     * 
     * @throws URISyntaxException
     *             is thrown during URI creation, is not expected.
     * @throws Exception
     */
    @Test
    @InSequence(10)
    public void test010_v1_cacheModified_Pass() throws URISyntaxException, Exception {
        this.logger.debug(LOG_PREFIX + "test010_v1_cacheModified_Pass [uri=" + this.buildUri(WEB_ARCHIVE_NAME) + "]");

        Response response = null;
        EntityTag retEntityTag = null;

        // ___________________________________________
        // 1. Start a initial invocation. No eTag will be set. The response has
        // status '200' and returns a entity. The eTag has to be cached.
        try {
            // Setup resteasy client.
            resteasyClient = new ResteasyClientBuilder()
                    .connectionTTL(DEFAULT_REST_CLIENT_CONNECTION_TTL, TimeUnit.MILLISECONDS).build();
            this.resteasyClient.register(new ResteasyClientLogger(logger, true));

            // Invoke endpoint by proxy.
            final CustomerRestEndpoint proxy = this.resteasyClient.target(this.buildUri(WEB_ARCHIVE_NAME))
                    .proxy(CustomerRestEndpoint.class);
            response = proxy.getLatestModifiedDate(ApplicationUserEnum.TEST_USER.name());
            assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));

            retEntityTag = response.getEntityTag();
            assertThat(retEntityTag, notNullValue());

            // Read response.
            final JsonObject jsonObject = response.readEntity(JsonObject.class);
            assertThat(jsonObject, notNullValue());
            this.logger.info(LOG_PREFIX + "test010_v1_cacheModified_Pass [version=" + jsonObject + "]");
        } finally {
            if (response != null) {
                response.close();
            }
        }

        // ___________________________________________
        // 2. Set eTag to header from previous invocation and invoke endpoint
        // again. A response with http-status '304' and no entity will be
        // returned.
        try {
            final RestClientIfNotMatchFilter restClientIfNotMatchFilter = new RestClientIfNotMatchFilter(
                    retEntityTag.getValue());

            // Invoke endpoint by proxy.
            final CustomerRestEndpoint proxy = this.invokeProxy(restClientIfNotMatchFilter);
            response = proxy.getLatestModifiedDate(ApplicationUserEnum.TEST_USER.name());
            // Returns 304, that means noting has change on server.
            assertThat(response.getStatus(), equalTo(Response.Status.NOT_MODIFIED.getStatusCode()));
        } finally {
            if (response != null) {
                response.close();
            }
        }

        // ___________________________________________
        // 3. Change modified date for one item in db to current date.
        final List<CustomerEntity> customerEntityList = this.jpaCustomerDao.findAll();
        assertThat(customerEntityList.size(), greaterThanOrEqualTo(0));

        final CustomerEntity customerEntity = customerEntityList.get(0);
        customerEntity.setUsername("myNewEmail");

        this.utx.begin();
        this.jpaCustomerDao.merge(customerEntity, ApplicationUserEnum.TEST_USER.name());
        this.utx.commit();

        // ___________________________________________
        // 4. Next invocation: with header 'If-No-Match' and eTag from last
        // invocation. The db has changed a entity and status '200' will be
        // returned. The eTag from response has to be cached for next
        // invocation.
        try {
            // Set header with 'IF_MODIFIED_SINCE'.
            final RestClientIfNotMatchFilter restClientIfNotMatchFilter = new RestClientIfNotMatchFilter(
                    retEntityTag.getValue());

            // Invoke endpoint by proxy.
            final CustomerRestEndpoint proxy = this.invokeProxy(restClientIfNotMatchFilter);
            response = proxy.getLatestModifiedDate(ApplicationUserEnum.TEST_USER.name());

            // Read response.
            final JsonObject jsonObject = response.readEntity(JsonObject.class);
            assertThat(jsonObject, notNullValue());
            this.logger.info(LOG_PREFIX + "test010_v1_cacheModified_Pass [version=" + jsonObject + "]");

            // Read last modified date from response.
            retEntityTag = response.getEntityTag();
            assertThat(retEntityTag, notNullValue());
        } finally {
            if (response != null) {
                response.close();
            }
        }

        // ___________________________________________
        // 5. Next invocation: with header 'If-No-Match' eTag from last
        // invocation. A empty response and status '304' is returned.
        try {
            final RestClientIfNotMatchFilter restClientIfNotMatchFilter = new RestClientIfNotMatchFilter(
                    retEntityTag.getValue());

            // Invoke endpoint by proxy.
            final CustomerRestEndpoint proxy = this.invokeProxy(restClientIfNotMatchFilter);
            response = proxy.getLatestModifiedDate(ApplicationUserEnum.TEST_USER.name());

            assertThat(response.getStatus(), equalTo(Response.Status.NOT_MODIFIED.getStatusCode()));
        } finally {
            if (response != null) {
                response.close();
            }
        }

        // ___________________________________________
        // 6. Next invocation: with header 'If-No-Match' eTag from last
        // invocation. A empty response and status '304' is returned.
        try {
            final RestClientIfNotMatchFilter restClientIfNotMatchFilter = new RestClientIfNotMatchFilter(
                    retEntityTag.getValue());

            // Invoke endpoint by proxy.
            final CustomerRestEndpoint proxy = this.invokeProxy(restClientIfNotMatchFilter);
            response = proxy.getLatestModifiedDate(ApplicationUserEnum.TEST_USER.name());

            assertThat(response.getStatus(), equalTo(Response.Status.NOT_MODIFIED.getStatusCode()));
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    private CustomerRestEndpoint invokeProxy(final RestClientIfNotMatchFilter restClientIfNotMatchFilter) throws NullPointerException, URISyntaxException {
        // Setup resteasy client.
        resteasyClient = new ResteasyClientBuilder()
                .connectionTTL(DEFAULT_REST_CLIENT_CONNECTION_TTL, TimeUnit.MILLISECONDS).build();
        this.resteasyClient.register(restClientIfNotMatchFilter);
        this.resteasyClient.register(new ResteasyClientLogger(logger, true));
        // Invoke endpoint by proxy.
        final CustomerRestEndpoint proxy = this.resteasyClient.target(this.buildUri(WEB_ARCHIVE_NAME))
                .proxy(CustomerRestEndpoint.class);
        
        return proxy;
    }
}