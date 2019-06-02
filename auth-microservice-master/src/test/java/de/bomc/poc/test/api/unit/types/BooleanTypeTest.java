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
import de.bomc.poc.api.generic.types.BooleanType;
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
public class BooleanTypeTest {

    private static final String LOG_PREFIX = "BooleanTypeTest#";

    @Test
    public void test01_createBooleanType() {
        System.out.println(LOG_PREFIX + "test01_createBooleanType - starte...");

        final BooleanType b = new BooleanType(true);

        assertThat(true, is(b.getValue()));

        final Class<Boolean> expectedClass = Boolean.class;
        assertThat(expectedClass, equalTo(b.getReturnedClass()));

        assertThat("1", is(b.getLiteral(true)));
    }

    @Test
    public void test02_createBooleanType() {
        System.out.println(LOG_PREFIX + "test02_createBooleanType - starte...");

        final BooleanType b = new BooleanType(true);

        assertThat(true, is(b.getValue()));

        final Class<Boolean> expectedClass = Boolean.class;
        assertThat(expectedClass, equalTo(b.getReturnedClass()));

        assertThat("1", is(b.getLiteral(true)));
    }

    @Test
    public void test03_hash_equals() {
        System.out.println(LOG_PREFIX + "test03_hash_equals - starte...");

        final Set<BooleanType> s = new HashSet<>();

        s.add(new BooleanType(true));
        s.add(new BooleanType(false));
        s.add(new BooleanType(true));

        assertThat(2, is(s.size()));
        assertThat(s, contains(new BooleanType(false), new BooleanType(true)));
    }

    @Test
    public void test04_hash_equals() {
        System.out.println(LOG_PREFIX + "test04_hash_equals - starte...");

        final List<BooleanType> l = new ArrayList<>();

        l.add(new BooleanType(true));
        l.add(new BooleanType(false));
        l.add(new BooleanType(true));

        assertThat(3, is(l.size()));

        assertThat(l, contains(new BooleanType(true), new BooleanType(false), new BooleanType(true)));
    }

    @Test
    public void test05_nullValue() {
        System.out.println(LOG_PREFIX + "test05_nullValue - starte...");

        final BooleanType b = new BooleanType(Boolean.valueOf(null));

        b.toString();

        assertThat(false, is(b.getValue()));
    }

    @Test
    public void test06_jaxb() throws Exception {
        System.out.println(LOG_PREFIX + "test06_jaxb - starte");

        final JAXBContext context = JAXBContext.newInstance(Parameter.class, AbstractType.class, BooleanType.class);

        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        final BooleanType b = new BooleanType(true);

        marshaller.marshal(b, System.out);
        marshaller.marshal(b, new File("target/x.xml"));

        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final BooleanType bUnmarshall = (BooleanType)unmarshaller.unmarshal(new File("target/x.xml"));

        assertThat(bUnmarshall, is(equalTo(b)));
    }
}
