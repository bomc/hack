/**
 * Project: MY_POC
 * <p/>
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
 * <p/>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.zookeeper.system;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.utils.ZKPaths;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import de.bomc.poc.exception.app.AppZookeeperException;

/**
 * This test is runs against a running zookeeper instance.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class SystemTestZookeeperClient {

    private static final Logger LOGGER = Logger.getLogger(SystemTestZookeeperClient.class);
    private static final String ROOT_ZNODE = "/local/node1/services";
    // The name of the endpoint registered in zookeeper.
    private static final String SERVICE_ENDPOINT_NAME = "usermanagement";
    private static final String URI = "http://ip:host/my/uri";
    private long startTime = 0;
    
    /**
     * <pre>
     * This test runs against a running zookeeper instance (address="localhost:2181").
     * First a curator curator instance is created.
     * Next a service discovery request is started against zookeeper.
     * Than a request against the auth-microservice is performed.
     * </pre>
     */
    public static final void main(String... args) {
    	final SystemTestZookeeperClient systemTestZookeeperClient = new SystemTestZookeeperClient();
    	systemTestZookeeperClient.invokeRequestAgainstARunningAuthMicroservice();
    }

    /**
     * Creates a new instance of <code>SystemTestZookeeperClient</code>.
     *  
     */
    public SystemTestZookeeperClient() {
    	LOGGER.info("SystemTestZookeeperClient#co");
    	
    }
    
    /**
     * <pre>
     * This methods initializes a zk-curator and start a request to zk to get the URI and than a request is started against the running microservice.
     * ________________________
     * NOTE: Zk sessionTimeOut: 
     * Session expiration is managed by the ZooKeeper cluster itself, not by the curator.
     * When the ZK curator establishes a session with the cluster it provides a "timeout" value.
     * This value is used by the cluster to determine when the curator's session expires. Expirations happens when the cluster does not hear from the
     * curator within the specified session timeout period (i.e. no heartbeat). At session expiration the cluster will delete any/all ephemeral nodes
     * owned by that session and immediately notify any/all connected clients of the change (anyone watching those znodes). At this point the curator
     * of the expired session is still disconnected from the cluster, it will not be notified of the session expiration until/unless it is able to 
     * re-establish a connection to the cluster. The curator will stay in disconnected state until the TCP connection is re-established with the
     * cluster, at which point the watcher of the expired session will receive the "session expired" notification.
     * ___________________________
     * NOTE: Zk connectionTimeOut:
     * the maximum amount of time to wait for the connection to the ZK cluster to be established; 0 to wait forever
     * </pre>
     */
    private void invokeRequestAgainstARunningAuthMicroservice() {
    	LOGGER.info("SystemTestZookeeperClient#invokeRequestAgainstARunningAuthMicroservice");
    	
        CuratorFramework client = null;
        TreeCache cache = null;
        
        try {
            // 
            // Initialize a new curator curator.
            client = CuratorFrameworkFactory.builder()
                                       .connectString("localhost:2181")
                                       .sessionTimeoutMs(2000)
                                       .connectionTimeoutMs(1000)
                                       // Set the retry strategy 'new BoundedExponentialBackoffRetry(100, 10000, 100)', 'RetryOneTime(1000)'.
                                       .retryPolicy(new ExponentialBackoffRetry(1000, 2)/*new RetryNTimes(2, 1000)*/)
                                       .build();
            //
            // Add a listener.
            client.getCuratorListenable().addListener(new CuratorListener() {
				@Override
				public void eventReceived(CuratorFramework client,
						CuratorEvent event) throws Exception {
					LOGGER.info("SystemTestZookeeperClient#invokeRequestAgainstARunningAuthMicroservice - [curatorEvent=" + event.getType().name() + "]");
				}
			});
            
			client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
				@Override
				public void stateChanged(CuratorFramework client, ConnectionState newState) {
					switch (newState) {
					case CONNECTED: {
						LOGGER.info(
								"SystemTestZookeeperClient#invokeRequestAgainstARunningAuthMicroservice - [connectionState="
										+ newState.name() + "]");
						break;
					}
					case LOST: {
						/**
						 * The connection is confirmed to be lost. Close any
						 * locks, leaders, etc. and attempt to re-create them.
						 * NOTE: it is possible to get a {@link #RECONNECTED}
						 * state after this but you should still consider any
						 * locks, etc. as dirty/unstable
						 */
						LOGGER.info(
								"SystemTestZookeeperClient#invokeRequestAgainstARunningAuthMicroservice - [connectionState="
										+ newState.name() + "]");
						break;
					}
					case READ_ONLY: {
						/**
						 * The connection has gone into read-only mode. This can
						 * only happen if you pass true for
						 * {@link CuratorFrameworkFactory.Builder#canBeReadOnly()}
						 * . See the ZooKeeper doc regarding read only
						 * connections: <a href=
						 * "http://wiki.apache.org/hadoop/ZooKeeper/GSoCReadOnlyMode">
						 * http://wiki.apache.org/hadoop/ZooKeeper/
						 * GSoCReadOnlyMode</a>. The connection will remain in
						 * read only mode until another state change is sent.
						 */
						LOGGER.info(
								"SystemTestZookeeperClient#invokeRequestAgainstARunningAuthMicroservice - [connectionState="
										+ newState.name() + "]");
						break;
					}
					case RECONNECTED: {
						/**
						 * A suspended, lost, or read-only connection has been
						 * re-established
						 */
						LOGGER.info(
								"SystemTestZookeeperClient#invokeRequestAgainstARunningAuthMicroservice - [connectionState="
										+ newState.name() + "]");
						break;
					}
					case SUSPENDED: {
						/**
						 * There has been a loss of connection. Leaders, locks,
						 * etc. should suspend until the connection is
						 * re-established. If the connection times-out you will
						 * receive a {@link #LOST} notice
						 */
						LOGGER.info(
								"SystemTestZookeeperClient#invokeRequestAgainstARunningAuthMicroservice - [connectionState="
										+ newState.name() + "]");
						break;
					}
					default:
						LOGGER.info(
								"SystemTestZookeeperClient#invokeRequestAgainstARunningAuthMicroservice - [connectionState="
										+ newState.name() + "]");
					}
				}
			});
			
			client.getUnhandledErrorListenable().addListener(new UnhandledErrorListener() {
                @Override
                public void unhandledError(String message, Throwable e) {
                    LOGGER.warn("SystemTestZookeeperClient#invokeRequestAgainstARunningAuthMicroservice - Unknown exception in curator stack [message=" + message + "]", e);
                }
            });
			
			cache = new TreeCache(client, ROOT_ZNODE);
			cache.start();
			
			
			// LATENT means = start has not yet been called
        	CuratorFrameworkState frameworkState = client.getState();
        	
            if (CuratorFrameworkState.LATENT.equals(frameworkState) || CuratorFrameworkState.STOPPED.equals(frameworkState)) {
            	LOGGER.info("SystemTestZookeeperClient#invokeRequestAgainstARunningAuthMicroservice - start has not been called.");
            }
            
            //
            // Start the curator.
            client.start();
            this.addListener(cache);
            
            final String zNodePath = this.registerService(client, SERVICE_ENDPOINT_NAME, "http://localhost:8080/");
            
            this.startTime = System.currentTimeMillis();
            
            //
            // Get the registered URI service endpoint name from zookeeper.
