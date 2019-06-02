package de.bomc.poc.exception.test.arq.mock.application;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Describes the MockResource REST endpoint.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 */
@Path("mock")
@Produces(MediaType.APPLICATION_JSON)
public interface MockWebAppExResourceInterface {

    @GET
    @Path("/check-mock-response-object/{id}")
    Response checkMockResponseObject(@PathParam("id") Long id);
}
