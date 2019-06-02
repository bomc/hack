package de.bomc.poc.rest.endpoints.v1;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Reads some health data from running system.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 07.03.2016
 */
@Path("health")
@Consumes({HealthRESTResource.MEDIA_TYPE_JSON_V})
@Produces({HealthRESTResource.MEDIA_TYPE_JSON_V})
public interface HealthRESTResource {

    String MEDIA_TYPE_JSON_V = "application/vnd.health-v1+json";

    /**
     * @description Display the current svn version.<br>
     * @responseMessage 200
     * @responseMessage 400
     * @responseMessage 401
     */
    @GET
    @Path("/current-memory")
    Response availableHeap();

    /**
     * @description Display the current svn version.<br>
     * @responseMessage 200
     * @responseMessage 400
     * @responseMessage 401
     */
    @GET
    @Path("/os-info")
    Response osInfo();

//    @GET
//    @Path("/read-file")
//    Response readFile() throws Exception;
}
