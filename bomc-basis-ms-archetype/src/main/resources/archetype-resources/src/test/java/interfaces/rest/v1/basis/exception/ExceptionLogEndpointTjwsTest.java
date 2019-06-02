#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: ${symbol_dollar}Author: bomc ${symbol_dollar}
 *
 *  date: ${symbol_dollar}Date: ${symbol_dollar}
 *
 *  revision: ${symbol_dollar}Revision: ${symbol_dollar}
 *
 * </pre>
 */
package ${package}.interfaces.rest.v1.basis.exception;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import de.bomc.poc.exception.core.app.AppRuntimeException;

import ${package}.CategoryBasisUnitTest;
import ${package}.application.basis.log.ExceptionLogController;
import ${package}.application.basis.log.ExceptionLogEntityListFactory;
import ${package}.domain.shared.DomainObjectUtils;
import ${package}.interfaces.rest.PortFinder;
import ${package}.interfaces.rest.v1.basis.exception.ExceptionLogEndpointImpl;
import ${package}.interfaces.rest.v1.basis.exception.dto.ExceptionLogDTO;

/**
 * Tests the {@link ExceptionLogEndpointTjwsTest}.
 * 
 * <pre>
 *     mvn clean install -Dtest=ExceptionLogEndpointTjwsTest
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 07.12.2018
 */
@SuppressWarnings("deprecation")
@RunWith(MockitoJUnitRunner.class)
@Category(CategoryBasisUnitTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExceptionLogEndpointTjwsTest {

    private static final String LOG_PREFIX = "ExceptionLogEndpointTjwsTest${symbol_pound}";
    private static final Logger LOGGER = Logger.getLogger(ExceptionLogEndpointTjwsTest.class);
    private static int port;
    private static TJWSEmbeddedJaxrsServer server;
    @Mock
    private Logger logger;
    @Mock
    private ExceptionLogController exceptionLogControllerEJB;
    @InjectMocks
    private static final ExceptionLogEndpointImpl sut = new ExceptionLogEndpointImpl();
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    // _______________________________________________
    // Test data
    // -----------------------------------------------
    private static final String JSON_STRING = "[{\"shortErrorCodeDescription\":\"Describes an error\",\"category\":\"category\",\"responseStatus\":\"ACCEPTED\",\"exceptionUuid\":\"exceptionUuid0\",\"createDate\":\"04.11.2018\"},{\"shortErrorCodeDescription\":\"Describes an error\",\"category\":\"category\",\"responseStatus\":\"ACCEPTED\",\"exceptionUuid\":\"exceptionUuid1\",\"createDate\":\"04.11.2018\"}]";

    @Before
    public void init() {
        //
    }

    @BeforeClass
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void beforeClass() throws Exception {
        port = PortFinder.findPort();

        server = new TJWSEmbeddedJaxrsServer();
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
     *  mvn clean install -Parq-wildfly-remote -Dtest=ExceptionLogEndpointTjwsTest${symbol_pound}test010_readStoredExceptions_pass
     *
     * <b><code>test010_readStoredExceptions_pass</code>:</b><br>
     *  Tests the behavior reading the exceptions from db by the endpoint and mocked <code>ExceptionLogController</code>.
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *  - A list of <code>ExceptionLogDTO</code>s must be created. 
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The endpoint is invoked.
     *
     * <b>Postconditions:</b><br>
     *  A string is received.
     * </pre>
     * @throws IOException 
     */
    @Test
    public void test010_readStoredExceptions_pass() throws IOException {
        LOGGER.debug(LOG_PREFIX + "test010_readStoredExceptions_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        when(this.exceptionLogControllerEJB.readStoredExceptions()).thenReturn(JSON_STRING);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        final Response response = sut.getStoredExceptions();

        assertThat(response, notNullValue());
        assertThat(response.getStatus(), equalTo(Status.OK.getStatusCode()));

        final String jsonArray = (String) response.getEntity();
        assertThat(jsonArray, notNullValue());

        final ObjectMapper mapper = new ObjectMapper();
        // Register a deserializer for localDate mapping. 
        mapper.registerModule(new SimpleModule().addDeserializer(LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ofPattern(DomainObjectUtils.DATE_PATTERN))));
        
        // Transform json string to array.
        final ExceptionLogDTO[] exceptionLogDTOAsArray = (ExceptionLogDTO[]) mapper.readValue(jsonArray,
                ExceptionLogDTO[].class);
        
        // Do asserts.
        Arrays.stream(exceptionLogDTOAsArray).forEach(exceptionLogDTO -> {
            assertThat(exceptionLogDTO.getCategory(), equalTo(ExceptionLogEntityListFactory.CATEGORY));
            assertThat(exceptionLogDTO.getShortErrorCodeDescription(),
                    equalTo(ExceptionLogEntityListFactory.SHORT_ERROR_CODE_DESCRIPTION));
            assertThat(exceptionLogDTO.getCreateDate(), equalTo(ExceptionLogEntityListFactory.CREATE_DATE));
            assertThat(exceptionLogDTO.getResponseStatus(), equalTo(ExceptionLogEntityListFactory.RESPONSE_STATUS));
        });
    }

    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=ExceptionLogEndpointTjwsTest${symbol_pound}test020_readStoredExceptions_fail
     *
     * <b><code>test020_readStoredExceptions_fail</code>:</b><br>
     *  Tests the behavior reading the exceptions from db by the endpoint and mocked <code>ExceptionLogController</code>.
     *  The <code>ExceptionLogController</code> throws a <code>IOException</code>.
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed in Wildfly.
     *  - A list of <code>ExceptionLogDTO</code>s must be created. 
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The endpoint is invoked.
     *  - A IOException is thrown and catched by the endpoint.
     *  - The endpopint throws a AppRuntimeException up to the client. 
     *
     * <b>Postconditions:</b><br>
     *  A AppruntimeException is thrown.
     * </pre>
     * @throws AppruntimeException 
     */
    @Test
    public void test020_readStoredExceptions_fail() throws IOException {
        LOGGER.debug(LOG_PREFIX + "test020_readStoredExceptions_fail");

        this.thrown.expect(AppRuntimeException.class);
        
        // ___________________________________________
        // GIVEN
        // -------------------------------------------

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        when(this.exceptionLogControllerEJB.readStoredExceptions()).thenThrow(IOException.class);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        sut.getStoredExceptions();
    }
} // end class
