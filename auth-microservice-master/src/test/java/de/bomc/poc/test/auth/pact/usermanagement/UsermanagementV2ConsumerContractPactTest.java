/**
 * Project: MY_POC
 * <p/>
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * <p/>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.auth.pact.usermanagement;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRule;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactFragment;
import de.bomc.poc.auth.rest.application.JaxRsActivator;
import de.bomc.poc.auth.rest.endpoint.v2.usermanagement.AuthUserManagementRestEndpoint;
import de.bomc.poc.rest.filter.authorization.AuthorizationTokenHeaderRequestFilter;
import de.bomc.poc.rest.logger.ResteasyClientLogger;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Creates the consumer contract for {@link AuthUserManagementRestEndpoint}}
 * <pre>
 *	mvn clean install -Pcdc-tests
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UsermanagementV2ConsumerContractPactTest {
	private static final Logger LOGGER = Logger.getLogger(UsermanagementV2ConsumerContractPactTest.class);
	private static final String BASE_URI = "/auth-microservice" + "/" + JaxRsActivator.APPLICATION_PATH;
    private static final String USERMANGEMENT_V2_FIND_ALL_ROLES_BY_USERNAME_REST_PROVIDER = "UsermanagementV2FindAllRolesByUsernameRestProvider";
    @Rule
    public PactProviderRule rule_FindAllRolesByUsername = new PactProviderRule(USERMANGEMENT_V2_FIND_ALL_ROLES_BY_USERNAME_REST_PROVIDER, "localhost", 8080, this);

    /**
     * @param builder
     * @return
     */
    @Pact(provider = USERMANGEMENT_V2_FIND_ALL_ROLES_BY_USERNAME_REST_PROVIDER, consumer = "AuthUserManagementRestEndpoint")
    public PactFragment createFindAllRolesByUsernameV2Fragment(final PactDslWithProvider builder) {
        return builder.given("Describe the state the provider needs to be in for the pact test to be verified")
            .uponReceiving("Description of the request that is expected to be received")
            // _____________________________________________________________________________________________
            // Add here the requestParam to define the full endpoint resource path, otherwise a test failed.
            .path(BASE_URI + JaxRsActivator.USERMANAGEMENT_ENDPOINT_PATH + "/roles-by-username/Default-System_user")
            .method("GET")
            .headers(this.requestHeaders())
            .willRespondWith()
            .headers(this.responseHeaders())
            .body(getJsonReponse(), AuthUserManagementRestEndpoint.MEDIA_TYPE_JSON_V2)
            .status(200)
            .toFragment();
    }
    
    private Map<String, String> requestHeaders() {
        final Map<String, String> requestHeaders = new HashMap<>();
        //
        // The request header is not set here. Because the necessary header informations are set by the resteasy proxy framework.
        //
        requestHeaders.put("X-BOMC-AUTHORIZATION", "BOMC_USER");
        
        return Collections.unmodifiableMap(requestHeaders);
    }

    private Map<String, String> responseHeaders() {
        final Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", AuthUserManagementRestEndpoint.MEDIA_TYPE_JSON_V2);

        return Collections.unmodifiableMap(responseHeaders);
    }

    /**
     * mvn clean install -Dtest=UsermanagementV2ConsumerContractPactTest#test01_findAllRolesByUsername_Pass
     * @throws Exception
     */
    @Test
    @PactVerification(USERMANGEMENT_V2_FIND_ALL_ROLES_BY_USERNAME_REST_PROVIDER)
    public void test01_findAllRolesByUsername_Pass() throws Exception {
        System.out.println("UsermanagementV2ConsumerContractPactTest#test01_findAllRolesByUsername_Pass");

		final ResteasyClient
            client = new ResteasyClientBuilder().establishConnectionTimeout(100, TimeUnit.SECONDS).socketTimeout(2, TimeUnit.SECONDS).build();
		client.register(new ResteasyClientLogger(LOGGER, true));
		client.register(new AuthorizationTokenHeaderRequestFilter("BOMC_USER"));
        final ResteasyWebTarget webTarget = client.target(rule_FindAllRolesByUsername.getConfig().url() + BASE_URI);
        final AuthUserManagementRestEndpoint proxy = webTarget.proxy(AuthUserManagementRestEndpoint.class);

        final Response response = proxy.findAllRolesByUsername("Default-System_user");

        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
    }

    private String getJsonReponse() {
        return "[{\"id\":3,\"name\":\"Default-System_user\",\"description\":\"This role allows restricted access to the system\",\"grantDTO\":[{\"id\":2,\"name\":\"read2\",\"description\":\"Allows reading (2) parts "
               + "from db.\"},{\"id\":1,\"name\":\"read1\",\"description\":\"Allows reading (1) parts from db.\"}]}]\n";
    }
}
