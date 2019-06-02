package de.bomc.poc.zk.test.unit;

import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import de.bomc.poc.zk.config.accessor.ZookeeperConfigAccessor;
import de.bomc.poc.zk.config.accessor.impl.ZookeeperConfigAccessorImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests {@link ZookeeperConfigAccessorImpl} with a embedded ZookeeperServer.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 03.08.2016
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ZookeeperAccessorTest extends UnitBase {

    private static final String LOG_PREFIX = "ZookeeperAccessorTest#";
    private static final Logger LOGGER = Logger.getLogger(ZookeeperAccessorTest.class);
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Before
    public void initCuratorClient() {
        LOGGER.debug(LOG_PREFIX + "setup");

        this.client = this.createClient(CONNECTION_TIMEOUT_MS, SESSION_TIMEOUT_MS, BASE_ROOT_Z_NODE);
    }

    @Test
    public void test010_createZookeeperConfigAccessor() {
        LOGGER.debug(LOG_PREFIX + "test010_createZookeeperConfigAccessor");

        assertThat(this.client, notNullValue());
        assertThat(this.client.getState(), is(equalTo(CuratorFrameworkState.STARTED)));

        final ZookeeperConfigAccessor zookeeperConfigAccessor = new ZookeeperConfigAccessorImpl(this.client);

        assertThat(zookeeperConfigAccessor.isConnected(), is(equalTo(true)));
    }

    @Test
    public void test020_json_Write_Read_Back_Pass() {
        LOGGER.debug(LOG_PREFIX + "test020_json_Write_Read_Back_Pass");

        try {
            // Generate some input data.
            final Map<Object, Object> dataMap = new HashMap<>();
            dataMap.put("my_key1", "my_value1");
            dataMap.put("my_key3", "my_value3");
            dataMap.put("my_key5", "my_value5");

            final CuratorFrameworkState state = this.client.getState();

            LOGGER.debug(LOG_PREFIX + "test020_json_Write_Read_Back_Pass - [state.name=" + state.name() + "]");

            assertThat(state.name(), Matchers.is(equalTo(CuratorFrameworkState.STARTED.name())));

            final ZookeeperConfigAccessor zookeeperConfigAccessor = new ZookeeperConfigAccessorImpl(this.client);

            // Write to zookeeper...
            zookeeperConfigAccessor.writeJSON(BASE_ROOT_Z_NODE + "/config", dataMap);

            // ...wait for write down...
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (final InterruptedException e) {
                // Ignore.
            }

            // ...read back from zookeeper.
            final Map<Object, Object> retMap = zookeeperConfigAccessor.readJSON(BASE_ROOT_Z_NODE + "/config");

            assertThat(retMap, IsMapContaining.hasKey("my_key1"));
            assertThat(retMap, IsMapContaining.hasKey("my_key3"));
            assertThat(retMap, IsMapContaining.hasKey("my_key5"));

            LOGGER.debug(LOG_PREFIX + "test020_json_Write_Read_Back_Pass [retMap=" + retMap + "]");
        } finally {
            if (this.client != null && this.client.getState()
                                                  .name()
                                                  .equals(CuratorFrameworkState.STARTED)) {
                this.client.close();
            }
        }
    }

    @Test
    public void test030_createPersistentZNodePath_Pass() {
        LOGGER.debug(LOG_PREFIX + "test030_createPersistentZNodePath_Pass");

        try {
            final String TEST_ROOT_PATH = "/test030_createPersistentZNodePath_Pass";
            // Generate some input data.
            final Map<Object, Object> dataMap = new HashMap<>();
            dataMap.put("my_key1", "my_value1");

            final CuratorFrameworkState state = this.client.getState();

            LOGGER.debug(LOG_PREFIX + "test030_createPersistentZNodePath_Pass - [state.name=" + state.name() + "]");

            assertThat(state.name(), Matchers.is(equalTo(CuratorFrameworkState.STARTED.name())));

            final ZookeeperConfigAccessor zookeeperConfigAccessor = new ZookeeperConfigAccessorImpl(this.client);

            zookeeperConfigAccessor.createPersistentZNodePath(TEST_ROOT_PATH);
            zookeeperConfigAccessor.writeJSON(TEST_ROOT_PATH, dataMap);
            final Map<Object, Object> retMap = zookeeperConfigAccessor.readJSON(TEST_ROOT_PATH);

            assertThat(retMap, IsMapContaining.hasKey("my_key1"));
        } finally {
            if (this.client != null && this.client.getState()
                                                  .name()
                                                  .equals(CuratorFrameworkState.STARTED)) {
                this.client.close();
            }
        }
    }

    @Test
    public void test040_createPersistentZNodePath_PathNull_Pass() {
        LOGGER.debug(LOG_PREFIX + "test040_createPersistentZNodePath_PathNull_Pass");

        try {
            this.thrown.expect(RuntimeException.class);

            final CuratorFrameworkState state = this.client.getState();

            LOGGER.debug(LOG_PREFIX + "test040_createPersistentZNodePath_PathNull_Pass - [state.name=" + state.name() + "]");

            assertThat(state.name(), Matchers.is(equalTo(CuratorFrameworkState.STARTED.name())));

            final ZookeeperConfigAccessor zookeeperConfigAccessor = new ZookeeperConfigAccessorImpl(this.client);

            zookeeperConfigAccessor.createPersistentZNodePath(null);

            fail(LOG_PREFIX + "test040_createPersistentZNodePath_PathNull_Pass - A exception should be thrown.");
        } finally {
            if (this.client != null && this.client.getState()
                                                  .name()
                                                  .equals(CuratorFrameworkState.STARTED)) {
                this.client.close();
            }
        }
    }

    @Test
    public void test050_deleteDataFromPath_Pass() {
        LOGGER.debug(LOG_PREFIX + "test050_deleteDataFromPath_Pass");

        try {
            final String TEST_ROOT_PATH = "/test050_deleteDataFromPath_Pass";
            // Generate some input data.
            final Map<Object, Object> dataMap = new HashMap<>();
            dataMap.put("my_key1", "my_value1");

            final CuratorFrameworkState state = this.client.getState();

            LOGGER.debug(LOG_PREFIX + "test050_deleteDataFromPath_Pass - [state.name=" + state.name() + "]");

            assertThat(state.name(), Matchers.is(equalTo(CuratorFrameworkState.STARTED.name())));

            final ZookeeperConfigAccessor zookeeperConfigAccessor = new ZookeeperConfigAccessorImpl(this.client);

            zookeeperConfigAccessor.writeJSON(TEST_ROOT_PATH, dataMap);
            zookeeperConfigAccessor.deleteDataFromPath(TEST_ROOT_PATH);
            final Map<Object, Object> retMap = zookeeperConfigAccessor.readJSON(TEST_ROOT_PATH);

            assertThat(retMap.isEmpty(), is(equalTo(true)));
        } finally {
            if (this.client != null && this.client.getState()
                                                  .name()
                                                  .equals(CuratorFrameworkState.STARTED)) {
                this.client.close();
            }
        }
    }

    @Test
    public void test060_deleteNodeWithChildrenIfNeeded_Pass() {
        LOGGER.debug(LOG_PREFIX + "test060_deleteNodeWithChildrenIfNeeded_Pass");

        try {
            final String ADD_PATH = "/test060_deleteNodeWithChildrenIfNeeded_Pass";
            // Generate some input data.
            final Map<Object, Object> dataMap = new HashMap<>();
            dataMap.put("my_key1", "my_value1");

            final CuratorFrameworkState state = this.client.getState();

            LOGGER.debug(LOG_PREFIX + "test060_deleteNodeWithChildrenIfNeeded_Pass - [state.name=" + state.name() + "]");

            assertThat(state.name(), Matchers.is(equalTo(CuratorFrameworkState.STARTED.name())));

            final ZookeeperConfigAccessor zookeeperConfigAccessor = new ZookeeperConfigAccessorImpl(this.client);

            zookeeperConfigAccessor.writeJSON(BASE_ROOT_Z_NODE + ADD_PATH, dataMap);
            zookeeperConfigAccessor.deleteNodeWithChildrenIfNeeded(ADD_PATH);
            final Map<Object, Object> retMap = zookeeperConfigAccessor.readJSON(BASE_ROOT_Z_NODE);

            assertThat(retMap.isEmpty(), is(equalTo(true)));
        } finally {
            if (this.client != null && this.client.getState()
                                                  .name()
                                                  .equals(CuratorFrameworkState.STARTED)) {
                this.client.close();
            }
        }
    }

    @Test
    public void test070_deleteNodeWithChildrenIfNeeded_InvalidPath_Pass() {
        LOGGER.debug(LOG_PREFIX + "test070_deleteNodeWithChildrenIfNeeded_InvalidPath_Pass");

        try {
            final String ADD_PATH = "/crud/test070_deleteNodeWithChildrenIfNeeded_InvalidPath_Pass";
            // Generate some input data.
            final Map<Object, Object> dataMap = new HashMap<>();
            dataMap.put("my_key1", "my_value1");

            final CuratorFrameworkState state = this.client.getState();

            LOGGER.debug(LOG_PREFIX + "test070_deleteNodeWithChildrenIfNeeded_InvalidPath_Pass - [state.name=" + state.name() + "]");

            assertThat(state.name(), Matchers.is(equalTo(CuratorFrameworkState.STARTED.name())));

            final ZookeeperConfigAccessor zookeeperConfigAccessor = new ZookeeperConfigAccessorImpl(this.client);

            zookeeperConfigAccessor.writeJSON(BASE_ROOT_Z_NODE + ADD_PATH, dataMap);
            zookeeperConfigAccessor.deleteNodeWithChildrenIfNeeded(BASE_ROOT_Z_NODE + "/crud");

            final Map<Object, Object> retMap = zookeeperConfigAccessor.readJSON(BASE_ROOT_Z_NODE);

            assertThat(retMap.isEmpty(), is(equalTo(true)));
        } finally {
            if (this.client != null && this.client.getState()
                                                  .name()
                                                  .equals(CuratorFrameworkState.STARTED)) {
                this.client.close();
            }
        }
    }

    @Test
    public void test080_createAlreadyAvailable_Pass() {
        LOGGER.debug(LOG_PREFIX + "test080_createAlreadyAvailable_Pass");

        try {
            final String TEST_ROOT_PATH = "/test080_createAlreadyAvailable_Pass";

            final CuratorFrameworkState state = this.client.getState();

            LOGGER.debug(LOG_PREFIX + "test080_createAlreadyAvailable_Pass - [state.name=" + state.name() + "]");

            assertThat(state.name(), Matchers.is(equalTo(CuratorFrameworkState.STARTED.name())));

            final ZookeeperConfigAccessor zookeeperConfigAccessor = new ZookeeperConfigAccessorImpl(this.client);

            zookeeperConfigAccessor.createPersistentZNodePath(TEST_ROOT_PATH);
            zookeeperConfigAccessor.createPersistentZNodePath(TEST_ROOT_PATH);

            // Nothing happen test finished successfully.
        } finally {
            if (this.client != null && this.client.getState()
                                                  .name()
                                                  .equals(CuratorFrameworkState.STARTED)) {
                this.client.close();
            }
        }
    }

    @Test
    public void test090_deleteDataFromPath_PathNull_Pass() {
        LOGGER.debug(LOG_PREFIX + "test090_deleteDataFromPath_PathNull_Pass");

        try {
            this.thrown.expect(RuntimeException.class);

            final CuratorFrameworkState state = this.client.getState();

            LOGGER.debug(LOG_PREFIX + "test090_deleteDataFromPath_PathNull_Pass - [state.name=" + state.name() + "]");

            assertThat(state.name(), Matchers.is(equalTo(CuratorFrameworkState.STARTED.name())));

            final ZookeeperConfigAccessor zookeeperConfigAccessor = new ZookeeperConfigAccessorImpl(this.client);

            zookeeperConfigAccessor.deleteDataFromPath(null);

            fail(LOG_PREFIX + "test090_deleteDataFromPath_PathNull_Pass - A exception should be thrown.");
        } finally {
            if (this.client != null && this.client.getState()
                                                  .name()
                                                  .equals(CuratorFrameworkState.STARTED)) {
                this.client.close();
            }
        }
    }

    @Test
    public void test100_deleteNodeWithChildrenIfNeeded_PathNull_Pass() {
        LOGGER.debug(LOG_PREFIX + "test100_deleteNodeWithChildrenIfNeeded_PathNull_Pass");

        try {
            this.thrown.expect(RuntimeException.class);

            final CuratorFrameworkState state = this.client.getState();

            LOGGER.debug(LOG_PREFIX + "test100_deleteNodeWithChildrenIfNeeded_PathNull_Pass - [state.name=" + state.name() + "]");

            assertThat(state.name(), Matchers.is(equalTo(CuratorFrameworkState.STARTED.name())));

            final ZookeeperConfigAccessor zookeeperConfigAccessor = new ZookeeperConfigAccessorImpl(this.client);

            zookeeperConfigAccessor.deleteNodeWithChildrenIfNeeded(null);

            fail(LOG_PREFIX + "test100_deleteNodeWithChildrenIfNeeded_PathNull_Pass - A exception should be thrown.");
        } finally {
            if (this.client != null && this.client.getState()
                                                  .name()
                                                  .equals(CuratorFrameworkState.STARTED)) {
                this.client.close();
            }
        }
    }

    @Test
    public void test110_deleteDataFromPath_InvalidPath_Pass() {
        LOGGER.debug(LOG_PREFIX + "test110_deleteDataFromPath_InvalidPath_Pass");

        try {
            // A path that is not available in zookeeper.
            final String TEST_ROOT_PATH_INVALID = "/test110_deleteDataFromPath_InvalidPath_Pass";

            final CuratorFrameworkState state = this.client.getState();

            LOGGER.debug(LOG_PREFIX + "test110_deleteDataFromPath_InvalidPath_Pass - [state.name=" + state.name() + "]");

            assertThat(state.name(), Matchers.is(equalTo(CuratorFrameworkState.STARTED.name())));

            final ZookeeperConfigAccessor zookeeperConfigAccessor = new ZookeeperConfigAccessorImpl(this.client);

            zookeeperConfigAccessor.deleteDataFromPath(TEST_ROOT_PATH_INVALID);

            // Nothing happen, only a log entry is set.
        } finally {
            if (this.client != null && this.client.getState()
                                                  .name()
                                                  .equals(CuratorFrameworkState.STARTED)) {
                this.client.close();
            }
        }
    }
}
