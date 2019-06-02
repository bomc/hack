package de.bomc.poc.prometheus.collector.boundary;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

/**
 * A endpoint for requesting prometheus metrics from a jee service. 
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 02.06.2017
 */
@Path("metrics")
@Produces(MediaType.TEXT_PLAIN)
public interface MetricsResource {

    @GET
    @Path("{name}")
	void metrics(@Suspended AsyncResponse response, @PathParam("name") String name);
}
