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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
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
import de.bomc.poc.auth.rest.endpoint.v2.usermanagement.impl.AuthUserManagementRestEndpointImpl;

/**
 * Tests the endpoint v2 for user management operations.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
@RunWith(MockitoJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UsermanagementRestEndpointV2TjwsMockTest {
	private static final String LOG_PREFIX = "#rest#tjws#v2#";
	private static final Logger LOGGER = Logger.getLogger(UsermanagementRestEndpointV2TjwsMockTest.class);
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
	
	@Test
	@SuppressWarnings("unchecked")
	public void test01_findAllRolesByUsername_Pass() {
		LOGGER.debug(LOG_PREFIX + "UsermanagementRestEndpointV2TjwsMockTest#test01_findAllRolesByUsername_Pass");

		final String username = "dummy-user";

		final RoleDTO roleDTO = new RoleDTO("System-User", "system-user description");

		when(this.userManagementEJB.findAllRolesByUsername(username)).thenReturn(Arrays.asList(roleDTO));

		final Response response = sut.findAllRolesByUsername(username);

		assertThat(response, notNullValue());
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			LOGGER.debug(LOG_PREFIX + "UsermanagementRestEndpointV2TjwsMockTest#test01_findAllRolesByUsername_Pass -");

			final List<RoleDTO> responseObjectDTOList = (List<RoleDTO>)response.getEntity();

			System.out.println(responseObjectDTOList.iterator().next().toString());
		}
	}
}
