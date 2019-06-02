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
package de.bomc.poc.upload.transfer.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import de.bomc.poc.exception.errorcode.WebUploadErrorCode;
import de.bomc.poc.exception.web.WebUploadRuntimeException;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.rest.logger.client.ResteasyClientLogger;
import de.bomc.poc.upload.api.UploadResponseObject;
import de.bomc.poc.upload.rest.api.v1.LagacyUploadResource;
import de.bomc.poc.upload.rest.api.v1.ResponseObject;
import de.bomc.poc.upload.rest.client.RestClientBuilder;
import de.bomc.poc.upload.rest.endpoint.v1.UploadEndpoint;
import de.bomc.poc.upload.transfer.TransferLocal;

/**
 * Implementation of the worker ejb for file uploading.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 07.03.2016
 */
@Stateless
@Local(TransferLocal.class)
public class TransferEJB implements TransferLocal {

	private static final String LOG_PREFIX = "TransferEJB#";
	private static final Logger LOGGER = Logger.getLogger(TransferEJB.class);
	private static final String INPUT_PART_NAME_ATTACHMENT = "attachment";
	private static final String VALIDATION_SERVICE_FAILED_MESSAGE = "Upload to validation-service failed";
	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	private RestClientBuilder restClientBuilder;

	@PostConstruct
	public void init() {
		LOGGER.debug(LOG_PREFIX + "init");

	}

