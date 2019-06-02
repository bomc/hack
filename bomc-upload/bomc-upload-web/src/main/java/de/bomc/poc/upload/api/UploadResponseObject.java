/**
 * Project: Poc-upload
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: 2017-01-18 09:18:34 +0100 (Mi, 18 Jan 2017) $
 *
 *  revision: $Revision: 9699 $
 *
 * </pre>
 */
package de.bomc.poc.upload.api;

import java.io.Serializable;

/**
 * Response object for upload-service.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 21.12.2016
 */
public class UploadResponseObject implements Serializable {

	/**
	 * The serial uuid.
	 */
	private static final long serialVersionUID = 7086436673021904558L;
	/**
	 * @required true
	 * @description Code beschreibt den Status des Uploads.
	 */
	private String statusCode;
	/**
	 * @required true
	 * @description Text beschreibt den Status der Uploads.
	 */
	private String statusText;
	/**
	 * @required true
	 * @description unique identifier of the request. Is a UUID Type 4 Random
	 *              string, length 36 character.
	 */
	private String requestId;
	/**
	 * @required true
	 * @description duration of the method call (in ms).
	 */
	private long duration;

	/**
	 * Creates a new instance of <code>UploadResponseObject</code> (default-co).
	 */
	public UploadResponseObject() {
		// indicates a pojo.
	}

	/**
	 * Creates a new instance of <code>UploadResponseObject</code>.
	 * 
	 * @param statusCode
	 *            is required, the result of the validation.
	 * @param statusText
	 *            is required, describes the result of the validation in text
	 *            form.
	 * @param requestId
	 *            is required, unique identifier for this request.
	 * @param duration
	 *            duration of the method call (in ms).
	 */
	public UploadResponseObject(final String statusCode, final String statusText, final String requestId, final long duration) {
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
	
	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof UploadResponseObject)) {
			return false;
		}

		final UploadResponseObject that = (UploadResponseObject) o;

		if (!this.getStatusCode().equals(that.getStatusCode())) {
			return false;
		}
		if (!this.getStatusText().equals(that.getStatusText())) {
			return false;
		}
		return this.getRequestId().equals(that.getRequestId());
	}

	@Override
	public int hashCode() {
		int result = this.getStatusCode().hashCode();
		result = 31 * result + this.getStatusText().hashCode();
		result = 31 * result + this.getRequestId().hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "UploadResponseObject [statusCode=" + this.statusCode + ", statusText=" + this.statusText
				+ ", requestId=" + this.requestId + ", duration=" + this.duration + "]";
	}
}
