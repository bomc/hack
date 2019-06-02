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

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This class is a constraint for validating a parameter in <code>RequestObjectDTO</code>. The constraint is expressed via Java annotations.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Target({ANNOTATION_TYPE, METHOD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = FieldsMatchToRequestObjectDTOValidator.class)
@Documented
public @interface FieldsMatchToRequestObjectDTO {

    String message() default "{validation.constraints.FieldsMatchToRequestObjectDTO.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return The parameter that must be validated.
     */
    String parameter();

    /**
     * Defines several <code>@FieldsMatchToRequestObjectDTO</code> annotations on the same element
     * @see FieldsMatchToRequestObjectDTO
     */
    @Target({ANNOTATION_TYPE, METHOD, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        FieldsMatchToRequestObjectDTO[] value();
    }
}
