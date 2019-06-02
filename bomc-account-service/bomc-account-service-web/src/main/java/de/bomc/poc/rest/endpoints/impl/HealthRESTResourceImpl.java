package de.bomc.poc.rest.endpoints.impl;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.rest.endpoints.v1.HealthRESTResource;
import de.bomc.poc.service.impl.ServerStatisticsSingletonEJB;

import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * Reads some health data from running system.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 07.03.2016
 */
public class HealthRESTResourceImpl implements HealthRESTResource {

	@Inject
	@LoggerQualifier
	private Logger logger;
	@EJB
	private ServerStatisticsSingletonEJB serverStatisticsEJB;

	/**
	 * <pre>
	 *	http://192.168.4.1:8180/bomc-war/rest/health/current-memory
	 * </pre>
	 */
	@Override
	public Response availableHeap() {
		this.logger.debug("HealthRESTResourceImpl#availableHeap");

		final JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
		jsonObjectBuilder.add("Available memory in mb", this.serverStatisticsEJB.availableMemoryInMB())
				.add("Used memory in mb", this.serverStatisticsEJB.usedMemoryInMb());

		return Response.ok().entity(jsonObjectBuilder.build()).build();
	}

	/**
	 * <pre>
	 *	http://192.168.4.1:8180/bomc-war/rest/health/os-info
	 * </pre>
	 */
	@Override
	public Response osInfo() {
		this.logger.debug("HealthRESTResourceImpl#osInfo");

		final JsonObject jsonObject = this.serverStatisticsEJB.osInfo();

		return Response.ok().entity(jsonObject.toString()).build();
	}

//	/**
//	 * Thats not the way we love it - 'throws Exeption'.
//	 * 
//	 * @return a json string.
//	 * @throws Exception
//	 *             if reading of the 'test-json.json' failed.
//	 */
//	@Override
//	public Response readFile() throws Exception {
//		try (InputStream is = this.getClass().getResourceAsStream("/test-json.json");
//				BufferedReader buffer = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
//
//			final String jsonString = buffer.lines().collect(Collectors.joining("\n"));
//
//			return Response.ok().entity(jsonString).build();
//		}
//	}
}
