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
package de.bomc.poc.test.auth.arq.usermanagement;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.performance.annotation.Performance;
import org.jboss.arquillian.performance.annotation.PerformanceTest;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bomc.poc.api.generic.Parameter;
import de.bomc.poc.api.generic.transfer.request.RequestObjectDTO;
import de.bomc.poc.api.generic.types.StringType;
import de.bomc.poc.api.http.CustomHttpResponseCode;
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
import de.bomc.poc.rest.ext.ListResponseObjDtoMessageBodyReader;
import de.bomc.poc.rest.ext.ListResponseObjDtoMessageBodyWriter;
import de.bomc.poc.rest.ext.RequestObjDtoMessageBodyReader;
import de.bomc.poc.rest.ext.RequestObjDtoMessageBodyWriter;
import de.bomc.poc.rest.ext.ResponseObjDtoMessageBodyReader;
import de.bomc.poc.rest.ext.ResponseObjDtoMessageBodyWriter;
import de.bomc.poc.rest.filter.uid.UIDHeaderRequestFilter;
import de.bomc.poc.rest.logger.ResteasyClientLogger;
import de.bomc.poc.rest.logger.ResteasyServerLogger;
import de.bomc.poc.test.GlobalArqTestProperties;
import de.bomc.poc.test.auth.arq.ArquillianAuthBase;
import de.bomc.poc.test.auth.arq.TransferDTOMockData;
import de.bomc.poc.test.auth.arq.usermanagement.mock.EnvConfigUsermanagementMockProducer;
import de.bomc.poc.test.auth.arq.usermanagement.mock.UsermanagementMockEJB;
import de.bomc.poc.test.auth.arq.usermanagement.mock.ZookeeperConfigAccessorMockImpl;
import de.bomc.poc.validation.ConstraintViolationEntry;
import de.bomc.poc.validation.ConstraintViolationMapper;
import de.bomc.poc.zookeeper.config.ZookeeperConfigAccessor;

