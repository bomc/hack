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

import de.bomc.poc.api.generic.transfer.request.RequestObjectDTO;
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

import javax.ws.rs.Consumes;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * A {@link MessageBodyReader} for unmarshalling the {@link RequestObjectDTO}.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Provider
@Consumes(MediaType.WILDCARD)
public class RequestObjDtoMessageBodyReader implements MessageBodyReader<RequestObjectDTO> {

	private static final String LOG_PREFIX = "#rest#jaxrs#";
	// NOTE: Hier kann nicht der 'Logger' injected werde, da der Reader auch im
	// Client verwendet werden koennte.
	private final Logger logger = Logger.getLogger(RequestObjDtoMessageBodyReader.class.getSimpleName());

    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {

        return type == RequestObjectDTO.class;
    }

    @Override
    public RequestObjectDTO readFrom(final Class<RequestObjectDTO> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders,
                                     final InputStream entityStream) throws IOException, WebApplicationException {
        try {
        	this.logger.debug(LOG_PREFIX + "RequestObjDtoMessageBodyReader#readFrom");

            final JAXBContext jaxbContext = JAXBContext.newInstance(RequestObjectDTO.class, Parameter.class, AbstractType.class, BooleanType.class, DoubleType.class, IntegerType.class, LongType.class, StringType.class,
                JaxbGenMapAdapter.class, MapEntryType.class, MapType.class);

            return (RequestObjectDTO)jaxbContext.createUnmarshaller()
                                         .unmarshal(entityStream);
        } catch (final JAXBException jaxbException) {
            throw new ProcessingException("Deserializing of RequestObjectDTO failed!", jaxbException);
        }
    }
}
