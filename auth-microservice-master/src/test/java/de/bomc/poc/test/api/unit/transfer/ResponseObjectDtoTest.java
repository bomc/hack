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

import de.bomc.poc.api.generic.transfer.response.ResponseObjectDTO;
import de.bomc.poc.api.generic.types.AbstractType;
import de.bomc.poc.api.generic.types.BooleanType;
import de.bomc.poc.api.generic.types.DoubleType;
import de.bomc.poc.api.generic.types.IntegerType;
import de.bomc.poc.api.generic.types.LongType;
import de.bomc.poc.api.generic.types.StringType;
import de.bomc.poc.api.jaxb.JaxbGenMapAdapter;
import de.bomc.poc.api.jaxb.MapEntryType;
import de.bomc.poc.api.jaxb.MapType;
import de.bomc.poc.api.generic.Parameter;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ResponseObjectDtoTest {

    private static final String LOG_PREFIX = "api#test#request#ResponseObjectDtoTest#";
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void test01_createResponseObjectDto() {
        System.out.println(LOG_PREFIX + "test01_createResponseObjectDto");

        final ResponseObjectDTO dto = ResponseObjectDTO.with(new Parameter("param1", new LongType(2l)));
        dto.and(new Parameter("param2", new StringType("tzdbmm")));

        assertThat(2, is(dto.parameters()
                            .size()));
    }

    @Test
    public void test02_jaxbList() throws Exception {
        System.out.println(LOG_PREFIX + "test02_jaxbList - starte");

        final Parameter p1 = new Parameter("tzdbmm1", new BooleanType(true));
        final Parameter p2 = new Parameter("tzdbmm2", new DoubleType(1.1));
        final Parameter p3 = new Parameter("tzdbmm3", new IntegerType(1));
        final Parameter p4 = new Parameter("tzdbmm4", new LongType(1l));
        final Parameter p5 = new Parameter("tzdbmm5", new StringType("tzdbmm"));

        final ResponseObjectDTO dto = ResponseObjectDTO.with(p1)
                                                       .and(p2)
                                                       .and(p3)
                                                       .and(p4)
                                                       .and(p5);

        final JAXBContext context = JAXBContext.newInstance(ResponseObjectDTO.class, Parameter.class, AbstractType.class, BooleanType.class, DoubleType.class, IntegerType.class, LongType.class, StringType.class,
            JaxbGenMapAdapter.class, MapEntryType.class, MapType.class);

        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        marshaller.marshal(dto, System.out);
        marshaller.marshal(dto, new File("target/x.xml"));

        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final ResponseObjectDTO dtoUnmarshall = (ResponseObjectDTO)unmarshaller.unmarshal(new File("target/x.xml"));

        System.out.println("test03_jaxbList\n " + dtoUnmarshall);

        assertThat(5, is(dto.parameters()
                            .size()));
    }

    @Test
    public void test04_equalsHashcode() {
        System.out.println(LOG_PREFIX + "test04_equalsHashcode - starte");

        final Set<ResponseObjectDTO> set = new HashSet<>();

        final Parameter p1 = new Parameter("tzdbmm1", new BooleanType(true));
        final Parameter p2 = new Parameter("tzdbmm2", new DoubleType(1.1));
        final Parameter p3 = new Parameter("tzdbmm3", new IntegerType(1));
        final Parameter p4 = new Parameter("tzdbmm4", new LongType(1l));
        final Parameter p5 = new Parameter("tzdbmm5", new StringType("tzdbmm"));

        final ResponseObjectDTO dto = ResponseObjectDTO.with(p1)
                                                       .and(p2)
                                                       .and(p3)
                                                       .and(p4)
                                                       .and(p5);

        set.add(dto);

        final Parameter p11 = new Parameter("tzdbmm1", new BooleanType(true));
        // Die Änderung, die den Vergleich fehlschlagen lässt.
        final Parameter p21 = new Parameter("tzdbmm2", new DoubleType(1.2));
        final Parameter p31 = new Parameter("tzdbmm3", new IntegerType(1));
        final Parameter p41 = new Parameter("tzdbmm4", new LongType(1l));
        final Parameter p51 = new Parameter("tzdbmm5", new StringType("tzdbmm"));

        final ResponseObjectDTO dto1 = ResponseObjectDTO.with(p11)
                                                        .and(p21)
                                                        .and(p31)
                                                        .and(p41)
                                                        .and(p51);

        assertThat(set, not(contains(dto1)));
    }

    @Test
    public void test05_equalsHashcode() {
        System.out.println(LOG_PREFIX + "test05_equalsHashcode - starte");

        final Set<ResponseObjectDTO> set = new HashSet<>();

        final Parameter p1 = new Parameter("tzdbmm1", new BooleanType(true));
        final Parameter p2 = new Parameter("tzdbmm2", new DoubleType(1.1));
        final Parameter p3 = new Parameter("tzdbmm3", new IntegerType(1));
        final Parameter p4 = new Parameter("tzdbmm4", new LongType(1l));
        final Parameter p5 = new Parameter("tzdbmm5", new StringType("tzdbmm"));

        final ResponseObjectDTO dto = ResponseObjectDTO.with(p1)
                                                       .and(p2)
                                                       .and(p3)
                                                       .and(p4)
                                                       .and(p5);

        set.add(dto);

        final Parameter p11 = new Parameter("tzdbmm1", new BooleanType(true));
        final Parameter p21 = new Parameter("tzdbmm2", new DoubleType(1.1));
        final Parameter p31 = new Parameter("tzdbmm3", new IntegerType(1));
        final Parameter p41 = new Parameter("tzdbmm4", new LongType(1l));
        final Parameter p51 = new Parameter("tzdbmm5", new StringType("tzdbmm"));

        final ResponseObjectDTO dto1 = ResponseObjectDTO.with(p11)
                                                        .and(p21)
                                                        .and(p31)
                                                        .and(p41)
                                                        .and(p51);

        // dto und dto1 sind gleich.
        assertThat(set, contains(dto1));
    }

    @Test
    public void test06_getValue() {
        System.out.println(LOG_PREFIX + "test06_getValue - starte");

        final Parameter p1 = new Parameter("tzdbmm1", new BooleanType(true));
        final Parameter p2 = new Parameter("tzdbmm2", new DoubleType(1.1));
        final Parameter p3 = new Parameter("tzdbmm3", new IntegerType(1));
        final Parameter p4 = new Parameter("tzdbmm4", new LongType(1l));
        final Parameter p5 = new Parameter("tzdbmm5", new StringType("tzdbmm"));

        final ResponseObjectDTO dto = ResponseObjectDTO.with(p1)
                                                       .and(p2)
                                                       .and(p3)
                                                       .and(p4)
                                                       .and(p5);

        final List<Parameter> dtoList = dto.parameters();

        for (final Parameter parameter : dtoList) {
            if (parameter.getType()
                         .getReturnedClass()
                         .cast(parameter.getValue()) instanceof Boolean) {
                assertThat(true, is(parameter.getValue()));
            } else if (parameter.getType()
                                .getReturnedClass()
                                .cast(parameter.getValue()) instanceof Double) {
                assertThat(1.1, is(parameter.getValue()));
            } else if (parameter.getType()
                                .getReturnedClass()
                                .cast(parameter.getValue()) instanceof Integer) {
                assertThat(1, is(parameter.getValue()));
            } else if (parameter.getType()
                                .getReturnedClass()
                                .cast(parameter.getValue()) instanceof Long) {
                assertThat(1l, is(parameter.getValue()));
            } else if (parameter.getType()
                                .getReturnedClass()
                                .cast(parameter.getValue()) instanceof String) {
                assertThat("tzdbmm", is(parameter.getValue()));
            }
        }
    }

    @Test
    public void test07_NullValue() {
        System.out.println(LOG_PREFIX + "test07_NullValue");

        this.thrown.expect(NullPointerException.class);

        ResponseObjectDTO.with(null);
    }
}
