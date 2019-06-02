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
package de.bomc.poc.test.zookeeper.unit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.log4j.Logger;
import org.apache.zookeeper.data.Stat;

import de.bomc.poc.exception.app.AppZookeeperException;

/**
 * This class generates a initialized and connect zookeeper curator.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
// TODO Use builder pattern
public class ZookeeperClient {

	private static final Logger LOGGER = Logger.getLogger(ZookeeperClient.class);
	private CuratorFramework client;
//	private TreeCache cache = null;
	private final String connectString;
	private final int connectionTimeoutMs;
	private final int sessionTimeoutMs;
	private final String rootZnode;

	/**
	 * Creates a new instance of <code>ZookeeperClient</code>.
	 * 
	 * @param connectString
	 *            the quorum <address1>:<port1>;<address2>:<port2>;<address3>:
	 *            <port3>
	 * 
	 * @param connectionTimeoutMs
	 *            connection timeout
	 * @param sessionTimeoutMs
	 *            session timeout
	 * @param rootZnode
	 *            the root node.
	 * 
	 * @throws AppZookeeperException
	 *             if zookeeper curator initialization failed.
	 */
	public ZookeeperClient(final String connectString, final int connectionTimeoutMs, final int sessionTimeoutMs,
			final String rootZnode) {
		LOGGER.debug("ZookeeperClient#co [connectString=" + connectString + ", connectionTimeoutMs="
				+ connectionTimeoutMs + ", sessionTimeoutMs=" + sessionTimeoutMs + ", rootZnode=" + rootZnode + "]");

		this.connectString = connectString;
		this.connectionTimeoutMs = connectionTimeoutMs;
		this.sessionTimeoutMs = sessionTimeoutMs;
		this.rootZnode = rootZnode;

		this.createClient(connectString, connectionTimeoutMs, sessionTimeoutMs, rootZnode);
	}

	/**
	 * Create a new instance of <code>CuratorFramework</code>.
	 * 
	 * @param connectString
	 * 
	 *            <pre>
	 *            Running ZooKeeper in standalone mode is convenient for
	 *            development, and testing. In production ZooKeeper should run
	 *            in replicated mode. A replicated group of servers in the same
	 *            application is called a quorum, and in replicated mode, all
	 *            servers in the quorum have copies of the same configuration 
	 *            file.
	 *            server.<positive id> = <address1>:<port1>:<port2>[:role];[<curator port address>:]<curator port>
	 *            
	 *            Examples of legal server statements:
	 *            server.5 = 125.23.63.23:1234:1235;1236
	 *            server.5 = 125.23.63.23:1234:1235:participant;1236
	 *            server.5 = 125.23.63.23:1234:1235:observer;1236
	 *            server.5 = 125.23.63.23:1234:1235;125.23.63.24:1236
	 *            server.5 = 125.23.63.23:1234:1235:participant;125.23.63.23:1236
	 *            </pre>
	 * 
	 * @param connectionTimeoutMs
	 *            connection timeout
	 * @param sessionTimeoutMs
	 *            session timeout
	 * @param rootZnode
	 *            Every node in a ZooKeeper tree is referred to as a znode.
	 *            Znodes maintain a stat structure that includes version numbers
	 *            for data changes.
	 * 
	 * @return a initialized and connected zookepper curator.
	 * 
	 * @throws AppZookeeperException
	 *             if zookeeper curator initialization failed.
	 */
	private CuratorFramework createClient(final String connectString, final int connectionTimeoutMs,
			final int sessionTimeoutMs, final String rootZnode) {
		LOGGER.debug("ZookeeperClient#createClient [connectString=" + connectString + ", connectionTimeoutMs="
				+ connectionTimeoutMs + ", sessionTimeoutMs=" + sessionTimeoutMs + ", rootZnode=" + rootZnode + "]");
		if(client == null) {
			try {
				client =
					CuratorFrameworkFactory.builder()
										   .connectString(connectString)
//											   .retryPolicy(new RetryOneTime(1000))
										   .retryPolicy(new ExponentialBackoffRetry(1000, 1))
										   .connectionTimeoutMs(connectionTimeoutMs)
										   .sessionTimeoutMs(sessionTimeoutMs)
										   .build();

//			cache = new TreeCache(curator, rootZnode);
//			cache.start();

				// Start the curator. Most mutator methods will not work until the
				// curator
				// is started.
				client.start();
//			this.addListener(cache);
				// Make sure you're connected to zookeeper.
				client.getZookeeperClient()
					  .blockUntilConnectedOrTimedOut();
			} catch (InterruptedException ex) {
				LOGGER.error("ZookeeperClient#createClient - blockUntilConnectedOrTimedOut failed. ", ex);

				throw new AppZookeeperException(ex.getMessage());
			} catch (Exception ex) {
				LOGGER.error("ZookeeperClient#createClient - failed. ", ex);

				throw new AppZookeeperException("ZookeeperClient#createClient - failed!");
			}
		}

		return client;
	}		
	
	/**
	 * Remove the root zNode.
	 * 
	 */
	public void removeRootZnode() {
		LOGGER.debug("ZookeeperClient#removeRootZnode [rootZnode=" + this.rootZnode + "]");

		if(this.client != null && this.client.getZookeeperClient().isConnected()) {
			Stat stat;
			try {
				if((stat = this.client.checkExists().forPath(rootZnode)) != null) {
					LOGGER.debug("ZookeeperClient#removeRootZnode [stat.version=" + stat.getVersion() + "]");
					this.client.delete()/*.guaranteed().deletingChildrenIfNeeded().withVersion(stat.getVersion())*/.forPath(rootZnode);
				}
			} catch (Exception e) {
				LOGGER.error("ZookeeperClient#removeRootZnode - failed! " + e.getMessage());
				// TODO: Brauchts das, ist doch void.
				throw new AppZookeeperException("ZookeeperClient#removeRootZnode - failed!");
			}
		}
	}
	
	/**
	 * 
	 * @return the root zNode.
	 */
	public String getRootZnode() {
		LOGGER.debug("ZookeeperClient#getRootZnode [rootZnode=" + this.rootZnode + "]");

		return this.rootZnode;
	}

	/**
	 * 
	 * @return the connect string.
	 */
	public String getConnectString() {
		LOGGER.debug("ZookeeperClient#getConnectionString [connectString=" + this.connectString + "]");
		
		return this.connectString;
	}

	/**
	 * 
	 * @return the connection timeout in ms.
	 */
	public int getConnectionTimeoutMs() {
		LOGGER.debug("ZookeeperClient#getConnectionTimeoutMs [connectionTimeoutMs=" + this.connectionTimeoutMs + "]");
		
		return this.connectionTimeoutMs;
	}
	
	/**
	 * 
	 * @return the session timeout in ms.
	 */
	public int getSessionTimeoutMs() {
		LOGGER.debug("ZookeeperClient#getSessionTimeoutMs [sessionTimeoutMs=" + this.sessionTimeoutMs + "]");
		
		return this.sessionTimeoutMs;
	}
	
	/**
	 * Returns true if the curator is current connected
	 *
	 * @return true/false
	 */
	public boolean isConnected() {
		final boolean isConnected = this.client.getZookeeperClient().isConnected();

		LOGGER.debug("ZookeeperClient#isConnected [isConnected=" + isConnected + "]");

		return isConnected;
	}

	public CuratorFramework getCuratorFramework() {
		LOGGER.debug("ZookeeperClient#getCuratorFramework");
		
		return this.client;
	}
	
	/**
	 * Close the curator and cache.
	 * 
	 */
	public void close() {
		LOGGER.debug("ZookeeperClient#close");

//		if (this.cache != null) {
//			this.cache.close();
//		}

		if (this.client != null) {
			CloseableUtils.closeQuietly(this.client);
		}
	}

