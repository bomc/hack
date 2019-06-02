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

import de.bomc.poc.api.generic.types.DateType;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
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
public class DateTypeTest {

    private static final String LOG_PREFIX = "DateTypeTest#";
    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    private final DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    @Test
    public void test01_createDateType() {
        System.out.println(LOG_PREFIX + "test01_createDateType - starte...");

        final Date date = new Date();

        final DateType d = new DateType(date);

        assertThat(date, is(d.getValue()));

        final Class<Date> expectedClass = Date.class;
        assertThat(expectedClass, equalTo(d.getReturnedClass()));

        assertThat(this.df.format(date), is(d.getLiteral(date)));
    }

    @Test
    public void test03_hash_equals() {
        System.out.println(LOG_PREFIX + "test03_hash_equals - starte...");

        final Set<DateType> s = new HashSet<>();

        final Date date = new Date();

        s.add(new DateType(date));
        s.add(new DateType(date));
        s.add(new DateType(date));

        assertThat(1, is(s.size()));
        assertThat(s, contains(new DateType(date)));
    }

    @Test
    public void test04_equals() {
        System.out.println(LOG_PREFIX + "test04_equals - starte...");

        final Calendar cal = new GregorianCalendar(2015, 5, 5);

        final Date date = cal.getTime();
        final Date date2 = cal.getTime();

        final DateType d1 = new DateType(date);
        final DateType d2 = new DateType(date2);

        assertThat(true, is(d1.equals(d2)));
    }

	@Test
	@SuppressWarnings("unused")
    public void test05_nullValue() {
        System.out.println(LOG_PREFIX + "test05_nullValue - starte...");

        this.thrown.expect(NullPointerException.class);

        final DateType b = new DateType(null);
    }

    @Test
    public void test06_jaxb() throws Exception {
        System.out.println(LOG_PREFIX + "test06_jaxb - starte");

        final JAXBContext context = JAXBContext.newInstance(DateType.class);

        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        final Date date = new Date();

        final DateType d = new DateType(date);

        final StringWriter sw = new StringWriter();
        marshaller.marshal(d, System.out);
        marshaller.marshal(d, sw);

        final StringReader sr = new StringReader(sw.toString());
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final DateType dUnmarshall = (DateType)unmarshaller.unmarshal(sr);

        assertThat(dUnmarshall, is(d));
    }
}
