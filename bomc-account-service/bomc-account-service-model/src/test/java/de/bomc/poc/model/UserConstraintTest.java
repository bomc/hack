package de.bomc.poc.model;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.bomc.poc.model.account.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.executable.ExecutableValidator;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Set;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This class tests {@link Person} functionality.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 10.08.2016
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserConstraintTest {
    private static final Logger LOGGER = Logger.getLogger(UserConstraintTest.class);
    private static final String LOG_PREFIX = "UserConstraintTest#";

    private static final String ERROR_CONSTRAINT_MESSAGE_NOT_NULL = "darf nicht null sein";
    private static final String ERROR_CONSTRAINT_MESSAGE_SIZE = "muss zwischen 5 und 32 liegen";

    // Validator types.
    private static Validator validator;

    /**
     * Setting up the validator types.
     */
    @BeforeClass
    public static void setupValidators() {
        final ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        validator = vf.getValidator();
        final ExecutableValidator executableValidator = validator.forExecutables();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test01_ConstructorUsernameConstraint_Pass() {
        LOGGER.debug(LOG_PREFIX + "test01_ConstructorUsernameConstraint_Pass");

        // Check NotNull constraint.
        final User user = new User(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(ERROR_CONSTRAINT_MESSAGE_NOT_NULL, equalTo(violations.iterator()
                                                                     .next()
                                                                     .getMessage()));
        assertThat(violations.size(), is(1));

        // Check annotation.
        Class<? extends Annotation>
            constraintType =
            violations.iterator()
                      .next()
                      .getConstraintDescriptor()
                      .getAnnotation()
                      .annotationType();
        assertThat(constraintType, is(equalTo(NotNull.class)));

        // Check size constraint, min = 5.
        violations.clear();
        final User user2 = new User("");
        violations = validator.validate(user2);
        final Iterator<ConstraintViolation<User>> iterator = violations.iterator();
        assertThat(iterator.next()
                           .getMessage(), anyOf(equalTo(ERROR_CONSTRAINT_MESSAGE_SIZE)));
        assertThat(violations.size(), is(1));

        // Check annotation.
        constraintType =
            violations.iterator()
                      .next()
                      .getConstraintDescriptor()
                      .getAnnotation()
                      .annotationType();
        assertThat(constraintType, anyOf(equalTo(Size.class)));
    }
}
