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
package de.bomc.poc.hrm.application;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.bomc.poc.hrm.application.exception.AppErrorCodeEnum;
import de.bomc.poc.hrm.application.exception.AppRuntimeException;
import de.bomc.poc.hrm.application.log.method.Loggable;
import de.bomc.poc.hrm.domain.model.UserEntity;
import de.bomc.poc.hrm.infrastructure.UserRepository;
import de.bomc.poc.hrm.interfaces.mapper.UserDto;
import de.bomc.poc.hrm.interfaces.mapper.UserMapper;

/**
 * The implementation of {@link UserService}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 20.09.2019
 */
@Service
public class UserServiceImpl implements UserService {

	private static final String LOG_PREFIX = "UserService#";
	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class.getName());

	/* --------------------- member variables ----------------------- */
	private final UserRepository userRepository;
	private final UserMapper userMapper;

	/* --------------------- constructor ---------------------------- */

	/**
	 * Creates a new instance of <code>UserService</code>.
	 * 
	 * @param customerRepository the given customer repository.
	 * @param customerService    the
	 */
	public UserServiceImpl(final UserRepository userRepository, final UserMapper userMapper) {

		this.userRepository = userRepository;
		this.userMapper = userMapper;
	}

	/* --------------------- methods -------------------------------- */

	@Transactional
	@Loggable(result = true, params = true, value = LogLevel.DEBUG)
	public UserDto createUser(final UserDto userDto) {

		try {
			final UserEntity userEntity = this.userMapper.mapDtoToEntity(userDto);
			// Set metadata.
			userEntity.setCreateUser(userEntity.getUsername());
			// Write down to db.
			final UserEntity retUserEntity = this.userRepository.save(userEntity);

			LOGGER.debug(LOG_PREFIX + "createUser - [id=" + retUserEntity.getId() + "]");

			final UserDto retUserDto = this.userMapper.mapEntityToDto(retUserEntity);

			return retUserDto;
		} catch (final DataIntegrityViolationException ex) {
			final AppRuntimeException appRuntimeException = new AppRuntimeException(
					"There is already a user avaible in db [username=" + userDto.getUsername() + "]", ex,
					AppErrorCodeEnum.JPA_PERSISTENCE_10401);

			LOGGER.error(LOG_PREFIX + "createUser " + appRuntimeException.stackTraceToString());

			throw appRuntimeException;
		}
	}

	@Transactional(readOnly = true)
	@Loggable(result = true, params = true, value = LogLevel.DEBUG)
	public UserDto findByUsername(final String username) {

		List<UserEntity> mapstream = Collections.emptyList();

		try (Stream<UserEntity> stream = this.userRepository.findByUsername(username)) {
			mapstream = stream.collect(Collectors.toList());
		}

		// 'username' is unique, so only one element is returned.
		return this.userMapper.mapEntityToDto(mapstream.get(0));
	}

	@Transactional(readOnly = true)
	@Loggable(result = true, params = true, value = LogLevel.DEBUG)
	public UserDto findByUsernameAndPassword(final String username, final String persistedPassword) {
		
		List<UserEntity> mapstream = Collections.emptyList();

		try (Stream<UserEntity> stream = this.userRepository.findByUsernameAndPassword(username, persistedPassword)) {
			mapstream = stream.collect(Collectors.toList());
		}

		// the combination of'username' and 'persistedPassword' is unique, so only one element is returned.
		return this.userMapper.mapEntityToDto(mapstream.get(0));		
	}
	
	@Transactional(readOnly = true)
	@Loggable(result = true, params = true, value = LogLevel.DEBUG)
	public List<UserDto> findAllUsers() {
	    
		final List<UserEntity> userEntityList = this.userRepository.findAll();
		
		return this.userMapper.mapEntitiesToDtos(userEntityList);
	}

}
