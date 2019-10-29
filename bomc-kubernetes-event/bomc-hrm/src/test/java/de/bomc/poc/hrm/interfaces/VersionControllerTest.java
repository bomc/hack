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

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import brave.Tracer;
import de.bomc.poc.hrm.GitConfig;
import de.bomc.poc.hrm.application.log.http.RequestGetLoggingInterceptor;
import de.bomc.poc.hrm.application.log.http.RequestResponseLoggerImpl;
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
@WebMvcTest(VersionController.class)
@Import({ RequestGetLoggingInterceptor.class, RequestResponseLoggerImpl.class })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VersionControllerTest {

	private static final String LOG_PREFIX = "VersionControllerTest#";

	// _______________________________________________
	// JUnit rules.
	// -----------------------------------------------
	// A rule for REST documentation and it will be used when building the mockMvc
	// object.
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

	private MockMvc mockMvc;

	private RestDocumentationResultHandler documentationResultHandler;

	// _______________________________________________
	// Mocks
	// -----------------------------------------------
	@MockBean
	private GitConfig gitConfig;
	@MockBean
	private Tracer tracer;

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
	@DisplayName("VersionControllerTest#test010_getGitVersion_pass")
	@Description("Handles the GET request of the VersionController to get git data. The test passes.")
	public void test010_getGitVersion_pass() throws Exception {
		log.debug(LOG_PREFIX + "test010_getGitVersion_pass");

		// GIVEN

		// WHEN
		when(this.gitConfig.getVersion()).thenReturn(VersionController.GIT_VERSION);
		when(this.gitConfig.getCommitId()).thenReturn(VersionController.GIT_COMMIT_ID);
		when(this.gitConfig.getCommitMessage()).thenReturn(VersionController.GIT_COMMIT_MESSAGE);

		// THEN
		this.mockMvc
				.perform(RestDocumentationRequestBuilders.get("/git/version")
						.accept(VersionController.MEDIA_TYPE_JSON_V1))
				.andDo(print()).andExpect(status().isOk()) //
				.andExpect(content().contentType(VersionController.MEDIA_TYPE_JSON_V1))
				.andExpect(jsonPath(JSON_PREFIX + VersionController.GIT_COMMIT_MESSAGE)
						.value(VersionController.GIT_COMMIT_MESSAGE)) //
				.andExpect(
						jsonPath(JSON_PREFIX + VersionController.GIT_COMMIT_ID).value(VersionController.GIT_COMMIT_ID)) //
				.andExpect(jsonPath(JSON_PREFIX + VersionController.GIT_VERSION).value(VersionController.GIT_VERSION)) //
				.andDo(this.documentationResultHandler.document( //
						responseFields( //
								fieldWithPath(VersionController.GIT_VERSION) //
										.description("The current version of this api."), //
								fieldWithPath(VersionController.GIT_COMMIT_ID) //
										.description("The last commit version."), //
								fieldWithPath(VersionController.GIT_COMMIT_MESSAGE) //
										.description("The last commit message.")) //
//				responseHeaders( //
//						headerWithName("X-B3-TraceId") //
//								.description("A trace-id for each incoming request"), //
//						headerWithName("X-B3-SpanId") //
//								.description("A -id for each incoming request"))));
				));
	}
}