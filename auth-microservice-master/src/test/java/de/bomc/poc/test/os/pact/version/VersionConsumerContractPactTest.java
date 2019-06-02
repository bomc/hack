/**
 * Project: MY_POC_MICROSERVICE
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
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.os.pact.version;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.Response;

import de.bomc.poc.auth.rest.application.JaxRsActivator;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRule;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactFragment;
import de.bomc.poc.auth.rest.endpoint.v1.version.VersionRestEndpoint;
import de.bomc.poc.rest.filter.authorization.AuthorizationTokenHeaderRequestFilter;
import de.bomc.poc.rest.logger.ResteasyClientLogger;
import org.junit.runners.MethodSorters;

/**
 * Creates the consumer contract for {@link VersionRestEndpoint}}
 * <pre>
 *	mvn clean install -Pcdc-tests
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VersionConsumerContractPactTest {
	private static final Logger LOGGER = Logger.getLogger(VersionConsumerContractPactTest.class);
	
    private static final String BASE_URI = "/auth-microservice" + "/" + JaxRsActivator.APPLICATION_PATH;

    private static final String VERSION_REST_PROVIDER = "VersionRestProvider";

    @Rule
    public PactProviderRule rule = new PactProviderRule(VERSION_REST_PROVIDER, "localhost", 8080, this);
    
    /**
     * @param builder
     * @return
     */
    @Pact(provider = VERSION_REST_PROVIDER, consumer = "VersionRestEndpoint")
    public PactFragment createVersionFragment(final PactDslWithProvider builder) {
        return builder.given("Describe the state the provider needs to be in for the pact test to be verified")
                      .uponReceiving("Description of the request that is expected to be received")
                      // /auth-microservice/auth-api/version/current-version
                      .path(BASE_URI + JaxRsActivator.VERSION_ENDPOINT_PATH + "/current-version")
                      .method("GET")
                      .headers(this.requestHeaders())
                      .willRespondWith()
                      .headers(this.responseHeaders())
                      .status(200)
                      .toFragment();
    }

    private Map<String, String> requestHeaders() {
        final Map<String, String> requestHeaders = new HashMap<>();
        //
        // The request header is not set here. Because the necessary header informations are set by the resteasy client proxy framework.
        //
        requestHeaders.put("X-BOMC-AUTHORIZATION", "BOMC_USER");
        return Collections.unmodifiableMap(requestHeaders);
    }

    private Map<String, String> responseHeaders() {
        final Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", VersionRestEndpoint.MEDIA_TYPE_JSON_V1);

        return Collections.unmodifiableMap(responseHeaders);
    }

    /**
     * mvn clean install -Dtest=VersionConsumerContractPactTest#test01_getVersion_Pass
     * @throws Exception
     */
    @Test
    @PactVerification(VERSION_REST_PROVIDER)
    public void test01_getVersion_Pass() throws Exception {
        System.out.println("VersionConsumerContractPactTest#test01_getVersion_Pass");
        
		final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(100, TimeUnit.SECONDS).socketTimeout(2, TimeUnit.SECONDS).build();
        client.register(new ResteasyClientLogger(LOGGER, true));
        client.register(new AuthorizationTokenHeaderRequestFilter("BOMC_USER"));
        // /auth-microservice/auth-api/version/current-version
        final ResteasyWebTarget webTarget = client.target(rule.getConfig().url() + BASE_URI);
        final VersionRestEndpoint proxy = webTarget.proxy(VersionRestEndpoint.class);
        final Response response = proxy.getVersion();
        
        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
    }
}
