package de.bomc.poc.service.impl;

import org.apache.log4j.Logger;

import de.bomc.poc.logging.qualifier.LoggerQualifier;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * A startup singleton EJB that returns os information.
 *
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 07.03.2016
 */
@Startup
@Singleton
public class ServerStatisticsSingletonEJB {
    private static final String LOG_PREFIX = "ServerStatisticsSingletonEJB#";
    private static final long MEGA_BYTE = 1024L;
    @Inject
    @LoggerQualifier
    private Logger logger;
    private String startTime;
    private MemoryMXBean memoryMxBean;

    @PostConstruct
    public void initialize() {
        this.logger.debug(LOG_PREFIX + "initialize");

        this.initializeStartTime();
        this.memoryMxBean = ManagementFactory.getMemoryMXBean();
    }

    private void initializeStartTime() {
        this.logger.debug(LOG_PREFIX + "initializeStartTime");

        final LocalDateTime dateTime = LocalDateTime.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withLocale(new Locale("de"));

        this.startTime = dateTime.format(formatter);
    }

    public String getDateTimeAsString() {
        this.logger.debug(LOG_PREFIX + "getDateTimeAsString");

        return this.startTime;
    }

    public double availableMemoryInMB() {
        this.logger.debug(LOG_PREFIX + "availableMemoryInMB");

        final MemoryUsage current = this.memoryMxBean.getHeapMemoryUsage();
        final long available = current.getCommitted() - current.getUsed();

        return this.asMb(available);
    }

    public double usedMemoryInMb() {
        this.logger.debug(LOG_PREFIX + "usedMemoryInMb");

        final MemoryUsage current = this.memoryMxBean.getHeapMemoryUsage();

        return this.asMb(current.getUsed());
    }

    private double asMb(final long bytes) {
        return (double)bytes / MEGA_BYTE / MEGA_BYTE;
    }

    /**
     * @return a <code>JsonObject</code> with os informations.
     */
    public JsonObject osInfo() {
        this.logger.debug(LOG_PREFIX + "osInfo");

        final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

        final JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("System Load Average", osBean.getSystemLoadAverage())
               .add("Available CPUs", osBean.getAvailableProcessors())
               .add("Architecture", osBean.getArch())
               .add("OS Name", osBean.getName())
               .add("Version", osBean.getVersion());

        return builder.build();
    }
}
