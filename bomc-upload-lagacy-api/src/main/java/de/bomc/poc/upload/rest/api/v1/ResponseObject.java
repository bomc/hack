/**
 * Project: Poc-upload
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: 2017-01-18 07:20:52 +0100 (Mi, 18 Jan 2017) $
 *
 *  revision: $Revision: 9690 $
 *
 * </pre>
 */
package de.bomc.poc.upload.rest.api.v1;

import java.io.Serializable;

/**
 * A simple object to represent an http response as a result of a service validation of a hoko-request. The response object providing additional information that is useful to identify the state of the validation
 * request.
 *
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 13.12.2016
 */
public class ResponseObject implements Serializable {

    /**
	 * The serial uuid.
	 */
	private static final long serialVersionUID = 2757642206143134053L;
	/**
     * @required true
     * @description Code beschreibt den Status der Validierung.
     */
    private String statusCode;
    /**
     * @required true
     * @description Text beschreibt den Status der Validierung.
     */
    private String statusText;
    /**
     * @required true
     * @description unique identifier of the request. Is a UUID Type 4 Random string, length 36 character.
     */
    private String requestId;
    /**
     * @required true
     * @description time that is required by the service to perform the upload (in ms).
     */
    private String duration;

    /**
     * Creates a new instance of <code>ResponseObject</code> (default-co).
     */
    public ResponseObject() {
        // indicates a pojo.
    }

    /**
     * Creates a new instance of <code>ResponseObject</code>.
     * @param statusCode is required, the result of the validation.
     * @param statusText is required, describes the result of the validation in text form.
     * @param requestId  is optional, for additional validation state information.
     * @param duration   is required, time that is required by the service to perform the validation (in ms).
     */
    public ResponseObject(final String statusCode, final String statusText, final String requestId, final String duration) {
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.requestId = requestId;
        this.duration = duration;
    }

    public String getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(final String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusText() {
        return this.statusText;
    }

    public void setStatusText(final String statusText) {
        this.statusText = statusText;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(final String duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResponseObject)) {
            return false;
        }

        final ResponseObject that = (ResponseObject)o;

        if (!this.getStatusCode()
                 .equals(that.getStatusCode())) {
            return false;
        }
        if (!this.getStatusText()
                 .equals(that.getStatusText())) {
            return false;
        }
        return this.getRequestId().equals(that.getRequestId());
    }

    @Override
    public int hashCode() {
        int
            result =
            this.getStatusCode()
                .hashCode();
        result =
            31 * result + this.getStatusText()
                              .hashCode();
        result =
            31 * result + this.getRequestId()
                              .hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ResponseObject [statusCode=" + this.statusCode + ", statusText=" + this.statusText + ", requestId=" + this.requestId + ", duration=" + this.duration + "]";
    }
}
