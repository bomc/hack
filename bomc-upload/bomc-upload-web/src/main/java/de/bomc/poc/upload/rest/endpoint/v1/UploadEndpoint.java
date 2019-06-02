/**
 * Project: Poc-upload
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: 2016-12-23 14:20:47 +0100 (Fr, 23 Dez 2016) $
 *
 *  revision: $Revision: 9598 $
 *
 * </pre>
 */
package de.bomc.poc.upload.rest.endpoint.v1;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

/**
 * REST interface for file uploading.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 07.03.2016
 */
@Path(UploadEndpoint.BOMC_UPLOAD_REST_RESOURCE_PATH)
public interface UploadEndpoint {
	public static final String BOMC_UPLOAD_MEDIA_TYPE = "application/vnd.bomc-v1+json";
	public static final String BOMC_UPLOAD_REST_RESOURCE_PATH = "api";
	public static final String QUERY_PARAM_REQUEST_ID = "requestId";

	/**
	 * Endpoint for uploading files.
	 * 
	 * @param requestId
	 *            a unique identifier
	 * @param multipartFormDataInput
	 *            multipart content consists of content-header: Content-Disposition, Content-type and the payload 
	 * @return
	 */
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(UploadEndpoint.BOMC_UPLOAD_MEDIA_TYPE)
	Response uploadFile(
			@NotNull @Pattern(regexp = "[0-9a-fA-F]{8}-?[0-9a-fA-F]{4}-?4[0-9a-fA-F]{3}-?[89abAB][0-9a-fA-F]{3}-?[0-9a-fA-F]{12}", message = "The given requestId is not in valid format.") 
			@QueryParam(UploadEndpoint.QUERY_PARAM_REQUEST_ID) String requestId, MultipartFormDataInput multipartFormDataInput);
}
