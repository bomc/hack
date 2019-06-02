/**
 * Project: MY_POC_MICROSERVICE
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.exception.handling;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A error response object that is handles a web exception.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "status", "errorCode", "message" })
public class ApiErrorResponse {

	@XmlJavaTypeAdapter(ResponseStatusAdapter.class)
	private Response.Status status;
	private String errorCode;
	private String message;

	/**
	 * Creates a new instance of <code>ApiErrorResponse</code>.
	 * 
	 */
	public ApiErrorResponse() {
	}

	/**
	 * Creates a new instance of <code>ApiErrorResponse</code>.
	 * 
	 * @param apiError
	 *            the description of this error.
	 * @param locale
	 *            the given locale.
	 */
	public ApiErrorResponse(ApiError apiError, Locale locale) {
		this.status = apiError.getStatus();
		this.errorCode = apiError.getErrorCode();
		this.message = getLocalizedMessage(apiError, locale);
	}

	/**
	 * Creates a new instance of <code>ApiErrorResponse</code>.
	 * 
	 * @param exception
	 *            the given exception object.
	 * @param locale
	 *            the given locale.
	 */
	public ApiErrorResponse(Exception exception, Locale locale) {
		this.status = Response.Status.INTERNAL_SERVER_ERROR;
		this.errorCode = "0";
		this.message = exception.getLocalizedMessage();
	}

	/**
	 * Creates a new instance of <code>ApiErrorResponse</code>.
	 * 
	 * @param status
	 *            the given error status.
	 * @param errorCode
	 *            the given error code.
	 * @param messageKey
	 *            the given message key.
	 * @param locale
	 *            te given locale.
	 */
	public ApiErrorResponse(Response.Status status, String errorCode, String messageKey, Locale locale) {
		this.status = status;
		this.errorCode = errorCode;
		this.message = getLocalizedMessage(messageKey, locale);
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public Response.Status getStatus() {
		return status;
	}

	public String getMessage() {
		return this.message;
	}

	private String getLocalizedMessage(final ApiError apiError, final Locale locale) {
		System.out.println("ApiErrorResponse#getLocalizedMessage [apiError=" + apiError.getStatus() + ", locale=" + locale.toString() + ", locale.baseName=" + apiError.getClass().getName() + "]");

		final ResourceBundle resourceBundle = ResourceBundle.getBundle("apiUsermanagementError"/*apiError.getClass().getName()*/, locale);
		
		return resourceBundle.getString(apiError.getMessageKey());
	}

	private String getLocalizedMessage(final String messageKey, final Locale locale) {
		final ResourceBundle resourceBundle = ResourceBundle.getBundle(getClass().getName(), locale);
		
		return resourceBundle.getString(messageKey);
	}

	@Override
	public String toString() {
		return "ApiErrorResponse [status=" + status + ", errorCode=" + errorCode + ", message=" + message + "]";
	}
}
