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
package de.bomc.poc.hrm.interfaces.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runners.MethodSorters;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Description;

import de.bomc.poc.hrm.AbstractBaseUnit;
import de.bomc.poc.hrm.domain.model.UserEntity;

/**
 * Tests the user mapping entity to dto.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 30.10.2019
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserMapperTest extends AbstractBaseUnit {

	private static final String LOG_PREFIX = "UserMapperTest#";
	private static final Logger LOGGER = LoggerFactory.getLogger(UserMapperTest.class.getName());

	/* --------------------- member variables ----------------------- */
	private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

	/* --------------------- methods -------------------------------- */

	@Test
	@DisplayName("UserMapperTest#test010_mapEntityToDto_pass")
	@Description("Tests the mapping from userEntity to userDto.")
	public void test010_mapEntityToDto_pass() {

		final UserEntity userEntity = createNonPersistedUserEntityWithUserDetailsEntity(USER_USER_NAME, USER_PASSWORD);
		userEntity.setId(USER_ID);
		
		final UserDto userDto = userMapper.mapEntityToDto(userEntity);
		
		assertThat(userDto.getId(), equalTo(userEntity.getId()));
		
		LOGGER.debug(LOG_PREFIX + "test010_mapEntityToDto_pass " + userDto.toString());
	}
	
	@Test
	@DisplayName("UserMapperTest#test020_mapEntityListToDtoList_pass")
	@Description("Tests the mapping from a userEntity list to a userDto list.")
	public void test020_mapEntityListToDtoList_pass() {

		final UserEntity userEntity = createNonPersistedUserEntityWithUserDetailsEntity(USER_USER_NAME, USER_PASSWORD);
		userEntity.setId(USER_ID);
		
		final List<UserEntity> userEntityList = new ArrayList<>();
		userEntityList.add(userEntity);
		
		final List<UserDto> userDtoList = userMapper.mapEntitiesToDtos(userEntityList);
		
		assertThat(userDtoList.size(), equalTo(1));
		assertThat(userDtoList.get(0), notNullValue());
		
		LOGGER.debug(LOG_PREFIX + "test020_mapEntityListToDtoList_pass " + userDtoList.get(0).toString());
	}
	
	@Test
	@DisplayName("UserMapperTest#test030_mapDtoToEntity_pass")
	@Description("Tests the mapping from userDto to userEntity.")
	public void test030_mapDtoToEntity_pass() {

		final UserDto userDto = createUserDto();
		
		final UserEntity userEntity = userMapper.mapDtoToEntity(userDto);
		
		assertThat(userEntity.getId(), equalTo(userDto.getId()));
		
		LOGGER.debug(LOG_PREFIX + "test030_mapDtoToEntity_pass " + userDto.toString());
	}
	
	/**
	 * mvn clean install -Dtest=UserMapperTest#test040_mapDtoListToEntityList_pass
	 */
	@Test
	@DisplayName("UserMapperTest#test040_mapDtoListToEntityList_pass")
	@Description("Tests the mapping from a userDto list to a userEntity list.")
	public void test040_mapDtoListToEntityList_pass() {

		final UserDto userDto = createUserDto();
		
		final List<UserDto> userDtoList = new ArrayList<>();
		userDtoList.add(userDto);
		
		final List<UserEntity> userEntityList = userMapper.mapDtosToEntities(userDtoList);
		
		assertThat(userEntityList.size(), equalTo(1));
		assertThat(userEntityList.get(0), notNullValue());
		
		LOGGER.debug(LOG_PREFIX + "test040_mapDtoListToEntityList_pass " + userDtoList.get(0).toString());
	}
}
