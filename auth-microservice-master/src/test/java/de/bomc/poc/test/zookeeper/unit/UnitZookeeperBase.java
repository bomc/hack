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

import com.netflix.curator.test.TestingServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.IOException;
import java.net.BindException;

/**
 * Base class for zookeeper tests. This class starts an embedded zookeeper server for JUnit tests.
 */
public class UnitZookeeperBase {

    protected static TestingServer server;
    private static final String INTERNAL_PROPERTY_DONT_LOG_CONNECTION_ISSUES;

    static {
        String logConnectionIssues = null;
        try {
            // Use reflection to avoid adding a circular dependency in the pom
            Class<?> debugUtilsClazz = Class.forName("org.apache.curator.utils.DebugUtils");
            logConnectionIssues = (String)debugUtilsClazz.getField("PROPERTY_DONT_LOG_CONNECTION_ISSUES")
                                                         .get(null);
        } catch (Exception e) {
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
            } catch (BindException e) {
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            server = null;
        }
    }
}
