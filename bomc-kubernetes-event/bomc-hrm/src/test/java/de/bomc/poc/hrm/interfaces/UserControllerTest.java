/**
 * Project: hrm
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
import org.junit.jupiter.api.DisplayName;
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
import de.bomc.poc.hrm.GitConfig;
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
 * Tests the {@link UserController} and creates the documentation.
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 21.10.2019
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(UserController.class) // Cover integration tests with the web layer.
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
	@MockBean
	private GitConfig gitConfig;
	
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
	@DisplayName("UserControllerTest#test010_createUser_pass")
	@Description("Handles the POST request of the UserController to create a user. The test passes.")
	public void test010_createUser_pass() throws Exception {
		log.debug(LOG_PREFIX + "test010_createUser_pass");

		// GIVEN
		final UserDto userDto = createUserDto();

		// WHEN
		when(this.userService.createUser(any(UserDto.class))).then((Answer<UserDto>) invocationOnMock -> {
			if (invocationOnMock.getArguments().length > 0 && invocationOnMock.getArguments()[0] instanceof UserDto) {
				UserDto mockUserDto = (UserDto) invocationOnMock.getArguments()[0];
				mockUserDto.setId(USER_ID);

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
		        .andExpect(jsonPath(JSON_PREFIX + "username").value(USER_USER_NAME))
		        .andDo(this.documentationResultHandler.document( //
		                requestFields( //
		                        fieldWithPath("id") //
		                                .description("A unique identifier of the user."), //
		                        fieldWithPath("username") //
		                                .description("The username you enter is represented by an email address.."),
		                        fieldWithPath("password") //
		                                .description("The given password."),
		                        fieldWithPath("fullname") //
		                                .description("The entered full name, consisting of first and last name."),
		                        fieldWithPath("comment") //
		                                .description("The given comment."),
		                        fieldWithPath("phoneNo") //
		                                .description("The given phoneNo."),
		                        fieldWithPath("image") //
		                                .description("The given image."),
		                        fieldWithPath("sex") //
		                                .description("The given sex.")),
		                responseFields( //
		                        fieldWithPath("id") //
		                                .description("A unique identifier of the user."), //
		                        fieldWithPath("username") //
		                                .description("The username you enter is represented by an email address.."),
		                        fieldWithPath("password") //
		                                .description("The given password."),
		                        fieldWithPath("fullname") //
		                                .description("The entered full name, consisting of first and last name."),
		                        fieldWithPath("comment") //
		                                .description("The given comment."),
		                        fieldWithPath("phoneNo") //
		                                .description("The given phoneNo."),
		                        fieldWithPath("image") //
		                                .description("The given image."),
		                        fieldWithPath("sex") //
		                                .description("The given sex.")) //
//						responseHeaders( //
//								headerWithName("X-B3-TraceId") //
//										.description("A trace-id for each incoming request"), //
//								headerWithName("X-B3-SpanId") //
//										.description("A -id for each incoming request"))));
				));

	}

	@Test
	@DisplayName("UserControllerTest#test015_createUser_fail")
	@Description("Handles the POST request for trying to create a user. A AppRuntimeException is expected, the test fails.")
	public void test015_createUser_fail() throws Exception {
		log.debug(LOG_PREFIX + "test015_createUser_fail");

		final String EX_ERR_MSG = "There is already a user avaible in db [username=test]";

		// GIVEN
		final UserDto userDto = createUserDto();

		final DataIntegrityViolationException dataIntegrityViolationException = new DataIntegrityViolationException(
		        "UserControllerTest#test015_createUser_fail - errMsg");
		final AppRuntimeException appRuntimeException = new AppRuntimeException(EX_ERR_MSG,
		        dataIntegrityViolationException, AppErrorCodeEnum.JPA_PERSISTENCE_ENTITY_NOT_AVAILABLE_10401);

		// WHEN
		when(this.userService.createUser((any(UserDto.class)))).thenThrow(appRuntimeException);

		// THEN
		this.mockMvc
		        .perform(RestDocumentationRequestBuilders.post("/user")
		                .content(this.objectMapper.writeValueAsString(userDto))
		                .contentType(UserController.MEDIA_TYPE_JSON_V1).accept(UserController.MEDIA_TYPE_JSON_V1))
		        .andDo(print()).andExpect(status().isInternalServerError()) //
		        .andExpect(jsonPath(JSON_PREFIX + "status").value("INTERNAL_SERVER_ERROR")) //
		        .andExpect(jsonPath(JSON_PREFIX + "shortErrorCodeDescription").value(EX_ERR_MSG)) //
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
	@DisplayName("UserControllerTest#test020_findById_pass")
	@Description("Handles the GET request for finding a user by the given id. The test passes.")
	public void test020_findById_pass() throws Exception {
		log.debug(LOG_PREFIX + "test020_findById_pass");

		// GIVEN
		final UserDto userDto = createUserDto();
		userDto.setId(USER_ID);

		// WHEN
		when(this.userService.findById((any(Long.class)))).thenReturn(userDto);

		// THEN
		this.mockMvc.perform(RestDocumentationRequestBuilders.get("/user/{id}/id", 1L) //
		        .accept(UserController.MEDIA_TYPE_JSON_V1)) //
		        .andExpect(jsonPath(JSON_PREFIX + "id").value(USER_ID)) //
		        .andExpect(jsonPath(JSON_PREFIX + "username").value(USER_USER_NAME)).andDo(print())
		        .andExpect(status().isOk()) //
		        .andDo(this.documentationResultHandler.document( //
		                pathParameters(parameterWithName("id") //
		                        .description("Identifier of the person to be obtained.")))) //
		        .andDo(this.documentationResultHandler.document( //
		                responseFields( //
		                        fieldWithPath("id") //
		                                .description("A unique identifier of the user."), //
		                        fieldWithPath("username") //
		                                .description("The username you enter is represented by an email address.."),
		                        fieldWithPath("password") //
		                                .description("The given password."),
		                        fieldWithPath("fullname") //
		                                .description("The entered full name, consisting of first and last name."),
		                        fieldWithPath("comment") //
		                                .description("The given comment."),
		                        fieldWithPath("phoneNo") //
		                                .description("The given phoneNo."),
		                        fieldWithPath("image") //
		                                .description("The given image."),
		                        fieldWithPath("sex") //
		                                .description("The given sex.")) //
//						responseHeaders( //
//								headerWithName("X-B3-TraceId") //
//										.description("A trace-id for each incoming request"), //
//								headerWithName("X-B3-SpanId") //
//										.description("A -id for each incoming request"))));
				));

	}

}
