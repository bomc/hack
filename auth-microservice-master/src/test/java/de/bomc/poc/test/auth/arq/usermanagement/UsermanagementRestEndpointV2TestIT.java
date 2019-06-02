package de.bomc.poc.test.auth.arq.usermanagement;

import de.bomc.poc.api.mapper.transfer.GrantDTO;
import de.bomc.poc.api.mapper.transfer.RoleDTO;
import de.bomc.poc.auth.business.UsermanagementLocal;
import de.bomc.poc.auth.rest.application.JaxRsActivator;
import de.bomc.poc.auth.rest.endpoint.v2.usermanagement.AuthUserManagementRestEndpoint;
import de.bomc.poc.auth.rest.endpoint.v2.usermanagement.impl.AuthUserManagementRestEndpointImpl;
import de.bomc.poc.exception.handling.ApiError;
import de.bomc.poc.exception.interceptor.ApiExceptionInterceptor;
import de.bomc.poc.exception.qualifier.ApiExceptionQualifier;
import de.bomc.poc.exception.web.WebAuthRuntimeException;
import de.bomc.poc.logger.producer.LoggerProducer;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.rest.logger.ResteasyClientLogger;
import de.bomc.poc.rest.logger.ResteasyServerLogger;
import de.bomc.poc.test.auth.arq.ArquillianAuthBase;
import de.bomc.poc.test.auth.arq.TransferDTOMockData;
import de.bomc.poc.test.auth.arq.usermanagement.mock.UsermanagementMockEJB;
import de.bomc.poc.validation.ConstraintViolationMapper;
import org.apache.log4j.Logger;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests the {@link AuthUserManagementRestEndpoint}.
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=UsermanagementRestEndpointV2TestIT
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@PerformanceTest(resultsThreshold = 10)
@RunWith(Arquillian.class)
public class UsermanagementRestEndpointV2TestIT extends ArquillianAuthBase {

    private static final String WEB_ARCHIVE_NAME = "auth-usermanagement-v2";
    private static ResteasyWebTarget webTarget;
    @Inject
    @LoggerQualifier
    private Logger logger;

    // 'testable = true', means all the tests are running inside the container.
    @Deployment(testable = true)
    public static Archive<?> createDeployment() {
		final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
		webArchive.addClass(UsermanagementRestEndpointV2TestIT.class);
		webArchive.addClasses(LoggerProducer.class, LoggerQualifier.class);
		webArchive.addClasses(AuthUserManagementRestEndpoint.class, AuthUserManagementRestEndpointImpl.class);
		webArchive.addClasses(JaxRsActivator.class);
		webArchive.addClasses(ResteasyClientLogger.class, ResteasyServerLogger.class);
		webArchive.addClasses(UsermanagementMockEJB.class, UsermanagementLocal.class, TransferDTOMockData.class);
		webArchive.addPackages(true, "de.bomc.poc.api"); // TODO: Use package import here.

        webArchive.addPackages(true, ApiError.class.getPackage().getName());
        webArchive.addPackages(true, ApiExceptionInterceptor.class.getPackage().getName());
        webArchive.addPackages(true, ApiExceptionQualifier.class.getPackage().getName());
        webArchive.addPackages(true, WebAuthRuntimeException.class.getPackage().getName());
        webArchive.addPackages(true, ConstraintViolationMapper.class.getPackage().getName());

        System.out.println("UsermanagementRestEndpointV2TestIT#createDeployment: " + webArchive.toString(true));

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
                                                 				 .register(org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider.class)
                                                 				 .register(org.codehaus.jackson.jaxrs.JacksonJsonProvider.class)
                                                 				 .build();

        webTarget = client.target(buildBaseUrl(WEB_ARCHIVE_NAME) + "/" + JaxRsActivator.APPLICATION_PATH);
    }

    /**
     * Test reading all roles from a <code>User</code> by username via v2 REST endpoint
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=UsermanagementRestEndpointV2TestIT#test01_findAllRolesByUsername_v2_Pass
     * </pre>
     * @throws Exception
     */
	@Test
    @InSequence(1)
	@SuppressWarnings("unchecked")
    @Performance(time = 1000)
    public void test01_findAllRolesByUsername_v2_Pass() {
        this.logger.debug("UsermanagementRestEndpointV2TestIT#test01_findAllRolesByUsername_v2_Pass");

        Response response = null;

        try {
            // Start REST request...
            final AuthUserManagementRestEndpoint proxy = webTarget.proxy(AuthUserManagementRestEndpoint.class);
            response = proxy.findAllRolesByUsername("dummy_user");

            // Check and get response.
            if (response != null && response.getStatus() == Response.Status.OK.getStatusCode()) {
                // Read response.
                final List<RoleDTO> roleDTOList = response.readEntity(new GenericType<List<RoleDTO>>() {});

                assertThat(roleDTOList.size(), is(equalTo(2)));
                roleDTOList.forEach(roleDTO -> {
                    final List<GrantDTO> grantDTOList = roleDTO.getGrantDTOList();
                    assertThat(grantDTOList.size(), anyOf(is(1), anyOf(is(3))));
                });
            } else {
                fail("UsermanagementRestEndpointV2TestIT#test01_findAllRolesByUsername_v2_Pass - failed, responseCode != 200");
            }
        } catch (Exception ex) {
            this.logger.error("UsermanagementRestEndpointV2TestIT#test01_findAllRolesByUsername_v2_Pass - failed! ", ex);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}
