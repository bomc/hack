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
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.rest.arq.filter;

import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bomc.poc.api.generic.Parameter;
import de.bomc.poc.api.generic.transfer.request.RequestObjectDTO;
import de.bomc.poc.api.generic.types.StringType;
import de.bomc.poc.api.jaxb.GenericResponseObjectDTOCollectionMapper;
import de.bomc.poc.api.jaxb.JaxbGenMapAdapter;
import de.bomc.poc.api.mapper.transfer.RoleDTO;
import de.bomc.poc.auth.business.UsermanagementLocal;
import de.bomc.poc.auth.rest.application.JaxRsActivator;
import de.bomc.poc.config.EnvConfigKeys;
import de.bomc.poc.config.qualifier.EnvConfigQualifier;
import de.bomc.poc.exception.app.AppAuthRuntimeException;
import de.bomc.poc.exception.app.AppInvalidPasswordException;
import de.bomc.poc.exception.handling.ApiError;
import de.bomc.poc.exception.interceptor.ApiExceptionInterceptor;
import de.bomc.poc.exception.qualifier.ApiExceptionQualifier;
import de.bomc.poc.exception.web.WebAuthRuntimeException;
import de.bomc.poc.logger.producer.LoggerProducer;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.rest.client.RestClientBuilder;
import de.bomc.poc.rest.client.config.RestClientConfigKeys;
import de.bomc.poc.rest.client.config.producer.RestClientConfigProducer;
import de.bomc.poc.rest.client.config.qualifier.RestClientConfigQualifier;
import de.bomc.poc.rest.ext.ListResponseObjDtoMessageBodyReader;
import de.bomc.poc.rest.ext.ListResponseObjDtoMessageBodyWriter;
import de.bomc.poc.rest.ext.RequestObjDtoMessageBodyReader;
import de.bomc.poc.rest.ext.RequestObjDtoMessageBodyWriter;
import de.bomc.poc.rest.ext.ResponseObjDtoMessageBodyReader;
import de.bomc.poc.rest.ext.ResponseObjDtoMessageBodyWriter;
import de.bomc.poc.rest.filter.authorization.AuthorizationTokenFilter;
import de.bomc.poc.rest.filter.authorization.AuthorizationTokenHeaderRequestFilter;
import de.bomc.poc.rest.filter.uid.UIDHeaderRequestFilter;
import de.bomc.poc.rest.logger.ResteasyClientLogger;
import de.bomc.poc.rest.logger.ResteasyServerLogger;
import de.bomc.poc.test.GlobalArqTestProperties;
import de.bomc.poc.test.rest.arq.ArquillianRestBase;
import de.bomc.poc.test.rest.arq.mock.UsermanagementMockEJB;
import de.bomc.poc.validation.ConstraintViolationMapper;
import de.bomc.poc.zookeeper.config.ZookeeperConfigAccessor;

