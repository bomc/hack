/**
 * Project: Poc-upload
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: 2017-01-23 10:56:24 +0100 (Mo, 23 Jan 2017) $
 *
 *  revision: $Revision: 9942 $
 *
 * </pre>
 */
package de.bomc.poc.exception.errorcode;

import de.bomc.poc.exception.core.ErrorCode;

/**
 * Defines error codes for upload-service.
 *
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 03.08.2016
 */
public enum AppUploadErrorCode implements ErrorCode {

	UPLOAD_SERVICE_INITIALIZATION_00101("Fatal: Initialization failed!");

	private final String shortErrorCodeDescription;

	/**
	 * Creates a new instance of <code>AppUploadErrorCode</code>.
	 *
	 * @param shortErrorCodeDescription
	 *            the a short error code description.
	 */
	AppUploadErrorCode(final String shortErrorCodeDescription) {
		this.shortErrorCodeDescription = shortErrorCodeDescription;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getShortErrorCodeDescription() {
		return this.shortErrorCodeDescription;
	}
}
