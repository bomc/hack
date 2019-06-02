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
package de.bomc.poc.test.os.pact.runtime;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRule;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactFragment;
import de.bomc.poc.auth.rest.application.JaxRsActivator;
import de.bomc.poc.auth.rest.endpoint.v1.runtime.RuntimeRestEndpoint;
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
 * Creates the consumer contract for {@link RuntimeRestEndpoint}}
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RuntimeConsumerContractPactTest {
	private static final Logger LOGGER = Logger.getLogger(RuntimeConsumerContractPactTest.class);
	
	private static final String BASE_URI = "/auth-microservice" + "/" + JaxRsActivator.APPLICATION_PATH;

    private static final String RUNTIME_AVAILABLE_HEAP_REST_PROVIDER = "RuntimeAvailableHeapRestProvider";
    private static final String RUNTIME_NODENAME_REST_PROVIDER = "RuntimeNodeNameRestProvider";
    private static final String RUNTIME_OS_INFO_REST_PROVIDER = "RuntimeOsInfoRestProvider";
    @Rule
    public PactProviderRule ruleAvailableHeap = new PactProviderRule(RUNTIME_AVAILABLE_HEAP_REST_PROVIDER, "localhost", 8080, this);
    @Rule
    public PactProviderRule ruleNodeName = new PactProviderRule(RUNTIME_NODENAME_REST_PROVIDER, "localhost", 8080, this);
    @Rule
    public PactProviderRule ruleOsInfo = new PactProviderRule(RUNTIME_OS_INFO_REST_PROVIDER, "localhost", 8080, this);

    /**
     * @param builder
     * @return
     */
    @Pact(provider = RUNTIME_AVAILABLE_HEAP_REST_PROVIDER, consumer = "RuntimeRestEndpoint")
    public PactFragment createAvailableHeapFragment(final PactDslWithProvider builder) {
        return builder.given("Describe the state the provider needs to be in for the pact test to be verified")
                      .uponReceiving("Description of the request that is expected to be received")
                      .path(BASE_URI + "/runtime/available-heap")
                      .method("GET")
                      .headers(this.requestHeaders())
                      .willRespondWith()
                      .headers(this.responseHeaders())
                      .status(200)
                      .toFragment();
    }

    /**
     * @param builder
     * @return
     */
    @Pact(provider = RUNTIME_NODENAME_REST_PROVIDER, consumer = "RuntimeRestEndpoint")
    public PactFragment createNodeNameFragment(final PactDslWithProvider builder) {
        return builder.given("Describe the state the provider needs to be in for the pact test to be verified")
                      .uponReceiving("Description of the request that is expected to be received")
                      .path(BASE_URI + "/runtime/node-name")
                      .method("GET")
                      .headers(this.requestHeaders())
                      .willRespondWith()
                      .headers(this.responseHeaders())
                      .status(200)
                      .toFragment();
    }

    /**
     * @param builder
     * @return
     */
    @Pact(provider = RUNTIME_OS_INFO_REST_PROVIDER, consumer = "RuntimeRestEndpoint")
    public PactFragment createOsInfoFragment(final PactDslWithProvider builder) {
        return builder.given("Describe the state the provider needs to be in for the pact test to be verified")
                      .uponReceiving("Description of the request that is expected to be received")
                      .path(BASE_URI + "/runtime/os-info")
                      .method("GET")
                      .headers(this.requestHeaders())
                      .willRespondWith()
                      .headers(this.responseHeaders())
                      .status(200)
                      .toFragment();
    }

    private Map<String, String> requestHeaders() {
        Map<String, String> requestHeaders = new HashMap<>();
        //
        // The request header is not set here. Because the necessary header informations are set by the resteasy client proxy framework.
        //
        requestHeaders.put("X-BOMC-AUTHORIZATION", "BOMC_USER");
        return Collections.unmodifiableMap(requestHeaders);
    }

    private Map<String, String> responseHeaders() {
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", RuntimeRestEndpoint.MEDIA_TYPE_JSON_V1);

        return Collections.unmodifiableMap(responseHeaders);
    }

    /**
     * <pre>
     * 	mvn clean install -Dtest=RuntimeConsumerContractPactTest#test01_getAvailableHeap_Pass
     * </pre>
     * @throws Exception
     */
    @Test
    @PactVerification(RUNTIME_AVAILABLE_HEAP_REST_PROVIDER)
    public void test01_getAvailableHeap_Pass() throws Exception {
        System.out.println("RuntimeConsumerContractPactTest#test01_getAvailableHeap_Pass");

        final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(100, TimeUnit.SECONDS)
                                                                 .socketTimeout(2, TimeUnit.SECONDS)
                                                                 .build();
        client.register(new ResteasyClientLogger(LOGGER, true));
        client.register(new AuthorizationTokenHeaderRequestFilter("BOMC_USER"));
        final ResteasyWebTarget webTarget = client.target(this.ruleAvailableHeap.getConfig()
                                                                           .url() + BASE_URI);
        final RuntimeRestEndpoint proxy = webTarget.proxy(RuntimeRestEndpoint.class);
        final Response response = proxy.availableHeap();

        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
    }

    /**
     * <pre>
     * 	mvn clean install -Dtest=RuntimeConsumerContractPactTest#test02_getNodeName_Pass
     * </pre>
     * @throws Exception
     */
    @Test
    @PactVerification(RUNTIME_NODENAME_REST_PROVIDER)
    public void test02_getNodeName_Pass() throws Exception {
        System.out.println("RuntimeConsumerContractPactTest#test02_getNodeName_Pass");

        final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(100, TimeUnit.SECONDS)
                                                                 .socketTimeout(2, TimeUnit.SECONDS)
                                                                 .build();
        client.register(new ResteasyClientLogger(LOGGER, true));
        client.register(new AuthorizationTokenHeaderRequestFilter("BOMC_USER"));
        final ResteasyWebTarget webTarget = client.target(this.ruleNodeName.getConfig()
                                                                      .url() + BASE_URI);
        final RuntimeRestEndpoint proxy = webTarget.proxy(RuntimeRestEndpoint.class);
        final Response response = proxy.nodeName();

        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
    }

    /**
     * <pre>
     * 	mvn clean install -Dtest=RuntimeConsumerContractPactTest#test03_getOsInfo_Pass
     * </pre>
     * @throws Exception
     */
    @Test
    @PactVerification(RUNTIME_OS_INFO_REST_PROVIDER)
    public void test03_getOsInfo_Pass() throws Exception {
        System.out.println("RuntimeConsumerContractPactTest#test03_getOsInfo_Pass");

        final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(100, TimeUnit.SECONDS)
                                                                 .socketTimeout(2, TimeUnit.SECONDS)
                                                                 .build();
        client.register(new ResteasyClientLogger(LOGGER, true));
        client.register(new AuthorizationTokenHeaderRequestFilter("BOMC_USER"));
        final ResteasyWebTarget webTarget = client.target(this.ruleOsInfo.getConfig()
                                                                    .url() + BASE_URI);
        final RuntimeRestEndpoint proxy = webTarget.proxy(RuntimeRestEndpoint.class);
        final Response response = proxy.osInfo();

        assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
    }
}
