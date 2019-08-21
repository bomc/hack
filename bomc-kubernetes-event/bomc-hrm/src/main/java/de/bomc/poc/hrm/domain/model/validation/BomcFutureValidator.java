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
package de.bomc.poc.hrm.domain.model.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import de.bomc.poc.hrm.domain.model.validation.constraint.BomcFuture;

import java.time.LocalDate;
import java.time.temporal.Temporal;

/**
 * The annotated element must be a date in the future.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a
 *
 */
public class BomcFutureValidator implements ConstraintValidator<BomcFuture, Temporal> {

	@Override
	public void initialize(BomcFuture constraintAnnotation) {
	}

	@Override
	public boolean isValid(Temporal value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}

		LocalDate ld = LocalDate.from(value);
		return ld.isAfter(LocalDate.now());
	}
}
