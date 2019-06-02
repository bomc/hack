package de.bomc.poc.test.exception.arq.mock;

import de.bomc.poc.auth.rest.endpoint.v2.usermanagement.AuthUserManagementRestEndpoint;
import de.bomc.poc.exception.handling.ApiUsermanagementError;
import de.bomc.poc.exception.interceptor.ApiExceptionInterceptor;
import de.bomc.poc.exception.qualifier.ApiExceptionQualifier;
import de.bomc.poc.exception.web.WebUsermanagementException;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;
import java.util.Locale;

/**
 * A mock implementation for endpoint {@link AuthUserManagementRestEndpoint}.
 */
@ApiExceptionQualifier
@Interceptors(ApiExceptionInterceptor.class)
public class MockAuthUserManagementRestEndpointImpl implements AuthUserManagementRestEndpoint {
    @Inject
    @LoggerQualifier
    Logger logger;

    @Override
    public Response findAllRolesByUsername(final String username) {
        this.logger.debug("MockAuthUserManagementRestEndpointImpl#findAllRolesByUsername");

        final ApiUsermanagementError apiUsermanagementError = ApiUsermanagementError.U10001;

        // Throws a exception.
        throw new WebUsermanagementException(apiUsermanagementError, Locale.getDefault());
    }
}
