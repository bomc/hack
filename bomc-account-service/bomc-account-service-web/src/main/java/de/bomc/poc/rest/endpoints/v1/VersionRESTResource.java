package de.bomc.poc.rest.endpoints.v1;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Reads the current version of this project from 'version.properties'-file.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
@Path("version")
@Consumes({VersionRESTResource.MEDIA_TYPE_JSON_V})
@Produces({VersionRESTResource.MEDIA_TYPE_JSON_V})
public interface VersionRESTResource {

    String MEDIA_TYPE_JSON_V = "application/vnd.version-v1+json";

    /**
     * @description Display the current svn version.<br>
     * @responseMessage 200
     * @responseMessage 400
     * @responseMessage 401
     */
    @GET
    @Path("/current-version")
    Response getVersion();
}

