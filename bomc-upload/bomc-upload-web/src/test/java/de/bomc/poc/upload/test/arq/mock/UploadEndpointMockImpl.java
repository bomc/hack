/**
 * Project: Poc-upload
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: 2017-01-19 12:31:32 +0100 (Do, 19 Jan 2017) $
 *
 *  revision: $Revision: 9753 $
 *
 * </pre>
 */
package de.bomc.poc.upload.test.arq.mock;

import org.apache.commons.io.IOUtils;
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
 * Implements the lagacy upload service as a mock.
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 21.12.2016
 */
public class UploadEndpointMockImpl implements LagacyUploadResource {

    public static final String TIMEOUT_FILENAME = "timeout.csv";
    private static final String LOG_PREFIX = "UploadEndpointMockImpl#";
    private static final String INPUT_PART_NAME_ATTACHMENT = "attachment";
    @Inject
    @LoggerQualifier
    private Logger logger;

    @Override
    public Response uploadFile(final String requestId, final MultipartFormDataInput multipartFormDataInput) {
        this.logger.debug(LOG_PREFIX + "uploadFile [requestId=" + requestId + "]");

        final long time = System.currentTimeMillis();

        ResponseObject responseObject = new ResponseObject();

        // Retrieve headers, read the Content-Disposition header to obtain the original name of the file.
        final Map<String, List<InputPart>> map = multipartFormDataInput.getFormDataMap();
        final List<InputPart> listInputPart = map.get(INPUT_PART_NAME_ATTACHMENT);

        for (final InputPart inputPart : listInputPart) {
            //
            // Get filename to be uploaded.
            final MultivaluedMap<String, String> multivaluedMap = inputPart.getHeaders();
            final String fileName = this.getFileName(multivaluedMap);

            if (null != fileName && !"".equalsIgnoreCase(fileName)) {
                InputStream inputStream = null;

                if (!fileName.equals(TIMEOUT_FILENAME)) {
                    try {
                        //
                        // Extract payload from body.
                        try {
                            inputStream = inputPart.getBody(InputStream.class, null);

                            final List<String> readLines = IOUtils.readLines(inputStream);
                            readLines.forEach(line -> {this.logger.debug(LOG_PREFIX + "uploadFile [line" + line + "]");});
                            
                            
                            try {
                                TimeUnit.SECONDS.sleep(15L);
                            } catch (final InterruptedException e) {
                                // Ignore
                            }

                            responseObject = this.buildResponseObject(requestId, LagacyUploadServiceResponseCodesEnum.UPLOAD_SUCCESS, time);

                            this.logger.info(LOG_PREFIX + "uploadFile - finish [fileName=" + fileName + ", responseObject=" + responseObject + "]");
                        } catch (final IOException e) {
                            this.logger.error(LOG_PREFIX + "uploadFile finish with error. " + e);
                        }
                    } finally {
                        // release resources, if any
                        // Close the stream.
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (final IOException iox) {
                                this.logger.warn(LOG_PREFIX + "uploadFile - inputsteam is null, ignore.");
                            }
                        }
                    }
                } else {
                	// Simulate for 45 seconds.
                    try {
                        TimeUnit.SECONDS.sleep(45L);
                    } catch (final InterruptedException e) {
                        // Ignore
                    }
                    
                    this.logger.info(LOG_PREFIX + "uploadFile - wakeup...");

                    responseObject = this.buildResponseObject(requestId, LagacyUploadServiceResponseCodesEnum.UPLOAD_SUCCESS, time);
                }
            } else {
                responseObject = this.buildResponseObject(requestId, LagacyUploadServiceResponseCodesEnum.UPLOAD_FAILED, time);
            }
        } // end for

        return Response.ok()
                       .entity(responseObject)
                       .type(LagacyUploadResource.UPLOAD_LAGACY_MEDIA_TYPE)
                       .build();
    }

    /**
     * Parse Content-Disposition header to get the original file name.
     * @param multivaluedMap contains the header parameter.
     * @return the filename.
     */
    private String getFileName(final MultivaluedMap<String, String> multivaluedMap) {
        final String[] contentDisposition = multivaluedMap.getFirst("Content-Disposition")
                                                          .split(";");

        for (final String filename : contentDisposition) {

            if (filename.trim()
                        .startsWith("filename")) {
                final String[] name = filename.split("=");

                return name[1].trim()
                              .replaceAll("\"", "");
            }
        }

        throw new IllegalArgumentException("Could not extract header tag 'filename' from header map (hint: see request log)! ");
    }

    @Override
    public Response ping(final String requestId) {
        this.logger.debug(LOG_PREFIX + "ping [requestId=" + requestId + "]");

        final long time = System.currentTimeMillis();

        try {
            final Random random = new Random();
            final int minValue = 3;
            final int maxValue = 5;

            // Generates a random value between 3 and 5.
            final int timeout = random.ints(minValue, maxValue + 1)
                                      .limit(1)
                                      .findFirst()
                                      .getAsInt();

            TimeUnit.SECONDS.sleep(timeout);
        } catch (final InterruptedException e) {
            // Ignore
        }

        final ResponseObject responseObject = this.buildResponseObject(requestId, LagacyUploadServiceResponseCodesEnum.PING_STATUS, time);

        return Response.ok()
                       .entity(responseObject)
                       .type(LagacyUploadResource.UPLOAD_LAGACY_MEDIA_TYPE)
                       .build();
    }

    private ResponseObject buildResponseObject(final String requestId, final LagacyUploadServiceResponseCodesEnum lagacyUploadServiceResponseCodesEnum, final long time) {
        final ResponseObject responseObject = new ResponseObject();

        responseObject.setRequestId(requestId);
        responseObject.setStatusCode(Long.toString(lagacyUploadServiceResponseCodesEnum.getCode()));
        responseObject.setStatusText(lagacyUploadServiceResponseCodesEnum.toString());
        responseObject.setDuration(Long.toString(System.currentTimeMillis() - time));

        return responseObject;
    }
}
