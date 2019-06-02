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
import de.bomc.poc.api.jaxb.GenericResponseObjectDTOCollectionMapper;
import de.bomc.poc.api.jaxb.JaxbGenMapAdapter;
import de.bomc.poc.api.jaxb.MapEntryType;
import de.bomc.poc.api.jaxb.MapType;
import de.bomc.poc.api.generic.Parameter;

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
public class ListResponseObjDtoMessageBodyWriter implements MessageBodyWriter<GenericResponseObjectDTOCollectionMapper> {

	// NOTE: A logger injection is not possible here, because this class could be
	// used by a client, that is not running in a cdi container.

	@Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {

//        if ("de.bomc.poc.api.transfer.response".equals(type.getPackage()
//            .getName()) && mediaType.getType()
//            .equals("application") && mediaType.getSubtype()
//            .matches("vnd\\.bomc\\..*\\..*\\+xml"))
//        {
//            return true;
//        }
//        return false;
		
		// Example for ArrayList type.
//    	return type == new ArrayList<ResponseObjectDTO>().getClass();
		return type == GenericResponseObjectDTOCollectionMapper.class;
    }

    @Override
    public long getSize(final GenericResponseObjectDTOCollectionMapper genericResponseObjectDTOCollectionMapper, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        // deprecated by JAX-RS 2.0.
        return -1;
    }

	@Override
	public void writeTo(final GenericResponseObjectDTOCollectionMapper genericResponseObjectDTOCollectionMapper, final Class<?> type,
			final Type genericType, final Annotation[] annotations, final MediaType mediaType,
			final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream)
					throws IOException, WebApplicationException {
		try {
			final JAXBContext jaxbContext = JAXBContext.newInstance(GenericResponseObjectDTOCollectionMapper.class,
					ResponseObjectDTO.class, Parameter.class, AbstractType.class, BooleanType.class, DoubleType.class,
					IntegerType.class, LongType.class, StringType.class, JaxbGenMapAdapter.class, MapEntryType.class,
					MapType.class);

			// Do the marshalling.
			jaxbContext.createMarshaller().marshal(genericResponseObjectDTOCollectionMapper, entityStream);
		} catch (final JAXBException jaxbException) {
			throw new ProcessingException("ListResponseObjDtoMessageBodyWriter#writeTo Serializing of the ResponseObjectDTO failed!", jaxbException);
		}
	}
}
