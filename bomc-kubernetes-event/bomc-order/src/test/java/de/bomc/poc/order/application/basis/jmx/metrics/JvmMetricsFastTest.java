/**
 * Project: bomc-onion-architecture
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
package de.bomc.poc.order.application.basis.jmx.metrics;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;

import de.bomc.poc.order.CategoryBasisUnitTest;
import de.bomc.poc.order.application.basis.jmx.AbstractMBean;
import de.bomc.poc.order.application.basis.jmx.MBeanController;
import de.bomc.poc.order.application.basis.jmx.MBeanInfo;
import de.bomc.poc.order.application.basis.jmx.RootName;

/**
 * Tests the JvmMetrics MBean.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Category(CategoryBasisUnitTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JvmMetricsFastTest {

    private static final String LOG_PREFIX = "JvmMetricsFastTest#";
    private static final Logger LOGGER = Logger.getLogger(JvmMetricsFastTest.class);
    private JMXConnectorServer jMXConnectorServer;
    private JMXConnector jMXConnector;

    @Test
    public void test010_readJmvSystemData_pass() {
        LOGGER.debug(LOG_PREFIX + "test010_readJmvSystemData_pass");

        JvmMetricsMBean mbean = new JvmMetrics();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (final InterruptedException e) {
            // Ignore
        }

        LOGGER.debug(Double.toString(mbean.getProcessCpuLoad()));
        LOGGER.debug(Double.toString(mbean.getSystemCpuLoad()));

        assertThat(mbean.getProcessCpuLoad(), notNullValue());
        assertThat(mbean.getSystemCpuLoad(), notNullValue());
    }

    @Test
    public void test020_readMemoryData_pass() {
        LOGGER.debug(LOG_PREFIX + "test020_readMemoryData_pass");

        JvmMetricsMBean mbean = new JvmMetrics();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (final InterruptedException e) {
            // Ignore
        }

        LOGGER.debug(Double.toString(mbean.getHeapMemoryUsage()));
        LOGGER.debug(Double.toString(mbean.getNonHeapMemoryUsage()));

        assertThat(mbean.getHeapMemoryUsage(), greaterThanOrEqualTo(0.0));
        assertThat(mbean.getHeapMemoryUsage(), greaterThanOrEqualTo(0.0));
    }

    @Test
    public void test030_registerMBeanToParentMBean_pass() throws Exception {
        LOGGER.debug(LOG_PREFIX + "test030_registerMBeanToParentMBean_pass");

        try {
            final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            // The parent mbean.
            final MBeanInfo mBeanParent = new RootName();
            //
            final JvmMetrics mBeanLeaf = new JvmMetrics();

            final MBeanController mBeanController = new MBeanController(mBeanServer);

            // Register first parent...
            mBeanController.register(mBeanParent, null);
            // ...now register leaf with reference to the parent.
            mBeanController.register((MBeanInfo)mBeanLeaf, mBeanParent);

            // Check for registering in MBeanServer.
            this.setUpJMXConnector(mBeanServer);

            final Set<ObjectName> setObjNames = this.dump();

            // Check registered objectNames.
            assertThat(setObjNames.contains(new ObjectName("de.bomc.poc.order.bomc-order:name0=Microservice")), equalTo(true));
            assertThat(setObjNames.contains(new ObjectName("de.bomc.poc.order.bomc-order:name0=Microservice,name1=JvmMetrics")), equalTo(true));

            mBeanController.unregisterAll();
        } finally {
            this.tearDown();
        }
    }

    // __________________________________________________________
    // Jmx helper methods.
    // ----------------------------------------------------------
    private Set<ObjectName> dump() throws IOException {
        LOGGER.debug(LOG_PREFIX + "dump");

        Set<ObjectName> beans;

        try {
            beans =
                this.conn()
                    .queryNames(new ObjectName(AbstractMBean.DOMAIN_NAME + ":*"), null);
        } catch (final MalformedObjectNameException e) {
            throw new RuntimeException(e);
        }

        for (ObjectName bean : beans) {
            LOGGER.debug(LOG_PREFIX + "dump - [bean = " + bean.toString() + "]");
        }

        return beans;
    }

    private void setUpJMXConnector(final MBeanServer mBeanServer) throws IOException {
        LOGGER.debug(LOG_PREFIX + "setUpJMXConnector");

        final JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://");
        this.jMXConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mBeanServer);
        this.jMXConnectorServer.start();

        final JMXServiceURL addr = jMXConnectorServer.getAddress();

        this.jMXConnector = JMXConnectorFactory.connect(addr);
    }

    private MBeanServerConnection conn() throws IOException {
        return jMXConnector.getMBeanServerConnection();
    }

    private void tearDown() {
        LOGGER.debug(LOG_PREFIX + "tearDown");

        try {
            if (this.jMXConnector != null) {
                this.jMXConnector.close();
            }
        } catch (final IOException e) {
            LOGGER.debug(LOG_PREFIX + "#tearDown - Unexpected, ignoring!" + e);
        }
        // Help GC.
        this.jMXConnector = null;

        try {
            if (this.jMXConnectorServer != null) {
                this.jMXConnectorServer.stop();
            }
        } catch (final IOException e) {
            LOGGER.debug(LOG_PREFIX + "tearDown - Unexpected, ignoring" + e);
        }
        // Help GC.
        this.jMXConnectorServer = null;
    }
}
