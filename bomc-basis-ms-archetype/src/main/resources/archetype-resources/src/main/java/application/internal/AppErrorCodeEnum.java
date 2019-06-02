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

import de.bomc.poc.exception.core.BasisErrorCodeEnum;
import de.bomc.poc.exception.core.ErrorCode;
import org.apache.log4j.Logger;

/**
 * This enum holds all errors with code and description.
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
    // Indicates connection error (10500-10599)
    //
    // Application error codes (10600-10699)
    APP_INITILIZATION_START_COMPONENT_FAILURE_10600(10600, Category.ERROR, "Error during initialization of component (e.g. ejb or cdi bean). "),
    APP_CREATE_LOG_ENTRY_FAILED_10601(10601, Category.ERROR, "Creating log entry failed."),
    APP_READ_VERSION_FAILED_10602(10602, Category.ERROR, "Reading current version from 'version.properties' file failed!"),
    APP_JSON_MAPPING_FAILED_10603(10603, Category.ERROR, "Mapping or parsing to json failed!"),
    // MBean handling error (10700-10799)
    MBEAN_PATH_TO_NULL_10700(10700, Category.FATAL, "Could not register MBean, because given path is null."),
    MBEAN_REGISTRATION_FAILED_10701(10701, Category.FATAL, "Register MBean failed."),
    MBEAN_UNREGISTRATION_FAILED_10702(10702, Category.ERROR, "Unregister MBean failed."),
    MBEAN_HANDLING_FAILED_10703(10703, Category.ERROR, "Processing operation on MBean failed."),
    MBEAN_NOT_FOUND_10704(10704, Category.ERROR, "MBean not found by the given name."),
    MBEAN_SETUP_GAUGE_MONITOR_FAILED_10705(10705, Category.ERROR, "MBean setup GaugeMonitor failed.");
    //
    // Member variables.
    private static final String LOG_PREFIX = "AppErrorCodeEnum${symbol_pound}";
    private static final Logger LOGGER = Logger.getLogger(AppErrorCodeEnum.class);
    //
    // Error code description.
    private final int code;
    private final Category category;
    private String shortErrorCodeDescription = "no description available";

    /**
     * Creates a new enum by the given values.
     * @param errorCode                 the given errorCode.
     * @param category                  the given category.
     * @param shortErrorCodeDescription the given shortErrorCodeDescription.
     */
    AppErrorCodeEnum(final int errorCode, final Category category, final String shortErrorCodeDescription) {
        this.code = errorCode;
        this.category = category;
        this.shortErrorCodeDescription = shortErrorCodeDescription;
    }

    /**
     * Returns the numerical value for this error code.
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
     * @param errorCode the given int value.
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
     * @param errorCodeStr the given string value.
     * @return the <code>ErrorCode</code> on the depending string value.
     */
    public static ErrorCode errorCodeFromString(final String errorCodeStr) {
        try {
            return Enum.valueOf(AppErrorCodeEnum.class, errorCodeStr);
        } catch (final IllegalArgumentException e) {
            LOGGER.warn(LOG_PREFIX + "errorCodefromString - could not parse error code to Integer: " + errorCodeStr + ", set new errorCode: " + BasisErrorCodeEnum.UNEXPECTED_10000);
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
