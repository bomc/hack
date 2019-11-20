/**
 * Project: hrm
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
package de.bomc.poc.hrm.config.rest;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;

/**
 * @formatter:off
 * In HrmHttpClientConfig class, there are configuring mainly two things 
 * – PoolingHttpClientConnectionManager – As name suggest, its connection pool manager. 
 *   Here, connections are pooled on a per route basis. A request for a route which 
 *   already the manager has persistent connections for available in the pool will be 
 *   services by leasing a connection from the pool rather than creating a brand new 
 *   connection.
 *   ConnectionKeepAliveStrategy helps in setting time which decide how long a 
 *   connection can remain idle before being reused.
 * - And set a idleConnectionMonitor thread, which periodically check all connections 
 *   and free up which have not been used and idle time has elapsed.
 * The real http client to use is CloseableHttpClient bean. It is what RestTemplate 
 * will use to get the connection to API endpoints. Further:
 * - Supports both HTTP and HTTPS.
 * - Uses a connection pool to re-use connections and save overhead of creating connections.
 * - Has a custom connection keep-alive strategy (to apply a default keep-alive if one 
 *   isn't specified).
 * - Starts an idle connection monitor to continuously clean up stale connections.
 * @formatter:on
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 06.11.2019
 */
@Slf4j
@Configuration
public class HrmHttpClientConfig {

	private static final String LOG_PREFIX = HrmHttpClientConfig.class.getName() + "#";

	// _______________________________________________
	// Configuration constants
	// -----------------------------------------------
	// The timeout for a schdeuler that checks for idle connecitons.
	private static final int IDLE_CONNECTION_MONITOR_SCHEDULER_MS = 10 * 1000;
	// _______________________________________________
	// Member variables
	// -----------------------------------------------
	@Autowired
	private HrmHttpClientProperties hrmHttpClientProperties;

	@Bean
	public PoolingHttpClientConnectionManager poolingConnectionManager() {
		
		final SSLContextBuilder builder = new SSLContextBuilder();

		try {
			builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
		} catch (final NoSuchAlgorithmException | KeyStoreException e) {
			log.error(LOG_PREFIX + "poolingConnectionManager - initialization failure because of: " + e.getMessage(),
			        e);
		}

		SSLConnectionSocketFactory sslsf = null;

		try {
			sslsf = new SSLConnectionSocketFactory(builder.build());
		} catch (final KeyManagementException | NoSuchAlgorithmException e) {
			log.error(LOG_PREFIX + "poolingConnectionManager - initialisation failure because of: " + e.getMessage(),
			        e);
		}

		final Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
		        .<ConnectionSocketFactory>create().register("https", sslsf)
		        .register("http", new PlainConnectionSocketFactory()).build();

		final PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager(
		        socketFactoryRegistry);
		poolingConnectionManager.setMaxTotal(this.hrmHttpClientProperties.getMaxTotalConnections());

		return poolingConnectionManager;
	}

	@Bean
	public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
		
		return new ConnectionKeepAliveStrategy() {
			@Override
			public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
				final HeaderElementIterator it = new BasicHeaderElementIterator(
				        response.headerIterator(HTTP.CONN_KEEP_ALIVE));

				while (it.hasNext()) {
					final HeaderElement headerElement = it.nextElement();
					final String param = headerElement.getName();
					final String value = headerElement.getValue();

					if (value != null && param.equalsIgnoreCase("timeout")) {
						return Long.parseLong(value) * 1000;
					}
				}

				log.info(LOG_PREFIX + "ConnectionKeepAliveStrategy - using default keep alive time. [keepAlive in ms="
				        + hrmHttpClientProperties.getKeppAliveTime() + "]");
				return hrmHttpClientProperties.getKeppAliveTime();
			}
		};
	}

	@Bean
	public CloseableHttpClient httpClient() {
		
		final RequestConfig requestConfig = RequestConfig.custom()
		        .setConnectionRequestTimeout(this.hrmHttpClientProperties.getRequestTimeout())
		        .setConnectTimeout(this.hrmHttpClientProperties.getConnectionTimeoutInMillis())
		        .setSocketTimeout(this.hrmHttpClientProperties.getSocketTimeout()).build();

		return HttpClients.custom().setDefaultRequestConfig(requestConfig)
		        .setConnectionManager(poolingConnectionManager()).setKeepAliveStrategy(connectionKeepAliveStrategy())
		        .build();
	}

	@Bean
	public Runnable idleConnectionMonitor(final PoolingHttpClientConnectionManager connectionManager) {
		
		return new Runnable() {
			@Override
			@Scheduled(fixedDelay = IDLE_CONNECTION_MONITOR_SCHEDULER_MS)
			public void run() {
				try {
					if (connectionManager != null) {
						log.trace(LOG_PREFIX + "idleConnectionMonitor#run closing expired and idle connections...");

						connectionManager.closeExpiredConnections();
						connectionManager.closeIdleConnections(hrmHttpClientProperties.getCloseIdleConnectionWaitTime(),
						        TimeUnit.SECONDS);
					} else {
						log.trace(LOG_PREFIX
						        + "idleConnectionMonitor#run - HttpClientConnectionManager is not initialised!");
					}
				} catch (final Exception e) {
					log.error(LOG_PREFIX + "idleConnectionMonitor#run - exception occurred. [msg={}, e={}]",
					        e.getMessage(), e);
				}
			}
		};
	}
}
