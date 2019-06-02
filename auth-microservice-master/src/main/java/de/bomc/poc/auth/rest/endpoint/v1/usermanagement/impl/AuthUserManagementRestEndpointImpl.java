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
package de.bomc.poc.auth.rest.endpoint.v1.usermanagement.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import de.bomc.poc.api.generic.Parameter;
import de.bomc.poc.api.generic.transfer.request.RequestObjectDTO;
import de.bomc.poc.api.generic.transfer.response.ResponseObjectDTO;
import de.bomc.poc.api.generic.types.LongType;
import de.bomc.poc.api.generic.types.StringType;
import de.bomc.poc.api.http.CustomHttpResponseCode;
import de.bomc.poc.api.jaxb.GenericResponseObjectDTOCollectionMapper;
import de.bomc.poc.api.mapper.transfer.RoleDTO;
import de.bomc.poc.auth.rest.endpoint.v1.usermanagement.AuthUserManagementRestEndpoint;
import de.bomc.poc.exception.handling.ApiUsermanagementError;
import de.bomc.poc.exception.interceptor.ApiExceptionInterceptor;
import de.bomc.poc.exception.qualifier.ApiExceptionQualifier;
import de.bomc.poc.exception.web.WebUsermanagementException;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.monitor.qualifier.PerformanceTrackingQualifier;
import de.bomc.poc.rest.client.RestClientBuilder;
import de.bomc.poc.rest.filter.authorization.AuthorizationTokenHeaderRequestFilter;

/**
 * A facade v1 for user management operations.
 * <p/>
 * Die Idee, bei dieser Art von Schnittstellen Versionierung ist, dass die
 * bestehenden Clients weiterhin diesen Service ohne Beeintraechtigungen aufrufen
 * können sollen. Sie bekommen allerdings den Status 301 zurück. Das bedeutet
 * für die Clients, dass es eine neue Version V2 gibt. Damit nicht mehrere
 * Implementationen einer bestimmten Methode vorhanden sind, ruft v1, v2 auf. In
 * v2 steckt die tatsaechliche Implemenatation der Methode, in v1 wird der
 * Methodenaufruf entprechend angepasst, so dass v1 v2 aufrufen kann, ohne dass
 * die Schnittstelle gebrochen wird.
 * <p/>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@ApiExceptionQualifier
@Interceptors(ApiExceptionInterceptor.class)
@PerformanceTrackingQualifier
public class AuthUserManagementRestEndpointImpl implements AuthUserManagementRestEndpoint {
	/**
	 * The logger.
	 */
	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	private RestClientBuilder restClientBuilder;
	@Context
	private UriInfo uriInfo;
	@Context
	private HttpHeaders headers;

	@PostConstruct
	public void init() {
		if (this.restClientBuilder == null) {
			this.logger
					.debug("AuthUserManagementRestEndpointImpl#init - restClientBuilder is null, could not be injected. [ApiError="
							+ ApiUsermanagementError.U10020.toString() + "]");

			throw new WebUsermanagementException(ApiUsermanagementError.U10020, Locale.getDefault());
		}

		if (this.uriInfo == null) {
			this.logger
					.debug("AuthUserManagementRestEndpointImpl#init#U10020 - uriInfo is null, could not be injected. [ApiError="
							+ ApiUsermanagementError.U10020.toString() + "]");

			throw new WebUsermanagementException(ApiUsermanagementError.U10020, Locale.getDefault());
		}

		if (this.headers == null) {
			this.logger
					.debug("AuthUserManagementRestEndpointImpl#init - headers is null, could not be injected. [ApiError="
							+ ApiUsermanagementError.U10020.toString() + "]");

			throw new WebUsermanagementException(ApiUsermanagementError.U10020, Locale.getDefault());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response findAllRolesByUsername(final RequestObjectDTO requestObjectDTO) {
		this.logger.debug("AuthUserManagementRestEndpointImpl#findAllRolesByUsername");

		Response responseV2 = null;
		List<ResponseObjectDTO> responseObjectDTOList = new ArrayList<>();
		int status = -1;

		try {
			final List<Parameter> parameterList = requestObjectDTO.parameters();
			final Parameter parameter = parameterList.iterator().next();

			if (parameter.getName().equals(USERNAME_REQUEST_PARAMETER) && (parameter.getType() instanceof StringType)) {
				// Start request to underlying layer v2.
				final String username = parameter.getValue().toString();

				// Get token from header.
				final List<String> authTokenList = this.headers
						.getRequestHeader(AuthorizationTokenHeaderRequestFilter.AUTHORIZATION_PROPERTY);
				String authToken = null;

				if (authTokenList != null && (!authTokenList.isEmpty())) {
					authToken = authTokenList.get(0);
				}
				// ___________________________________
				// Here the method fits to version v2.
				final ResteasyWebTarget resteasyClient = this.restClientBuilder.buildResteasyClient(authToken,
						this.uriInfo.getBaseUri());
				de.bomc.poc.auth.rest.endpoint.v2.usermanagement.AuthUserManagementRestEndpoint proxy = resteasyClient
						.proxy(de.bomc.poc.auth.rest.endpoint.v2.usermanagement.AuthUserManagementRestEndpoint.class);
				responseV2 = proxy.findAllRolesByUsername(username);

				// Check and get response.
				if (responseV2 != null && responseV2.getStatus() == Response.Status.OK.getStatusCode()) {
					final List<RoleDTO> roleDTOList = responseV2.readEntity(new GenericType<List<RoleDTO>>() {
					});
					//
					// Create response object.
					for (final RoleDTO roleDTO : roleDTOList) {
						this.logger.debug("AuthUserManagementRestEndpointImpl#findAllRolesByUsername -->" + roleDTO);
						responseObjectDTOList
								.add(ResponseObjectDTO
										.with(new Parameter("roleId", new LongType(roleDTO.getId()))).and(
												new Parameter("roleName", new StringType(roleDTO.getName())))
								.and(new Parameter("roleDescription", new StringType(roleDTO.getDescription()))));
					}

					status = CustomHttpResponseCode.DEPRECATED_API;
				}
			}
		} finally {
			if (responseV2 != null) {
				responseV2.close();
			}
		}

		// NOTE: The GenericType mapper could not be used here. A own list
		// mapper is used.
		final Response.ResponseBuilder builder = Response.status(status)
				.entity(new GenericResponseObjectDTOCollectionMapper(responseObjectDTOList));

		return builder.build();
	}
}
