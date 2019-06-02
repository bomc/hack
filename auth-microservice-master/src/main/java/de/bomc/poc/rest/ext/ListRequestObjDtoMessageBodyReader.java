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
import de.bomc.poc.api.jaxb.GenericRequestObjectDTOCollectionMapper;
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

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * A {@link MessageBodyReader} for unmarshalling the {@link GenericRequestObjectDTOCollectionMapper}. This is a custom wrapper for {@link RequestObjectDTO} list. 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Provider
@Consumes(MediaType.WILDCARD)
public class ListRequestObjDtoMessageBodyReader implements MessageBodyReader<GenericRequestObjectDTOCollectionMapper> {

	// NOTE: A logger injection is not possible here, because this class could be
	// used by a client that is not running in a cdi-container.

	@Override
	public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
		// Example for using with a ArrayList.
		// return type == new ArrayList<RequestObjectDTO>().getClass();

		return type == GenericRequestObjectDTOCollectionMapper.class;
	}

	@Override
    public GenericRequestObjectDTOCollectionMapper readFrom(final Class<GenericRequestObjectDTOCollectionMapper> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders,
                                     final InputStream entityStream) throws IOException, WebApplicationException {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(GenericRequestObjectDTOCollectionMapper.class, RequestObjectDTO.class, Parameter.class, AbstractType.class, BooleanType.class, DoubleType.class, IntegerType.class, LongType.class, StringType.class,
                JaxbGenMapAdapter.class, MapEntryType.class, MapType.class);
            final GenericRequestObjectDTOCollectionMapper
                genericRequestObjectDTOCollectionMapper = (GenericRequestObjectDTOCollectionMapper)jaxbContext.createUnmarshaller().unmarshal(entityStream);

            return genericRequestObjectDTOCollectionMapper;
        } catch (final JAXBException jaxbException) {
            throw new ProcessingException("ListRequestObjDtoMessageBodyReader#readFrom - Deserializing of RequestObjectDTO failed!", jaxbException);
        }
    }
}