/**
 * <pre>
 *  Tests the interface of {@link de.bomc.poc.auth.rest.endpoint.v1.usermanagement.AuthUserManagementRestEndpoint} v1.
 *  This means v1 is invoking v2 and tha the business service is invoking.
 *
 *  mvn clean install -Parq-wildfly-remote -Dtest=UsermanagementRestEndpointV1TestIT
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@PerformanceTest(resultsThreshold = 10)
@RunWith(Arquillian.class)
public class UsermanagementRestEndpointV1TestIT extends ArquillianAuthBase {
    public static final String WEB_ARCHIVE_NAME = "auth-usermanagement-v1";
    public static final String LOG_PREFIX = "UsermanagementRestEndpointV1TestIT#";
    /** Logger */
    @Inject
    @LoggerQualifier
    private Logger logger;
    private static ResteasyWebTarget webTarget;

    // 'testable = true', means all the tests are running inside the container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
        final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
        webArchive.addClass(UsermanagementRestEndpointV1TestIT.class);
        webArchive.addClasses(LoggerProducer.class, LoggerQualifier.class);
        webArchive.addClasses(AppAuthRuntimeException.class, AppInvalidPasswordException.class);
        webArchive.addPackage(UIDHeaderRequestFilter.class.getPackage());
        webArchive.addClasses(de.bomc.poc.auth.rest.endpoint.v1.usermanagement.AuthUserManagementRestEndpoint.class,
            de.bomc.poc.auth.rest.endpoint.v1.usermanagement.impl.AuthUserManagementRestEndpointImpl.class);
        webArchive.addClasses(de.bomc.poc.auth.rest.endpoint.v2.usermanagement.AuthUserManagementRestEndpoint.class, de.bomc.poc.auth.rest.endpoint.v2.usermanagement.impl.AuthUserManagementRestEndpointImpl.class);
        webArchive.addClasses(JaxRsActivator.class);
        webArchive.addClasses(ResteasyClientLogger.class, ResteasyServerLogger.class);
        webArchive.addClasses(RequestObjDtoMessageBodyReader.class, RequestObjDtoMessageBodyWriter.class);
        webArchive.addClasses(ResponseObjDtoMessageBodyReader.class, ResponseObjDtoMessageBodyWriter.class);
        webArchive.addClasses(ListResponseObjDtoMessageBodyWriter.class, ListResponseObjDtoMessageBodyReader.class, GenericResponseObjectDTOCollectionMapper.class);
        webArchive.addClasses(UsermanagementMockEJB.class, UsermanagementLocal.class, TransferDTOMockData.class);
        webArchive.addClasses(EnvConfigQualifier.class, EnvConfigKeys.class, EnvConfigUsermanagementMockProducer.class, GlobalArqTestProperties.class);
        
        webArchive.addPackages(true, ApiError.class.getPackage().getName());
        webArchive.addPackages(true, ApiExceptionInterceptor.class.getPackage().getName());
        webArchive.addPackages(true, ApiExceptionQualifier.class.getPackage().getName());
        webArchive.addPackages(true, WebAuthRuntimeException.class.getPackage().getName());
        webArchive.addPackages(true, ConstraintViolationMapper.class.getPackage().getName());

        webArchive.addPackages(true, Parameter.class.getPackage().getName()); 
        webArchive.addPackages(true, RestClientBuilder.class.getPackage().getName());
        webArchive.addPackages(true, JaxbGenMapAdapter.class.getPackage().getName());
        webArchive.addPackage(RoleDTO.class.getPackage().getName()); 

        // Mock for using ZookeeperConfigAccessor in RestClientBuilder.
		webArchive.addClasses(ZookeeperConfigAccessorMockImpl.class, ZookeeperConfigAccessor.class);
		
        webArchive.addAsResource("ValidationMessages.properties");
        webArchive.addAsResource("ValidationMessages_en.properties");

        // Add dependencies
        final MavenResolverSystem resolver = Maven.resolver();

        // shared module library

        webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml")
                                          .resolve("org.apache.curator:curator-framework:jar:?")
