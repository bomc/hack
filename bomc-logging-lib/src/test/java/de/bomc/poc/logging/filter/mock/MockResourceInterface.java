package de.bomc.poc.logging.filter.mock;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Describes the MockResource REST endpoint.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: 7096 $ $Author: tzdbmm $ $Date: 2016-08-03 14:59:36 +0200 (Mi, 03 Aug 2016) $
 * @since 12.07.2016
 */
@Path("mock")
@Produces(MediaType.APPLICATION_JSON)
public interface MockResourceInterface {

    @GET
    @Path("/log-to-me/{id}")
    Response logToMe(@PathParam("id") Long id);
}
