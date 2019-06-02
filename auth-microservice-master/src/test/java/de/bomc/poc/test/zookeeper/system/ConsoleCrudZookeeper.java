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
package de.bomc.poc.test.zookeeper.system;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.Logger;

import de.bomc.poc.test.GlobalArqTestProperties;
import de.bomc.poc.test.zookeeper.unit.ZookeeperAccessor;
import de.bomc.poc.test.zookeeper.unit.ZookeeperClient;

/**
 * This class runs CRUD operations from console against a running zookeeper
 * instance.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class ConsoleCrudZookeeper {
	private static final Logger LOGGER = Logger.getLogger(ConsoleCrudZookeeper.class);
	private static final String LOG_PREFIX = "ConsoleCrudZookeeper#";
	private static final String CONNECTION_STRING = "localhost:2181";
	private static final String BASE_ROOT_Z_NODE = "/crud" + GlobalArqTestProperties.RELATIVE_ROOT_Z_NODE;
	private static final String CONFIG_PATH_REST_CLIENT = "/config/rest/client";
	private ZookeeperClient zookeeperClient;
	private ZookeeperAccessor zookeeperAccessor;

	/**
	 * <pre>
	 * The app runs with (address="localhost:2181").
	 * Allows CRUD operations from console. Enter a, b, c, d, e or f to perform the operations.
	 * </pre>
	 * 
	 * @throws Exception
	 */
	public static final void main(String... args) throws Exception {
		final Scanner scanner = new Scanner(System.in);

		final ConsoleCrudZookeeper consoleCrudZookeeper = new ConsoleCrudZookeeper();
		consoleCrudZookeeper.init();

		final Map<Object, Object> dataMap = new HashMap<>();
		dataMap.put("connectionTimeout", "15000");
		dataMap.put("socketTimeout", "10000");

		boolean noMatch = false;
		String action;

		try {
			do {
				System.out.println("a) write data.");
				System.out.println("b) read data.");
				System.out.println("c) create a zNode.");
				System.out.println("d) delete data from path.");
				System.out.println("e) delete node with children if needed.");
				System.out.println("f) exit.");

				try {
					action = scanner.next();

					// Check if action is a character a, b, c, d, e or f.
					if (action.length() == 1 && Character.isLetter(action.charAt(0))) {
						switch (action) {
						case "a":
							consoleCrudZookeeper.writeJsonObject(dataMap);
							System.out.println("Write data down to zookeeper on path [" + BASE_ROOT_Z_NODE
									+ CONFIG_PATH_REST_CLIENT + "].");
							break;
						case "b":
							consoleCrudZookeeper.readJsonObject(BASE_ROOT_Z_NODE + CONFIG_PATH_REST_CLIENT);
							System.out.println("Read data from zookeeper on path [" + BASE_ROOT_Z_NODE
									+ CONFIG_PATH_REST_CLIENT + "].");
							break;
						case "c":
							consoleCrudZookeeper.createPersistentZNodePath(BASE_ROOT_Z_NODE + CONFIG_PATH_REST_CLIENT);
							System.out.println(
									"Create a PERSISTENT node -> " + BASE_ROOT_Z_NODE + CONFIG_PATH_REST_CLIENT);
							break;
						case "d":
							consoleCrudZookeeper.deleteDataFromPath(BASE_ROOT_Z_NODE + CONFIG_PATH_REST_CLIENT);
							System.out
									.println("Delete data form path -> " + BASE_ROOT_Z_NODE + CONFIG_PATH_REST_CLIENT);
							break;
						case "e":
							consoleCrudZookeeper.deleteNodeWithChildrenIfNeeded(BASE_ROOT_Z_NODE + "/config");
							System.out.println("Delete node '/config' this includes '/rest/client' and the data too!");
							break;
						case "f":
							noMatch = true;
							break;
						default:
							break;
						}
					} else {
						System.out.println("Input must be a character a, b, c, d, e or f for exit!");
					}
				} catch (Exception nosEx) {
					nosEx.printStackTrace();
				}
			} while (!noMatch);
		} finally {
			scanner.close();
			consoleCrudZookeeper.cleanup();
			System.exit(1);
		}
	}

	/**
	 * Creates a new instance of <code>ConsoleCrudZookeeper</code>.
	 * 
	 */
	public ConsoleCrudZookeeper() {
		LOGGER.info(LOG_PREFIX + "co");

	}

	private void init() throws Exception {
		LOGGER.debug(LOG_PREFIX + "init");

		// Create curator.
		this.zookeeperClient = new ZookeeperClient(CONNECTION_STRING, 2000, 2000, BASE_ROOT_Z_NODE);
		this.zookeeperAccessor = new ZookeeperAccessor(this.zookeeperClient.getCuratorFramework());
	}

	private void writeJsonObject(final Map<Object, Object> data) {
		LOGGER.debug(LOG_PREFIX + "writeJsonObject");

		final Set<Map.Entry<Object, Object>> entries = data.entrySet();
		entries.stream().forEach(entry -> {
			System.out.println(
					"data: key=" + String.valueOf(entry.getKey()) + ", value=" + String.valueOf(entry.getValue()));
		});

		this.zookeeperAccessor.writeJSON(BASE_ROOT_Z_NODE + CONFIG_PATH_REST_CLIENT, data);
	}

	private void readJsonObject(final String path) {
		LOGGER.debug(LOG_PREFIX + "readJsonObject [path=" + path + "]");

		final Map<Object, Object> readJSONMap = this.zookeeperAccessor.readJSON(path);

		if (!readJSONMap.isEmpty()) {
			final Set<Map.Entry<Object, Object>> entries = readJSONMap.entrySet();
			entries.stream().forEach(entry -> {
				System.out.println(
						"data: key=" + String.valueOf(entry.getKey()) + ", value=" + String.valueOf(entry.getValue()));
			});
		} else {
			LOGGER.debug(LOG_PREFIX + "readJsonObject - no data on this path. [path=" + path + "]");
		}
	}

	private void deleteDataFromPath(final String path) {
		LOGGER.debug(LOG_PREFIX + "deleteDataFromPath [path=" + path + "]");

		this.zookeeperAccessor.deleteDataFromPath(path);
	}

	private void deleteNodeWithChildrenIfNeeded(final String path) {
		LOGGER.debug(LOG_PREFIX + "deleteNodeWithChildrenIfNeeded [path=" + path + "]");

		this.zookeeperAccessor.deleteNodeWithChildrenIfNeeded(path);
	}

	private void createPersistentZNodePath(final String path) {
		LOGGER.debug(LOG_PREFIX + "createZNodePath [path=" + path + "]");

		this.zookeeperAccessor.createPersistentZNodePath(path);
	}

	private void cleanup() {
		LOGGER.debug(LOG_PREFIX + "cleanup");

		this.zookeeperClient.close();
	}
}
