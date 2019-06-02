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
package de.bomc.poc.order.interfaces.rest.v1.basis.overview;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.bomc.poc.exception.cdi.interceptor.ExceptionHandlerInterceptor;
import de.bomc.poc.exception.core.BasisErrorCodeEnum;
import de.bomc.poc.exception.core.app.AppRuntimeException;
import de.bomc.poc.logging.qualifier.LoggerQualifier;
import org.apache.log4j.Logger;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ResourceMethodRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.bomc.poc.order.application.basis.log.qualifier.AuditLogQualifier;
import de.bomc.poc.order.application.basis.performance.qualifier.PerformanceTrackingQualifier;
import de.bomc.poc.order.interfaces.rest.v1.basis.ApiOverviewRestEndpoint;

/**
 * A resource implementation that displays a list of available endpoints.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
@AuditLogQualifier
@PerformanceTrackingQualifier
@Interceptors({ ExceptionHandlerInterceptor.class })
public class ApiOverviewRestEndpointImpl implements ApiOverviewRestEndpoint {

    private static final String LOG_PREFIX = "ApiOverviewRestEndpointImpl#";
    @Inject
    @LoggerQualifier
    private Logger logger;

    @Override
    public Response getAvailableEndpoints(@Context final Dispatcher dispatcher) {

        final ResourceMethodRegistry registry = (ResourceMethodRegistry) dispatcher.getRegistry();

        final List<ResourceDescription> descriptions = ResourceDescription
                .fromBoundResourceInvokers(registry.getBounded().entrySet());

        final JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        descriptions.forEach(description -> {
            final JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
            jsonObjectBuilder.add("basePath", description.getBasePath());
            description.getCalls().forEach(methodDescription -> {
                jsonObjectBuilder.add("fullPath", methodDescription.getFullPath());
                jsonObjectBuilder.add("method", methodDescription.getMethod());
                jsonObjectBuilder.add("consumes",
                        ((methodDescription.getConsumes() == null) ? "not set" : methodDescription.getConsumes()));
                jsonObjectBuilder.add("produces",
                        ((methodDescription.getProduces() == null) ? "not set" : methodDescription.getProduces()));
                jsonObjectBuilder.add("deprecated", methodDescription.isDeprecated());
                jsonArrayBuilder.add(jsonObjectBuilder);
            });
        });

        final ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        final String jsonString = jsonArrayBuilder.build().toString();

        try {
            final Object json = mapper.readValue(jsonString, Object.class);
            final String formattedJsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);

            return Response.ok().entity(formattedJsonString).build();
        } catch (final IOException ioEx) {
            final String errorMessage = LOG_PREFIX + "getAvailableEndpoints - Read value from json string failed! ";
            final AppRuntimeException appRuntimeException = new AppRuntimeException(errorMessage, BasisErrorCodeEnum.UNEXPECTED_10000);
            this.logger.error(appRuntimeException);
            
            throw appRuntimeException;
        }
    }

    // ______________________________________________________
    // Inner classes
    /**
     * POJO to represent Method information collected using reflection and rest
     * easy registry.
     */
    public static final class MethodDescription implements Serializable {

        /**
         * The serial UID.
         */
        private static final long serialVersionUID = 6057048858487208370L;
        private String method;
        private String fullPath;
        private String produces;
        private String consumes;
        private boolean deprecated;

        /**
         * Creates a new instance of <code>MethodDescription</code>.
         */
        public MethodDescription() {
            //
        }

        public MethodDescription(final String method, final String fullPath, final String produces,
                final String consumes, final boolean deprecated) {
            this.method = method;
            this.fullPath = fullPath;
            this.produces = produces;
            this.consumes = consumes;
            this.deprecated = deprecated;
        }

        public String getMethod() {
            return this.method;
        }

        public void setMethod(final String method) {
            this.method = method;
        }

        public String getFullPath() {
            return this.fullPath;
        }

        public void setFullPath(final String fullPath) {
            this.fullPath = fullPath;
        }

        public String getProduces() {
            return this.produces;
        }

        public void setProduces(final String produces) {
            this.produces = produces;
        }

        public String getConsumes() {
            return this.consumes;
        }

        public void setConsumes(final String consumes) {
            this.consumes = consumes;
        }

        public boolean isDeprecated() {
            return this.deprecated;
        }

        public void setDeprecated(final boolean deprecated) {
            this.deprecated = deprecated;
        }
    } // end inner class

    /**
     * POJO to represent Resource information collected using reflection and
     * rest easy registry
     */
    public static final class ResourceDescription implements Serializable {

        private static final long serialVersionUID = 4692040940508432363L;
        private static final String LOG_PREFIX = "ApiOverviewRestEndpointImpl#ResourceDescription#";
        private static final Logger LOGGER = Logger.getLogger(ResourceDescription.class);
        private String basePath;
        private List<MethodDescription> calls;

        /**
         * Creates a new instance of <code>ResourceDescription</code>.
         */
        public ResourceDescription() {
            //
        }

        public ResourceDescription(final String basePath) {
            this.basePath = basePath;
            this.calls = new ArrayList<MethodDescription>();
        }

        public void addMethod(final String path, final ResourceInvoker resourceInvoker) {
            if (resourceInvoker instanceof ResourceMethodInvoker) {
                ResourceMethodInvoker method = (ResourceMethodInvoker) resourceInvoker;
                String produces = mostPreferredOrNull(Arrays.asList(method.getProduces()));
                String consumes = mostPreferredOrNull(Arrays.asList(method.getConsumes()));

                // Read method description.
                calls.addAll(method.getHttpMethods().stream()
                        .map(verb -> new MethodDescription(verb, path, produces, consumes,
                                method.getMethod().isAnnotationPresent(Deprecated.class)))
                        .collect(Collectors.toList()));
            }
        }

        private static String mostPreferredOrNull(final List<MediaType> preferred) {
            if (preferred.isEmpty()) {
                return null;
            } else {
                return preferred.get(0).toString();
            }
        }

        public static List<ResourceDescription> fromBoundResourceInvokers(
                final Set<Map.Entry<String, List<ResourceInvoker>>> bound) {
            final Map<String, ResourceDescription> descriptions = new HashMap<String, ResourceDescription>();

            for (Map.Entry<String, List<ResourceInvoker>> entry : bound) {
                final ResourceInvoker aMethod = entry.getValue().get(0);
                final String basePath = aMethod.getMethod().getDeclaringClass().getAnnotation(Path.class).value();

                if (aMethod.getMethod().getAnnotation(Path.class) != null) {
                    final String methodEndpoint = aMethod.getMethod().getAnnotation(Path.class).value();

                    if (!descriptions.containsKey(basePath)) {
                        descriptions.put(basePath, new ResourceDescription(basePath));
                    }

                    for (ResourceInvoker invoker : entry.getValue()) {
                        descriptions.get(basePath).addMethod(basePath + methodEndpoint, invoker);
                    }
                } else {
                    LOGGER.warn(LOG_PREFIX + "fromBoundResourceInvokers - no methodEndpoint set to basepath. [basePath"
                            + basePath + "]");
                }
            }

            final List<ResourceDescription> retList = new ArrayList<ResourceDescription>();
            retList.addAll(descriptions.values());

            return retList;
        }

        public String getBasePath() {
            return this.basePath;
        }

        public void setBasePath(final String basePath) {
            this.basePath = basePath;
        }

        public List<MethodDescription> getCalls() {
            return this.calls;
        }

        public void setCalls(final List<MethodDescription> calls) {
            this.calls = calls;
        }
    } // end inner class
}