//            final String uri = this.discoverServiceURI(curator, zNodePath);

//            LOGGER.info("------------->" + uri);
            
            LOGGER.info("SystemTestZookeeperClient#invokeRequestAgainstARunningAuthMicroservice [discovery-duration=" + (System.currentTimeMillis() - startTime) + "ms]");

//            //
//            // Create a Resteasy-Client and than start a request to Wildfly.
//            final ResteasyClient resteasyClient = new ResteasyClientBuilder().establishConnectionTimeout(10, TimeUnit.SECONDS)
//                                                                             .socketTimeout(10, TimeUnit.SECONDS)
//                                                                             .register(new ResteasyClientLogger(LOGGER, true))
//                                                                             .register(new UIDHeaderRequestFilter(null))
//                                                                             .register(org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider.class)
//                                                                             .register(org.codehaus.jackson.jaxrs.JacksonJsonProvider.class)
//                                                                             .build();
//            final ResteasyWebTarget webTarget = resteasyClient.target(uri);
//
//            Response response = null;
//
//            try {
//                // Start REST request...
//                final AuthUserManagementRestEndpoint proxy = webTarget.proxy(AuthUserManagementRestEndpoint.class);
//                response = proxy.findAllRolesByUsername("Default-System_user");
//
//                // Check and get response.
//                if (response != null && response.getStatus() == Response.Status.OK.getStatusCode()) {
//                    // Read response.
//                    final List<RoleDTO> roleDTOList = response.readEntity(new GenericType<List<RoleDTO>>() {});
//
//                    assertThat(roleDTOList.size(), is(equalTo(1)));
//                    roleDTOList.forEach(roleDTO -> {
//                        final List<GrantDTO> grantDTOList = roleDTO.getGrantDTOList();
//                        assertThat(grantDTOList.size(), (is(2)));
//                    });
//                } else {
//                    fail("SystemTestZookeeperClient#invokeRequestAgainstARunningAuthMicroservice - failed, responseCode != 200");
//                }
//            } catch (Exception ex) {
//                LOGGER.error("SystemTestZookeeperClient#invokeRequestAgainstARunningAuthMicroservice - failed! ", ex);
//            } finally {
//                if (response != null) {
//                    response.close();
//                }
//            }

            LOGGER.info("SystemTestZookeeperClient#invokeRequestAgainstARunningAuthMicroservice [complete duration=" + (System.currentTimeMillis() - startTime) + "ms]");
        } catch(Exception ex) {
        	LOGGER.error("SystemTestZookeeperClient#invokeRequestAgainstARunningAuthMicroservice - ", ex);
        } finally {
            if(client != null) {
            	CloseableUtils.closeQuietly(cache);
            	CloseableUtils.closeQuietly(client);
            }
        }
    }

	/**
	 * Register service in zookeeper with structure:
	 * 
	 * <pre>
	 * 										----------
	 * 										| /local |
	 * 										----------		
	 *										/		 \
	 * 								----------   
	 * 								| /node1 |		 ...
	 * 								----------		
	 * 								/        \
	 * 					-------------		-------------
	 * 					| /services |		| /config	|
	 * 					-------------		-------------
	 * 					/		\
	 * 	    ---------------		---------
	 * 		| /management |		| /task |
	 * 		---------------		---------
	 * </pre>
	 */
	private String registerService(final CuratorFramework client, final String name, final String uri) {
		LOGGER.info("SystemTestZookeeperClient#registerService [name=" + name + ", uri=" + uri + "]");

		try {
			// TODO: creates a zNode hierarchy.
			final String zNode = ROOT_ZNODE + "/" + name;

			if (client.checkExists().forPath(zNode) == null) {
				final String created = client.create().creatingParentsIfNeeded().forPath(zNode);

				LOGGER.info("ZookeeperServiceRegistry#registerService - creatingParentsIfNeeded [created=" + created + "]");
			}

			// EPHEMERAL_SEQUENTIAL means: The zNode will be deleted upon the curator's disconnect,
			// and its name will be appended with a monotonically increasing number.
			final String zNodePath = client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(zNode + "/_", uri.getBytes());

			LOGGER.info("SystemTestZookeeperClient#registerService - create the path. [zNodePath=" + zNodePath + "]");

			return zNodePath;
		} catch (Exception ex) {
			final String errorMessage = "SystemTestZookeeperClient#registerService - Could not register service \"" + name + "\", with URI \"" + uri + "\": ";
			LOGGER.error(errorMessage, ex);

			throw new AppZookeeperException(errorMessage + ex.getLocalizedMessage());
		}
	}
    
    /**
     * Get the URI of the registered endpoint from zookeeper.
     * @param client the zookeeper curator.
     * @param name   the registered service name of the endpoint.
     * @return the URI of the registered endpoint service.
     */
