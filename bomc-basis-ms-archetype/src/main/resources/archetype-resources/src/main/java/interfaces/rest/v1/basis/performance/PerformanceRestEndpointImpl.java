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
package ${package}.interfaces.rest.v1.basis.performance;

import de.bomc.poc.exception.cdi.interceptor.ExceptionHandlerInterceptor;
import de.bomc.poc.logging.qualifier.LoggerQualifier;

import ${package}.application.basis.jmx.MBeanController;
import ${package}.application.basis.jmx.performance.PerformanceEntry;
import ${package}.application.basis.jmx.performance.PerformanceTracking;
import ${package}.application.basis.jmx.performance.PerformanceTrackingMXBean;
import ${package}.interfaces.rest.v1.basis.PerformanceRestEndpoint;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;
import java.util.function.Function;

/**
 * This endpoint returns metrics in worst cases (time, average and count) for all deployed endpoints.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
@Interceptors({ExceptionHandlerInterceptor.class})
public class PerformanceRestEndpointImpl implements PerformanceRestEndpoint {

    private static final String LOG_PREFIX = "PerformanceRestEndpointImpl${symbol_pound}";

    @Inject
    @LoggerQualifier
    private Logger logger;

    @Inject
    private MBeanController mBeanController;

    @Override
    public Response getWorstByAverage() {
        this.logger.debug(LOG_PREFIX + "getWorstByAverage");

        return toResponse(PerformanceTrackingMXBean::getWorstByAverage);
    }

    @Override
    public Response getWorstByCount() {
        this.logger.debug(LOG_PREFIX + "getWorstByCount");

        return toResponse(PerformanceTrackingMXBean::getWorstByCount);
    }

    @Override
    public Response getWorstByTime() {
        this.logger.debug(LOG_PREFIX + "getWorstByTime");

        return toResponse(PerformanceTrackingMXBean::getWorstByTime);
    }

    /**
     * Convert the given <code>PerformanceEntry</code> to a response object.
     *
     * @param extractor function to extract the correct entry from a given performance tracking bean.
     * @return the response object, or nothing available an empty object.
     */
    private Response toResponse(Function<PerformanceTrackingMXBean, PerformanceEntry> extractor) {
        this.logger.debug(LOG_PREFIX + "toResponse");

        final PerformanceTrackingMXBean performanceTracking = (PerformanceTrackingMXBean) this.mBeanController.getMBeanBySimpleName(PerformanceTracking.class.getSimpleName());
        final PerformanceEntry performanceEntry = extractor.apply(performanceTracking);

        // Convert entry to JSON and build response.
        final JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        
        if (performanceEntry != null) {
            jsonObjectBuilder.add("service", performanceEntry.getService())
                    .add("method", performanceEntry.getMethod())
                    .add("monitoring enabled", performanceEntry.isMonitoringEnabled())
                    .add("average", performanceEntry.getAverage())
                    .add("count", performanceEntry.getInvocationCount())
                    .add("min", performanceEntry.getMin())
                    .add("max", performanceEntry.getMax());
        }

        return Response.ok()
                .entity(jsonObjectBuilder.build())
                .build();
    }
}
