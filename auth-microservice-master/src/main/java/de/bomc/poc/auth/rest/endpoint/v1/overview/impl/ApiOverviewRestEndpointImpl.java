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
package de.bomc.poc.auth.rest.endpoint.v1.overview.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

import de.bomc.poc.exception.interceptor.ApiExceptionInterceptor;
import de.bomc.poc.exception.qualifier.ApiExceptionQualifier;
import de.bomc.poc.exception.web.WebJsonProcessingException;
import org.apache.log4j.Logger;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ResourceMethodRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.bomc.poc.auth.rest.endpoint.v1.overview.ApiOverviewRestEndpoint;
import de.bomc.poc.exception.handling.ApiOverviewError;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.monitor.qualifier.PerformanceTrackingQualifier;

/**
 * A resource that displays a list of available endpoints.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@PerformanceTrackingQualifier
@ApiExceptionQualifier
@Interceptors(ApiExceptionInterceptor.class)
public class ApiOverviewRestEndpointImpl implements ApiOverviewRestEndpoint {
	/**
	 * The logger.
	 */
	@Inject
	@LoggerQualifier
	private Logger logger;

	@Override
	public Response getAvailableEndpoints(@Context final Dispatcher dispatcher) {
		this.logger.debug("ApiOverviewRestEndpointImpl#getAvailableEndpoints");

		final ResourceMethodRegistry registry = (ResourceMethodRegistry) dispatcher.getRegistry();

		final List<ResourceDescription> descriptions = ResourceDescription.fromBoundResourceInvokers(registry.getBounded().entrySet());
		
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
		} catch (IOException ioEx) {
			final String errorMessage = "ApiOverviewRestEndpointImpl#getAvailableEndpoints - Read value from json string failed! ";
			this.logger.error(errorMessage, ioEx);

			throw new WebJsonProcessingException(ApiOverviewError.O10001, Locale.getDefault());
		}	
	}

	// ______________________________________________________
	// Inner classes

	/**
	 * POJO to represent Method information collected using reflection and rest
	 * easy registry
	 */
	public static final class MethodDescription implements Serializable {

		private static final long serialVersionUID = 4395400223398707629L;
		private String method;
		private String fullPath;
		private String produces;
		private String consumes;
		private boolean deprecated;

		public MethodDescription() {

		}

		public MethodDescription(final String method, final String fullPath, final String produces,
				final String consumes, final boolean deprecated) {
			super();
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
	}

	/**
	 * POJO to represent Resource information collected using reflection and
	 * rest easy registry
	 */
	public static final class ResourceDescription implements Serializable {

		private static final long serialVersionUID = 4692040940508432363L;
		private String basePath;
		private List<MethodDescription> calls;

		public ResourceDescription() {

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

		public static List<ResourceDescription> fromBoundResourceInvokers(final Set<Map.Entry<String, List<ResourceInvoker>>> bound) {
			final Map<String, ResourceDescription> descriptions = new HashMap<String, ResourceDescription>();
			
			for (Map.Entry<String, List<ResourceInvoker>> entry : bound) {
				final ResourceInvoker aMethod = entry.getValue().get(0);
				final String basePath = aMethod.getMethod().getDeclaringClass().getAnnotation(Path.class).value();
				final String methodEndpoint = aMethod.getMethod().getAnnotation(Path.class).value();

				if (!descriptions.containsKey(basePath)) {
					descriptions.put(basePath, new ResourceDescription(basePath));
				}

				for (ResourceInvoker invoker : entry.getValue()) {
					descriptions.get(basePath).addMethod(basePath + methodEndpoint, invoker);
				}
			}

			final List<ResourceDescription> retList = new ArrayList<ResourceDescription>();
			retList.addAll(descriptions.values());

			return retList;
		}

		public String getBasePath() {
			return basePath;
		}

		public void setBasePath(final String basePath) {
			this.basePath = basePath;
		}

		public List<MethodDescription> getCalls() {
			return calls;
		}

		public void setCalls(final List<MethodDescription> calls) {
			this.calls = calls;
		}
	}
}
