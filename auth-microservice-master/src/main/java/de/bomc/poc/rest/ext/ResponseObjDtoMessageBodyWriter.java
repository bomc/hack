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
package de.bomc.poc.rest.ext;

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
import org.apache.log4j.Logger;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * A {@link MessageBodyWriter} for marshalling the {@link ResponseObjectDTO}.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Provider
@Produces(MediaType.WILDCARD)
public class ResponseObjDtoMessageBodyWriter implements MessageBodyWriter<ResponseObjectDTO> {

    private static final String LOG_PREFIX = "#rest#jaxrs#";
    // NOTE: Hier kann nicht der 'Logger' injected werde, da der Writer auch im Client verwendet werden koennte.
    private final Logger logger = Logger.getLogger(ResponseObjDtoMessageBodyWriter.class.getSimpleName());

    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {

//        if ("ch.bs.zid.kdm.api.transfer.request".equals(type.getPackage()
//            .getName()) && mediaType.getType()
//            .equals("application") && mediaType.getSubtype()
//            .matches("vnd\\.bomc\\..*\\..*\\+xml"))
//        {
//            return true;
//        }
//        return false;

    	System.out.println(LOG_PREFIX + "ResponseObjDtoMessageBodyWriter#isWriteable [isWriteable=" + (type == ResponseObjectDTO.class) + "]");
    	this.logger.debug(LOG_PREFIX + "ResponseObjDtoMessageBodyWriter#isWriteable [isWriteable=" + (type == ResponseObjectDTO.class) + "]");
    	
        return type == ResponseObjectDTO.class;
    }

    @Override
    public long getSize(final ResponseObjectDTO responseObjectDTO, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        // deprecated by JAX-RS 2.0.
        return -1;
    }

	@Override
	public void writeTo(final ResponseObjectDTO responseObjectDTO, final Class<?> type, final Type genericType,
			final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders,
			final OutputStream entityStream) throws IOException, WebApplicationException {
		this.logger.debug(LOG_PREFIX + "ResponseObjDtoMessageBodyWriter#writeTo [" + responseObjectDTO + "]");

		try {
			final JAXBContext jaxbContext = JAXBContext.newInstance(ResponseObjectDTO.class, Parameter.class,
					AbstractType.class, BooleanType.class, DoubleType.class, IntegerType.class, LongType.class,
					StringType.class, JaxbGenMapAdapter.class, MapEntryType.class, MapType.class);

			// Serialisiere das Entity-ResponseObjectDTO nach Entity-output
			// stream.
			jaxbContext.createMarshaller().marshal(responseObjectDTO, entityStream);
		} catch (final JAXBException jaxbException) {
			throw new ProcessingException(LOG_PREFIX + "serializing of the ResponseObjectDTO failed!", jaxbException);
		}
	}
}
