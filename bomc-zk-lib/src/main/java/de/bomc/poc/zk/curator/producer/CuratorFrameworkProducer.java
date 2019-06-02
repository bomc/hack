package de.bomc.poc.zk.curator.producer;

import de.bomc.poc.zk.concurrent.qualifier.ManagedThreadFactoryQualifier;
import de.bomc.poc.zk.curator.qualifier.CuratorFrameworkQualifier;
import de.bomc.poc.zk.exception.AppZookeeperException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.log4j.Logger;

import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

/**
 * A zookeeper curator producer. This producer works only in wildfly, because a
 * wildfly specific <code>ManagedThreadFactory</code> is used.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 22.07.2016
 */
public class CuratorFrameworkProducer {

	private static final Logger LOGGER = Logger.getLogger(CuratorFrameworkProducer.class);
	private static final String LOG_PREFIX = "CuratorFrameworkProducer#";
	// Default properties for building a zookeeper curator.
	private static final int SESSION_TIMEOUT_MS = 10 * 1000;
	private static final int CONNECTION_TIMEOUT_MS = 5 * 1000;
	private static final int RETRY_NO = 10;
	private static final int RETRY_SLEEP = 1000;
	@Inject
	@ManagedThreadFactoryQualifier
	private ManagedThreadFactory defaultManagedThreadFactory;
	// The zookeeper curator as singleton.
	private CuratorFramework client;

	@Produces
	@ApplicationScoped
	@CuratorFrameworkQualifier
	public CuratorFramework produceCuratorFramework() {
		if (this.client == null) {
			LOGGER.info(LOG_PREFIX + "produceCuratorFramework");

			try {
				// Read the properties.
				final Properties properties = new Properties();
				properties.load(this.getClass().getResourceAsStream("/zookeeper.properties"));

				//
				// Create a quorumList from properties, in form
				// ip:port,ip:port,ip:port.
				final StringJoiner quorumListStringJoiner = new StringJoiner(",");
				properties.forEach((key, value) -> {
					final String[] hostnamePorts = value.toString().split(":");

					try {
						final InetAddress[] inetAddress = SystemDefaultDnsResolver.INSTANCE.resolve(hostnamePorts[0]);
						quorumListStringJoiner.add(inetAddress[0].getHostAddress() + ":" + hostnamePorts[1]);
					} catch (UnknownHostException uEx) {
						final String errorMessage = LOG_PREFIX
								+ "produceCuratorFramework - Could not resolve DNS name! ";
						LOGGER.error(errorMessage, uEx);

						throw new AppZookeeperException(errorMessage + "[message=" + uEx.getMessage() + "]", uEx);
					}
				});

				// Build a new curator.
				this.client = CuratorFrameworkFactory.builder().connectString(quorumListStringJoiner.toString())
						.sessionTimeoutMs(SESSION_TIMEOUT_MS).connectionTimeoutMs(CONNECTION_TIMEOUT_MS)
						.retryPolicy(new ExponentialBackoffRetry(RETRY_SLEEP, RETRY_NO))
						.threadFactory(this.defaultManagedThreadFactory).build();
				// Start the curator curator.
				this.client.start();

				// Block until a connection to ZooKeeper is available or the
				// maxWaitTime has been exceeded.
				// maxWaitTime = The maximum wait time.
				// Specify a value <= 0 to wait indefinitely.
				boolean isConnected = false;
				try {
					isConnected = this.client.blockUntilConnected(3, TimeUnit.SECONDS);
				} catch (final InterruptedException iEx) {
					// Should not happen.
					LOGGER.error(LOG_PREFIX + "produceCuratorFramework - interrupted! [isConnected=" + false + "]");
				}

				if (!isConnected) {
					throw new AppZookeeperException(
							LOG_PREFIX + "produceCuratorFramework - Could not connect to zookeeper!");
				} else {
					LOGGER.info(LOG_PREFIX + "produceCuratorFramework - successful connected to zookeeper.");
				}
			} catch (UnknownHostException uEx) {
				final String errorMessage = LOG_PREFIX
						+ "produceCuratorFramework - Could not resolve DNS name!! ";
				LOGGER.error(errorMessage, uEx);

				throw new AppZookeeperException(errorMessage + "[message=" + uEx.getMessage() + "]", uEx);

			} catch (final IOException ex) {
				final String errorMessage = LOG_PREFIX
						+ "produceCuratorFramework - Reading of zookeeper.properties failed! ";
				LOGGER.error(errorMessage, ex);

				throw new AppZookeeperException(errorMessage + "[message=" + ex.getMessage() + "]", ex);
			} catch (final Exception ioEx) {
				final String errorMessage = LOG_PREFIX
						+ "produceCuratorFramework - Initialization of curator framework failed! ";
				LOGGER.error(errorMessage, ioEx);

				throw new AppZookeeperException(errorMessage + "[message=" + ioEx.getMessage() + "]", ioEx);
			}
		}

		return this.client;
	}

	/**
	 * Close and cleanup zookeeper curator resources.
	 * 
	 * @param client
	 *            the given zookeeper curator.
	 */
	public void closeCuratorFramework(@CuratorFrameworkQualifier @Disposes final CuratorFramework client) {
		LOGGER.debug(LOG_PREFIX + "closeCuratorFramework");

		CloseableUtils.closeQuietly(client);
		// NOTE: Waiting for 1 second will (in most cases) prevent
		// "java.lang.NoClassDefFoundError:
		// org/apache/zookeeper/server/ZooTrace" from occurring when
		// un-deploying the application. It would be better to wait for
		// ZooKeepers "ClientCnxn" class to finish closing the connection, but
		// there doesn't seem to be a notification or synchronization mechanism
		// for this.
		try {
			Thread.sleep(1000);
		} catch (final InterruptedException e) {
			// Should not happen.
			LOGGER.error(LOG_PREFIX + "closeCuratorFramework - interrupted!");
		}
	}
}
