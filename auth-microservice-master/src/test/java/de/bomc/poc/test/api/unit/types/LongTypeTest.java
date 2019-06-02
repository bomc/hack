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
import de.bomc.poc.api.generic.types.LongType;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
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
public class LongTypeTest {

    private static final String LOG_PREFIX = "LongTypeTest#";
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void test01_createLongType() {
        System.out.println(LOG_PREFIX + "test01_createLongType - starte...");

        final LongType l = new LongType(1l);

        assertThat(1l, is(l.getValue()));

        final Class<Long> expectedClass = Long.class;
        assertThat(expectedClass, equalTo(l.getReturnedClass()));

        assertThat("1", is(l.getLiteral(1l)));
    }

    @Test
    public void test02_createLongType() {
        System.out.println(LOG_PREFIX + "test02_createLongType - starte...");

        final LongType l = new LongType((long)1);

        assertThat(1l, is(l.getValue()));
    }

    @Test
    public void test03_hash_equals() {
        System.out.println(LOG_PREFIX + "test03_hash_equals - starte...");

        final Set<LongType> s = new HashSet<>();

        s.add(new LongType(1));
        s.add(new LongType(2));
        s.add(new LongType(2));

        assertThat(2, is(s.size()));
        assertThat(s, contains(new LongType(1), new LongType(2)));
    }

    @Test
    public void test04_hash_equals() {
        System.out.println(LOG_PREFIX + "test04_hash_equals - starte...");

        final List<LongType> l = new ArrayList<>();

        l.add(new LongType(1));
        l.add(new LongType(2));
        l.add(new LongType(1));

        assertThat(3, is(l.size()));

        assertThat(l, contains(new LongType(1), new LongType(2), new LongType(1)));
    }

	@Test
	@SuppressWarnings("unused")
    public void test05_nullValue() {
        System.out.println(LOG_PREFIX + "test05_nullValue - starte...");

        this.thrown.expect(NumberFormatException.class);

        final LongType b = new LongType(new Integer(null));
    }

    @Test
    public void test06_jaxb() throws Exception {
        System.out.println(LOG_PREFIX + "test06_jaxb - starte");

        final JAXBContext context = JAXBContext.newInstance(Parameter.class, AbstractType.class, LongType.class);

        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        final LongType b = new LongType(1l);

        final StringWriter sw = new StringWriter();

        marshaller.marshal(b, System.out);
        marshaller.marshal(b, sw);

        final StringReader sr = new StringReader(sw.toString());
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final LongType bUnmarshall = (LongType)unmarshaller.unmarshal(sr);

        assertThat(bUnmarshall, is(equalTo(b)));
    }
}
