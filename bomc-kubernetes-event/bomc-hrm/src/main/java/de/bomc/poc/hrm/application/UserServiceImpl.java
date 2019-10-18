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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

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

	@Loggable(result = true, params = true, value = LogLevel.DEBUG)
	public UserDto createUser(final UserEntity userEntity) {

		try {
			// Set metadata.
			userEntity.setCreateUser(userEntity.getUsername());
			// Write down to db.
			final UserEntity retUserEntity = this.userRepository.save(userEntity);

			LOGGER.debug(LOG_PREFIX + "createUser - [id=" + retUserEntity.getId() + "]");

			final UserDto userDto = this.userMapper.mapEntityToDto(retUserEntity);

			return userDto;
		} catch (final DataIntegrityViolationException ex) {
			final AppRuntimeException appRuntimeException = new AppRuntimeException(
					"There is already a user avaible in db [username=" + userEntity.getUsername() + "]", ex,
					AppErrorCodeEnum.JPA_PERSISTENCE_10401);

			LOGGER.error(LOG_PREFIX + "createUser " + appRuntimeException.stackTraceToString());

			throw appRuntimeException;
		}
	}

}
