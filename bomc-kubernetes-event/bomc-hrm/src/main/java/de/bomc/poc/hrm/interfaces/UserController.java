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

import javax.validation.Valid;

import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.bomc.poc.hrm.application.UserService;
import de.bomc.poc.hrm.application.log.method.Loggable;
import de.bomc.poc.hrm.interfaces.mapper.UserDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The controller for customer handling.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@RestController
@RequestMapping(value = "/user")
@CrossOrigin(origins = "*") // TODO: security issue
@Api(tags = "User queries", value = "User Management System", description = "Operations pertaining to user in User Management System", produces = "application/vnd.hrm-v1+json;charset=UTF-8")
public class UserController {

	protected static final String MEDIA_TYPE_JSON_V1 = "application/vnd.hrm-v1+json;charset=UTF-8";
	
	private final UserService userService;
	
	/**
	 * Creates a new instance of <code>UserController</code>.
	 * 
	 * @param userService the given user service.
	 */
	// @Autowired: is here not necessary, is done by IOC container.
	public UserController(final UserService userService) {
		this.userService = userService;
	}
	
	@ApiOperation(value = "Creates a user.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully create user in db."),
			@ApiResponse(code = 401, message = "Not authorized to view the resource."),
			@ApiResponse(code = 403, message = "Accessing the resource that trying to reach is forbidden."),
			@ApiResponse(code = 404, message = "The resource that trying to reach is not found.") })
	@ApiImplicitParams(@ApiImplicitParam(name = "userDto", value = "The user to persist.", dataType = "UserDto", dataTypeClass = de.bomc.poc.hrm.interfaces.mapper.UserDto.class, required = true))
	@PostMapping(produces = MEDIA_TYPE_JSON_V1, consumes = MEDIA_TYPE_JSON_V1)
	@ResponseStatus(HttpStatus.OK)
	@Loggable(result = true, params = true, value = LogLevel.DEBUG, time = true)
	public ResponseEntity<UserDto> createUser(@Valid @RequestBody final UserDto userDto) {
	
		return new ResponseEntity<UserDto>(
				this.userService.createUser(userDto), HttpStatus.OK);
	}
}
