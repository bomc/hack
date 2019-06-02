/**
 * Project: MY_POC
 * <p/>
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
 * <p/>
 * Copyright (c): BOMC, 2015
 */
package de.bomc.poc.auth.model.validation.constraint;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <pre>
 *
 * 	'^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\S+$).{6,10}$'
 *
 * 	^ 				# start-of-string 
 * 	(?=.*[0-9]) 	# a digit must occur at least once
 * 	(?=.*[a-z]) 	# a lower case letter must occur at least once 
 * 	(?=.*[A-Z]) 	# an upper case letter must occur at least once 
 * 	(?=.*[@#$%^&+=])# a special character must occur at least once 
 * 	(?=\S+$) 		# no whitespace allowed in the entire string 
 * 	.{6,10} 		# anything, at least six to ten places though 
 * 	$ 				# end-of-string
 *
 * </pre>
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 *
 */
@Documented
@NotBlank
@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,10}$")
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
public @interface BomcPasswordValidator {

    // String message() default
    // "{org.hibernate.validator.referenceguide.chapter06.CheckCase." +
    // "message}";
    String message() default "Password enspricht nicht den Vorgaben, das Passwort muss...";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
