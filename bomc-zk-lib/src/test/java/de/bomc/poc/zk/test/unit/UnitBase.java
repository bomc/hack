package de.bomc.poc.zk.test.unit;

import com.netflix.curator.test.TestingServer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.IOException;
import java.net.BindException;

/**
 * Base class for zookeeper tests. This class starts an embedded zookeeper server for JUnit tests.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.08.2016
 */
public class UnitBase {

    private static TestingServer server;
    private static final String INTERNAL_PROPERTY_DONT_LOG_CONNECTION_ISSUES;
    private static final Logger LOGGER = Logger.getLogger(UnitBase.class);
    private static final String LOG_PREFIX = "UnitBase#";
    static final String BASE_ROOT_Z_NODE = "/base/local/node0";
    static final int CONNECTION_TIMEOUT_MS = 2000;
    static final int SESSION_TIMEOUT_MS = 2000;
    CuratorFramework client;

    static {
        String logConnectionIssues = null;
        try {
            // Use reflection to avoid adding a circular dependency in the pom
            final Class<?> debugUtilsClazz = Class.forName("org.apache.curator.utils.DebugUtils");
            logConnectionIssues = (String)debugUtilsClazz.getField("PROPERTY_DONT_LOG_CONNECTION_ISSUES")
                                                         .get(null);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        INTERNAL_PROPERTY_DONT_LOG_CONNECTION_ISSUES = logConnectionIssues;
    }

    @BeforeClass
    public static void setup() throws Exception {
        if (INTERNAL_PROPERTY_DONT_LOG_CONNECTION_ISSUES != null) {
            System.setProperty(INTERNAL_PROPERTY_DONT_LOG_CONNECTION_ISSUES, "true");
        }

        while (server == null) {
            try {
                server = new TestingServer();
            } catch (final BindException e) {
                System.err.println("Getting bind exception - retrying to allocate server");
                server = null;
            }
        }
    }

    /**
     * After all the tests in the class have run this method is performed.
     * @throws Exception
     */
    @AfterClass
    public static void teardown() throws Exception {
        if (server != null) {
            try {
                server.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
            server = null;
        }
    }

    /**
     * Create a new instance of <code>CuratorFramework</code>.
     * <pre>
     *  connectString
     *    Running ZooKeeper in standalone mode is convenient for
     *    development, and testing. In production ZooKeeper should run
     *    in replicated mode. A replicated group of servers in the same
     *    application is called a quorum, and in replicated mode, all
     *    servers in the quorum have copies of the same configuration
     *    file.
     *    server.<positive id> = <address1>:<port1>:<port2>[:role];[<curator port address>:]<curator port>
     *
     *    Examples of legal server statements:
     *    server.5 = 125.23.63.23:1234:1235;1236
     *    server.5 = 125.23.63.23:1234:1235:participant;1236
     *    server.5 = 125.23.63.23:1234:1235:observer;1236
     *    server.5 = 125.23.63.23:1234:1235;125.23.63.24:1236
     *    server.5 = 125.23.63.23:1234:1235:participant;125.23.63.23:1236
     * </pre>
     * @param connectionTimeoutMs connection timeout
     * @param sessionTimeoutMs    session timeout
     * @param rootZnode           Every node in a ZooKeeper tree is referred to as a znode. Znodes maintain a stat structure that includes version numbers for data changes.
     * @return a initialized and connected zookepper curator.
     * @throws RuntimeException if zookeeper curator initialization failed.
     */
    CuratorFramework createClient(final int connectionTimeoutMs, final int sessionTimeoutMs, final String rootZnode) {
        LOGGER.debug(
            LOG_PREFIX + "createClient [connectString=" + server.getConnectString() + ", connectionTimeoutMs=" + connectionTimeoutMs + ", sessionTimeoutMs=" + sessionTimeoutMs + ", rootZnode=" + rootZnode + "]");
        if (this.client == null) {
            try {
                this.client =
                    CuratorFrameworkFactory.builder()
                                           .connectString(server.getConnectString())
                                           .retryPolicy(new RetryOneTime(1000))
                                           .connectionTimeoutMs(connectionTimeoutMs)
                                           .sessionTimeoutMs(sessionTimeoutMs)
                                           .build();

                // Start the curator. Most mutator methods will not work until the curator is started.
                this.client.start();
                // Make sure you're connected to zookeeper.
                this.client.getZookeeperClient()
                      .blockUntilConnectedOrTimedOut();
            } catch (final InterruptedException ex) {
                LOGGER.error(LOG_PREFIX + "createClient - blockUntilConnectedOrTimedOut failed. ", ex);

                throw new RuntimeException(ex.getMessage());
            } catch (final Exception ex) {
                LOGGER.error(LOG_PREFIX + "createClient - failed. ", ex);

                throw new RuntimeException(LOG_PREFIX + "createClient - failed!");
            }
        }

        return this.client;
    }
}
