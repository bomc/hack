/**
 * Project: Poc-upload
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: 2017-01-18 08:58:05 +0100 (Mi, 18 Jan 2017) $
 *
 *  revision: $Revision: 9697 $
 *
 * </pre>
 */
package de.bomc.poc.upload.rest.api.v1;

/**
 * Describes the response stati for upload.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 13.12.2016
 */
public enum LagacyUploadServiceResponseCodesEnum {
    UPLOAD_SUCCESS(10100L),	// Upload is successful finished. 
    UPLOAD_FAILED(10101L),	// Upload could not successful finished.
	PING_STATUS(10600);		// Returned ping-status.

    private final long code;

    LagacyUploadServiceResponseCodesEnum(final long code) {
        this.code = code;
    }

    public Long getCode() {
        return this.code;
    }
}
