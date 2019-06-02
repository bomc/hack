package de.bomc.poc.exception.errorcode;

import de.bomc.poc.exception.core.ErrorCode;

/**
 * Defines error codes for account-service.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.08.2016
 */
public enum AccountServiceErrorCode implements ErrorCode {
	ACCOUNT_SERVICE_INITIALIZATION_00101("Fatal: Initialization failed!"),
	ACCOUNT_SERVICE_ILLEGAL_PARAMETER_00102("Fatal: The value does not correspond to the assumed value, value could be null!");
	
	private final String shortErrorCodeDescription;

	/**
	 * Creates a new instance of <code>AccountServiceErrorCode</code>.
	 * 
	 * @param shortErrorCodeDescription
	 *            the a short error code description.
	 */
	AccountServiceErrorCode(final String shortErrorCodeDescription) {
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
