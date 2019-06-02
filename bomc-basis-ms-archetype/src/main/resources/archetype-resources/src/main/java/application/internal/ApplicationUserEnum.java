#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: ${symbol_dollar}Author: bomc ${symbol_dollar}
 *
 *  date: ${symbol_dollar}Date: ${symbol_dollar}
 *
 *  revision: ${symbol_dollar}Revision: ${symbol_dollar}
 *
 * </pre>
 */
package ${package}.application.internal;

/**
 * This enum holds the app users.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public enum ApplicationUserEnum {

    SYSTEM_USER("User that should be used for operation inside the application."),
    TEST_USER("User that should be used for tests.");
    //
    // Short description.
    private String shortDescription = "no description available";

    /**
     * Creates a new enum by the given values.
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
