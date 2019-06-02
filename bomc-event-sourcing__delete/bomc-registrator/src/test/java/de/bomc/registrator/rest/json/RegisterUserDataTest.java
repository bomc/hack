package de.bomc.registrator.rest.json;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;

import de.bomc.registrator.category.ModulTest;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

@Category(ModulTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegisterUserDataTest {

	// _______________________________________________
	// Member constants.
	// -----------------------------------------------
	private static final String LOG_PREFIX = "RegisterUserDataTest#";
	private static final Logger LOGGER = Logger.getLogger(RegisterUserDataTest.class);
	// _______________________________________________
	// Simple test data.
	// -----------------------------------------------
	private static final String FULLNAME = "My Fullname";
	private static final String ADDRESS = "Strasse 6, D-12345 Stadt";
	private static final String USERNAME = "username@bomc.org";
	private static final String TELEPHONNUBER = "01234/123456";
	// _______________________________________________
	// Member variables.
	// -----------------------------------------------
	private Jsonb jsonb;

	@Before
	public void init() {
		final JsonbConfig config = new JsonbConfig().withFormatting(true);
		this.jsonb = JsonbBuilder.newBuilder().withConfig(config).build();
	}

	@Test
	public void test010_createRegisterUserDataTest_pass() {
		LOGGER.debug(LOG_PREFIX + "test010_createRegisterUserDataTest_pass");

		final RegisterUserData registerUserData = new RegisterUserData(FULLNAME, ADDRESS, USERNAME, TELEPHONNUBER);

		LOGGER.debug(LOG_PREFIX + "test010_createRegisterUserDataTest_pass " + registerUserData.toJson());
		
		assertThat(registerUserData.toJson(), containsString(FULLNAME));
	}

	@Test
	public void test020_createRegisterUserDataTest_pass() {
		LOGGER.debug(LOG_PREFIX + "test010_createRegisterUserDataTest_pass");

		final RegisterUserData registerUserData1 = new RegisterUserData(FULLNAME, ADDRESS, USERNAME, TELEPHONNUBER);
		final RegisterUserData registerUserData2 = new RegisterUserData(FULLNAME, ADDRESS, USERNAME, TELEPHONNUBER);

		final List<RegisterUserData> list = new ArrayList<RegisterUserData>();
		list.add(registerUserData1);
		list.add(registerUserData2);

		LOGGER.debug(LOG_PREFIX + "test010_createRegisterUserDataTest_pass " + this.jsonb.toJson(list));
		
		assertThat(this.jsonb.toJson(list), containsString(FULLNAME));
	}
}