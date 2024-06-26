package de.bomc.poc.exception.test.unit;

import de.bomc.poc.exception.core.web.ApiErrorResponseObject;
import de.bomc.poc.exception.test.TestErrorCode;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Tests marshalling and unmarshalling of {link ApiErrorResponeObject}.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: 6790 $ $Author: tzdbmm $ $Date: 2016-07-19 09:06:34 +0200 (Di, 19 Jul 2016) $
 * @since 13.07.2016
 */
public class ApiErrorResponeObjectTest {

    private static final String LOG_PREFIX = "ApiErrorResponeObjectTest#";
    private static final Logger LOGGER = Logger.getLogger(ApiErrorResponeObjectTest.class);
    private ApiErrorResponseObject apiErrorResponseObject;
    private String uuid;

    @Before
    public void setup() {
        this.uuid =
            UUID.randomUUID()
                          .toString();

        this.apiErrorResponseObject = new ApiErrorResponseObject(this.uuid, Response.Status.INTERNAL_SERVER_ERROR, TestErrorCode.TEST_00102);
    }

    @Test
    public void test010_marshall_unmarshall_pass() throws Exception {
        LOGGER.debug(LOG_PREFIX + "test010_marshall_unmarshall_pass");

        final JAXBContext context = JAXBContext.newInstance(ApiErrorResponseObject.class);

        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

        final StringWriter sw = new StringWriter();
        marshaller.marshal(this.apiErrorResponseObject, sw);

        LOGGER.debug(LOG_PREFIX + "test010_marshall_unmarshall_pass - " + System.lineSeparator() + sw);

        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final ApiErrorResponseObject unmarshalled = (ApiErrorResponseObject)unmarshaller.unmarshal(new StreamSource(new StringReader(sw.toString())));

        LOGGER.debug(LOG_PREFIX + "test010_marshall_unmarshall_pass - " + System.lineSeparator() + unmarshalled);

        assertThat(unmarshalled, is(equalTo(this.apiErrorResponseObject)));
    }

    @Test
    public void test020_json_pass() throws Exception {
        LOGGER.debug(LOG_PREFIX + "test020_json_pass");

        final JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("ErrorCode", this.apiErrorResponseObject.getErrorCode());
        jsonObjectBuilder.add("ShortErrorCodeDescription", this.apiErrorResponseObject.getShortErrorCodeDescription());
        jsonObjectBuilder.add("Uuid", this.apiErrorResponseObject.getUuid());
        jsonObjectBuilder.add("Status", this.apiErrorResponseObject.getStatus()
                                                                   .toString());

        final String
            jsonString =
            jsonObjectBuilder.build()
                             .toString();

        LOGGER.debug(LOG_PREFIX + "test020_json_pass" + System.lineSeparator() + jsonString);

        final JsonReader reader = Json.createReader(new StringReader(jsonString));
        final JsonObject apiErrorResponseObject = reader.readObject();
        reader.close();

        jsonObjectBuilder.add(TestErrorCode.TEST_00102.name(), apiErrorResponseObject.getString("ErrorCode"));
        jsonObjectBuilder.add(TestErrorCode.TEST_00102.getShortErrorCodeDescription(), apiErrorResponseObject.getString("ShortErrorCodeDescription"));
        jsonObjectBuilder.add(this.uuid, apiErrorResponseObject.getString("Uuid"));
        jsonObjectBuilder.add(Response.Status.INTERNAL_SERVER_ERROR.name(), apiErrorResponseObject.getString("Status"));
    }
}
