/**
 * Project: POC PaaS
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
package de.bomc.poc.hrm.interfaces;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
//import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
//import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import brave.Tracer;
import de.bomc.poc.hrm.AbstractBaseUnit;
import de.bomc.poc.hrm.application.UserService;
import de.bomc.poc.hrm.application.UserServiceImpl;
import de.bomc.poc.hrm.application.exception.AppErrorCodeEnum;
import de.bomc.poc.hrm.application.exception.AppRuntimeException;
import de.bomc.poc.hrm.application.log.http.RequestGetLoggingInterceptor;
import de.bomc.poc.hrm.application.log.http.RequestResponseLoggerImpl;
import de.bomc.poc.hrm.interfaces.mapper.UserDto;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * Tests the {@link VersionController} and creates the documentation.
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 21.10.2019
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(UserController.class)
@Import({ RequestGetLoggingInterceptor.class, RequestResponseLoggerImpl.class, UserServiceImpl.class })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserControllerTest extends AbstractBaseUnit {
	private static final String LOG_PREFIX = "UserControllerTest#";

	// _______________________________________________
	// JUnit rule annotation instructs Spring Rest Doc where to put the generated
	// documentation snippets.
	// -----------------------------------------------
	@Rule
	public JUnitRestDocumentation jUnitRestDocumentation = new JUnitRestDocumentation();

	// _______________________________________________
	// Constants
	// -----------------------------------------------
	private static final String JSON_PREFIX = "$.";

	// _______________________________________________
	// Member variables
	// -----------------------------------------------
	@Autowired
	private WebApplicationContext context;
	@Autowired
	private ObjectMapper objectMapper;

	private MockMvc mockMvc;

	private RestDocumentationResultHandler documentationResultHandler;

	// _______________________________________________
	// Mocks will then automatically be initialized with a mock instance of their
	// type.
	// -----------------------------------------------
	@MockBean
	private Tracer tracer;
	@MockBean
	private UserService userService;

	@Before
	public void setup() {

		this.documentationResultHandler = document("{class-name}/{method-name}", preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()));

		mockMvc = MockMvcBuilders.webAppContextSetup(context) //
				.apply(documentationConfiguration(this.jUnitRestDocumentation)) //
				.alwaysDo(this.documentationResultHandler) //
				.build();
	}

	@Test
	@Description("Handles the POST request for trying to create a user.")
	public void test010_createUser_pass() throws Exception {
		log.debug(LOG_PREFIX + "test010_createUser_pass");

		// GIVEN
		final UserDto userDto = new UserDto();
		userDto.setId(null);
		userDto.setUsername("test_username");

		// WHEN
		when(this.userService.createUser(any(UserDto.class))).then((Answer<UserDto>) invocationOnMock -> {
			if (invocationOnMock.getArguments().length > 0 && invocationOnMock.getArguments()[0] instanceof UserDto) {
				UserDto mockUserDto = (UserDto) invocationOnMock.getArguments()[0];
				mockUserDto.setId(42L);

				return mockUserDto;
			}
			return null;
		});

		// THEN
		this.mockMvc
				.perform(RestDocumentationRequestBuilders.post("/user")
						.content(this.objectMapper.writeValueAsString(userDto))
						.contentType(UserController.MEDIA_TYPE_JSON_V1).accept(UserController.MEDIA_TYPE_JSON_V1))
				.andDo(print()).andExpect(status().isOk()) //
				.andExpect(jsonPath(JSON_PREFIX + "id").value(42L)) //
				.andExpect(jsonPath(JSON_PREFIX + "username").value("test_username")).andDo(
						this.documentationResultHandler.document( //
								requestFields( //
										fieldWithPath("id") //
												.description("Unique identifier of the user."), //
										fieldWithPath("username") //
												.description("The given username of the user to be obtained.")),
								responseFields( //
										fieldWithPath("id") //
												.description("Unique identifier of the user."), //
										fieldWithPath("username") //
												.description("The given username of the user to be obtained.")) //
//						responseHeaders( //
//								headerWithName("X-B3-TraceId") //
//										.description("A trace-id for each incoming request"), //
//								headerWithName("X-B3-SpanId") //
//										.description("A -id for each incoming request"))));
						));

	}

	@Test
	@Description("Handles the POST request for trying to create a user. The invocation fails, a AppRuntimeException is thrown.")
	public void test015_createUser_fail() throws Exception {
		log.debug(LOG_PREFIX + "test015_createUser_fail");

		// GIVEN
		final UserDto userDto = new UserDto();
		userDto.setId(null);
		userDto.setUsername("test_username");

		final DataIntegrityViolationException dataIntegrityViolationException = new DataIntegrityViolationException(
				"test");
		final AppRuntimeException appRuntimeException = new AppRuntimeException(
				"There is already a user avaible in db [username=test]", dataIntegrityViolationException,
				AppErrorCodeEnum.JPA_PERSISTENCE_ENTITY_NOT_AVAILABLE_10401);

		// WHEN
		when(this.userService.createUser((any(UserDto.class)))).thenThrow(appRuntimeException);

		// THEN
		this.mockMvc
				.perform(RestDocumentationRequestBuilders.post("/user")
						.content(this.objectMapper.writeValueAsString(userDto))
						.contentType(UserController.MEDIA_TYPE_JSON_V1).accept(UserController.MEDIA_TYPE_JSON_V1))
				.andDo(print()).andExpect(status().isInternalServerError()) //
				.andExpect(jsonPath(JSON_PREFIX + "status").value("INTERNAL_SERVER_ERROR")) //
				.andExpect(jsonPath(JSON_PREFIX + "shortErrorCodeDescription")
						.value("There is already a user avaible in db [username=test]")) //
				.andExpect(jsonPath(JSON_PREFIX + "errorCode").value("JPA_PERSISTENCE_ENTITY_NOT_AVAILABLE_10401")) //
				.andDo(this.documentationResultHandler.document( //
						responseFields( //
								fieldWithPath("status") //
										.description("The http status of the response."), //
								fieldWithPath("shortErrorCodeDescription") //
										.description("A short description of the thrown errors."), //
								fieldWithPath("errorCode") //
										.description("The error code of the exception."), //
								fieldWithPath("uuid") //
										.description("A unique identifier of the error.")) //
//						responseHeaders( //
//								headerWithName("X-B3-TraceId") //
//										.description("A trace-id for each incoming request"), //
//								headerWithName("X-B3-SpanId") //
//										.description("A -id for each incoming request"))));
				));

	}

	@Test
	@Description("Handles the GET request for finding a user by the given id.")
	public void test020_findById_pass() throws Exception {
		log.debug(LOG_PREFIX + "test020_findById_pass");

		// GIVEN
		final UserDto userDto = new UserDto();
		userDto.setId(42L);
		userDto.setUsername("test_username");

		// WHEN
		when(this.userService.findById((any(Long.class)))).thenReturn(userDto);

		// THEN
		this.mockMvc.perform(RestDocumentationRequestBuilders.get("/user/{id}", 1L) //
				.accept(UserController.MEDIA_TYPE_JSON_V1)) //
				.andExpect(jsonPath(JSON_PREFIX + "id").value(42L)) //
				.andExpect(jsonPath(JSON_PREFIX + "username").value("test_username")).andDo(print())
				.andExpect(status().isOk()) //
				.andDo(this.documentationResultHandler.document( //
						pathParameters(parameterWithName("id") //
								.description("Identifier of the person to be obtained.")))) //
				.andDo(this.documentationResultHandler.document( //
						responseFields( //
								fieldWithPath("id") //
										.description("Unique identifier of the user."), //
								fieldWithPath("username") //
										.description("The given username of the user to be obtained."))// , //
//						responseHeaders( //
//								headerWithName("X-B3-TraceId") //
//										.description("A trace-id for each incoming request"), //
//								headerWithName("X-B3-SpanId") //
//										.description("A -id for each incoming request"))));
				));

	}
}
