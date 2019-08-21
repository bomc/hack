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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bomc.poc.exception.core.BasisErrorCodeEnum;
import de.bomc.poc.exception.core.ErrorCode;

/**
 * This enum holds all errors with code and description.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public enum AppErrorCodeEnum implements ErrorCode {

    // NOTE: The UNEXPECETD error is in exception-lib-ext defined.
    // BasisErrorCodeEnum.UNEXPECTED_10000(10000, Category.ERROR, "This error
    // should not happen! ");
    // _______________________________________________
    // Add here new errorCodes.
    // -----------------------------------------------
    // MAJOR /general response errors (10000-10499)
    //
    // Indicates jpa/persistence error (10400-10499)
    JPA_PERSISTENCE_10400(10400, Category.ERROR, "Error occurs with entityManager handling."),
    JPA_PERSISTENCE_10401(10401, Category.ERROR, "No entity available in db."),
    // Indicates connection error (10500-10599)
    //
    // Application error codes (10600-10699)
    APP_INITILIZATION_START_COMPONENT_FAILURE_10600(10600, Category.ERROR, "Error during initialization of component. "), 
    APP_CREATE_LOG_ENTRY_FAILED_10601(10601, Category.ERROR, "Creating log entry failed."), 
    APP_READ_VERSION_FAILED_10602(10602, Category.ERROR, "Reading current version from 'version.properties' file failed!"), 
    APP_JSON_MAPPING_FAILED_10603(10603, Category.ERROR, "Mapping or parsing to json failed!"),
    APP_ILLEGAL_ARGUMENT_10604(10604, Category.ERROR, "Illegal argument parameter!"),
    APP_NO_RESOURCES_AVAILABLE_10605(10605, Category.ERROR, "No resources available in system!"),
	APP_INVALID_PASSWORD_10606(10606, Category.FATAL, "Invalid password"),
	APP_PASSWORD_CONFIRMS_NOT_RULES_10607(10607, Category.ERROR, "Password confirms not the defined rules."),
	APP_PASSWORD_OLDEST_PASSWORD_NOT_DETERMIND_10608(10608, Category.ERROR, "The oldest password could not be determined.");
    //
    // Member variables.
    private static final String LOG_PREFIX = "AppErrorCodeEnum#";
    private static final Logger LOGGER = LoggerFactory.getLogger(AppErrorCodeEnum.class.getName());
    //
    // Error code description.
    private final int code;
    private final Category category;
    private String shortErrorCodeDescription = "no description available";

    /**
     * Creates a new enum by the given values.
     * 
     * @param errorCode
     *            the given errorCode.
     * @param category
     *            the given category.
     * @param shortErrorCodeDescription
     *            the given shortErrorCodeDescription.
     */
    AppErrorCodeEnum(final int errorCode, final Category category, final String shortErrorCodeDescription) {
        this.code = errorCode;
        this.category = category;
        this.shortErrorCodeDescription = shortErrorCodeDescription;
    }

    /**
     * Returns the numerical value for this error code.
     * 
     * @return the error code as an unique {@code int} value.
     */
    @Override
    public int intValue() {
        return this.code;
    }

    @Override
    public String getShortErrorCodeDescription() {
        return this.shortErrorCodeDescription;
    }

    /**
     * Returns the <code>ErrorCode</code> on the depending int value.
     * 
     * @param errorCode
     *            the given int value.
     * @return the <code>ErrorCode</code> on the depending int value.
     */
    public static ErrorCode fromInt(final int errorCode) {
        final ErrorCode[] errorCodes = values();

        for (int i = 0; i < errorCodes.length; i++) {
            final ErrorCode error = errorCodes[i];
            if (error.intValue() == errorCode) {
                return error;
            }
        }

        throw new IllegalArgumentException("Unknown error code: " + errorCode);
    }

    /**
     * Returns the <code>ErrorCode</code> on the depending string value.
     * 
     * @param errorCodeStr
     *            the given string value.
     * @return the <code>ErrorCode</code> on the depending string value.
     */
    public static ErrorCode errorCodeFromString(final String errorCodeStr) {
        try {
            return Enum.valueOf(AppErrorCodeEnum.class, errorCodeStr);
        } catch (final IllegalArgumentException e) {
            LOGGER.warn(LOG_PREFIX + "errorCodefromString - could not parse error code to Integer: " + errorCodeStr
                    + ", set new errorCode: " + BasisErrorCodeEnum.UNEXPECTED_10000);
            return BasisErrorCodeEnum.UNEXPECTED_10000;
        }
    }

    /**
     * Returns the getCategory of the error.
     * <p>
     * Errors come in 2 categories: ERROR, FATAL
     * <p>
     * ERROR: A temporary error.
     * <p>
     * FATAL: The flow has to be aborted.
     * <p>
     * 
     * @return the severity of the error.
     */
    @Override
    public Category getCategory() {
        return this.category;
    }

    @Override
    public String toString() {
        // Do not overwrite this method. It has an impact to the enum.name()
        // method.
        return super.toString();
    }
}
