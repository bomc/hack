package de.bomc.poc.exception.core.web;

/**
 * Project: bomc-exception-lib-ext
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
import de.bomc.poc.exception.core.ErrorCode;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * A error response object that holds data of a web exception.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: bomc $ $Date: $
 * @since 09.02.2018
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "uuid", "status", "errorCode", "shortErrorCodeDescription" })
public class ApiErrorResponseObject {

	// The server status.
	@XmlJavaTypeAdapter(ResponseStatusXmlAdapter.class)
	private Response.Status status;
	// A short description of this error in text form.
	private String shortErrorCodeDescription;
	// The description of this error by a number.
	private String errorCode;
	// The identifier for the exception.
	private String uuid;

	/**
	 * Creates a new instance of <code>ApiErrorResponseObject</code>.
	 */
	public ApiErrorResponseObject() {
		//
		// Used by JAXB-Provider
	}

	/**
	 * Creates a new instance of <code>ApiErrorResponseObject</code>.
	 * 
	 * @param uuid
	 *            identifier for the exception.
	 * @param status
	 *            the given reponse status.
	 * @param errorCode
	 *            the description of this error.
	 */
	public ApiErrorResponseObject(final String uuid, final Response.Status status, final ErrorCode errorCode) {
		this.uuid = uuid;
		this.status = status;
		this.shortErrorCodeDescription = errorCode.getShortErrorCodeDescription();
		this.errorCode = errorCode.toString();
	}

	/**
	 * Creates a new instance of <code>ApiErrorResponseObject</code>.
	 * 
	 * @param uuid
	 *            identifier for the exception.
	 * @param status
	 *            the given reponse status.
	 * @param shortErrorCodeDescription
	 *            the description of this error as string.
	 */
	public ApiErrorResponseObject(final String uuid, final Response.Status status,
			final String shortErrorCodeDescription) {
		this.uuid = uuid;
		this.status = status;
		this.shortErrorCodeDescription = shortErrorCodeDescription;
		this.errorCode = "0";
	}

	/**
	 * @return a short description of this error.
	 */
	public String getShortErrorCodeDescription() {
		return this.shortErrorCodeDescription;
	}

	/**
	 * @return the response status.
	 */
	public Response.Status getStatus() {
		return this.status;
	}

	/**
	 * @return the errorCode as a String e.g. "
	 *
	 */
	public String getErrorCode() {
		return this.errorCode;
	}

	/**
	 * @return the identifier for this exception.
	 */
	public String getUuid() {
		return this.uuid;
	}

	@Override
	public String toString() {
		return "ApiErrorResponseObject [uuid=" + this.uuid + ", response.status=" + this.status + ", errorCode="
				+ this.errorCode + ", shortErrorCodeDescription=" + this.shortErrorCodeDescription + "]";
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || this.getClass() != o.getClass()) {
			return false;
		}

		final ApiErrorResponseObject that = (ApiErrorResponseObject) o;

		if (this.status != that.status) {
			return false;
		}
		if (!this.shortErrorCodeDescription.equals(that.shortErrorCodeDescription)) {
			return false;
		}
		if (!this.errorCode.equals(that.errorCode)) {
			return false;
		}
		return this.uuid.equals(that.uuid);
	}

	@Override
	public int hashCode() {
		final int hashPrime = 31;
		int result = this.status.hashCode();
		result = hashPrime * result + this.shortErrorCodeDescription.hashCode();
		result = hashPrime * result + this.errorCode.hashCode();
		result = hashPrime * result + this.uuid.hashCode();
		return result;
	}
}
