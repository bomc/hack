/**
 * Project: bomc-swagger
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
 */
package de.bomc.poc.rest.endpoints.impl;

import org.apache.log4j.Logger;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.rest.endpoints.v1.HealthRESTResource;
import de.bomc.poc.service.ServerStatisticsSingletonEJB;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;

/**
 * Reads some health data from running system.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 09.12.2017
 */
public class HealthRESTResourceImpl implements HealthRESTResource {

	@Inject
	@LoggerQualifier
	private Logger logger;
	@EJB
	private ServerStatisticsSingletonEJB serverStatisticsEJB;

	@Override
	public Response availableHeap() {
		this.logger.debug("HealthRESTResourceImpl#availableHeap");

		final JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
		jsonObjectBuilder.add("Available memory in mb", this.serverStatisticsEJB.availableMemoryInMB())
				.add("Used memory in mb", this.serverStatisticsEJB.usedMemoryInMb());

		return Response.ok().entity(jsonObjectBuilder.build()).build();
	}

	@Override
	public Response osInfo() {
		this.logger.debug("HealthRESTResourceImpl#osInfo");

		final JsonObject jsonObject = this.serverStatisticsEJB.osInfo();

		return Response.ok().entity(jsonObject.toString()).build();
	}
}
