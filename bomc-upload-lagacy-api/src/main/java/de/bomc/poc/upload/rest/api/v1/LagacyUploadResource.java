/**
 * Project: Poc-upload
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: 2017-01-17 16:15:07 +0100 (Di, 17 Jan 2017) $
 *
 *  revision: $Revision: 9677 $
 *
 * </pre>
 */
package de.bomc.poc.upload.rest.api.v1;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Implementation of the lagacy upload service.
 *
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 13.12.2016
 */
@Path(LagacyUploadResource.UPLOAD_LAGACY_REST_RESOURCE_PATH)
public interface LagacyUploadResource {

	public static final String UPLOAD_LAGACY_MEDIA_TYPE = "application/vnd.upload-lagacy-v1+json";
	public static final String UPLOAD_LAGACY_REST_RESOURCE_PATH = "api-lagacy";
	public static final String QUERY_PARAM_REQUEST_ID = "requestId";

	/**
	 * @param requestId
	 *            eindeutiger identifier des Requests.
	 * @param multipartFormDataInput
	 *            der eigentliche Payload, enthält Content-Disposition, sowie
	 *            Content-Type.
	 * @requiredParams requestId, multipartFormDataInput
	 * @description Service stellt Schnittstelle zum Hochladen von Dateien zur
	 *              Verfügung.
	 * @responseType de.bomc.poc.upload.rest.api.v1.ResponseObject
	 * @status 200 Der Request wurde erfolgreich ausgeführt.
	 * @status 401 Unauthorized - Basic Authentication ist fehlgeschlagen.
	 * @status 404 (Not Found) Der Request wurde nicht angenommen. Details
	 *         stehen im Response Body.
	 * @status 410 (Gone) Die API steht in dieser Version nicht mehr zur
	 *         Verfügung.
	 * @status 500 (Internal Server Error) Es ist ein serverseitiger Fehler
	 *         aufgetreten. Details stehen im Response Body.
	 * @status 503 (Service Unavailable) Der Service ist vorübergehend nicht
	 *         erreichbar (z. B. wegen Wartung).
	 */
	@POST
	@Path("/upload-lagacy")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(LagacyUploadResource.UPLOAD_LAGACY_MEDIA_TYPE)
	Response uploadFile(
			@NotNull @Pattern(regexp = "[0-9a-fA-F]{8}-?[0-9a-fA-F]{4}-?4[0-9a-fA-F]{3}-?[89abAB][0-9a-fA-F]{3}-?[0-9a-fA-F]{12}", message = "The given requestId is not in valid format.") 
			@QueryParam(LagacyUploadResource.QUERY_PARAM_REQUEST_ID) String requestId,
			MultipartFormDataInput multipartFormDataInput);
	
	/**
	 * @param requestId
	 *            eindeutiger identifier des Requests.
	 * @requiredParams requestId
	 * @description Senden einer regelmässigen (1/Min.) Ping-Anfrage an den
	 *              Lagacy-Upload-Service, um festzustellen, ob der Dienst im
	 *              Falle eines vermuteten Ausfalls verfügbar ist. Das
	 *              ResponseObject liefert immer den 'status'='10600' zurück und
	 *              als 'statusText' die Startzeit des Servers.
	 * @responseType de.bomc.poc.upload.rest.api.v1.ResponseObject
	 * @status 200 Der Request wurde erfolgreich ausgeführt.
	 * @status 401 Unauthorized - Basic Authentication ist fehlgeschlagen.
	 * @status 404 (Not Found) Der Request wurde nicht angenommen. Details
	 *         stehen im Response Body.
	 * @status 410 (Gone) Die API steht in dieser Version nicht mehr zur
	 *         Verfügung.
	 * @status 500 (Internal Server Error) Es ist ein serverseitiger Fehler
	 *         aufgetreten. Details stehen im Response Body.
	 * @status 503 (Service Unavailable) Der Service ist vorübergehend nicht
	 *         erreichbar (z. B. wegen Wartung).
	 */
	@GET
	@Path("/ping")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response ping(@QueryParam(LagacyUploadResource.QUERY_PARAM_REQUEST_ID) String requestId);
}
