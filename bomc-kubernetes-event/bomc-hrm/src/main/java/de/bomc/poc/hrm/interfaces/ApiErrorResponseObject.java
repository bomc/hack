/**
 * Project: POC PaaS
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
package de.bomc.poc.hrm.interfaces;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.ToString;

/**
 * A error response container.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.05.2019
 */
@Builder
@ToString
public class ApiErrorResponseObject {

	// The HTTP status code.
	private HttpStatus status;
	// A short error description.
    private String shortErrorCodeDescription;
    // A unique error code.
    private String errorCode;
    // A unique identifier.
    private String uuid;
    

}
