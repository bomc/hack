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
package de.bomc.poc.test.api.unit.transfer;

import de.bomc.poc.api.generic.Parameter;
import de.bomc.poc.api.generic.types.AbstractType;
import de.bomc.poc.api.generic.types.BooleanType;
import de.bomc.poc.api.generic.types.IntegerType;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.Set;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ParameterTest {

    private static final String LOG_PREFIX = "api#test#request#ParameterTest#";
    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void test01_createParameterInstance() {
        System.out.println(LOG_PREFIX + "test01_createParameterInstance - starte");

        final Parameter p = new Parameter("tzdbmm", new IntegerType(1));

        assertThat(p.getName(), is("tzdbmm"));

        final Class<Integer> expectedClass = Integer.class;
        assertThat(expectedClass, equalTo(p.getType()
                                           .getReturnedClass()));
    }

    @Test
    public void test02_jaxb() throws Exception {
        System.out.println(LOG_PREFIX + "test02_jaxb - starte");

        final JAXBContext context = JAXBContext.newInstance(Parameter.class, AbstractType.class, BooleanType.class);

        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        final Parameter p = new Parameter("tzdbmm1", new BooleanType(true));

        marshaller.marshal(p, System.out);
        marshaller.marshal(p, new File("target/x.xml"));

        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final Parameter pUnmarshall = (Parameter)unmarshaller.unmarshal(new File("target/x.xml"));

        assertThat(pUnmarshall, is(equalTo(p)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test03_NullValue() throws Exception {
        System.out.println(LOG_PREFIX + "test03_NullValue - starte");

        //
        // Testet ConstraintViolations
        final Parameter p = new Parameter(null, null);

        final Set<ConstraintViolation<Parameter>> constraintViolations = validator.validate(p);

        assertThat(2, is(constraintViolations.size()));
        for (final ConstraintViolation<Parameter> violation : constraintViolations) {
            System.out.format("%s: %s%n", violation.getPropertyPath(), violation.getMessage());
            // Der Englische Text wird für englische Spracheinstellung benötigt, wie z.B. auf dem Jenkins.
            assertThat(violation.getMessage(), anyOf(equalTo("darf nicht null sein"), anyOf(equalTo("may not be null"), anyOf(equalTo("darf nicht leer sein"), anyOf(equalTo("may not be empty"))))));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test04_EmptyValue() throws Exception {

        System.out.println(LOG_PREFIX + "test03_NullValue - starte");

        //
        // Testet ConstraintViolations
        final Parameter p = new Parameter("", new IntegerType(1));

        final Set<ConstraintViolation<Parameter>> constraintViolations = validator.validate(p);

        assertThat(1, is(constraintViolations.size()));
        for (final ConstraintViolation<Parameter> violation : constraintViolations) {
            System.out.format("%s: %s%n", violation.getPropertyPath(), violation.getMessage());
            // Der Englische Text wird für englische Spracheinstellung benötigt, wie z.B. auf dem Jenkins.
            assertThat(violation.getMessage(), anyOf(equalTo("darf nicht leer sein"), anyOf(equalTo("may not be empty"))));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test05_NullValue() throws Exception {
        //
        // Testet ConstraintViolations
        final Parameter p = new Parameter("test", null);

        final Set<ConstraintViolation<Parameter>> constraintViolations = validator.validate(p);

        assertThat(1, is(constraintViolations.size()));
        for (final ConstraintViolation<Parameter> violation : constraintViolations) {
            System.out.format("%s: %s%n", violation.getPropertyPath(), violation.getMessage());
            // Der Englische Text wird für englische Spracheinstellung benötigt, wie z.B. auf dem Jenkins.
            assertThat(violation.getMessage(), anyOf(equalTo("darf nicht null sein"), anyOf(equalTo("may not be null"))));
        }
    }
}
