package de.bomc.poc.exception.core.web;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.bomc.poc.exception.core.ErrorCode;

/**
 * A error response object that holds data of a web exception.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"uuid", "status", "errorCode", "shortErrorCodeDescription"})
public class ApiErrorResponseObject {

    @XmlJavaTypeAdapter(ResponseStatusXmlAdapter.class)
    private Response.Status status;
    private String shortErrorCodeDescription;
    private String errorCode;
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
     * @param uuid      identifier for the exception.
     * @param status    the given reponse status.
     * @param errorCode the description of this error.
     */
    public ApiErrorResponseObject(final String uuid, final Response.Status status, final ErrorCode errorCode) {
        this.uuid = uuid;
        this.status = status;
        this.shortErrorCodeDescription = errorCode.getShortErrorCodeDescription();
        this.errorCode = errorCode.toString();
    }

    /**
     * Creates a new instance of <code>ApiErrorResponseObject</code>.
     * @param uuid                      identifier for the exception.
     * @param status                    the given reponse status.
     * @param shortErrorCodeDescription the description of this error as string.
     */
    public ApiErrorResponseObject(final String uuid, final Response.Status status, final String shortErrorCodeDescription) {
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
	 * @param shortErrorCodeDescription the shortErrorCodeDescription to set
	 */
	protected void setShortErrorCodeDescription(String shortErrorCodeDescription) {
		this.shortErrorCodeDescription = shortErrorCodeDescription;
	}

    /**
     * @return the response status.
     */
    public Response.Status getStatus() {
        return this.status;
    }

    /**
	 * @param status the status to set
	 */
    protected void setStatus(Response.Status status) {
		this.status = status;
	}
	
    /**
     * @return the errorCode.
     */
    public String getErrorCode() {
        return this.errorCode;
    }

	/**
	 * @param errorCode the errorCode to set
	 */
    protected void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
    /**
     * @return the identifier for this exception.
     */
    public String getUuid() {
        return this.uuid;
    }

	/**
	 * @param uuid the uuid to set
	 */
    protected void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
    @Override
    public String toString() {
        return "ApiErrorResponseObject [uuid=" + this.uuid + ", response.status=" + this.status + ", errorCode=" + this.errorCode + ", shortErrorCodeDescription=" + this.shortErrorCodeDescription + "]";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final ApiErrorResponseObject that = (ApiErrorResponseObject)o;

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
        int hashPrime = 31;
        int result = this.status.hashCode();
        result = hashPrime * result + this.shortErrorCodeDescription.hashCode();
        result = hashPrime * result + this.errorCode.hashCode();
        result = hashPrime * result + this.uuid.hashCode();
        return result;
    }
}

