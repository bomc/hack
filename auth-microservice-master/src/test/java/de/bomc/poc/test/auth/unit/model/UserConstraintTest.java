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
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.auth.unit.model;

import de.bomc.poc.auth.model.usermanagement.SystemUser;
import de.bomc.poc.auth.model.usermanagement.User;
import de.bomc.poc.auth.model.validation.constraint.BomcFuture;
import org.hibernate.validator.constraints.NotBlank;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.executable.ExecutableValidator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;

/**
 * This class tests the constraints of the {@link User}.
 * <p/>
 * https://github.com/hibernate/hibernate-validator/blob/master/documentation/src/test/java/org/hibernate/validator/referenceguide/chapter03/validation/CarTest.java
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 16.03.2016
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserConstraintTest {

    private static final String TEST_USER1 = "Test Bomc1";
    private static final String ERROR_CONSTRAINT_MESSAGE_FUTURE = "Date must be in the future!";
    private static final String ERROR_CONSTRAINT_MESSAGE_NULL = "darf nicht null sein";
    private static final String ERROR_CONSTRAINT_MESSAGE_EMPTY = "darf nicht leer sein";
    private static final String ERROR_CONSTRAINT_MESSAGE_SIZE = "muss zwischen 5 und 2147483647 liegen";
    // Validator types.
    private static Validator validator;
    private static ExecutableValidator executableValidator;

    /**
     * Setting up the validator types.
     */
    @BeforeClass
    public static void setupValidators() {

        final ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        validator = vf.getValidator();
        executableValidator = validator.forExecutables();
    }

    @Test
    public void test01_coUsernameConstraint_Pass() {
        System.out.println("UserTest#test01_coUsernameConstraint_Pass");

        final User user = new User(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(ERROR_CONSTRAINT_MESSAGE_EMPTY, equalTo(violations.iterator()
                                                                     .next()
                                                                     .getMessage()));
        assertThat(violations.size(), is(1));

        Class<? extends Annotation>
            constraintType =
            violations.iterator()
                      .next()
                      .getConstraintDescriptor()
                      .getAnnotation()
                      .annotationType();
        assertThat(constraintType, is(equalTo(NotBlank.class)));

        //
        violations.clear();
        final User user2 = new User("");
        violations = validator.validate(user2);
        Iterator<ConstraintViolation<User>> iterator = violations.iterator();
        assertThat(iterator.next()
                           .getMessage(), anyOf(equalTo(ERROR_CONSTRAINT_MESSAGE_SIZE), equalTo(ERROR_CONSTRAINT_MESSAGE_EMPTY)));
        assertThat(iterator.next()
                           .getMessage(), anyOf(equalTo(ERROR_CONSTRAINT_MESSAGE_SIZE), equalTo(ERROR_CONSTRAINT_MESSAGE_EMPTY)));
        assertThat(violations.size(), is(2));

        constraintType =
            violations.iterator()
                      .next()
                      .getConstraintDescriptor()
                      .getAnnotation()
                      .annotationType();
        assertThat(constraintType, anyOf(equalTo(NotBlank.class), equalTo(Size.class)));

        //
        violations.clear();
        final User user3 = new User(" ");
        violations = validator.validate(user3);

        iterator = violations.iterator();
        assertThat(iterator.next()
                           .getMessage(), anyOf(equalTo(ERROR_CONSTRAINT_MESSAGE_SIZE), equalTo(ERROR_CONSTRAINT_MESSAGE_EMPTY)));
        assertThat(iterator.next()
                           .getMessage(), anyOf(equalTo(ERROR_CONSTRAINT_MESSAGE_SIZE), equalTo(ERROR_CONSTRAINT_MESSAGE_EMPTY)));

        assertThat(violations.size(), is(2));

        constraintType =
            violations.iterator()
                      .next()
                      .getConstraintDescriptor()
                      .getAnnotation()
                      .annotationType();
        assertThat(constraintType, anyOf(equalTo(NotBlank.class), equalTo(Size.class)));
    }

    @Test
    public void test02_coSystemUser_PasswordConstraint_Pass() throws Exception {
        System.out.println("UserTest#test02_coSystemUser_UsernamePasswordConstraint");

        //
        Constructor<SystemUser> constructor = SystemUser.class.getConstructor(String.class, String.class);
        final Object[] parameterValues = {TEST_USER1, null};
        Set<ConstraintViolation<SystemUser>> violations = executableValidator.validateConstructorParameters(constructor, parameterValues);

        assertThat(ERROR_CONSTRAINT_MESSAGE_EMPTY, is(equalTo(violations.iterator()
                                                                        .next()
                                                                        .getMessage())));
        assertThat(violations.size(), is(1));

        Class<? extends Annotation>
            constraintType =
            violations.iterator()
                      .next()
                      .getConstraintDescriptor()
                      .getAnnotation()
                      .annotationType();
        assertThat(constraintType, is(equalTo(NotBlank.class)));

        //
        violations.clear();
        final Object[] parameterValues1 = {TEST_USER1, ""};
        violations = executableValidator.validateConstructorParameters(constructor, parameterValues1);

        assertThat(ERROR_CONSTRAINT_MESSAGE_EMPTY, is(equalTo(violations.iterator()
                                                                        .next()
                                                                        .getMessage())));
        assertThat(violations.size(), is(1));

        constraintType =
            violations.iterator()
                      .next()
                      .getConstraintDescriptor()
                      .getAnnotation()
                      .annotationType();
        assertThat(constraintType, is(equalTo(NotBlank.class)));
    }

    @Test
    public void test03_FutureExpirationDateConstraint_Pass() {
        System.out.println("UserTest#test03_FutureExpirationDateConstraint_Pass");

        final User user = new User(TEST_USER1);
        user.setExpirationDate(LocalDateTime.of(2010, 11, 29, 9, 4, 3, 100));

        final Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(ERROR_CONSTRAINT_MESSAGE_FUTURE, is(equalTo(violations.iterator()
                                                                         .next()
                                                                         .getMessage())));
        assertThat(violations.size(), is(1));

        Class<? extends Annotation>
            constraintType =
            violations.iterator()
                      .next()
                      .getConstraintDescriptor()
                      .getAnnotation()
                      .annotationType();
        assertThat(constraintType, is(equalTo(BomcFuture.class)));
    }

    @Test
    public void test04_NullMethodExpirationDateConstraint() throws Exception {
        System.out.println("UserTest#test04_NullMethodExpirationDateConstraint");

        final User user = new User(TEST_USER1);
        final Method method = User.class.getMethod("setExpirationDate", LocalDateTime.class);
        final Object[] parameterValues = {null};
        Set<ConstraintViolation<User>> violations = executableValidator.validateParameters(user, method, parameterValues);

        assertThat(ERROR_CONSTRAINT_MESSAGE_NULL, is(equalTo(violations.iterator()
                                                                       .next()
                                                                       .getMessage())));
        assertThat(violations.size(), is(1));

        Class<? extends Annotation>
            constraintType =
            violations.iterator()
                      .next()
                      .getConstraintDescriptor()
                      .getAnnotation()
                      .annotationType();
        assertThat(constraintType, is(equalTo(NotNull.class)));
    }
}
