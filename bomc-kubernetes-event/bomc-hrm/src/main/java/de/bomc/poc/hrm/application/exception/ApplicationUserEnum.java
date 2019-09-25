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
package de.bomc.poc.hrm.application.exception;

/**
 * This enum holds the app users.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public enum ApplicationUserEnum {

    SYSTEM_USER("UserEntity that should be used for operation inside the application."),
    TEST_USER("UserEntity that should be used for tests.");
    //
    // Short description.
    private String shortDescription = "no description available";

    /**
     * Creates a new enum by the given values.
     * 
     * @param shortDescription
     */
    ApplicationUserEnum(final String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getShortDescription() {
        return this.shortDescription;
    }

    @Override
    public String toString() {
        // Do not overwrite this method. It has an impact to the enum.name()
        // method.
        return super.toString();
    }
}
