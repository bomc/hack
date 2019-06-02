/**
 * Project: cdi-axon
 * <pre>
 *
 * Last change:
 *
 *  by:       $Author$
 *
 *  date:     $Date$
 *
 *  revision: $Revision$
 *
 *  © Bomc 2018
 *
 * </pre>
 */
package de.bomc.poc.axon.application.rest;

/**
 * A container that holds the connection parameter for hystrix client.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 23.10.2018
 */
@ConnectionParameterQualifier
public class ConnectionParameter {
	//
	// Hystrix configuration properties
	public static final int DEFAULT_EXECUTION_TIMEOUT_IN_MILLI_SECONDS = 1000;
	public static final boolean DEFAULT_REQUEST_LOG_ENABLED = true;
	public static final int DEFAULT_CIRCUIT_BREAKER_REQUEST_VOLUME = 1;
	public static final int DEFAULT_CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLIS_SECONDS = 4000;
	public static final boolean DEFAULT_CIRCUIT_BREAKER_ENABLED = true;
	// Resteasy client configurations.
	public static final long DEFAULT_ESTABLISH_CONNECTION_TIMEOUT = 2000;
	public static final int DEFAULT_SOCKET_TIMEOUT_OFFSET = 2000;
	public static final int DEFAULT_MAX_CONNECTIONS = 1;
	//
	// Initialize variables with default values.
	// Hystrix variables.
	private int executionTimeoutInMilliseconds = DEFAULT_EXECUTION_TIMEOUT_IN_MILLI_SECONDS;
	private boolean isRequestLogEnabled = DEFAULT_REQUEST_LOG_ENABLED;
	private int circuitBreakerRequestVolume = DEFAULT_CIRCUIT_BREAKER_REQUEST_VOLUME;
	private int circuitBreakerSleepWindowInMilliseconds = DEFAULT_CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLIS_SECONDS;
	private boolean circuitBreakerEnabled = DEFAULT_CIRCUIT_BREAKER_ENABLED;
	// Resteasy client variables.
	private long establishConnectionTimeout = DEFAULT_ESTABLISH_CONNECTION_TIMEOUT;
	private int socketTimeoutOffset = DEFAULT_SOCKET_TIMEOUT_OFFSET;
	private int maxConnections = DEFAULT_MAX_CONNECTIONS;

	/**
	 * Creates a new instance of <code>ConnectionParameter</code>.
	 */
	public ConnectionParameter() {
		// Used by CDI provider.
	}

	public int getSocketTimeoutOffset() {
		return this.socketTimeoutOffset;
	}

	public void setSocketTimeoutOffset(int socketTimeoutOffset) {
		this.socketTimeoutOffset = socketTimeoutOffset;
	}

	public int getExecutionTimeoutInMilliseconds() {
		return this.executionTimeoutInMilliseconds;
	}

	public void setExecutionTimeoutInMilliseconds(int executionTimeoutInMilliseconds) {
		this.executionTimeoutInMilliseconds = executionTimeoutInMilliseconds;
	}

	public long getEstablishConnectionTimeout() {
		return this.establishConnectionTimeout;
	}

	public void setEstablishConnectionTimeout(long establishConnectionTimeout) {
		this.establishConnectionTimeout = establishConnectionTimeout;
	}

	public boolean isRequestLogEnabled() {
		return this.isRequestLogEnabled;
	}

	public void setRequestLogEnabled(boolean isRequestLogEnabled) {
		this.isRequestLogEnabled = isRequestLogEnabled;
	}

	public int getCircuitBreakerRequestVolume() {
		return this.circuitBreakerRequestVolume;
	}

	public void setCircuitBreakerRequestVolume(int circuitBreakerRequestVolume) {
		this.circuitBreakerRequestVolume = circuitBreakerRequestVolume;
	}

	public int getCircuitBreakerSleepWindowInMilliseconds() {
		return this.circuitBreakerSleepWindowInMilliseconds;
	}

	public void setCircuitBreakerSleepWindowInMilliseconds(final int circuitBreakerSleepWindowInMilliseconds) {
		this.circuitBreakerSleepWindowInMilliseconds = circuitBreakerSleepWindowInMilliseconds;
	}

	public boolean isCircuitBreakerEnabled() {
		return this.circuitBreakerEnabled;
	}

	public void setCircuitBreakerEnabled(final boolean circuitBreakerEnabled) {
		this.circuitBreakerEnabled = circuitBreakerEnabled;
	}

	public int getMaxConnections() {
		return this.maxConnections;
	}

	public void setMaxConnections(final int maxConnections) {
		this.maxConnections = maxConnections;
	}

	@Override
	public String toString() {
		return "ConnectionParameter [maxConnections=" + this.maxConnections + ", socketTimeoutOffset="
				+ this.socketTimeoutOffset + ", executionTimeoutInMilliseconds=" + this.executionTimeoutInMilliseconds
				+ ", establishConnectionTimeout=" + this.establishConnectionTimeout + ", isRequestLogEnabled="
				+ this.isRequestLogEnabled + ", circuitBreakerRequestVolume=" + this.circuitBreakerRequestVolume
				+ ", circuitBreakerSleepWindowInMilliseconds=" + this.circuitBreakerSleepWindowInMilliseconds
				+ ", circuitBreakerEnabled=" + this.circuitBreakerEnabled + "]";
	}

}
