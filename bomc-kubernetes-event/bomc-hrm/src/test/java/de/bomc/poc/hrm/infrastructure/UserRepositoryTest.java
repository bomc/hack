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
package de.bomc.poc.hrm.infrastructure;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.stream.Stream;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.bomc.poc.hrm.AbstractBaseUnit;
import de.bomc.poc.hrm.application.exception.AppRuntimeException;
import de.bomc.poc.hrm.domain.model.UserDetailsEntity;
import de.bomc.poc.hrm.domain.model.UserDetailsEntity.SEX;
import de.bomc.poc.hrm.domain.model.UserEntity;

/**
 * Tests the {@link UserRepository}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@DataJpaTest
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("local")
//@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data-h2.sql")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest extends AbstractBaseUnit {

	private static final String LOG_PREFIX = "UserRepositoryTest#";
	private static final Logger LOGGER = LoggerFactory.getLogger(UserRepositoryTest.class.getName());

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	/* --------------------- constants ------------------------------ */
	private static final String USER_NAME = "boris@brexit.uk";
	private static final String USER_DETAIL_COMMENT = "comment";
	private static final String PHONE_NUMBER = "0121234567";
	private static final String PASSWORD = "password";
	private static final String NEW_PASSWORD = "newPassword";
	
	/* --------------------- member variables ----------------------- */
	@Autowired
	private UserRepository userRepository;
	
	/* --------------------- test methods --------------------------- */
	
	/**
	 * mvn clean install -Dtest=UserRepositoryTest#test010_findByUsername_pass
	 */
	@Test
	public void test010_findByUsername_pass() {
		LOGGER.info(LOG_PREFIX + "test010_findByUsername_pass");

		// GIVEN
		final UserEntity userEntity = new UserEntity(USER_NAME);
		userEntity.setCreateUser(USER_NAME);
		
		this.userRepository.save(userEntity);
		
		// WHEN
		final Stream<UserEntity> userEntityStream = this.userRepository.findByUsername(USER_NAME);

		// THEN
		assertThat(userEntityStream.count(), equalTo(1L));
	}

	@Test
	public void test020_findByWithUnavailableUsername_pass() {
		LOGGER.info(LOG_PREFIX + "test020_findByWithUnavailableUsername_pass");

		// GIVEN
		
		// WHEN
		final Stream<UserEntity> userEntityStream = this.userRepository.findByUsername(USER_NAME);

		// THEN
		assertThat(userEntityStream.count(), equalTo(0L));
	}
	
	@Test
	public void test030_createUser_pass() {
		LOGGER.info(LOG_PREFIX + "test030_createUser_pass");
		
		// GIVEN
		final UserEntity userEntity = new UserEntity(USER_NAME);
		userEntity.setCreateUser(USER_NAME);
		
		// WHEN
		final UserEntity retUserEntity = this.userRepository.save(userEntity);
		
		// THEN
		assertThat(retUserEntity.getId(), notNullValue());
		assertThat(retUserEntity.getCreateDateTime(), notNullValue());
		assertThat(retUserEntity.getCreateUser(), notNullValue());
	}
	
	@Test
	public void test040_createUserWithUserDetails_pass() {
		LOGGER.info(LOG_PREFIX + "test040_createUserWithUserDetails_pass");
		
		// GIVEN
		final UserEntity userEntity = new UserEntity(USER_NAME);
		
		final UserDetailsEntity userDetailsEntity = new UserDetailsEntity();
		userDetailsEntity.setComment(USER_DETAIL_COMMENT);
		userDetailsEntity.setPhoneNo(PHONE_NUMBER);
		userDetailsEntity.setSex(SEX.MALE);
		
		// WHEN
		userEntity.setUserDetails(userDetailsEntity);
		
		final UserEntity retUserEntity = this.userRepository.save(userEntity);
		
		// THEN
		assertThat(retUserEntity.getId(), notNullValue());
		assertThat(retUserEntity.getUserDetails(), notNullValue());
	}
	
	@Test
	public void test050_findByUsernameAndPassword_pass() {
		LOGGER.info(LOG_PREFIX + "test050_findByUsernameAndPassword_pass");
		
		// GIVEN
		final UserEntity userEntity = new UserEntity(USER_NAME);
		userEntity.setNewPassword(PASSWORD);
		userEntity.setCreateUser(USER_NAME);
		
		final UserDetailsEntity userDetailsEntity = new UserDetailsEntity();
		userDetailsEntity.setComment(USER_DETAIL_COMMENT);
		userDetailsEntity.setPhoneNo(PHONE_NUMBER);
		userDetailsEntity.setSex(SEX.MALE);
		final UserEntity retUserEntity = this.userRepository.save(userEntity);
		
		assertThat(retUserEntity.getId(), notNullValue());
		
		// WHEN
		final Stream<UserEntity> userEntityStream = this.userRepository.findByUsernameAndPassword(USER_NAME, PASSWORD);
		
		// THEN
		assertThat(userEntityStream.count(), equalTo(1L));
	}
	
	@Test
	public void test060_setNewPassword_pass() {
		LOGGER.info(LOG_PREFIX + "test060_setNewPassword_pass");
		
		// GIVEN
		final UserEntity userEntity = new UserEntity(USER_NAME);
		userEntity.setNewPassword(PASSWORD);
		userEntity.setCreateUser(USER_NAME);
		
		final UserDetailsEntity userDetailsEntity = new UserDetailsEntity();
		userDetailsEntity.setComment(USER_DETAIL_COMMENT);
		userDetailsEntity.setPhoneNo(PHONE_NUMBER);
		userDetailsEntity.setSex(SEX.MALE);
		final UserEntity retUserEntity = this.userRepository.save(userEntity);
		
		assertThat(retUserEntity.getId(), notNullValue());
		assertThat(retUserEntity.getPassword(), equalTo(PASSWORD));
		
		// WHEN
		retUserEntity.setNewPassword(NEW_PASSWORD);
		final UserEntity flushUserEntity = this.userRepository.saveAndFlush(retUserEntity);
		
		// THEN
		assertThat(flushUserEntity.getPassword(), equalTo(NEW_PASSWORD));
	}
	
	@Test
	public void test070_setNewPassword_fail() {
		LOGGER.info(LOG_PREFIX + "test070_setNewPassword_fail");
		
		this.exceptionRule.expect(AppRuntimeException.class);
		this.exceptionRule.expectMessage("Trying to set the new password equals to the current password.");
		
		// GIVEN
		final UserEntity userEntity = new UserEntity(USER_NAME);
		userEntity.setNewPassword(PASSWORD);
		userEntity.setCreateUser(USER_NAME);
		
		final UserDetailsEntity userDetailsEntity = new UserDetailsEntity();
		userDetailsEntity.setComment(USER_DETAIL_COMMENT);
		userDetailsEntity.setPhoneNo(PHONE_NUMBER);
		userDetailsEntity.setSex(SEX.MALE);
		final UserEntity retUserEntity = this.userRepository.save(userEntity);
		
		assertThat(retUserEntity.getId(), notNullValue());
		assertThat(retUserEntity.getPassword(), equalTo(PASSWORD));
		
		// WHEN
		retUserEntity.setNewPassword(PASSWORD);
		this.userRepository.saveAndFlush(retUserEntity);
		
		// THEN
	}
}
