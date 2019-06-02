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
package de.bomc.poc.order.interfaces.rest.v1.basis.performance;

import java.util.function.Function;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;

import de.bomc.poc.exception.cdi.interceptor.ExceptionHandlerInterceptor;
import de.bomc.poc.order.application.basis.jmx.MBeanController;
import de.bomc.poc.order.application.basis.jmx.performance.PerformanceEntry;
import de.bomc.poc.order.application.basis.jmx.performance.PerformanceTracking;
import de.bomc.poc.order.application.basis.jmx.performance.PerformanceTrackingMXBean;
import de.bomc.poc.order.application.basis.log.qualifier.AuditLogQualifier;
import de.bomc.poc.order.application.basis.performance.qualifier.PerformanceTrackingQualifier;
import de.bomc.poc.order.interfaces.rest.v1.basis.PerformanceRestEndpoint;

/**
 * This endpoint returns metrics in worst cases (time, average and count) for
 * all deployed endpoints.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
@AuditLogQualifier
@PerformanceTrackingQualifier
@Interceptors({ ExceptionHandlerInterceptor.class })
public class PerformanceRestEndpointImpl implements PerformanceRestEndpoint {

    @Inject
    private MBeanController mBeanController;

    @Override
    public Response getWorstByAverage() {

        return toResponse(PerformanceTrackingMXBean::getWorstByAverage);
    }

    @Override
    public Response getWorstByCount() {

        return toResponse(PerformanceTrackingMXBean::getWorstByCount);
    }

    @Override
    public Response getWorstByTime() {

        return toResponse(PerformanceTrackingMXBean::getWorstByTime);
    }

    /**
     * Convert the given <code>PerformanceEntry</code> to a response object.
     *
     * @param extractor
     *            function to extract the correct entry from a given performance
     *            tracking bean.
     * @return the response object, or nothing available an empty object.
     */
    private Response toResponse(Function<PerformanceTrackingMXBean, PerformanceEntry> extractor) {

        final PerformanceTrackingMXBean performanceTracking = (PerformanceTrackingMXBean) this.mBeanController
                .getMBeanBySimpleName(PerformanceTracking.class.getSimpleName());
        final PerformanceEntry performanceEntry = extractor.apply(performanceTracking);

        // Convert entry to JSON and build response.
        final JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();

        if (performanceEntry != null) {
            jsonObjectBuilder.add("service", performanceEntry.getService()).add("method", performanceEntry.getMethod())
                    .add("monitoring enabled", performanceEntry.isMonitoringEnabled())
                    .add("average", performanceEntry.getAverage()).add("count", performanceEntry.getInvocationCount())
                    .add("min", performanceEntry.getMin()).add("max", performanceEntry.getMax());
        }

        return Response.ok().entity(jsonObjectBuilder.build()).build();
    }
}
