/**
 * Project: Poc-upload
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: 2017-01-19 12:30:55 +0100 (Do, 19 Jan 2017) $
 *
 *  revision: $Revision: 9752 $
 *
 * </pre>
 */
package de.bomc.poc.upload.rest.endpoints.impl;

import org.apache.log4j.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import de.bomc.poc.upload.rest.api.v1.LagacyUploadResource;
import de.bomc.poc.upload.rest.api.v1.LagacyUploadServiceResponseCodesEnum;
import de.bomc.poc.upload.rest.api.v1.ResponseObject;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Mock implementation for upload service.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 20.12.2016
 */
public class LagacyUploadMockRESTResourceImpl implements LagacyUploadResource {

	private static final String LOG_PREFIX = "LagacyUploadMockRESTResourceImpl#";
	private static final String INPUT_PART_NAME_ATTACHMENT = "attachment";
	@Inject
	@LoggerQualifier
	private Logger logger;

	@Override
	public Response uploadFile(final String requestId, final MultipartFormDataInput multipartFormDataInput) {
		this.logger.debug(LOG_PREFIX + "uploadFile [requestId=" + requestId + "]");

		final long time = System.currentTimeMillis();

		ResponseObject responseObject = null;

		// Retrieve headers, read the Content-Disposition header to obtain the
		// original name of the file.
		final Map<String, List<InputPart>> map = multipartFormDataInput.getFormDataMap();
		final List<InputPart> listInputPart = map.get(INPUT_PART_NAME_ATTACHMENT);

		for (final InputPart inputPart : listInputPart) {
			//
			// Get filename from header.
			final MultivaluedMap<String, String> multivaluedMap = inputPart.getHeaders();
			final String fileName = this.getFileName(multivaluedMap);

			if (null != fileName && !"".equalsIgnoreCase(fileName)) {
				InputStream inputStream = null;

				try {
					//
					// Extract payload from body.
					try {
						inputStream = inputPart.getBody(InputStream.class, null);

						try {
							// Generates a random timeout between 3 and 5
							// seconds
							TimeUnit.SECONDS.sleep(this.generateRandomNumber(3, 5));
						} catch (final InterruptedException e) {
							// Ignore
						}

						responseObject = this.buildResponseObject(requestId,
								LagacyUploadServiceResponseCodesEnum.UPLOAD_SUCCESS, time);

						this.logger.info(LOG_PREFIX + "uploadFile [fileName=" + fileName + ", responseObject="
								+ responseObject + "]");
					} catch (final IOException e) {
						this.logger.error(LOG_PREFIX + "uploadFile " + e);
					}
				} finally {
					// release resources, if any
					// Close the stream.
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (final IOException iox) {
							this.logger.warn(LOG_PREFIX + "validateFileFA - inputstream is null, ignore.");
						}
					}
				}
			} else {
				responseObject = this.buildResponseObject(requestId, LagacyUploadServiceResponseCodesEnum.UPLOAD_FAILED,
						time);
			}
		} // end for

		return Response.ok().entity(responseObject).type(LagacyUploadResource.UPLOAD_LAGACY_MEDIA_TYPE).build();
	}

	@Override
	public Response ping(final String requestId) {
		this.logger.debug(LOG_PREFIX + "ping [requestId=" + requestId + "]");

		final long time = System.currentTimeMillis();

		try {
			// Generates a random timeout between 0 and 2 seconds
			TimeUnit.SECONDS.sleep(this.generateRandomNumber(0, 2));
		} catch (final InterruptedException e) {
			// Ignore
		}

		final ResponseObject responseObject = this.buildResponseObject(requestId,
				LagacyUploadServiceResponseCodesEnum.PING_STATUS, time);

		return Response.ok().type(LagacyUploadResource.UPLOAD_LAGACY_MEDIA_TYPE).entity(responseObject).build();
	}

	/**
	 * Parse Content-Disposition header to get the original file name.
	 * 
	 * @param multivaluedMap
	 *            contains the header parameter.
	 * @return the filename.
	 */
	private String getFileName(final MultivaluedMap<String, String> multivaluedMap) {
		final String[] contentDisposition = multivaluedMap.getFirst("Content-Disposition").split(";");

		for (final String filename : contentDisposition) {

			if (filename.trim().startsWith("filename")) {
				final String[] name = filename.split("=");

				return name[1].trim().replaceAll("\"", "");
			}
		}

		throw new IllegalArgumentException(
				"Could not extract header tag 'filename' from header map (hint: see request log)! ");
	}

	/**
	 * Generate a random number between the given min and max value.
	 * 
	 * @param minValue
	 *            the min number of the generated random value.
	 * @param maxValue
	 *            the min number of the generated random value.
	 * @return a random number between the given min and max value.
	 */
	private int generateRandomNumber(final int minValue, final int maxValue) {
		final Random random = new Random();

		// Generates a random value between 3 and 5.
		return random.ints(minValue, maxValue + 1).limit(1).findFirst().getAsInt();
	}

	/**
	 * Build the response object.
	 * 
	 * @param requestId
	 *            the given requestId.
	 * @param validationServiceResponseCodesEnum
	 *            the respopnse status code.
	 * @param duration
	 *            the duration of this operation.
	 * @return a initialized response object instance.
	 */
	private ResponseObject buildResponseObject(final String requestId,
			final LagacyUploadServiceResponseCodesEnum validationServiceResponseCodesEnum, final long duration) {
		final ResponseObject responseObject = new ResponseObject();

		responseObject.setStatusCode(Long.toString(validationServiceResponseCodesEnum.getCode()));
		responseObject.setRequestId(requestId);
		responseObject.setStatusText(validationServiceResponseCodesEnum.toString());
		responseObject.setDuration(Long.toString(System.currentTimeMillis() - duration));

		return responseObject;
	}
}
