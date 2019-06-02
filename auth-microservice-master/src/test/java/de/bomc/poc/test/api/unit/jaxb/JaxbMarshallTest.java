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
package de.bomc.poc.test.api.unit.jaxb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.bomc.poc.api.generic.Parameter;
import de.bomc.poc.api.generic.transfer.request.RequestObjectDTO;
import de.bomc.poc.api.generic.transfer.response.ResponseObjectDTO;
import de.bomc.poc.api.generic.types.AbstractType;
import de.bomc.poc.api.generic.types.BooleanType;
import de.bomc.poc.api.generic.types.DoubleType;
import de.bomc.poc.api.generic.types.IntegerType;
import de.bomc.poc.api.generic.types.LongType;
import de.bomc.poc.api.generic.types.StringType;
import de.bomc.poc.api.jaxb.GenericRequestObjectDTOCollectionMapper;
import de.bomc.poc.api.jaxb.GenericResponseObjectDTOCollectionMapper;
import de.bomc.poc.api.jaxb.JaxbGenMapAdapter;
import de.bomc.poc.api.jaxb.MapEntryType;
import de.bomc.poc.api.jaxb.MapType;

/**
 * Tests the marshalling and unmarshalling of the
 * {@link RequestObjectDTO}
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JaxbMarshallTest {

	@Test
	public void test01_simpleRequestObjectDTOMarshaller_Pass() throws Exception {
		System.out.println("JaxbTest#test01_simpleRequestObjectDTOMarshaller_Pass");

		final List<RequestObjectDTO> list = new ArrayList<>();

		for (int i = 0; i < 1; i++) {
			final Parameter p1 = new Parameter(("test" + i), new StringType("test" + i));
			final Parameter p2 = new Parameter(("test" + i + i), new StringType("test" + i + i));
			final RequestObjectDTO dto = RequestObjectDTO.with(p1).and(p2);
			list.add(dto);
		}

		final GenericRequestObjectDTOCollectionMapper g = new GenericRequestObjectDTOCollectionMapper();
		g.setRequestObjectDTOList(list);

		final JAXBContext jaxbContext = JAXBContext.newInstance(GenericRequestObjectDTOCollectionMapper.class, RequestObjectDTO.class,
				Parameter.class, AbstractType.class, BooleanType.class, DoubleType.class, IntegerType.class,
				LongType.class, StringType.class, JaxbGenMapAdapter.class, MapEntryType.class, MapType.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		final long time = System.currentTimeMillis();

		jaxbMarshaller.marshal(g, new File("target/request.xml"));

		System.out.println("m--->" + (System.currentTimeMillis() - time));
	}

	/**
	 * Precondition: The test before must successfully finished.
	 *
	 * @throws Exception
	 */
	@Test
	public void test02_simpleRequestObjectDTOUnmarshaller_Pass() throws Exception {
		System.out.println("JaxbTest#test02_simpleRequestObjectDTOUnmarshaller_Pass");

		final JAXBContext jaxbContext = JAXBContext.newInstance(GenericRequestObjectDTOCollectionMapper.class, RequestObjectDTO.class,
				Parameter.class, AbstractType.class, BooleanType.class, DoubleType.class, IntegerType.class,
				LongType.class, StringType.class, JaxbGenMapAdapter.class, MapEntryType.class, MapType.class);
		final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		final long time = System.currentTimeMillis();

		final GenericRequestObjectDTOCollectionMapper dtoUnmarshall = (GenericRequestObjectDTOCollectionMapper) jaxbUnmarshaller
				.unmarshal(new File("target/request.xml"));
		
		System.out.println("IF TEST FAILED, THE TEST BEFORE MUST BE FINSHED SUCCESSFUL!");
		
		System.out.println("JaxbTest#test02_simpleRequestObjectDTOUnmarshaller_Pass " + dtoUnmarshall.getRequestObjectDTOList().iterator().next().toString());
		System.out.println("u--->" + (System.currentTimeMillis() - time));
	}

	@Test
	public void test03_simpleResponseObjectDTOUnmarshaller_Pass() throws Exception {
		System.out.println("JaxbTest#test03_simpleResponseObjectDTOUnmarshaller_Pass");

		final List<ResponseObjectDTO> list = new ArrayList<>();

		for (int i = 0; i < 1; i++) {
			final Parameter p1 = new Parameter(("test" + i), new StringType("test" + i));
			final Parameter p2 = new Parameter(("test" + i + i), new StringType("test" + i + i));
			final ResponseObjectDTO dto = ResponseObjectDTO.with(p1).and(p2);
			list.add(dto);
		}

		final GenericResponseObjectDTOCollectionMapper g = new GenericResponseObjectDTOCollectionMapper();
		g.setResponseObjectDTOList(list);

		final JAXBContext jaxbContext = JAXBContext.newInstance(GenericResponseObjectDTOCollectionMapper.class, ResponseObjectDTO.class,
				Parameter.class, AbstractType.class, BooleanType.class, DoubleType.class, IntegerType.class,
				LongType.class, StringType.class, JaxbGenMapAdapter.class, MapEntryType.class, MapType.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		final long time = System.currentTimeMillis();

		jaxbMarshaller.marshal(g, new File("target/response.xml"));

		System.out.println("m--->" + (System.currentTimeMillis() - time));
	}

	/**
	 * Precondition: The test before must successfully finished.
	 *
	 * @throws Exception
	 */
	@Test
	public void test04_simpleResponseObjectDTOUnmarshaller_Pass() throws Exception {
		System.out.println("JaxbTest#test04_simpleResponseObjectDTOUnmarshaller_Pass");

		final JAXBContext jaxbContext = JAXBContext.newInstance(GenericResponseObjectDTOCollectionMapper.class, ResponseObjectDTO.class,
				Parameter.class, AbstractType.class, BooleanType.class, DoubleType.class, IntegerType.class,
				LongType.class, StringType.class, JaxbGenMapAdapter.class, MapEntryType.class, MapType.class);
		final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		final long time = System.currentTimeMillis();

		final GenericResponseObjectDTOCollectionMapper dtoUnmarshall = (GenericResponseObjectDTOCollectionMapper) jaxbUnmarshaller
				.unmarshal(new File("target/response.xml"));

		System.out.println("IF TEST FAILED, THE TEST BEFORE MUST BE FINSHED SUCCESSFUL!");

		System.out.println("JaxbTest#test04_simpleResponseObjectDTOUnmarshaller_Pass " + dtoUnmarshall.getResponseObjectDTOList().iterator().next().toString());
		System.out.println("u--->" + (System.currentTimeMillis() - time));
	}
}
