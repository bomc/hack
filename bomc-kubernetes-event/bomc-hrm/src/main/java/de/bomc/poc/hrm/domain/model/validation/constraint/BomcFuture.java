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
package de.bomc.poc.hrm.domain.model.validation.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;

import de.bomc.poc.hrm.domain.model.validation.BomcFutureValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The annotated element must be a date in the future.
 * <p/>
 * Supported types are:
 * <ul>
 * <li><code>java.time.LocalDateTime</code></li>
 * </ul>
 * <p/>
 * <code>null</code> elements are considered valid.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 *
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {BomcFutureValidator.class})
public @interface BomcFuture {

    String message() default "Date must be in the future!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several <code>@BomcFuture</code> annotations on the same element
     *
     * @see BomcFuture
     *
     * @author <a href="mailto:bomc@myHome.org">bomc</a>
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        BomcFuture[] value();
    }
}
