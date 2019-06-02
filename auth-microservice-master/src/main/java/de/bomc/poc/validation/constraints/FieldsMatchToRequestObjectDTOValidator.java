/**
 * Project: MY_POC_MICROSERVICE
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.validation.constraints;

import de.bomc.poc.api.generic.Parameter;
import de.bomc.poc.api.generic.transfer.request.RequestObjectDTO;
import de.bomc.poc.auth.rest.endpoint.v1.usermanagement.AuthUserManagementRestEndpoint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * This validator class validates the {@link RequestObjectDTO} for a parameter.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class FieldsMatchToRequestObjectDTOValidator implements ConstraintValidator<FieldsMatchToRequestObjectDTO, RequestObjectDTO> {

	private FieldsMatchToRequestObjectDTO constraintAnnotation;

	@Override
	public void initialize(final FieldsMatchToRequestObjectDTO constraintAnnotation) {
		this.constraintAnnotation = constraintAnnotation;
	}

	@Override
	public boolean isValid(final RequestObjectDTO requestObjectDTO, final ConstraintValidatorContext context) {
		final String fieldName = this.constraintAnnotation.parameter();

		try {
			final List<Parameter> parameterList = requestObjectDTO.parameters();

			// Check if RequestObjectDTO contains the annotated fieldName.
			final boolean valid = parameterList.stream()
					.anyMatch(parameter -> parameter.getName()
													.equals(fieldName));

			if (!valid) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(this.constraintAnnotation.message())
					   .addConstraintViolation();

				return false;
			}
			return true;
		} catch (final Exception e) {
			e.printStackTrace();
			// If s.o. would prefer that such an error is silently dropped, an
			// attribute "ignoreExceptions" could be added to the
			// FieldsMatchToRequestObjectDTO annotation.
			throw new RuntimeException("An error occurred when validating the field '"
					+ AuthUserManagementRestEndpoint.USERNAME_REQUEST_PARAMETER + "' on bean of type '"
					+ requestObjectDTO.getClass().getName(), e);
		}
	}
}
