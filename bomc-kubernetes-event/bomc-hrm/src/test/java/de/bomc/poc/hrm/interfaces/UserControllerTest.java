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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.bomc.poc.hrm.AbstractBaseUnit;
import de.bomc.poc.hrm.application.UserService;
import de.bomc.poc.hrm.domain.model.UserEntity;
import de.bomc.poc.hrm.interfaces.mapper.UserDto;
import de.bomc.poc.hrm.interfaces.mapper.UserMapper;

/**
 * Tests the user controller, in BDD context without Spring.
 * https://reflectoring.io/spring-boot-mock/
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@RunWith(MockitoJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserControllerTest extends AbstractBaseUnit {
	private static final String LOG_PREFIX = "UserControllerTest#";
	private static final Logger LOGGER = LoggerFactory.getLogger(UserControllerTest.class.getName());

	/* --------------------- constants ------------------------------ */

	/* --------------------- member variables ----------------------- */

	@Mock // will then automatically be initialized with a mock instance of their type.
	private UserService userService;

	@Mock // will then automatically be initialized with a mock instance of their type.
	private UserMapper userMapper;

	@InjectMocks // Mockito will then try to instantiate fields annotated with @InjectMocks by
					// passing all mocks into a constructor. Note that we need to provide such a
					// constructor for Mockito to work reliably. If Mockito doesn’t find a
					// constructor, it will try setter injection or field injection, but the
					// cleanest way is still a constructor.
	private UserController sut;

	@Test
	public void test010_createUser_pass() {
		LOGGER.debug(LOG_PREFIX + "test010_createUser_pass");

		// GIVEN: define the behavior that should be mocked.
		final UserEntity userEntity = new UserEntity(USER_USER_NAME);
		userEntity.setNewPassword(USER_PASSWORD);

		final UserDto userDto = new UserDto();

//		given(this.userMapper.mapDtoToEntity(userDto)).willReturn(userEntity);
		given(this.userService.createUser(eq(userDto))).willReturn(userDto);

		// WHEN
		final ResponseEntity<UserDto> response = this.sut.createUser(userDto);

		// THEN: check if certain methods have been called as expected.
		then(this.userService).should().createUser(eq(userDto));

		// Do the asserts.
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
	}

}
