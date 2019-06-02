package ch.bs.zid.egov.test.rest.endpoints.arq.mock;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
//import de.bomc.poc.rest.api.v1.dto.UserDTO;

/**
 * A mock implementation for the user controller.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 22.08.2016
 */
//@Stateless
//@Local(UserControllerLocal.class)
public class UserControllerMockEJB /*implements UserControllerLocal*/ {
//	private static final String LOG_PREFIX = "UserControllerMockEJB#";
//	@Inject
//	@LoggerQualifier
//	private Logger logger;
//
//	/**
//	 * Create a user in db.
//	 * 
//	 * @param userDTO
//	 *            contains the user data.
//	 * @return the created id in db.
//	 */
//	public Long createUser(final UserDTO userDTO) {
//		this.logger.debug(LOG_PREFIX + "createUser [" + userDTO + "]");
//
//		return 1l;
//	}
//
//	/**
//	 * Get user by id.
//	 * 
//	 * @param id
//	 *            the given id.
//	 * @return a {@link UserDTO} from db corresponds with the given id.
//	 */
//	public UserDTO getUserById(final Long id) {
//		this.logger.debug(LOG_PREFIX + "getUserById [" + id + "]");
//
//		final UserDTO userDTO = new UserDTO();
//		userDTO.setId(11L);
//		userDTO.setUsername("myUsername");
//
//		return userDTO;
//	}
//
//	/**
//	 * Read all users from db.
//	 * 
//	 * @return all users as {@link UserDTO} list.
//	 */
//	public List<UserDTO> getUsers() {
//		this.logger.debug(LOG_PREFIX + "getUsers");
//		
//		List<UserDTO> userDtoList = new ArrayList<UserDTO>(3);
//		
//		for (int i = 1; i < 4; i++) {
//			final UserDTO userDTO = new UserDTO();
//			userDTO.setId(Integer.toUnsignedLong(i));
//			userDTO.setUsername("myUsername_" + i);
//			
//			userDtoList.add(userDTO);
//		}
//		
//		return userDtoList;
//	}
}
