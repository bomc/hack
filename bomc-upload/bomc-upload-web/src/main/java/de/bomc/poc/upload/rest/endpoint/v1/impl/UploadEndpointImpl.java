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
package de.bomc.poc.upload.rest.endpoint.v1.impl;

import javax.ejb.EJB;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import de.bomc.poc.exception.cdi.interceptor.ExceptionHandlerInterceptor;
import de.bomc.poc.exception.cdi.qualifier.ExceptionHandlerQualifier;
import de.bomc.poc.logging.filter.MDCFilter;
import de.bomc.poc.upload.rest.endpoint.v1.UploadEndpoint;
import de.bomc.poc.upload.transfer.TransferLocal;

/**
 * REST implementation for file uploading.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 07.03.2016
 */
@ExceptionHandlerQualifier
@Interceptors(ExceptionHandlerInterceptor.class)
public class UploadEndpointImpl implements UploadEndpoint {

	private static final Logger LOGGER = Logger.getLogger(UploadEndpointImpl.class);
	private static final String LOG_PREFIX = "UploadEndpointImpl#";

	@Context
	private HttpHeaders httpHeaders;
	@EJB
	private TransferLocal transferEJB;
	
	/**
	 * curl -i -v -H "Accept: application/vnd.bomc-v1+json" -H "Content-Type: multipart/form-data; charset=utf-8" --form "attachment=@bomc_readMe.txt" "http://192.168.4.1:8180/bomc-upload/rest/api/upload?requestId=af4af44e-16aa-4910-b945-e2cba15ca583"
	 */
	@Override
	public Response uploadFile(final String requestId, final MultipartFormDataInput multipartFormDataInput) {
		LOGGER.debug(LOG_PREFIX + "upload [requestId=" + requestId + ", length=" + httpHeaders.getLength() + " bytes, X-BOMC-REQUEST-ID=" + httpHeaders.getRequestHeader(MDCFilter.HEADER_REQUEST_ID_ATTR).get(0) + "]");
		
        final Response response = this.transferEJB.transferUpload(requestId, multipartFormDataInput);

        // Close it manually to cleanup temporary generated files in user directory.
        if (multipartFormDataInput != null) {
            multipartFormDataInput.close();
        }

        return response;
	}
}
