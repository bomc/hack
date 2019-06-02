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
import de.bomc.poc.api.generic.types.StringType;
import org.junit.FixMethodOrder;
import org.junit.Test;
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
public class StringTypeTest {

    private static final String LOG_PREFIX = "StringTypeTest#";

    @Test
    public void test01_createStringType() {
        System.out.println(LOG_PREFIX + "test01_createStringType - starte...");

        final StringType s = new StringType("tzdbmm");

        assertThat("tzdbmm", is(s.getValue()));

        final Class<String> expectedClass = String.class;
        assertThat(expectedClass, equalTo(s.getReturnedClass()));

        assertThat("tzdbmm", is(s.getLiteral("tzdbmm")));
    }

    @Test
    public void test02_createStringType() {
        System.out.println(LOG_PREFIX + "test02_createStringType - starte...");

        final StringType s = new StringType("tzdbmm");

        assertThat("tzdbmm", is(s.getValue()));
    }

    @Test
    public void test03_hash_equals() {
        System.out.println(LOG_PREFIX + "test03_hash_equals - starte...");

        final Set<StringType> s = new HashSet<>();

        s.add(new StringType("tzdbmm1"));
        s.add(new StringType("tzdbmm2"));
        s.add(new StringType("tzdbmm2"));

        assertThat(2, is(s.size()));
        assertThat(s, contains(new StringType("tzdbmm1"), new StringType("tzdbmm2")));
    }

    @Test
    public void test04_hash_equals() {
        System.out.println(LOG_PREFIX + "test04_hash_equals - starte...");

        final List<StringType> l = new ArrayList<>();

        l.add(new StringType("tzdbmm1"));
        l.add(new StringType("tzdbmm2"));
        l.add(new StringType("tzdbmm1"));

        assertThat(3, is(l.size()));

        assertThat(l, contains(new StringType("tzdbmm1"), new StringType("tzdbmm2"), new StringType("tzdbmm1")));
    }

    @Test
    public void test05_nullValue() {
        System.out.println(LOG_PREFIX + "test05_nullValue - starte...");

        final StringType b = new StringType(null);

        b.toString();
    }

    @Test
    public void test06_jaxb() throws Exception {
        System.out.println(LOG_PREFIX + "test06_jaxb - starte");

        final JAXBContext context = JAXBContext.newInstance(Parameter.class, AbstractType.class, StringType.class);

        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        final StringType b = new StringType("tzdbmm");

        marshaller.marshal(b, System.out);
        marshaller.marshal(b, new File("target/x.xml"));

        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final StringType bUnmarshall = (StringType)unmarshaller.unmarshal(new File("target/x.xml"));

        assertThat(bUnmarshall, is(equalTo(b)));
    }
}
