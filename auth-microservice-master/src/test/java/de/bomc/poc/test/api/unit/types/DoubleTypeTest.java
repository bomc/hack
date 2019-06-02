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
import de.bomc.poc.api.generic.types.DoubleType;
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
 * Kommentar.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: 44286 $ $Author: tzdena $ $Date: 2015-10-01 17:26:19 +0200 (Do, 01 Okt 2015) $
 * @since 28.07.2015
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DoubleTypeTest {

    private static final String LOG_PREFIX = "DoubleTypeTest#";
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void test01_createDoubleType() {
        System.out.println(LOG_PREFIX + "test01_createDoubleType - starte...");

        final DoubleType d = new DoubleType(1d);

        assertThat(1d, is(d.getValue()));

        final Class<Double> expectedClass = Double.class;
        assertThat(expectedClass, equalTo(d.getReturnedClass()));

        assertThat("1.0", is(d.getLiteral(1d)));
    }

    @Test
    public void test02_createDoubleType() {
        System.out.println(LOG_PREFIX + "test02_createDoubleType - starte...");

        final DoubleType d = new DoubleType(1d);

        assertThat(1d, is(d.getValue()));
    }

    @Test
    public void test03_hash_equals() {
        System.out.println(LOG_PREFIX + "test03_hash_equals - starte...");

        final Set<DoubleType> s = new HashSet<>();

        s.add(new DoubleType(1d));
        s.add(new DoubleType(2d));
        s.add(new DoubleType(2d));

        assertThat(2, is(s.size()));
        assertThat(s, contains(new DoubleType(1d), new DoubleType(2d)));
    }

    @Test
    public void test04_hash_equals() {
        System.out.println(LOG_PREFIX + "test04_hash_equals - starte...");

        final List<DoubleType> l = new ArrayList<>();

        l.add(new DoubleType(1d));
        l.add(new DoubleType(2d));
        l.add(new DoubleType(1d));

        assertThat(3, is(l.size()));

        assertThat(l, contains(new DoubleType(1d), new DoubleType(2d), new DoubleType(1d)));
    }

	@Test
	@SuppressWarnings("unused")
    public void test05_nullValue() {
        System.out.println(LOG_PREFIX + "test05_nullValue - starte...");

        this.thrown.expect(NullPointerException.class);

        final DoubleType b = new DoubleType(new Double(null));
    }

    @Test
    public void test06_jaxb() throws Exception {
        System.out.println(LOG_PREFIX + "test06_jaxb - starte");

        final JAXBContext context = JAXBContext.newInstance(Parameter.class, AbstractType.class, DoubleType.class);

        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        final DoubleType b = new DoubleType(1.0);

        marshaller.marshal(b, System.out);
        marshaller.marshal(b, new File("target/x.xml"));

        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final DoubleType bUnmarshall = (DoubleType)unmarshaller.unmarshal(new File("target/x.xml"));

        assertThat(bUnmarshall, is(equalTo(b)));
    }
}
