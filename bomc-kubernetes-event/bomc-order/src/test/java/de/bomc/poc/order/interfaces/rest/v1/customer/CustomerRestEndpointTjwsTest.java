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
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.json.JsonObject;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.bomc.poc.order.CategoryFastUnitTest;
import de.bomc.poc.order.application.customer.CustomerController;
import de.bomc.poc.order.application.customer.dto.CustomerDTO;
import de.bomc.poc.order.interfaces.rest.PortFinder;

/**
 * Tests the {@link CustomerRestEndpoint}.
 * 
 * <pre>
 *     mvn clean install -Dtest=CustomerRestEndpointTjwsTest
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 07.12.2018
 */
@SuppressWarnings("deprecation")
@RunWith(MockitoJUnitRunner.class)
@Category(CategoryFastUnitTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomerRestEndpointTjwsTest {

    private static final String LOG_PREFIX = "CustomerRestEndpointTjwsTest#";
    private static final Logger LOGGER = Logger.getLogger(CustomerRestEndpointTjwsTest.class);
    private static int port;
    private static org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer server;
    @Mock
    private Logger logger;
    @Mock
    private CustomerController customerControllerEJB;
    @Mock
    private Request request;
    @InjectMocks
    private static final CustomerRestEndpointImpl sut = new CustomerRestEndpointImpl();
    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    // _______________________________________________
    // Test data
    // -----------------------------------------------
    private static final String USER_ID = UUID.randomUUID().toString();
    private static final Long TECHNICAL_ID = 42L;
    private static final String FIRST_NAME = "firstName";
    private static final String NAME = "name";
    private static final String USER_NAME = "userName";    

    @Before
    public void init() {
        //
    }

    @BeforeClass
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void beforeClass() throws Exception {
        port = PortFinder.findPort();

        server = new org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer();
        server.setPort(port);
        server.getDeployment().setResources((List) Collections.singletonList(sut));

        server.start();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=CustomerRestEndpointTjwsTest#test010_findLatestModifiedDateTime_pass
     *
     * <b><code>test010_readStoredExceptions_pass</code>:</b><br>
     *  Tests the behavior for reading the last modified date for CustomerEntity from db.
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed to server.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The endpoint is invoked.
     *
     * <b>Postconditions:</b><br>
     *  The response contains 'last-modified-date' attribute.
     * </pre>
     */
    @Test
    public void test010_findLatestModifiedDateTime_pass() {
        LOGGER.debug(LOG_PREFIX + "test010_findLatestModifiedDateTime_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        final LocalDateTime localDateTime = LocalDateTime.now();

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        when(this.customerControllerEJB.findLatestModifiedDateTime(USER_ID)).thenReturn(localDateTime);
        
        // ___________________________________________
        // THEN
        // -------------------------------------------
        final Response response = sut.getLatestModifiedDate(USER_ID);

        assertThat(response, notNullValue());
        assertThat(response.getStatus(), equalTo(Status.OK.getStatusCode()));

        final JsonObject jsonObject = (JsonObject) response.getEntity();
        assertThat(jsonObject, notNullValue());

        final String retLocalDate = jsonObject.get("last-modified-date").toString();
        assertThat(retLocalDate, notNullValue());
    }
    
    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=CustomerRestEndpointTjwsTest#test020_createCustomer_pass
     *
     * <b><code>test020_createCustomer_pass</code>:</b><br>
     *  
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed to server.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The endpoint is invoked.
     *
     * <b>Postconditions:</b><br>
     *  
     * </pre>
     */
    @Test
    public void test020_createCustomer_pass() {
        LOGGER.debug(LOG_PREFIX + "test020_createCustomer_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        final CustomerDTO customerDTO = new CustomerDTO();

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        when(this.customerControllerEJB.createCustomer(customerDTO, USER_ID)).thenReturn(TECHNICAL_ID);
        
        // ___________________________________________
        // THEN
        // -------------------------------------------
        final Response response = sut.createCustomer(customerDTO, USER_ID);

        assertThat(response, notNullValue());
        assertThat(response.getStatus(), equalTo(Status.OK.getStatusCode()));
        
        final JsonObject jsonObject = (JsonObject) response.getEntity();
        assertThat(jsonObject, notNullValue());

        final String id = jsonObject.get("id").toString();
        assertThat(id, notNullValue());
    }
    
    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=CustomerRestEndpointTjwsTest#test030_findCustomerByUsername_pass
     *
     * <b><code>test030_findCustomerByUsername_pass</code>:</b><br>
     *  
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed to server.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The endpoint is invoked.
     *
     * <b>Postconditions:</b><br>
     *  CustomerDTO is retuned as jsonObject.
     * </pre>
     */
    @Test
    public void test030_findCustomerByUsername_pass() {
        LOGGER.debug(LOG_PREFIX + "test030_findCustomerByUsername_pass");
        
        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        final CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setFirstname(FIRST_NAME);
        customerDTO.setName(NAME);
        customerDTO.setUsername(USER_NAME);
        
        // ___________________________________________
        // WHEN
        // -------------------------------------------
        when(this.customerControllerEJB.findCustomerByUsername(USER_NAME, USER_ID)).thenReturn(customerDTO);
        
        // ___________________________________________
        // THEN
        // -------------------------------------------
        final Response response = sut.findCustomerByUsername(USER_NAME, USER_ID);
        
        assertThat(response, notNullValue());
        assertThat(response.getStatus(), equalTo(Status.OK.getStatusCode()));
        
        final JsonObject jsonObject = (JsonObject) response.getEntity();
        assertThat(jsonObject, notNullValue());

        final String firstName = jsonObject.get("firstname").toString();
        assertThat(firstName.substring(1, firstName.length() - 1), equalTo(FIRST_NAME));
        
        final String name = jsonObject.get("name").toString();
        assertThat(name.substring(1, name.length() - 1), equalTo(NAME));
        
        final String userName = jsonObject.get("username").toString();
        assertThat(userName.substring(1, userName.length() - 1), equalTo(USER_NAME));
    }
}
