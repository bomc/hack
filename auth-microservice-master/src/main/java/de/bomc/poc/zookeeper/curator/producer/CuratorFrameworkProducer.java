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
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.zookeeper.curator.producer;

import java.io.IOException;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.log4j.Logger;

import de.bomc.poc.concurrency.qualifier.ManagedThreadFactoryQualifier;
import de.bomc.poc.exception.app.AppZookeeperException;
import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.zookeeper.curator.qualifier.CuratorFrameworkQualifier;

/**
 * A zookeeper curator producer.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
public class CuratorFrameworkProducer {
	// Properties for building a zookeeper curator.
	private static final int SESSION_TIMEOUT_MS = 10 * 1000;
	private static final int CONNECTION_TIMEOUT_MS = 5 * 1000;
	private static final int RETRY_NO = 10;
	private static final int RETRY_SLEEP = 1000;
	
	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	@ManagedThreadFactoryQualifier
	private ManagedThreadFactory defaultManagedThreadFactory;
	// The zookeeper curator as singleton.
	private CuratorFramework client;

	@Produces
	@CuratorFrameworkQualifier
	@ApplicationScoped
	CuratorFramework produceCuratorFramework() {
		if (this.client == null) {
			this.logger.info("CuratorFrameworkProducer#produceCuratorFramework");
			
			try {
				// Read the properties.
				final Properties properties = new Properties();
				properties.load(this.getClass().getResourceAsStream("/zookeeper.properties"));
				//
				// Create a quorumList from properties, in form
				// ip:port,ip:port,ip:port.
				final StringJoiner quorumListStringJoiner = new StringJoiner(",");
				properties.forEach((host, port) -> quorumListStringJoiner.add(port.toString()));

				// Build a new curator.
				this.client = CuratorFrameworkFactory.builder()
						.connectString(quorumListStringJoiner.toString())
						.sessionTimeoutMs(SESSION_TIMEOUT_MS)
						.connectionTimeoutMs(CONNECTION_TIMEOUT_MS)
						.retryPolicy(new ExponentialBackoffRetry(RETRY_SLEEP, RETRY_NO))
//						.threadFactory(this.defaultManagedThreadFactory)
						.build();
				// Start the curator curator.
				this.client.start();

				// Block until a connection to ZooKeeper is available or the maxWaitTime has been exceeded.
				// maxWaitTime The maximum wait time. Specify a value <= 0 to wait indefinitely.
				boolean isConnected = false;
				try {
					isConnected = this.client.blockUntilConnected(3, TimeUnit.SECONDS);
				} catch(InterruptedException iEx) {
					// Should not happen.
					this.logger.error("CuratorFrameworkProducer#produceCuratorFramework - interrupted! [isConnected=" + false + "]");
				}
				
				if(!isConnected) {
					throw new AppZookeeperException("CuratorFrameworkProducer#produceCuratorFramework - Could not connect to zookeeper!");
				} else {
					this.logger.info("CuratorFrameworkProducer#produceCuratorFramework - successful connected to zookeeper.");
				}
			} catch (IOException ex) {
				final String errorMessage = "CuratorFrameworkProducer#produceCuratorFramework - Reading of zookeeper.properties failed! ";
				this.logger.error(errorMessage, ex);

				throw new AppZookeeperException(errorMessage + ":" + ex.getMessage());
			} catch (Exception ioEx) {
				final String errorMessage = "CuratorFrameworkProducer#produceCuratorFramework - Initialization of curator framework failed! ";
				this.logger.error(errorMessage, ioEx);

				throw new AppZookeeperException(errorMessage + ":" + ioEx.getMessage());
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
	public void closeCuratorFramework(final @Disposes @CuratorFrameworkQualifier CuratorFramework client) {
		this.logger.debug("CuratorFrameworkProducer#closeCuratorFramework");

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
		} catch (InterruptedException e) {
			// Should not happen.
			this.logger.error("CuratorFrameworkProducer#closeCuratorFramework - interrupted!");
		}
	}
}
