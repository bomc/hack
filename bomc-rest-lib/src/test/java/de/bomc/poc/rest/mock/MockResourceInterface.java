package de.bomc.poc.rest.mock;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Describes the MockResource REST endpoint.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: 6791 $ $Author: tzdbmm $ $Date: 2016-07-19 09:07:13 +0200 (Di, 19 Jul 2016) $
 * @since 12.07.2016
 */
@Path("mock")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface MockResourceInterface {

    @GET
    @Path("/log-to-me/{id}")
    Response logToMe(@PathParam("id") Long id);

    @POST
    @Path("/mock-dto")
    Response getMockDTO(final MockDTO mockDTO);

    @POST
    @Path("/mock-dto-as-list")
    Response getMockDTOAsList(final List<MockDTO> mockDTO);
}
