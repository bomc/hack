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

import de.bomc.poc.test.GlobalArqTestProperties;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ZookeeperAccessorEmbeddedTest extends UnitZookeeperBase {

    private static final Logger LOGGER = Logger.getLogger(ZookeeperAccessorEmbeddedTest.class);
    private final String LOG_PREFIX = "ZookeeperAccessorEmbeddedTest#";
    private static final String ROOT_ZNODE = "/zookeeperAccessorEmbeddedTest" + GlobalArqTestProperties.RELATIVE_ROOT_Z_NODE;
    private ZookeeperClient zookeeperClient = null;

    @Before
    public void init() {
        // Create curator.
        zookeeperClient = new ZookeeperClient(server.getConnectString(), GlobalArqTestProperties.CONNECTION_TIMEOUT_MS, GlobalArqTestProperties.SESSION_TIMEOUT_MS, ROOT_ZNODE);
    }

    @Test
    public void test01_json_Write_Read_Back() throws Exception {
        LOGGER.debug(LOG_PREFIX + "test01_json_Write_Read_Back");

        assertThat(zookeeperClient.getCuratorFramework()
                                  .getState(), is(CuratorFrameworkState.STARTED));

        ZookeeperAccessor zookeeperAccessor = null;

        try {
            Map<Object, Object> dataMap = new HashMap<>();
            dataMap.put("my_key1", "my_value1");
            dataMap.put("my_key3", "my_value3");
            dataMap.put("my_key5", "my_value5");

            zookeeperAccessor = new ZookeeperAccessor(zookeeperClient.getCuratorFramework());

            // Write to zookeeper...
            zookeeperAccessor.writeJSON(ROOT_ZNODE + "/config", dataMap);

            // ...wait for write down...
            TimeUnit.MILLISECONDS.sleep(100);

            // ...read back from zookeeper.
            final Map<Object, Object> retMap = zookeeperAccessor.readJSON(ROOT_ZNODE + "/config");

            System.out.println("ZookeeperAccessorEmbeddedTest#test01_json_Write_Read_Back [retMap=" + retMap + "]");
        } finally {
            if (zookeeperClient != null) {
            	zookeeperClient.close();
            }
        }
    }
}
