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
package de.bomc.poc.auth.rest.endpoint.v1.usermanagement;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.bomc.poc.api.generic.transfer.request.RequestObjectDTO;
import de.bomc.poc.auth.model.usermanagement.Role;
import de.bomc.poc.auth.rest.application.JaxRsActivator;
import de.bomc.poc.validation.constraints.FieldsMatchToRequestObjectDTO;

/**
 * The entry point for user management actions.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Path(JaxRsActivator.USERMANAGEMENT_ENDPOINT_PATH)
@Consumes({AuthUserManagementRestEndpoint.MEDIA_TYPE_XML_V1})
@Produces({AuthUserManagementRestEndpoint.MEDIA_TYPE_XML_V1})
public interface AuthUserManagementRestEndpoint {
    /**
     * A request parameter that comes with the <code>RequestObjectDTO</code>.
     */
    String USERNAME_REQUEST_PARAMETER = "username";
    /**
     * A {@code String} constant representing xml v1 "{@value #MEDIA_TYPE_XML_V1}" media type .
     */
    String MEDIA_TYPE_XML_V1 = "application/vnd.usermanagement-v1+xml";
    /**
     * A {@link MediaType} constant representing xml v1 "{@value #MEDIA_TYPE_XML_V1}" media type.
     */
    MediaType MEDIA_TYPE_XML_V1_TYPE = new MediaType("application", "vnd.usermanagement-v1+xml");

    /**
     * <pre>
     * 	http://127.0.0.1:8180/auth-microservice/auth-api/usermanagement/roles-by-username
     * 	curl -v -H "Accept: application/vnd.usermanagement-v1+xml" -H "Content-Type: application/vnd.usermanagement-v1+xml" -H "X-BOMC-AUTHORIZATION: BOMC_USER" -H "X-BOMC-REQUEST-ID: SET-BY-CURL-123" -X POST "127.0.0.1:8180/auth-microservice/auth-api/usermanagement/roles-by-username" -d "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><requestObjectDTO><parameters><entry><key xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\">username</key><value xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"parameter\" name=\"username\"><type xsi:type=\"stringType\" value=\"Default-System_user\"/></value></entry></parameters></requestObjectDTO>"
     * </pre>
     * Find all {@link Role}s to given username.
     * @param requestObjectDTO contains the username.
     * @description Find all Roles by the given username. The method returns a javax.ws.rs.core.Response object that wraps de.bomc.poc.api.jaxb.GenericResponseObjectDTOCollectionMapper that contains a list of de.bomc.poc.api.generic.transfer.request.RequestObjectDTO.
     * @responseType de.bomc.poc.api.jaxb.GenericResponseObjectDTOCollectionMapper
     * @responseMessage 299 GenericResponseObjectDTOCollectionMapper. Is a wrapper that contains a list with de.bomc.poc.api.generic.transfer.request.RequestObjectDTO's. This api v1 is no longer supported in the future.
	 * @responseMessage 400 Invalid Request.
	 * @responseMessage 401 Unauthorized. API key does (not) have the requested access privileges.
	 * @responseMessage 404 Endpoint not found.
     * @deprecated Is only supported in 2016.
     * 
     * @return A list with all roles or a empty list if no roles are assigned to the user.
     */
    @POST
    @Path("/roles-by-username")
    @Deprecated // Is therefore respected in 'overview'-endpoint output. 
    Response findAllRolesByUsername(@Valid @FieldsMatchToRequestObjectDTO(parameter = "username") RequestObjectDTO requestObjectDTO);
}
