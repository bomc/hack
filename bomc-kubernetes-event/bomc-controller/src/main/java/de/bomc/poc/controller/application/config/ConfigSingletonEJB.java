/**
 * Project: bomc-onion-architecture
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
package de.bomc.poc.controller.application.config;

import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import org.apache.log4j.Logger;
/**
 * This EJB reads configuration properties from 'configuration.properties' file
 * at startup.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Startup
@Singleton
public class ConfigSingletonEJB {

    private static final String LOG_PREFIX = "ConfigSingletonEJB#";
    private static final String CONFIGURATION_PROPERTIES_FILE = "configuration.properties";
    // _______________________________________________
    // Default parameter for service name.
    // -----------------------------------------------
    public static final String DEFAULT_SERVICE_NAME = "bomc-controller";
    public static final String DEFAULT_EXCEPTION_LOG_SCHEDULER_MINUTE = "59";
    public static final String DEFAULT_EXCEPTION_LOG_SCHEDULER_HOUR = "23";
    public static final String DEFAULT_EXCEPTION_LOG_SCHEDULER_DAY_OF_WEEK = "Mon-Sun";
    public static final String DEFAULT_EXCEPTION_LOG_SCHEDULER_PERSISTENT = "false";
    public static final String DEFAULT_EXCEPTION_LOG_SCHEDULER_TEST_MODUS = "true";
    public static final String DEFAULT_EXCEPTION_LOG_DAYS_TO_SUBSTRACT = "30";
    // _______________________________________________
    // Member variables.
    // -----------------------------------------------
    private String serviceName = DEFAULT_SERVICE_NAME;
    private String exceptionSchedulerMinute = DEFAULT_EXCEPTION_LOG_SCHEDULER_MINUTE;
    private String exceptionSchedulerHour = DEFAULT_EXCEPTION_LOG_SCHEDULER_HOUR;
    private String exceptionSchedulerDayOfWeek = DEFAULT_EXCEPTION_LOG_SCHEDULER_DAY_OF_WEEK;
    private String exceptionSchedulerPersistent = DEFAULT_EXCEPTION_LOG_SCHEDULER_PERSISTENT;
    private String exceptionSchedulerTestModus = DEFAULT_EXCEPTION_LOG_SCHEDULER_TEST_MODUS;
    private String exceptionSchedulerDaysToSubstract = DEFAULT_EXCEPTION_LOG_DAYS_TO_SUBSTRACT;
    @Inject
    @LoggerQualifier
    private Logger logger;
    private String nodeName = "";

    @PostConstruct
    public void init() {
        this.logger.debug(LOG_PREFIX + "init");

        String propertyFromConfigFile = null;

        try {
            // Get the node name of the running wildfly instance
            // -Djboss.node.name=servicename.stage
            // 'servicename' indicates the service, and stage indicates the
            // stage: local, test, prod. 'local'-stage is used for testing.
            final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            final ObjectName environmentMBean = new ObjectName("jboss.as:core-service=server-environment");
            this.nodeName = (String)mBeanServer.getAttribute(environmentMBean, "nodeName");
            // Extract service name from node name.
            final String[] nodeNameParts = this.nodeName.split("\\.");
            // First part of the nodename indicates the servicename.
            this.serviceName = nodeNameParts[0];
            final String stageName = nodeNameParts[1];

            final InputStream
                inputStream =
                this.getClass()
                    .getClassLoader()
                    .getResourceAsStream(CONFIGURATION_PROPERTIES_FILE);

            final Properties properties = new Properties();
            // Loading the properties from file.
            properties.load(inputStream);

            // Constant for service name.
            // Constant for nodename, looks like 'servicename.stage'
            final String serviceStageNameDot = serviceName + "." + stageName + ".";
            logger.debug(LOG_PREFIX + "using prefix " + serviceStageNameDot + " for configuration properties [serviceName=" + this.serviceName + "]");

            // authorization config
            propertyFromConfigFile = properties.getProperty(serviceStageNameDot + ConfigKeys.EXCEPTION_LOG_SCHEDULER_MINUTE.getPropertyValue());
            this.setExceptionSchedulerMinute(propertyFromConfigFile);

            propertyFromConfigFile = properties.getProperty(serviceStageNameDot + ConfigKeys.EXCEPTION_LOG_SCHEDULER_HOUR.getPropertyValue());
            this.setExceptionSchedulerHour(propertyFromConfigFile);

            propertyFromConfigFile = properties.getProperty(serviceStageNameDot + ConfigKeys.EXCEPTION_LOG_SCHEDULER_DAY_OF_WEEK.getPropertyValue());
            this.setExceptionSchedulerDayOfWeek(propertyFromConfigFile);

            propertyFromConfigFile = properties.getProperty(serviceStageNameDot + ConfigKeys.EXCEPTION_LOG_SCHEDULER_PERSISTENT.getPropertyValue());
            this.setExceptionSchedulerPersistent(propertyFromConfigFile);

            propertyFromConfigFile = properties.getProperty(serviceStageNameDot + ConfigKeys.EXCEPTION_LOG_SCHEDULER_TEST_MODUS.getPropertyValue());
            this.setExceptionSchedulerTestModus(propertyFromConfigFile);

            propertyFromConfigFile = properties.getProperty(serviceStageNameDot + ConfigKeys.EXCEPTION_LOG_SCHEDULER_DAYS_TO_SUBSTRACT.getPropertyValue());
            this.setExceptionSchedulerDaysToSubstract(propertyFromConfigFile);
        } catch (final Exception ex) {
            this.logger.warn(LOG_PREFIX
                             + "init - reading properties from file failed! \nUsing default properties! \n[failed on="
                             + propertyFromConfigFile
                             + "] \nNOTE: Check if 'configuration.properties'-file is in deployment-artifact or node.name is set!", ex);
        }

        this.logger.info(LOG_PREFIX + "init - Starting service with following parameters - " + this.toString());
    }

    public String getServiceName() {
        this.logger.debug(LOG_PREFIX + "getServiceName [serviceName=" + serviceName + "]");

        return this.serviceName;
    }

    public void setServiceName(final String serviceName) {
        this.logger.debug(LOG_PREFIX + "setServiceName [serviceName=" + serviceName + "]");

        if(serviceName != null) {
            this.serviceName = serviceName;
        } else {
            this.logger.warn(LOG_PREFIX + "setServiceName - check node.name of app server, start with default value. [serviceName=" + DEFAULT_SERVICE_NAME + "]");
            this.serviceName = DEFAULT_SERVICE_NAME;
        }
    }

    public String getExceptionSchedulerMinute() {
        this.logger.debug(LOG_PREFIX + "getExceptionSchedulerMinute [exceptionSchedulerMinute=" + exceptionSchedulerMinute + "]");

        return this.exceptionSchedulerMinute;
    }

    public void setExceptionSchedulerMinute(final String exceptionSchedulerMinute) {
        this.logger.debug(LOG_PREFIX + "setExceptionSchedulerMinute [exceptionSchedulerMinute=" + exceptionSchedulerMinute + "]");

        if(exceptionSchedulerMinute != null) {
             this.exceptionSchedulerMinute = exceptionSchedulerMinute;
        } else {
            this.logger.warn(LOG_PREFIX + "setExceptionSchedulerMinute - check node.name of app server, start with default value. [exceptionSchedulerMinute=" + DEFAULT_EXCEPTION_LOG_SCHEDULER_MINUTE + "]");
            this.exceptionSchedulerMinute = DEFAULT_EXCEPTION_LOG_SCHEDULER_MINUTE;
        }
    }

    public String getExceptionSchedulerHour() {
        this.logger.debug(LOG_PREFIX + "getExceptionSchedulerHour [exceptionSchedulerHour=" + exceptionSchedulerHour + "]");

        return this.exceptionSchedulerHour;
    }

    public void setExceptionSchedulerHour(final String exceptionSchedulerHour) {
        this.logger.debug(LOG_PREFIX + "setExceptionSchedulerHour [exceptionSchedulerHour=" + exceptionSchedulerHour + "]");
        
        if(exceptionSchedulerHour != null) {
            this.exceptionSchedulerHour = exceptionSchedulerHour;
        } else {
            this.logger.warn(LOG_PREFIX + "setExceptionSchedulerHour - check node.name of app server, start with default value. [exceptionSchedulerHour=" + DEFAULT_EXCEPTION_LOG_SCHEDULER_HOUR + "]");
            this.exceptionSchedulerHour = DEFAULT_EXCEPTION_LOG_SCHEDULER_HOUR;
        }
    }

    public String getExceptionSchedulerDayOfWeek() {
        this.logger.debug(LOG_PREFIX + "getExceptionSchedulerDayOfWeek [exceptionSchedulerDayOfWeek=" + exceptionSchedulerDayOfWeek + "]");

        return this.exceptionSchedulerDayOfWeek;
    }

    public void setExceptionSchedulerDayOfWeek(final String exceptionSchedulerDayOfWeek) {
        this.logger.debug(LOG_PREFIX + "setExceptionSchedulerDayOfWeek [exceptionSchedulerDayOfWeek=" + exceptionSchedulerDayOfWeek + "]");

        if(exceptionSchedulerDayOfWeek != null) {
            this.exceptionSchedulerDayOfWeek = exceptionSchedulerDayOfWeek;
        } else {
            this.logger.warn(LOG_PREFIX + "setExceptionSchedulerDayOfWeek - check node.name of app server, start with default value. [exceptionSchedulerDayOfWeek=" + DEFAULT_EXCEPTION_LOG_SCHEDULER_DAY_OF_WEEK + "]");
            this.exceptionSchedulerDayOfWeek = DEFAULT_EXCEPTION_LOG_SCHEDULER_DAY_OF_WEEK;
        }
    }

    public String getExceptionSchedulerPersistent() {
        this.logger.debug(LOG_PREFIX + "getExceptionSchedulerPersistent [exceptionSchedulerPersistent=" + exceptionSchedulerPersistent + "]");

        return this.exceptionSchedulerPersistent;
    }

    public void setExceptionSchedulerPersistent(final String exceptionSchedulerPersistent) {
        this.logger.debug(LOG_PREFIX + "setExceptionSchedulerPersistent [exceptionSchedulerPersistent=" + exceptionSchedulerPersistent + "]");

        if(exceptionSchedulerPersistent != null) {
            this.exceptionSchedulerPersistent = exceptionSchedulerPersistent;
        } else {
            this.logger.warn(LOG_PREFIX + "exceptionSchedulerPersistent - check node.name of app server, start with default value. [exceptionSchedulerPersistent=" + DEFAULT_EXCEPTION_LOG_SCHEDULER_PERSISTENT + "]");
            this.exceptionSchedulerPersistent = DEFAULT_EXCEPTION_LOG_SCHEDULER_PERSISTENT;
        }
    }

    public String getExceptionSchedulerTestModus() {
        this.logger.debug(LOG_PREFIX + "getExceptionSchedulerTestModus [exceptionSchedulerTestModus=" + exceptionSchedulerTestModus + "]");

        return this.exceptionSchedulerTestModus;
    }

    public void setExceptionSchedulerTestModus(final String exceptionSchedulerTestModus) {
        this.logger.debug(LOG_PREFIX + "exceptionSchedulerTestModus [exceptionSchedulerTestModus=" + exceptionSchedulerTestModus + "]");

        if(exceptionSchedulerTestModus != null) {
            this.exceptionSchedulerTestModus = exceptionSchedulerTestModus;
        } else {
            this.logger.warn(LOG_PREFIX + "setExceptionSchedulerTestModus - check node.name of app server, start with default value. [exceptionSchedulerTestModus=" + DEFAULT_EXCEPTION_LOG_SCHEDULER_TEST_MODUS + "]");
            this.exceptionSchedulerTestModus = DEFAULT_EXCEPTION_LOG_SCHEDULER_TEST_MODUS;
        }
    }

    public String getExceptionSchedulerDaysToSubstract() {
        this.logger.debug(LOG_PREFIX + "getExceptionSchedulerDaysToSubstract [exceptionSchedulerDaysToSubstract=" + exceptionSchedulerDaysToSubstract + "]");

        return this.exceptionSchedulerDaysToSubstract;
    }

    public void setExceptionSchedulerDaysToSubstract(final String exceptionSchedulerDaysToSubstract) {
        this.logger.debug(LOG_PREFIX + "setExceptionSchedulerDaysToSubstract [exceptionSchedulerDaysToSubstract=" + exceptionSchedulerDaysToSubstract + "]");

        if(exceptionSchedulerDaysToSubstract != null) {
            this.exceptionSchedulerDaysToSubstract = exceptionSchedulerDaysToSubstract;
        } else {
            this.logger.warn(LOG_PREFIX + "setExceptionSchedulerDaysToSubstract - check node.name of app server, start with default value. [exceptionSchedulerDaysToSubstract=" + DEFAULT_EXCEPTION_LOG_DAYS_TO_SUBSTRACT + "]");
            this.exceptionSchedulerDaysToSubstract = DEFAULT_EXCEPTION_LOG_DAYS_TO_SUBSTRACT;
        }
    }

    @Override
    public String toString() {
        return LOG_PREFIX
               + "toString [exceptionSchedulerMinute="
               + exceptionSchedulerMinute
               + ", exceptionSchedulerHour="
               + exceptionSchedulerHour
               + ", exceptionSchedulerDayOfWeek="
               + exceptionSchedulerDayOfWeek
               + ", exceptionSchedulerTestModus="
               + exceptionSchedulerTestModus
               + ", exceptionSchedulerPersistent="
               + exceptionSchedulerPersistent
               + ", exceptionSchedulerDaysToSubstract="
               + exceptionSchedulerDaysToSubstract
               + ", nodeName="
               + nodeName
               + "]";
    }
}
