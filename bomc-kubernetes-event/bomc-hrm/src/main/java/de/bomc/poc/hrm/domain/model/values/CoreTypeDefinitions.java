/**
 * Project: hrm
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
package de.bomc.poc.hrm.domain.model.values;

/**
 * A CoreTypeDefinitions.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public final class CoreTypeDefinitions {

	/** Length of a Description field: {@value} */
	public static final int DESCRIPTION_LENGTH = 1024;
	/** Length of a Quantity field: {@value} */
	public static final int QUANTITY_LENGTH = 16;
	/** Minimum length of username: {@value} */
	public static final int MINIMUM_USERNAME_LENGTH = 5;
	/** Minimum length of fullname: {@value} */
	public static final int MINIMUM_FULLNAME_LENGTH = 5;
	/** Maximum length of fullname: {@value} */
	public static final int MAXIMUM_FULLNAME_LENGTH = 64;
	
	private CoreTypeDefinitions() {
	}
}
