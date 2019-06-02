package de.bomc.poc.exception.core.web;

import javax.ws.rs.core.Response;

import de.bomc.poc.exception.RootRuntimeException;

/**
 * WebRuntimeException is a unchecked top-level exception for all unhandled runtime exceptions of http requests.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: 6790 $ $Author: tzdbmm $ $Date: 2016-07-19 09:06:34 +0200 (Di, 19 Jul 2016) $
 * @since 11.07.2016
 */
public class WebRuntimeException extends RootRuntimeException {

    /**
	 * The serial UUID.
	 */
	private static final long serialVersionUID = -6717578971752083855L;
	
	/**
     * Defines the response status, default is set to INTERNAL_SERVER_ERROR (500).
     */
    private Response.Status responseStatus = Response.Status.INTERNAL_SERVER_ERROR;

    /**
     * Creates a new instance of <code>WebRuntimeException</code>.
     * @param apiError the error description.
     */
    public WebRuntimeException(final ApiError apiError) {
        super(apiError);
        this.responseStatus = apiError.getStatus();
    }

    /**
     * Creates a new instance of <code>WebRuntimeException</code>.
     * @param message   the specified detail message.
     * @param apiError the error description.
     */
    public WebRuntimeException(final String message, final ApiError apiError) {
        super(message, apiError);
        this.responseStatus = apiError.getStatus();
    }

    /**
     * Creates a new instance of <code>WebRuntimeException</code>.
     * @param cause     the <code>Throwable</code> of the exception.
     * @param apiError the error description.
     */
    public WebRuntimeException(final Throwable cause, final ApiError apiError) {
        super(cause, apiError);

        if(apiError != null) {
            this.responseStatus = apiError.getStatus();
        }
    }

    /**
     * Creates a new instance of <code>AppRuntimeException</code>.
     * @param message   the specified detail message.
     * @param cause     the <code>Throwable</code> of the exception.
     * @param apiError the error description.
     */
    public WebRuntimeException(final String message, final Throwable cause, final ApiError apiError) {
        super(message, cause, apiError);

        if(apiError != null) {
            this.responseStatus = apiError.getStatus();
        }
    }

    /**
     * @return the response status code.
     */
    public Response.Status getResponseStatus() {
        return this.responseStatus;
    }

    /**
     * Wraps the <code>Throwable</code> in a Web-Application-RuntimeException.
     * @param exception the given exception to wrap.
     * @return a Web-Application-RuntimeException.
     */
    public static WebRuntimeException wrap(final Throwable exception) {
        return wrap(exception, null);
    }

    /**
     * Wraps the <code>Throwable</code> and the <code>ErrorCode</code> in a Web-Application-RuntimeException.
     * @param exception the given exception to wrap.
     * @param apiError the given apiError.
     * @return a Web-Application-RuntimeException.
     */
    public static WebRuntimeException wrap(final Throwable exception, final ApiError apiError) {
        if (exception != null) {
            if (exception instanceof WebRuntimeException) {
                final WebRuntimeException se = (WebRuntimeException)exception;

                if (apiError != null && apiError != se.getErrorCode()) {
                    if(exception.getMessage() != null) {
                        return new WebRuntimeException(exception.getMessage(), exception, apiError);
                    } else {
                        return new WebRuntimeException(exception, apiError);
                    }
                }

                return se;
            } else {
                return new WebRuntimeException(exception.getMessage(), exception, apiError);
            }
        } else {
            throw new NullPointerException("Parameter 'exception' can not be null! ");
        }
    }
}