	public Response transferUpload(final String requestId, final MultipartFormDataInput multipartFormDataInput) {
		LOGGER.debug(LOG_PREFIX + "transferUpload [requestId=" + requestId + ", multipartFormDataInput]");

		final long time = System.currentTimeMillis();

		try {
			// Retrieve headers, read the Content-Disposition header to obtain
			// the original name of the file.
			final Map<String, List<InputPart>> map = multipartFormDataInput.getFormDataMap();
			final List<InputPart> listInputPart = map.get(INPUT_PART_NAME_ATTACHMENT);

			if (listInputPart != null) {
				for (final InputPart inputPart : listInputPart) {
					//
					// Get uploaded filename.
					final MultivaluedMap<String, String> multivaluedMap = inputPart.getHeaders();
					final String fileName = this.extractFileNameFromContentDisposition(multivaluedMap);

					if (null != fileName && !"".equalsIgnoreCase(fileName)) {
						//
						// Filename is successful extracted from Content-Disposition.
						//
						InputStream inputStream = null;
						Response response = null;

						try {
							// Get webTarget from restClientBuilder.
							final ResteasyWebTarget resteasyWebTarget = this.restClientBuilder.buildUploadRestClient();
							resteasyWebTarget.register(new ResteasyClientLogger(LOGGER, true));
							
							// Create query param.
							final MultivaluedMap<String, Object> multivaluedQueryMap = new MultivaluedHashMap<>(1);
							multivaluedQueryMap.add(LagacyUploadResource.QUERY_PARAM_REQUEST_ID, requestId);

							// Read body from multipart input.
							inputStream = inputPart.getBody(InputStream.class, null);

							// Create upload to data for uploading to validation service.
							final MultipartFormDataOutput multipartFormDataOutput = new MultipartFormDataOutput();
							multipartFormDataOutput.addFormData("attachment", inputStream,
									MediaType.MULTIPART_FORM_DATA_TYPE, fileName);

							final GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(
									multipartFormDataOutput) {
							};

							// Upload file to validation service.
							response = resteasyWebTarget
									.path("/" + LagacyUploadResource.UPLOAD_LAGACY_REST_RESOURCE_PATH + "/upload-lagacy")
									.queryParams(multivaluedQueryMap).request().accept(LagacyUploadResource.UPLOAD_LAGACY_MEDIA_TYPE)
									.post(Entity.entity(entity, MediaType.MULTIPART_FORM_DATA_TYPE));

							if (response.getStatus() == Response.Status.OK.getStatusCode()) {
								final ResponseObject responseObject = response.readEntity(ResponseObject.class);

								final UploadResponseObject uploadResponseObject = this.buildUploadResponseObject(
										responseObject.getStatusCode(), requestId, responseObject.getStatusText(), responseObject.getDuration());

								this.logger.info(LOG_PREFIX + "transferUpload [duration (full in ms)="
										+ (System.currentTimeMillis() - time) + ", response=" + responseObject + "]");

								return Response.ok().entity(uploadResponseObject).type(UploadEndpoint.BOMC_UPLOAD_MEDIA_TYPE)
										.build();
							} else if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
								final long duration = System.currentTimeMillis() - time;
								
								final UploadResponseObject uploadResponseObject = this.buildUploadResponseObject(
										Integer.toString(Response.Status.UNAUTHORIZED.getStatusCode()), requestId,
										"Username/Password are wrong.", Long.toString(duration));

								this.logger.info(
										LOG_PREFIX + "transferUpload - authentication failed. [duration (in ms)="
												+ (System.currentTimeMillis() - time) + ", response="
												+ uploadResponseObject + "]");

								return Response.ok().entity(uploadResponseObject).type(UploadEndpoint.BOMC_UPLOAD_MEDIA_TYPE)
										.build();
							} else {
								//
								// Request failed, build ResponseObject for error handling.
								final long duration = System.currentTimeMillis() - time;
								final String errMsg = "Upload to lagacy-upload-endpoint failed. [duration (in ms)="
										+ duration + ", status.validation-service=" + response.getStatus() + "]";
								this.logger.error(LOG_PREFIX + errMsg);

								final UploadResponseObject uploadResponseObject = this.buildUploadResponseObject(null, requestId,
										VALIDATION_SERVICE_FAILED_MESSAGE, Long.toString(duration));

								return Response.serverError().type(UploadEndpoint.BOMC_UPLOAD_MEDIA_TYPE)
										.entity(uploadResponseObject).build();
							}
						} finally {
							// Release resources, if any.
							if (inputStream != null) {
								// Close the stream.
								try {
									inputStream.close();
								} catch (final IOException iox) {
									this.logger.warn(LOG_PREFIX + "transferUpload - inputStream is null, ignore.");
								}
							}

							if (response != null) {
								response.close();
							}
						}
					}
				} // end for

				//
				// This code is reached if the tag 'filename' is not included in
				// the 'Content-Disposition'.
				final String errMsg = LOG_PREFIX
						+ "transferUpload - Wrong request parameter, filename could not extract from header (is not included).";
				final WebUploadRuntimeException webEx = new WebUploadRuntimeException(errMsg,
						WebUploadErrorCode.API_BOMC_00200);

				this.logger.error(LOG_PREFIX + "transferUpload " + errMsg);
				webEx.setIsLogged(true);

				throw webEx;
			} else {
				final String errMsg = LOG_PREFIX + "transferUpload - Multipart content is corrupt.";
				final WebUploadRuntimeException webEx = new WebUploadRuntimeException(errMsg,
						WebUploadErrorCode.API_BOMC_00300);

				this.logger.error(LOG_PREFIX + "transferUpload " + errMsg);
				webEx.setIsLogged(true);

				throw webEx;
			}
		} catch (final ProcessingException pEx) {
			final WebUploadRuntimeException webEx = new WebUploadRuntimeException(pEx.getMessage(), pEx,
					WebUploadErrorCode.API_BOMC_00100);

			this.logger.error(LOG_PREFIX + "transferUpload " + webEx.stackTraceToString());
			webEx.setIsLogged(true);

			throw webEx;
		} catch (final IOException ioe) {
			final WebUploadRuntimeException webEx = new WebUploadRuntimeException(ioe.getMessage(), ioe,
					WebUploadErrorCode.API_BOMC_00200);

			this.logger.error(LOG_PREFIX + "transferUpload " + webEx.stackTraceToString());
			webEx.setIsLogged(true);

			throw webEx;
		} catch (final Exception ex) {
			final WebUploadRuntimeException webEx = new WebUploadRuntimeException(ex.getMessage(), ex,
					WebUploadErrorCode.API_BOMC_00100);

			this.logger.error(LOG_PREFIX + "transferUpload " + webEx.stackTraceToString());
			webEx.setIsLogged(true);

			throw webEx;
		}
	}

	/**
	 * Parse Content-Disposition header to get the original file name.
	 * 
	 * @param multivaluedMap
	 *            contains the header parameter.
	 * @return the filename.
	 */
	private String extractFileNameFromContentDisposition(final MultivaluedMap<String, String> multivaluedMap) {
		this.logger.debug(LOG_PREFIX + "extractFileNameFromContentDisposition");

		final String[] contentDisposition = multivaluedMap.getFirst("Content-Disposition").split(";");

		for (final String filename : contentDisposition) {

			if (filename.trim().startsWith("filename")) {
				final String[] name = filename.split("=");
				final String exactFileName = name[1].trim().replaceAll("\"", "");

				return exactFileName;
			}
		}

		throw new IllegalArgumentException(LOG_PREFIX
				+ "Could not extract header tag 'filename' from header map (hint: see request log for naming)! ");
	}

	/**
	 * Maps the ResponseObject from lagacy-upload-service to the
	 * UploadResponseObject.
	 * 
	 * @param responseStatus
	 *            the status of the validation.
	 * @param requestId
	 *            the handled requestId.
	 * @param statusText
	 *            the status text of the validation.
	 * @param duration
	 *            time of upload request.
	 * @return a initialized UploadResponseObject instance.
	 */
	private UploadResponseObject buildUploadResponseObject(final String responseStatus, final String requestId,
			final String statusText, final String duration) {
		final UploadResponseObject uploadResponseObject = new UploadResponseObject();

		uploadResponseObject.setStatusCode(responseStatus);
		uploadResponseObject.setRequestId(requestId);
		uploadResponseObject.setStatusText(statusText);
		uploadResponseObject.setDuration(Long.parseLong(duration));

		return uploadResponseObject;
	}
}
