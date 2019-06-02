package de.bomc.poc.prometheus.configuration.boundary;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

/**
 * A endpoint for configuration of the service.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 02.06.2017
 */
@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("configurations")
public class ConfigurationResource {

	private static final String LOG_PREFIX = "ConfigurationResource#";
	private static final Logger LOGGER = Logger.getLogger(ConfigurationResource.class);
	
	@Inject
	ConfigurationStore configurationStore;

	@GET
	public JsonObject all() {
		LOGGER.debug(LOG_PREFIX + "all");
		
		return this.configurationStore.getAllConfigurations();
	}

	@GET
	@Path("{name}")
	public void getConfiguration(@Suspended AsyncResponse response, @PathParam("name") String configurationName) {
		LOGGER.debug(LOG_PREFIX + "getConfiguration [configurationName=" + configurationName + "]");
		
		response.setTimeout(1, TimeUnit.SECONDS);
		final Optional<JsonObject> configuration = this.configurationStore.getConfiguration(configurationName);
		
		if (configuration.isPresent()) {
			response.resume(configuration.get());
		} else {
			response.resume(Response.noContent().build());
		}
	}

	@PUT
	@Path("/{name}")
	public Response save(@PathParam("name") String name, JsonObject configuration, @Context UriInfo uriInfo) {
		LOGGER.debug(LOG_PREFIX + "save [name=" + name + ", configuration=" + configuration + ", uriInfo=" + uriInfo + "]");
		
		final boolean updated = this.configurationStore.saveOrUpdate(name, configuration);
		if (updated) {
			return Response.noContent().build();
		} else {
			URI uri = uriInfo.getAbsolutePathBuilder().build();
			return Response.created(uri).build();
		}
	}

	@DELETE
	@Path("{name}")
	public void delete(@PathParam("name") String name) {
		LOGGER.debug(LOG_PREFIX + "delete [name" + name + "]");
		
		this.configurationStore.remove(name);
	}
}