//                                          .withMavenCentralRepo(false)
                                          .withTransitivity()
                                          .asFile());
        
        System.out.println(LOG_PREFIX + "createDeployment: " + webArchive.toString(true));

        return webArchive;
    }

    /**
     * Setup.
     * @throws MalformedURLException
     */
    @Before
    public void setupClass() throws MalformedURLException {
        // Create rest client with Resteasy Client Framework.
        final ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS)
                                                                 .socketTimeout(10, TimeUnit.SECONDS)
                                                                 .register(new ResteasyClientLogger(this.logger, true))
                                                                 .register(ListResponseObjDtoMessageBodyReader.class)
                                                                 .register(ListResponseObjDtoMessageBodyWriter.class)
                                                                 .register(RequestObjDtoMessageBodyReader.class)
                                                                 .register(RequestObjDtoMessageBodyWriter.class)
                                                                 .register(ResponseObjDtoMessageBodyReader.class)
                                                                 .register(ResponseObjDtoMessageBodyWriter.class)
                                                                 .build();

        webTarget = client.target(buildBaseUrl(WEB_ARCHIVE_NAME) + "/" + JaxRsActivator.APPLICATION_PATH);
    }

    /**
     * Test reading a <code>User</code> by username.
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=UsermanagementRestEndpointV1TestIT#test01_findAllRolesByUsername_v1_Pass
     * </pre>
     * @throws Exception
     */
	@Test
    @InSequence(1)
    @Performance(time = 1500)
	@SuppressWarnings("deprecation")
    public void test01_findAllRolesByUsername_v1_Pass() {
        this.logger.info(LOG_PREFIX + "test01_findAllRolesByUsername_v1_Pass [uri=" + webTarget.getUri() + "]");

        Response response = null;

        try {
            // Create the request object.
            final Parameter p1 = new Parameter(("username"), new StringType("dummy_user"));
            final RequestObjectDTO requestObjectDTO = RequestObjectDTO.with(p1);

            // Start REST request...
            final de.bomc.poc.auth.rest.endpoint.v1.usermanagement.AuthUserManagementRestEndpoint proxy =
                webTarget.proxy(de.bomc.poc.auth.rest.endpoint.v1.usermanagement.AuthUserManagementRestEndpoint.class);
            response = proxy.findAllRolesByUsername(requestObjectDTO);

            assertThat(response, Matchers.notNullValue());
            assertThat(response.getStatus(), is(equalTo(CustomHttpResponseCode.DEPRECATED_API)));

            if (response != null && response.getStatus() == CustomHttpResponseCode.DEPRECATED_API) {
                // Read response.
                final GenericResponseObjectDTOCollectionMapper genericResponseObjectDTOCollectionMapper = response.readEntity(GenericResponseObjectDTOCollectionMapper.class);
                assertThat(genericResponseObjectDTOCollectionMapper, notNullValue());

                assertThat(genericResponseObjectDTOCollectionMapper.getResponseObjectDTOList().size(), greaterThan(1));
                genericResponseObjectDTOCollectionMapper.getResponseObjectDTOList().forEach(System.out::println);
            } else {
                fail(LOG_PREFIX + "test01_findAllRolesByUsername_v1_Pass - failed, response code is != 200");
            }
        } catch(Exception ex) {
        	this.logger.error(LOG_PREFIX + "test01_findAllRolesByUsername_v1_Pass - failed! ", ex);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * Test bean validation for {@link de.bomc.poc.validation.constraints.FieldsMatchToRequestObjectDTO} constraint.
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=UsermanagementRestEndpointV1TestIT#test10_findAllRolesByWrongUsernameConstraint_v1_validation_Pass
     * </pre>
     * @throws Exception
     */
	@Test
    @InSequence(10)
    @Performance(time = 1500)
	@SuppressWarnings("deprecation")
    public void test10_findAllRolesByWrongUsernameConstraint_v1_validation_Pass() {
        this.logger.info(LOG_PREFIX + "test10_findAllRolesByWrongUsernameConstraint_v1_validation_Pass [uri=" + webTarget.getUri() + "]");

        final String wrongParameterName = "username_wrong_param";

        Response response = null;

        try {
            // Create the request object.
            // Set a wrong parameter name 'username_validation', expected is 'username'.
            final Parameter p1 = new Parameter((wrongParameterName), new StringType("dummy_user"));
            final RequestObjectDTO requestObjectDTO = RequestObjectDTO.with(p1);

            // Start REST request...
            final de.bomc.poc.auth.rest.endpoint.v1.usermanagement.AuthUserManagementRestEndpoint proxy =
                webTarget.proxy(de.bomc.poc.auth.rest.endpoint.v1.usermanagement.AuthUserManagementRestEndpoint.class);
            response = proxy.findAllRolesByUsername(requestObjectDTO);
            assertThat(response.getStatus(), is(equalTo(Response.Status.PRECONDITION_FAILED.getStatusCode())));

            if (response != null && response.getStatus() == Response.Status.PRECONDITION_FAILED.getStatusCode()) {
                // Read response.
                final List<ConstraintViolationEntry> constraintViolationEntryList = response.readEntity(new GenericType<List<ConstraintViolationEntry>>() {});

                assertThat(constraintViolationEntryList, notNullValue());
                assertThat(constraintViolationEntryList.size(), is(equalTo(1)));
                assertThat(true, is(equalTo(constraintViolationEntryList.iterator().next().getWrongValue().contains(wrongParameterName))));

                constraintViolationEntryList.forEach(System.out::println);
            } else {
                fail(LOG_PREFIX + "test10_findAllRolesByWrongUsernameConstraint_v1_validation_Pass - failed, response code is != 200");
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}