//    private String discoverServiceURI(final CuratorFramework curator, final String name) {
//        LOGGER.info("SystemTestZookeeperClient#discoverServiceURI [name=" + name + "]");
//
//        try {
////            final String znode = ROOT_ZNODE + "/" + name;
//
//            final List<String> uris = curator.getChildren().forPath("/_0000000006");
//
////            return new String(curator.getData().forPath(ZKPaths.makePath(name, uris.get(0))));
//            return uris.get(0);
//        } catch (Exception ex) {
//        	LOGGER.info("SystemTestZookeeperClient#discoverServiceURI [full-running-time=" + (System.currentTimeMillis() - startTime) + "ms]");
//        	
//        	if(ex instanceof ConnectionLossException) {
//        		LOGGER.info("SystemTestZookeeperClient#discoverServiceURI - " + ex.getMessage());
//        	}
//        	
//            throw new RuntimeException("Service \"" + name + "\" not found: " + ex.getLocalizedMessage());
//        }
//    }
    
	private void addListener(final TreeCache cache) {
		LOGGER.info("SystemTestZookeeperClient#addListener");
		TreeCacheListener listener = new TreeCacheListener() {

			@Override
			public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
				switch (event.getType()) {
				case NODE_ADDED: {
				    /**
				     * A node was added.
				     */
					LOGGER.info("SystemTestZookeeperClient#addListener - TreeNode added: " + ZKPaths.getNodeFromPath(event.getData().getPath())
							+ ", value: " + new String(event.getData().getData()));
					break;
				}
				case NODE_UPDATED: {
				    /**
				     * A node's data was changed
				     */
					LOGGER.info("SystemTestZookeeperClient#addListener - TreeNode changed: " + ZKPaths.getNodeFromPath(event.getData().getPath())
							+ ", value: " + new String(event.getData().getData()));
					break;
				}
				case NODE_REMOVED: {
				    /**
				     * A node was removed from the tree
				     */
					LOGGER.info("SystemTestZookeeperClient#addListener - TreeNode removed: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
					break;
				}
				case CONNECTION_SUSPENDED: {
				    /**
				     * Called when the connection has changed to {@link org.apache.curator.framework.state.ConnectionState#SUSPENDED}
				     * <p>
				     * This is exposed so that users of the class can be notified of issues that *might* affect normal operation.
				     * The TreeCache is written such that listeners are not expected to do anything special on this
				     * event, except for those people who want to cause some application-specific logic to fire when this occurs.
				     * While the connection is down, the TreeCache will continue to have its state from before it lost
				     * the connection and after the connection is restored, the TreeCache will emit normal child events
				     * for all of the adds, deletes and updates that happened during the time that it was disconnected.
				     * </p>
				     */
					LOGGER.info("SystemTestZookeeperClient#addListener - TreeNode connection suspended: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
					break;
				}
				case CONNECTION_RECONNECTED: {
				    /**
				     * Called when the connection has changed to {@link org.apache.curator.framework.state.ConnectionState#RECONNECTED}
				     * <p>
				     * This is exposed so that users of the class can be notified of issues that *might* affect normal operation.
				     * The TreeCache is written such that listeners are not expected to do anything special on this
				     * event, except for those people who want to cause some application-specific logic to fire when this occurs.
				     * While the connection is down, the TreeCache will continue to have its state from before it lost
				     * the connection and after the connection is restored, the TreeCache will emit normal child events
				     * for all of the adds, deletes and updates that happened during the time that it was disconnected.
				     * </p><p>
				     * After reconnection, the cache will resynchronize its internal state with the server, then fire a
				     * {@link #INITIALIZED} event.
				     * </p>
				     */
					LOGGER.info("SystemTestZookeeperClient#addListener - TreeNode connection reconnected: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
					break;
				}
				case CONNECTION_LOST: {
				    /**
				     * Called when the connection has changed to {@link org.apache.curator.framework.state.ConnectionState#LOST}
				     * <p>
				     * This is exposed so that users of the class can be notified of issues that *might* affect normal operation.
				     * The TreeCache is written such that listeners are not expected to do anything special on this
				     * event, except for those people who want to cause some application-specific logic to fire when this occurs.
				     * While the connection is down, the TreeCache will continue to have its state from before it lost
				     * the connection and after the connection is restored, the TreeCache will emit normal child events
				     * for all of the adds, deletes and updates that happened during the time that it was disconnected.
				     * </p>
				     */
					LOGGER.info("SystemTestZookeeperClient#addListener - connection lost: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
					break;
				} 
				case INITIALIZED: {
					/**
				     * Posted after the initial cache has been fully populated.
				     * <p>
				     * On startup, the cache synchronizes its internal
				     * state with the server, publishing a series of {@link #NODE_ADDED} events as new nodes are discovered.  Once
				     * the cachehas been fully synchronized, this {@link #INITIALIZED} this event is published.  All events
				     * published after this event represent actual server-side mutations.
				     * </p><p>
				     * On reconnection, the cache will resynchronize its internal state with the server, and fire this event again
				     * once its internal state is completely refreshed.
				     * </p><p>
				     * Note: because the initial population is inherently asynchronous, so it's possible to observe server-side changes
				     * (such as a {@link #NODE_UPDATED}) prior to this event being published.
				     * </p>
				     */
					LOGGER.info("SystemTestZookeeperClient#addListener - TreeNode initiaized: "/* + ZKPaths.getNodeFromPath(event.getData().getPath())*/);
					break;
				}
				default:
					LOGGER.info("Other event: " + event.getType().name());
				}
			}

		};

		cache.getListenable().addListener(listener);
	}

}
