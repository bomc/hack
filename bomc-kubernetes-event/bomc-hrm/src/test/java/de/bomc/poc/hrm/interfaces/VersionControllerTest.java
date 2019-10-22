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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import brave.Tracer;
import de.bomc.poc.hrm.GitConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 21.10.2019
 */
@Slf4j
@WebMvcTest(VersionController.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("local")
@ComponentScan(value = { "de.bomc.poc" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VersionControllerTest {

	private static final String LOG_PREFIX = "VersionControllerTest#";
	// _______________________________________________
	// Constants
	// -----------------------------------------------
	private static final String JSON_PREFIX = "$.";
	// _______________________________________________
	// Member variables
	// -----------------------------------------------
	@Autowired
	private MockMvc mvc;
	// _______________________________________________
	// Mocks
	// -----------------------------------------------
	@MockBean
	private GitConfig gitConfig;
	@MockBean
	private Tracer tracer;

	@Test
	public void test010_readVersionDefaultValues_pass() throws Exception {
		log.debug(LOG_PREFIX + "test010_readVersionDefaultValues_pass");

		// GIVEN

		// WHEN
		when(this.gitConfig.getVersion()).thenReturn(VersionController.GIT_VERSION);
		when(this.gitConfig.getCommitId()).thenReturn(VersionController.GIT_COMMIT_ID);
		when(this.gitConfig.getCommitMessage()).thenReturn(VersionController.GIT_COMMIT_MESSAGE);

		// THEN
		this.mvc.perform(get("/git/version").accept(VersionController.MEDIA_TYPE_JSON_V1)).andDo(print())
				.andExpect(status().isOk()).andExpect(content().contentType(VersionController.MEDIA_TYPE_JSON_V1))
				.andExpect(jsonPath(JSON_PREFIX + VersionController.GIT_COMMIT_MESSAGE)
						.value(VersionController.GIT_COMMIT_MESSAGE))
				.andExpect(jsonPath(JSON_PREFIX + VersionController.GIT_COMMIT_ID).value(VersionController.GIT_COMMIT_ID))
				.andExpect(jsonPath(JSON_PREFIX + VersionController.GIT_VERSION).value(VersionController.GIT_VERSION));
	}
}