/**
 * Tests the MDC filter.
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
@RunWith(Arquillian.class)
public class MDCFilterTestIT extends ArquillianRestBase {
	public static final String WEB_ARCHIVE_NAME = "poc-web-mdc";
	/** Logger */
	@Inject
	@LoggerQualifier
	private Logger logger;
	private static final Logger LOGGER = Logger.getLogger(MDCFilterTestIT.class.getName());
	private static final String LOG_PREFIX = "MDCFilterTestIT#";
	private static ResteasyWebTarget webTarget;

	// NOTE:
	// __________________________________________________________________
	// 'testable = false', means all the tests are running outside of the
	// container.
	@Deployment(testable = false, order = 1)
	public static WebArchive createTestArchive() {
        final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
		webArchive.addClass(MDCFilterTestIT.class)
				  .addClasses(LoggerProducer.class, LoggerQualifier.class)
				  .addClasses(AppAuthRuntimeException.class, AppInvalidPasswordException.class)
				  .addClass(JaxRsActivator.class)
				  .addPackages(true, UIDHeaderRequestFilter.class.getPackage())
				  .addPackages(true, AuthorizationTokenFilter.class.getPackage())
				  .addClasses(RequestObjDtoMessageBodyReader.class, RequestObjDtoMessageBodyWriter.class)
				  .addClasses(ResponseObjDtoMessageBodyReader.class, ResponseObjDtoMessageBodyWriter.class)
				  .addClasses(ListResponseObjDtoMessageBodyWriter.class, ListResponseObjDtoMessageBodyReader.class, GenericResponseObjectDTOCollectionMapper.class)
				  .addClasses(de.bomc.poc.auth.rest.endpoint.v1.usermanagement.AuthUserManagementRestEndpoint.class, de.bomc.poc.auth.rest.endpoint.v1.usermanagement.impl.AuthUserManagementRestEndpointImpl.class)
				  .addClasses(de.bomc.poc.auth.rest.endpoint.v2.usermanagement.AuthUserManagementRestEndpoint.class, de.bomc.poc.auth.rest.endpoint.v2.usermanagement.impl.AuthUserManagementRestEndpointImpl.class)
				  .addClasses(UsermanagementLocal.class, UsermanagementMockEJB.class, ResteasyClientLogger.class, ResteasyServerLogger.class)
				  .addClasses(RestClientBuilder.class, RestClientConfigKeys.class, RestClientConfigProducer.class, RestClientConfigQualifier.class)
				  .addPackages(true, Parameter.class.getPackage().getName())
				  .addPackages(true, RestClientBuilder.class.getPackage().getName())
				  .addPackages(true, JaxbGenMapAdapter.class.getPackage().getName())
				  .addPackage(RoleDTO.class.getPackage().getName())

				  .addPackages(true, ConstraintViolationMapper.class.getPackage()
																	.getName())

				  .addPackages(true, ApiError.class.getPackage().getName())
				  .addPackages(true, ApiExceptionInterceptor.class.getPackage().getName())
				  .addPackages(true, ApiExceptionQualifier.class.getPackage().getName())
				  .addPackages(true, WebAuthRuntimeException.class.getPackage().getName())
				  .addPackages(true, ConstraintViolationMapper.class.getPackage().getName())
				  
				  // Dependencies for zookeeper config reading, used in RestClientBuilder.
				  .addClasses(ZookeeperConfigAccessorMockImpl.class, ZookeeperConfigAccessor.class)
				  .addClasses(EnvConfigQualifier.class, EnvConfigKeys.class, EnvConfigFilterMockProducer.class, GlobalArqTestProperties.class)

				  .addAsWebInfResource(getBeansXml(), "beans.xml")
				  .addAsWebInfResource("test-jboss-deployment-structure.xml", "jboss-deployment-structure.xml");

		

		// Add dependencies
  		final MavenResolverSystem resolver = Maven.resolver();

  		// Shared module library.
  		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
                                    .resolve("org.apache.curator:curator-framework:jar:?")
                                    .withMavenCentralRepo(false)
                                    .withTransitivity()
                                    .asFile());

		LOGGER.debug(LOG_PREFIX + "archiveContent: " + webArchive.toString(true));

		return webArchive;
	}

	@Before
	public void init() {
		LOGGER.info(LOG_PREFIX + "init");

		final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS)
																 .socketTimeout(10, TimeUnit.SECONDS)
																 .register(new ResteasyClientLogger(this.logger, true))
																 .register(ListResponseObjDtoMessageBodyReader.class)
																 .register(ListResponseObjDtoMessageBodyWriter.class)
																 .register(RequestObjDtoMessageBodyReader.class)
																 .register(RequestObjDtoMessageBodyWriter.class)
																 .register(ResponseObjDtoMessageBodyReader.class)
																 .register(ResponseObjDtoMessageBodyWriter.class)
																 .register(new AuthorizationTokenHeaderRequestFilter("test_token"))
																 .build();

		webTarget = client.target(buildBaseUrl(WEB_ARCHIVE_NAME) + "/" + JaxRsActivator.APPLICATION_PATH);
	}

	/**
	 * <pre>
	 * NOTE:
	 * This client is running outside the container. The test reads the jboss.bind.address and the port-offset from 
	 * 'systemProperties', defined in pom.xml. In this case the systemProperties are DIFFERENT to a InContainer test.
	 * 
	 * In the maven-surefire-plugin following must be set, to run the test successfully.
	 * 
	 *	<configuration>
	 *		...
	 * 		<systemProperties>
	 * 			<arquillian.launch>wildfly-managed</arquillian.launch>
	 * 				...
	 * 		<!-- These parameters are necessary for arquillian tests in 'RunAsClient' mode. -->
	 * 		<jboss.bind.address>${global.arq.wildfly.management.address}</jboss.bind.address>
	 * 		<jboss.socket.binding.port-offset>${global.arq.wildfly.port.offset}</jboss.socket.binding.port-offset>
	 * 
	 *   
	 *	mvn clean install -Parq-wildfly-remote -Dtest=MDCFilterTestIT#test01_MDC_Pass
	 * </pre>
	 */
	@Test
	@RunAsClient
	@InSequence(1)
	@SuppressWarnings("deprecation")
	public void test01_MDC_Pass() {
		LOGGER.debug(LOG_PREFIX + "MDCFilterTestIT#test01_MDC_Pass");

		Response response = null;

		try {
			// Create the request object.
			final Parameter p1 = new Parameter(("username"), new StringType("dummy_user"));
			final RequestObjectDTO requestObjectDTO = RequestObjectDTO.with(p1);

			// Start REST request...
			final de.bomc.poc.auth.rest.endpoint.v1.usermanagement.AuthUserManagementRestEndpoint proxy = webTarget.proxy(de.bomc.poc.auth.rest.endpoint.v1.usermanagement.AuthUserManagementRestEndpoint.class);
			response = proxy.findAllRolesByUsername(requestObjectDTO);

			if (response == null || response.getStatus() == Response.Status.OK.getStatusCode()) {
				fail(LOG_PREFIX + "test01_MDC_Pass - failed, response status is != 200");
			}
		} finally {
			if (response != null) {
				response.close();
			}
		}

		// Asserts see logfile.
	}
}

