/**
 * Project: MY_POC
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
 * Copyright (c): BOMC, 2015
 */
package de.bomc.poc.rest.filter.authorization;

import de.bomc.poc.logger.qualifier.LoggerQualifier;
import org.apache.log4j.Logger;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.metadata.ResourceMethod;

import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import de.bomc.poc.authorization.AuthorizationControllerLocal;

/**
 * A filter for authorization by a token header. This filter works only if one of the following annotations are present.
 * If no one of the anntoation below are present, the filter does not work.
 *
 * <pre>
 * JSR 250 definiert einige allgemeine Sicherheitsannotationen:
 * 	____________________________________
 * 	javax.annotation.security.PermitAll:
 * 		Kann auf Typ- oder Methodenebene verwendet werden.
 * 		Gibt an, dass alle Benutzer auf die angegebene Methode oder auf alle Geschaeftsmethoden der angegebenen REST-Resource zugreifen koennen.
 * 	__________________________________
 * 	javax.annotation.security.DenyAll:
 * 		Kann auf Methodenebene verwendet werden.
 * 		Gibt an, dass kein Benutzer auf die angegebene Methode der REST-Resource zugreifen kann.
 * 	_______________________________________
 * 	javax.annotation.security.RolesAllowed:
 * 		Kann auf Typ- oder Methodenebene verwendet werden.
 * 		Gibt an, dass auf die angegebene Methode oder alle Geschaeftsmethoden in der REST-Resource die Benutzer zugreifen koennen, die der Liste der Rollen zugeordnet sind.
 * 	_______________________________________
 * 	javax.annotation.security.DeclareRoles:
 * 		Kann auf Typebene verwendet werden.
 * 		Definiert Rollen für die Sicherheitspruefung. Muss von EJBContext.isCallerInRole, HttpServletRequest.isUserInRole und WebServiceContext.isUserInRole verwendet werden.
 * 	________________________________
 * 	javax.annotation.security.RunAs:
 * 		Kann auf Typebene verwendet werden.
 * 		Gibt die Rolle "Ausfuehren als" für die angegebenen Komponenten an.
 * </pre>
 *
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@Provider
@ServerInterceptor
@Priority(value = Priorities.AUTHORIZATION)
// TODO this with cookie
public class AuthorizationTokenFilter implements ContainerRequestFilter {
	/**
	 * Logger.
	 */
	@Inject
	@LoggerQualifier
	private Logger logger;
	private static final String AUTHORIZATION_TOKEN = "X-BOMC-AUTHORIZATION";
	private static final ServerResponse ACCESS_DENIED = new ServerResponse("Access denied for this resource (auth-microservice)", 401, new Headers<Object>());
	private static final ServerResponse ACCESS_FORBIDDEN = new ServerResponse("Nobody can access this resource (auth-microservice)", 403, new Headers<Object>());
	private static final ServerResponse SERVER_ERROR = new ServerResponse("INTERNAL SERVER ERROR (auth-microservice)", 500, new Headers<Object>());
// ISSUE_DELETE
//	@EJB
//	private AuthorizationControllerLocal ejb;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		logger.debug("AuthorizationTokenFilter#filter");

		ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) requestContext
				.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
		Method method = methodInvoker.getMethod();
		//
		// Access allowed for all, see above class description.
		// NOTE:
		// ISSUE_DELETE: 'method.getAnnotations().length > 0' allows access to all methods. Must be DELETED. in production mode.
		if (!method.isAnnotationPresent(PermitAll.class) || method.getAnnotations().length > 0) {
			//
			// Access denied for all
			if (method.isAnnotationPresent(DenyAll.class)) {
				requestContext.abortWith(ACCESS_FORBIDDEN);
				return;
			}

			final String authorizationToken = this.getToken(requestContext);

			if(authorizationToken == null) {
				requestContext.abortWith(ACCESS_DENIED);
				return;
			}

			logger.debug("AuthorizationTokenFilter#filter [username=" + authorizationToken + "]");

			// Verify user access.
			if (method.isAnnotationPresent(RolesAllowed.class)) {
				RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
				Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));

				// Is user valid?
				if (!isUserAllowed(authorizationToken, rolesSet)) {
					requestContext.abortWith(ACCESS_DENIED);
				}
			}
		}
	}

	private boolean isUserAllowed(final String username, final Set<String> rolesSet) {
		boolean isAllowed = false;
// ISSUE_DELETE: Must be commented out.
//		isAllowed = ejb.isUserInRole(username, rolesSet);

//		return isAllowed;
// ISSUE_DELETE: Must be deleted.
		// TODO: Start request to db.
		return true;
	}

	/**
	 * <p>
	 * Retrieve the token from the request, if present.
	 * </p>
	 *
	 * @param requestContext the incoming ContainerRequestContext.
	 * @return the token from the request, otherwise false.
	 */
	private String getToken(ContainerRequestContext requestContext) {
		String token = null;

		//
		// First check the header is used.
		//
		// Fetch authorization header.
		final MultivaluedMap<String, String> httpHeaders = requestContext.getHeaders();
		final List<String> authorizationList = httpHeaders.get(AUTHORIZATION_TOKEN);
		if (authorizationList != null && !authorizationList.isEmpty()) {
			token = authorizationList.iterator().next();
		}

		// If header token is not set, check if a cookie is set.
		if (token == null) {
			final Map<String, Cookie> cookies = requestContext.getCookies();
			final Cookie cookie = cookies.get(AUTHORIZATION_TOKEN);

			if (cookie != null && cookie.getValue() != null && !cookie.getValue().isEmpty()) {
				token = cookie.getValue();
			}
		}

		return token;
	}

	/**
	 * <p>
	 * Checks if the {@link ResourceMethod} requires authentication depends on
	 * the endpoint class.
	 * </p>
	 *
	 * @param method
	 *            the method to check.
	 * @return true if authorization is required otherwise false.
	 */
	@SuppressWarnings("unused")
	private boolean requiresAuthorization(ResourceMethod method) {
		Class<?> declaringClass = method.getMethod().getDeclaringClass();

		Class<?>[] arr = new Class[] { /*
										 * Enter here the resource endpoints in
										 * form xy.class
										 */ };

		List<Class<?>> classes = Arrays.asList(arr);

		return !classes.contains(declaringClass);
	}
}

