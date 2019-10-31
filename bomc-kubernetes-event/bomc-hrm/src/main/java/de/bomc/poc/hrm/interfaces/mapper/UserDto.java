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
package de.bomc.poc.hrm.interfaces.mapper;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import de.bomc.poc.hrm.domain.model.values.CoreTypeDefinitions;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A data transfer object for customer handling. Uses JSR-303 Annotations for
 * validation.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
// LOMBOK
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
// SWAGGER
@ApiModel(description = "All details about a user. ")
public class UserDto {

	@ApiModelProperty(notes = "The database generated id.")
	private Long id;

	@NotNull
	@NotEmpty
	@Size(min = CoreTypeDefinitions.MINIMUM_USERNAME_LENGTH, message = "must be minimum "
			+ CoreTypeDefinitions.MINIMUM_USERNAME_LENGTH + " characters!")
	@ApiModelProperty(notes = "The given username.")
	private String username;

	@NotNull
	@NotEmpty
	@ApiModelProperty(notes = "The given password.")
	private String password;

	@NotNull
	@NotEmpty
	@Size(min = CoreTypeDefinitions.MINIMUM_FULLNAME_LENGTH, max = CoreTypeDefinitions.MAXIMUM_FULLNAME_LENGTH, message = "must be between "
			+ CoreTypeDefinitions.MINIMUM_FULLNAME_LENGTH + " and " + CoreTypeDefinitions.MAXIMUM_FULLNAME_LENGTH
			+ " characters!")
	@ApiModelProperty(notes = "The full name of the user.")
	private String fullname;

	@Size(max = CoreTypeDefinitions.DESCRIPTION_LENGTH, message = "must not be longer than " + CoreTypeDefinitions.DESCRIPTION_LENGTH +  " characters!")
	@ApiModelProperty(notes = "A comment for additional information.")
	private String comment;

	@NotNull
	@NotEmpty
	@Pattern(regexp = "^(((((((00|\\+)49[ \\-/]?)|0)[1-9][0-9]{1,4})[ \\-/]?)|((((00|\\+)49\\()|\\(0)[1-9][0-9]{1,4}\\)[ \\-/]?))[0-9]{1,7}([ \\-/]?[0-9]{1,5})?)$")
	@ApiModelProperty(notes = "The given phone number.")
	private String phoneNo;

	@ApiModelProperty(notes = "The given image/avatar of the user.")
	private byte[] image;

	@NotNull
	@NotEmpty
	@ApiModelProperty(notes = "The given sex of the user.")
	private String sex;
}
