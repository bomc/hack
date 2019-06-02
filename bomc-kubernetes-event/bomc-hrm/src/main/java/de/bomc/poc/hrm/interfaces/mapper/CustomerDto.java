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

import java.time.LocalDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.bomc.poc.hrm.domain.model.basis.CustomLocalDateSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A data transfer object for customer handling.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel(description = "All details about a customer. ")
public class CustomerDto {

	@ApiModelProperty(notes = "The database generated id.")
	private Long id;

	@Email
	@NotNull
	@NotEmpty
	@ApiModelProperty(notes = "The email address of the customer.")
	private String emailAddress;
	
	@Size(min = 5, max = 30, message = "must be between 5 and 30!")
	@ApiModelProperty(notes = "The phone number of the customer.")
	private String phoneNumber;
	
	@NotNull
	@NotEmpty
	@Size(min = 5, max = 40, message = "must be between 3 and 40!")
	@ApiModelProperty(notes = "The first name of the customer.")
	private String firstName;
	
	@NotNull
	@NotEmpty
	@Size(min = 5, max = 40, message = "must be between 3 and 40!")
	@ApiModelProperty(notes = "The last name of the customer.")
	private String lastName;
	
	@NotNull
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@ApiModelProperty(notes = "The day of birth of the customer.")
	private LocalDate dateOfBirth;
	
	@NotNull
	@NotEmpty
	@Size(min = 5, max = 40, message = "must be between 3 and 40!")
	@ApiModelProperty(notes = "The place of residence of the customer.")
	private String city;
	
	@NotNull
	@NotEmpty
	@Size(min = 4, max = 10, message = "must be between 3 and 10!")
	@ApiModelProperty(notes = "The postal code of residence of the customer.")
	private String postalCode;
	
	@NotNull
	@NotEmpty
	@Size(min = 5, max = 60, message = "must be between 3 and 60!")
	@ApiModelProperty(notes = "The street of residence of the customer.")
	private String street;
	@NotNull
	@NotEmpty
	@Size(min = 1, max = 3, message = "must be between 1 and 3!")
	@ApiModelProperty(notes = "The house number of residence of the customer.")
	private String houseNumber;
	
	@NotNull
	@NotEmpty
	@Size(min = 2, max = 3, message = "must be 3!")
	@ApiModelProperty(notes = "The country of residence of the customer.")
	private String country;

}
