/**
 * Project: MY_POC
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
 * Copyright (c): BOMC, 2015
 */
package de.bomc.poc.test.auth.unit.usermanagement;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import de.bomc.poc.api.mapper.transfer.RoleDTO;
import de.bomc.poc.auth.business.UsermanagementLocal;
import de.bomc.poc.auth.rest.endpoint.v2.usermanagement.AuthUserManagementRestEndpoint;
import de.bomc.poc.auth.rest.endpoint.v2.usermanagement.impl.AuthUserManagementRestEndpointImpl;
import de.bomc.poc.rest.logger.ResteasyClientLogger;

/**
 * Tests the facade for user management operations with resteasy and tjws.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
@RunWith(MockitoJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UsermanagementResteasyEndpointV2TjwsMockTest {
	private static final String LOG_PREFIX = "resteasy#tjws#v2#";
	private static final Logger LOGGER = Logger.getLogger(UsermanagementResteasyEndpointV2TjwsMockTest.class);
	private static final String BASE_URI = "http://localhost:";
	private static int port;
	private static TJWSEmbeddedJaxrsServer server;
	@Mock
    private UsermanagementLocal userManagementEJB;
	@Mock
	private Logger logger;
	@InjectMocks
	private static final AuthUserManagementRestEndpointImpl sut = new AuthUserManagementRestEndpointImpl();
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@BeforeClass
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void beforeClass() throws Exception {
		port = PortFinder.findPort();
		
		server = new TJWSEmbeddedJaxrsServer();
		server.setPort(port);
		server.getDeployment().setResources((List)Collections.singletonList(sut));
		// NOTE: All providers introduced by the Application.class will be invoked and has to be mocked.
		// In this case the AuthorizationTokenFilter has to be mocked and the injected Logger inside the AuthorizationTokenFilter has to be mocked too.
		// Problem, the logger inside the AuthorizationTokenFilter is always null, how to mock this?
//		server.getDeployment().setApplicationClass(de.bomc.poc.rest.management.application.ManagementRESTApplication.class.getCanonicalName());
		
//		server.getDeployment().getActualProviderClasses().addAll((List<Class<?>>) Arrays
//				.asList(DataTransferMessageBodyReader.class, DataTransferMessageBodyWriter.class));
		
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
	 * mvn clean install -Dtest=UsermanagementResteasyEndpointV2TjwsMockTest#test01_findAllRolesByUsername_Pass
	 */
	@Test
	public void test01_findAllRolesByUsername_Pass() {
		LOGGER.debug(LOG_PREFIX + "UsermanagementResteasyEndpointV2TjwsMockTest#test01_findAllRolesByUsername_Pass");
		
		final String username = "dummy-user";

		final RoleDTO roleDTO = new RoleDTO("System-User", "system-user description");

		when(this.userManagementEJB.findAllRolesByUsername(username)).thenReturn(Collections.singletonList(roleDTO));

		// Create clients.
		final ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
		final ResteasyWebTarget webTarget = resteasyClient.target(UsermanagementResteasyEndpointV2TjwsMockTest.BASE_URI + port)
				.register(JacksonJaxbJsonProvider.class).register(JacksonJsonProvider.class).register(new ResteasyClientLogger(LOGGER, true));
		final AuthUserManagementRestEndpoint proxy = webTarget.proxy(AuthUserManagementRestEndpoint.class);
		final Response response = proxy.findAllRolesByUsername(username);
	
		assertThat(response, notNullValue());
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			LOGGER.debug(LOG_PREFIX + "UsermanagementResteasyEndpointV2TjwsMockTest#test01_findAllRolesByUsername_Pass -");

			final List<RoleDTO> responseRoleDTOList = response.readEntity(new GenericType<List<RoleDTO>>() {});

			System.out.println(responseRoleDTOList.iterator().next().toString());
		}
	}
}
