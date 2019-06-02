/**
 * Project: MY_POC_MICROSERVICE
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.os.runtime;

import de.bomc.poc.logger.qualifier.LoggerQualifier;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * A startup singleton ejb, that delivers some runtime information.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Startup
@Singleton
public class RuntimeInfoSingletonEJB {

    /**
     * Logger.
     */
    @Inject
    @LoggerQualifier
    private Logger logger;
    private String startTime;
    private MemoryMXBean memoryMxBean;

    @PostConstruct
    public void initialize() {
        this.logger.debug("RuntimeInfoSingletonEJB#initialize");

        this.initializeStartTime();
        this.memoryMxBean = ManagementFactory.getMemoryMXBean();
    }

    private void initializeStartTime() {
        this.logger.debug("RuntimeInfoSingletonEJB#initializeStartTime");

        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                                                       .withLocale(new Locale("de"));
        this.startTime = dateTime.format(formatter);
    }

    public String getDateTimeAsString() {
        this.logger.debug("RuntimeInfoSingletonEJB#getDateTimeAsString");

        return this.startTime;
    }

    public double availableMemoryInMB() {
        this.logger.debug("RuntimeInfoSingletonEJB#availableMemoryInMB");

        final MemoryUsage current = this.memoryMxBean.getHeapMemoryUsage();
        final long available = (current.getCommitted() - current.getUsed());

        return this.asMb(available);
    }

    public double usedMemoryInMb() {
        this.logger.debug("RuntimeInfoSingletonEJB#usedMemoryInMb");

        final MemoryUsage current = this.memoryMxBean.getHeapMemoryUsage();

        return this.asMb(current.getUsed());
    }

    private double asMb(final long bytes) {
        return bytes / 1024 / 1024;
    }

    /**
     * @return a <code>JsonObject</code> with os informations.
     */
    public JsonObject osInfo() {
        this.logger.debug("RuntimeInfoSingletonEJB#osInfo");

        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

        JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("System Load Average", osBean.getSystemLoadAverage())
               .add("Available CPUs", osBean.getAvailableProcessors())
               .add("Architecture", osBean.getArch())
               .add("OS Name", osBean.getName())
               .add("Version", osBean.getVersion());

        return builder.build();
    }

    public String getNodeName() {
        this.logger.debug("RuntimeInfoSingletonEJB#getNodeName");

        String nodeName;

        try {
            ObjectName serverMBean = new ObjectName("jgroups:type=channel,cluster=\"ee\"");
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            nodeName = (String)server.getAttribute(serverMBean, "address");
            
            this.logger.info("RuntimeInfoSingletonEJB#getNodeName [node.name=" + nodeName + "]");
        } catch (Exception e) {
            this.logger.error("Unable to identify the node.name", e);
            nodeName = "Unable to identify the node.name";
        }

        return nodeName;
    }
}
