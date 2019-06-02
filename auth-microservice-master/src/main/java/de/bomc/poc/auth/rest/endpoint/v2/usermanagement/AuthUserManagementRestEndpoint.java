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
package de.bomc.poc.auth.rest.endpoint.v2.usermanagement;

import de.bomc.poc.auth.model.usermanagement.Role;
import de.bomc.poc.auth.rest.application.JaxRsActivator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The entry point for user management actions.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Path(JaxRsActivator.USERMANAGEMENT_ENDPOINT_PATH)
public interface AuthUserManagementRestEndpoint {

    /**
     * A {@code String} constant representing json v2 "{@value #MEDIA_TYPE_JSON_V2}" media type .
     */
    String MEDIA_TYPE_JSON_V2 = "application/vnd.usermanagement-v2+json";
    /**
     * A {@link MediaType} constant representing json v2 "{@value #MEDIA_TYPE_JSON_V2}" media type.
     */
    MediaType MEDIA_TYPE_JSON_V2_TYPE = new MediaType("application", "vnd.usermanagement-v2+json");

    /**
     * <pre>
     * 	http://127.0.0.1:8180/auth-microservice/auth-api/usermanagement/roles-by-username/Default-System_user
     *  curl -v -H "Accept: application/vnd.usermanagement-v2+json" -H "X-BOMC-REQUEST-ID: SET-BY-CURL-123" -H "X-BOMC-AUTHORIZATION: BOMC_USER" -X GET "127.0.0.1:8180/auth-microservice/auth-api/usermanagement/roles-by-username/Default-System_user"
     * </pre>
     * Find all {@link Role}s to given username.
     * @param username contains the username.
     * @return A list with all roles or a empty list if no roles are assigned to the user.
     * @description Find all Roles by the given username. The method returns a javax.ws.rs.core.Response object that wraps a list of java.util.List<de.bomc.poc.api.mapper.transfer.RoleDTO>.
     * @responseType java.util.List<de.bomc.poc.api.mapper.transfer.RoleDTO>
     * @responseMessage 200 A list with all roles or a empty list if no roles are assigned to the user.
     * @responseMessage 400 Invalid Request.
     * @responseMessage 401 Unauthorized. API key does (not) have the requested access privileges.
     * @responseMessage 404 Endpoint not found.
     */
    @GET
    @Path("/roles-by-username/{username}")
    @Produces({AuthUserManagementRestEndpoint.MEDIA_TYPE_JSON_V2})
    Response findAllRolesByUsername(@PathParam("username") String username);
}
