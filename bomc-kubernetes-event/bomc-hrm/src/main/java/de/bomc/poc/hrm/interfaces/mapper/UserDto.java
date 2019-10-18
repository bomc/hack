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
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * A data transfer object for customer handling.
 * Uses JSR-303 Annotations for validation.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
@ApiModel(description = "All details about a user. ")
public class UserDto {

	@ApiModelProperty(notes = "The database generated id.")
	private Long id;
	
	@NotNull
	@NotEmpty
	@Size(min = 5, message = "must be minimum 5 characters!")
	@ApiModelProperty(notes = "The username of the user.")
	private String username;
}
