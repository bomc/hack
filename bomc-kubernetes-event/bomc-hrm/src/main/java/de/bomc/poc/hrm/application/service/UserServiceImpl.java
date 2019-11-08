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
package de.bomc.poc.hrm.application.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.logging.LogLevel;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.bomc.poc.hrm.application.exception.AppErrorCodeEnum;
import de.bomc.poc.hrm.application.exception.AppRuntimeException;
import de.bomc.poc.hrm.application.log.method.Loggable;
import de.bomc.poc.hrm.application.security.SecurityObjectRolesEnum;
import de.bomc.poc.hrm.domain.model.RoleEntity;
import de.bomc.poc.hrm.domain.model.UserEntity;
import de.bomc.poc.hrm.infrastructure.RoleRepository;
import de.bomc.poc.hrm.infrastructure.UserRepository;
import de.bomc.poc.hrm.interfaces.mapper.UserDto;
import de.bomc.poc.hrm.interfaces.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * The implementation of {@link UserService}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 20.09.2019
 */
@Slf4j
@Service // makes it eligible for Spring Component Scan
public class UserServiceImpl implements UserService {

	private static final String LOG_PREFIX = "UserService#";

	/* --------------------- member variables ----------------------- */
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserMapper userMapper;

	/* --------------------- constructor ---------------------------- */

	/**
	 * Creates a new instance of <code>UserService</code>.
	 * 
	 * @param userRepository     the given user repository to inject.
	 * @param roleRepository     the given role repository to inject.
	 * @param customerRepository the given customer repository to inject.
	 * @param userMapper         the userMapper to inject.
	 */
	public UserServiceImpl(final UserRepository userRepository, final RoleRepository roleRepository,
			final UserMapper userMapper) {

		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.userMapper = userMapper;
	}

	/* --------------------- methods -------------------------------- */

	@Override
	@Transactional
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
	public UserDto createUser(final UserDto userDto) {

		try {
			final UserEntity userEntity = this.userMapper.mapDtoToEntity(userDto);
			// Set metadata.
			userEntity.setCreateUser(userEntity.getUsername());
			// Write down to db.
			final UserEntity retUserEntity = this.userRepository.save(userEntity);

			// Add user to role 'APPLICATION_USER'.
			final RoleEntity roleEntity = this.roleRepository
					.findByName(SecurityObjectRolesEnum.APPLICATION_USER.name()).findFirst().get();

			roleEntity.addUser(retUserEntity);
			this.roleRepository.save(roleEntity);

			// Map entity to dto.
			final UserDto retUserDto = this.userMapper.mapEntityToDto(retUserEntity);

			return retUserDto;
		} catch (final DataIntegrityViolationException ex) {
			final AppRuntimeException appRuntimeException = new AppRuntimeException(
					"There is already a user avaible in db [username=" + userDto.getUsername() + "]", ex,
					AppErrorCodeEnum.JPA_PERSISTENCE_ENTITY_NOT_AVAILABLE_10401);
			
			log.error(LOG_PREFIX + "createUser " + appRuntimeException.stackTraceToString());

			throw appRuntimeException;
		}
	}

	@Override
	@Transactional(readOnly = true)
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
	public Boolean hasUserPermission(final String username, final String permission) {
		final RoleEntity roleEntity = this.roleRepository.findRoleByUsernameAndPermission(username, permission);

		if(roleEntity == null) {
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}

	@Override
	@Transactional(readOnly = true)
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
	public UserDto findById(final Long id) {

		final Optional<UserEntity> userEntityOptional = this.userRepository.findById(id);

		final UserEntity userEntity = userEntityOptional.orElseThrow(() -> new AppRuntimeException(
				AppErrorCodeEnum.JPA_PERSISTENCE_ENTITY_NOT_AVAILABLE_10401.getShortErrorCodeDescription(),
				AppErrorCodeEnum.JPA_PERSISTENCE_ENTITY_NOT_AVAILABLE_10401));

		// 'id' is unique, so only one element is returned.
		return this.userMapper.mapEntityToDto(userEntity);
	}

	@Transactional(readOnly = true)
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
	public UserDto findByUsername(final String username) {

		List<UserEntity> mapstream = Collections.emptyList();

		try (Stream<UserEntity> stream = this.userRepository.findByUsername(username)) {
			mapstream = stream.collect(Collectors.toList());
		}

		// 'username' is unique, so only one element is returned.
		return this.userMapper.mapEntityToDto(mapstream.get(0));
	}

	@Transactional(readOnly = true)
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
	public UserDto findByUsernameAndPassword(final String username, final String persistedPassword) {

		List<UserEntity> mapstream = Collections.emptyList();

		try (Stream<UserEntity> stream = this.userRepository.findByUsernameAndPassword(username, persistedPassword)) {
			mapstream = stream.collect(Collectors.toList());
		}

		// the combination of'username' and 'persistedPassword' is unique, so only one
		// element is returned.
		return this.userMapper.mapEntityToDto(mapstream.get(0));
	}

	@Transactional(readOnly = true)
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
	public List<UserDto> findAllUsers() {

		final List<UserEntity> userEntityList = this.userRepository.findAll();

		return this.userMapper.mapEntitiesToDtos(userEntityList);
	}

}
