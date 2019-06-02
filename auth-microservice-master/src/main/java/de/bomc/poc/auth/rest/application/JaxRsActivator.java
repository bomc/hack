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
package de.bomc.poc.auth.rest.application;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * <pre>
 * A class extending {@link Application} and annotated with @ApplicationPath is the Java EE 7 "no XML" approach to activating JAX-RS.
 * <p/>
 * <p> Resources are served relative to the servlet path specified in the {@link ApplicationPath} annotation. </p>
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@ApplicationPath(JaxRsActivator.APPLICATION_PATH)
public class JaxRsActivator extends Application {

    // Defines the base URI for all resource URIs
    public static final String APPLICATION_PATH = "auth-api";
    // Defines the REST endpoint paths.
    public static final String OVERVIEW_ENDPOINT_PATH = "/overview";
    public static final String RUNTIME_ENDPOINT_PATH = "/runtime";
    public static final String USERMANAGEMENT_ENDPOINT_PATH = "/usermanagement";
    public static final String VERSION_ENDPOINT_PATH = "/version";
}
