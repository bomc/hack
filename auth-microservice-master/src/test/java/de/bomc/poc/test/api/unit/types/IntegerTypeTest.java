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
package de.bomc.poc.test.api.unit.types;

import de.bomc.poc.api.generic.Parameter;
import de.bomc.poc.api.generic.types.AbstractType;
import de.bomc.poc.api.generic.types.IntegerType;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IntegerTypeTest {

    private static final String LOG_PREFIX = "IntegerTypeTest#";
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void test01_createIntegerType() {
        System.out.println(LOG_PREFIX + "test01_createIntegerType - starte...");

        final IntegerType d = new IntegerType(1);

        assertThat(1, is(d.getValue()));

        final Class<Integer> expectedClass = Integer.class;
        assertThat(expectedClass, equalTo(d.getReturnedClass()));

        assertThat("1", is(d.getLiteral(1)));
    }

    @Test
    public void test02_createIntegerType() {
        System.out.println(LOG_PREFIX + "test02_createIntegerType - starte...");

        final IntegerType d = new IntegerType(1);

        assertThat(1, is(d.getValue()));
    }

    @Test
    public void test03_hash_equals() {
        System.out.println(LOG_PREFIX + "test03_hash_equals - starte...");

        final Set<IntegerType> s = new HashSet<>();

        s.add(new IntegerType(1));
        s.add(new IntegerType(2));
        s.add(new IntegerType(2));

        assertThat(2, is(s.size()));
        assertThat(s, contains(new IntegerType(1), new IntegerType(2)));
    }

    @Test
    public void test04_hash_equals() {
        System.out.println(LOG_PREFIX + "test04_hash_equals - starte...");

        final List<IntegerType> l = new ArrayList<>();

        l.add(new IntegerType(1));
        l.add(new IntegerType(2));
        l.add(new IntegerType(1));

        assertThat(3, is(l.size()));

        assertThat(l, contains(new IntegerType(1), new IntegerType(2), new IntegerType(1)));
    }

	@Test
	@SuppressWarnings("unused")
    public void test05_nullValue() {
        System.out.println(LOG_PREFIX + "test05_nullValue - starte...");

        this.thrown.expect(NumberFormatException.class);

        final IntegerType b = new IntegerType(new Integer(null));
    }

    @Test
    public void test06_jaxb() throws Exception {
        System.out.println(LOG_PREFIX + "test06_jaxb - starte");

        final JAXBContext context = JAXBContext.newInstance(Parameter.class, AbstractType.class, IntegerType.class);

        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        final IntegerType b = new IntegerType(1);

        marshaller.marshal(b, System.out);
        marshaller.marshal(b, new File("target/x.xml"));

        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final IntegerType bUnmarshall = (IntegerType)unmarshaller.unmarshal(new File("target/x.xml"));

        assertThat(bUnmarshall, is(equalTo(b)));
    }
}