//	private void addListener(final TreeCache cache) {
//		LOGGER.info("ZookeeperClient#addListener");
//		TreeCacheListener listener = new TreeCacheListener() {
//
//			@Override
//			public void childEvent(final CuratorFramework curator, final TreeCacheEvent event) throws Exception {
//				switch (event.getType()) {
//				case NODE_ADDED: {
//					/**
//					 * A node was added.
//					 */
//					LOGGER.info("ZookeeperClient#addListener - treeNode added: "
//							+ ZKPaths.getNodeFromPath(event.getData().getPath()) + ", value: "
//							+ new String(event.getData().getData()));
//
//					break;
//				}
//				case NODE_UPDATED: {
//					/**
//					 * A node's data was changed
//					 */
//					LOGGER.info("ZookeeperClient#addListener - treeNode changed: "
//							+ ZKPaths.getNodeFromPath(event.getData().getPath()) + ", value: "
//							+ new String(event.getData().getData()));
//					break;
//				}
//				case NODE_REMOVED: {
//					/**
//					 * A node was removed from the tree
//					 */
//					LOGGER.info("ZookeeperClient#addListener - treeNode removed: "
//							+ ZKPaths.getNodeFromPath(event.getData().getPath()));
//					break;
//				}
//				case CONNECTION_SUSPENDED: {
//					/**
//					 * Called when the connection has changed to
//					 * {@link org.apache.curator.framework.state.ConnectionState#SUSPENDED}
//					 * <p>
//					 * This is exposed so that users of the class can be
//					 * notified of issues that *might* affect normal operation.
//					 * The TreeCache is written such that listeners are not
//					 * expected to do anything special on this event, except for
//					 * those people who want to cause some application-specific
//					 * logic to fire when this occurs. While the connection is
//					 * down, the TreeCache will continue to have its state from
//					 * before it lost the connection and after the connection is
//					 * restored, the TreeCache will emit normal child events for
//					 * all of the adds, deletes and updates that happened during
//					 * the time that it was disconnected.
//					 * </p>
//					 */
//					LOGGER.info("ZookeeperClient#addListener - treeNode connection suspended: "
//							+ ZKPaths.getNodeFromPath(event.getData().getPath()));
//					break;
//				}
//				case CONNECTION_RECONNECTED: {
//					/**
//					 * Called when the connection has changed to
//					 * {@link org.apache.curator.framework.state.ConnectionState#RECONNECTED}
//					 * <p>
//					 * This is exposed so that users of the class can be
//					 * notified of issues that *might* affect normal operation.
//					 * The TreeCache is written such that listeners are not
//					 * expected to do anything special on this event, except for
//					 * those people who want to cause some application-specific
//					 * logic to fire when this occurs. While the connection is
//					 * down, the TreeCache will continue to have its state from
//					 * before it lost the connection and after the connection is
//					 * restored, the TreeCache will emit normal child events for
//					 * all of the adds, deletes and updates that happened during
//					 * the time that it was disconnected.
//					 * </p>
//					 * <p>
//					 * After reconnection, the cache will resynchronize its
//					 * internal state with the server, then fire a
//					 * {@link #INITIALIZED} event.
//					 * </p>
//					 */
//					LOGGER.info("ZookeeperClient#addListener - treeNode connection reconnected: "
//							+ ZKPaths.getNodeFromPath(event.getData().getPath()));
//					break;
//				}
//				case CONNECTION_LOST: {
//					/**
//					 * Called when the connection has changed to
//					 * {@link org.apache.curator.framework.state.ConnectionState#LOST}
//					 * <p>
//					 * This is exposed so that users of the class can be
//					 * notified of issues that *might* affect normal operation.
//					 * The TreeCache is written such that listeners are not
//					 * expected to do anything special on this event, except for
//					 * those people who want to cause some application-specific
//					 * logic to fire when this occurs. While the connection is
//					 * down, the TreeCache will continue to have its state from
//					 * before it lost the connection and after the connection is
//					 * restored, the TreeCache will emit normal child events for
//					 * all of the adds, deletes and updates that happened during
//					 * the time that it was disconnected.
//					 * </p>
//					 */
//					LOGGER.info("ZookeeperClient#addListener - connection lost: "
//							+ ZKPaths.getNodeFromPath(event.getData().getPath()));
//					break;
//				}
//				case INITIALIZED: {
//					/**
//					 * Posted after the initial cache has been fully populated.
//					 * <p>
//					 * On startup, the cache synchronizes its internal state
//					 * with the server, publishing a series of
//					 * {@link #NODE_ADDED} events as new nodes are discovered.
//					 * Once the cachehas been fully synchronized, this
//					 * {@link #INITIALIZED} this event is published. All events
//					 * published after this event represent actual server-side
//					 * mutations.
//					 * </p>
//					 * <p>
//					 * On reconnection, the cache will resynchronize its
//					 * internal state with the server, and fire this event again
//					 * once its internal state is completely refreshed.
//					 * </p>
//					 * <p>
//					 * Note: because the initial population is inherently
//					 * asynchronous, so it's possible to observe server-side
//					 * changes (such as a {@link #NODE_UPDATED}) prior to this
//					 * event being published.
//					 * </p>
//					 */
//					LOGGER.info("ZookeeperClient#addListener - treeNode initiaized: "/*
//																						 * +
//																						 * ZKPaths
//																						 * .
//																						 * getNodeFromPath
//																						 * (
//																						 * event
//																						 * .
//																						 * getData
//																						 * (
//																						 * )
//																						 * .
//																						 * getPath
//																						 * (
//																						 * )
//																						 * )
//																						 */);
//					break;
//				}
//				default:
//					LOGGER.info("Other event: " + event.getType().name());
//				}
//			}
//
//		};
//
//		cache.getListenable().addListener(listener);
//	}
}
