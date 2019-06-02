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

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;

/**
 * This class allows crud operations on a started zookeeper instance.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
public class ZookeeperAccessor {
	private static final String LOG_PREFIX = "ZookeeperAccessor#";
	private static final Logger LOGGER = Logger.getLogger(ZookeeperAccessor.class);
	private CuratorFramework client;

	/**
	 * Creates a new instance of <code>ZookeeperAccessor</code>.
	 * 
	 * @param client
	 *            a initialized and connected zookeeper curator.
	 * @throws RuntimeException
	 */
	public ZookeeperAccessor(final CuratorFramework client) {
		LOGGER.debug(LOG_PREFIX + "co");

		if (client == null || client.getState() == CuratorFrameworkState.STARTED) {
			this.client = client;
		} else {
			LOGGER.error(LOG_PREFIX + "co - curator is null or not started!");

			throw new RuntimeException(LOG_PREFIX + "#co - curator is null or not started!");
		}
	}

	/**
	 * Creates a PERSISTENT new path, inclusive the parents if needed.
	 * 
	 * @param zNodePath
	 *            the given zNodePath with the basepath.
	 * @throws RuntimeException
	 *             when creating zNode path fails.
	 */
	public void createPersistentZNodePath(final String zNodePath) {
		LOGGER.debug(LOG_PREFIX + "createPersistentZNodePath [" + zNodePath + "]");

		try {
			// Check if path already exists.
			if (this.client.checkExists().forPath(zNodePath) == null) {
				this.client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
						.withACL(Ids.OPEN_ACL_UNSAFE).forPath(zNodePath, new byte[0]);
			}
		} catch (Exception ex) {
			LOGGER.error(LOG_PREFIX + "createPersistentZNodePath - fails. ", ex);

			throw new RuntimeException(LOG_PREFIX + "createPersistentZNodePath - fails. " + ex.getMessage());
		}
	}

	public void writeJSON(final String path, final Map<Object, Object> data) {
		LOGGER.debug(LOG_PREFIX + "writeJSON [path=" + path + ", data=" + data.toString() + "]");

		final JsonObjectBuilder dataBuilder = Json.createObjectBuilder();
		final Set<Map.Entry<Object, Object>> entries = data.entrySet();
		entries.stream()
				.forEach(entry -> dataBuilder.add(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())));

		final JsonObject jsonToWrite = Json.createObjectBuilder().add(path, dataBuilder).build();

		LOGGER.debug(LOG_PREFIX + "writeJSON [jsonToWrite=" + jsonToWrite.toString() + "]");

		this.writeBytes(path, jsonToWrite.toString().getBytes(Charset.forName("UTF-8")));
	}

	private void writeBytes(final String path, final byte[] bytes) {
		LOGGER.debug(LOG_PREFIX + "writeBytes [path=" + path + "]");

		try {
			if (this.client.checkExists().forPath(path) == null) {
				LOGGER.debug(LOG_PREFIX
						+ "writeBytes - path is not available, create path first, than write data down to zookeeper [path="
						+ path + "]");
				// The znode will NOT (-> is PERSISTENT) be automatically
				// deleted upon curator's disconnect.
				this.client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, bytes);
			} else {
				LOGGER.debug(LOG_PREFIX + "writeBytes - path is available, write data down to zookeeper [path=" + path
						+ "]");
				// The znode will not be automatically deleted upon curator's
				// disconnect.
				this.client.setData().forPath(path, bytes);
			}
		} catch (Exception ex) {
			LOGGER.error(LOG_PREFIX + "writeBytes - fails. ", ex);

			throw new RuntimeException(LOG_PREFIX + "writeBytes - fails. " + ex.getMessage());
		}
	}

	/**
	 * Read json object from given path.
	 * 
	 * @param path
	 *            the given path to read from.
	 * 
	 * @return a map.
	 */
	public Map<Object, Object> readJSON(final String path) {
		LOGGER.debug(LOG_PREFIX + "readJSON [path=" + path + "]");

		JsonReader reader = null;

		try {
			final byte[] b = this.readBytes(path);

			if (b == null || b.length < 1) {
				return Collections.emptyMap();
			}

			reader = Json.createReader(new StringReader(new String(b, "UTF-8")));

			final JsonValue jsonValue = reader.readObject().get(path);

			LOGGER.debug(LOG_PREFIX + "readJSON [jsonValue=" + jsonValue.toString() + "]");

			Map<Object, Object> returnMap = null;

			if (jsonValue.getValueType() == JsonValue.ValueType.OBJECT) {
				final JsonObject jsonObject = (JsonObject) jsonValue;

				returnMap = jsonObject.entrySet().stream()
						.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()));
			}

			return returnMap;
		} catch (Exception ex) {
			LOGGER.error(LOG_PREFIX + "readJSON - fails. ", ex);

			throw new RuntimeException(LOG_PREFIX + "readJSON - fails. " + ex.getMessage());
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	private byte[] readBytes(final String path) {
		LOGGER.debug(LOG_PREFIX + "readBytes [path=" + path + "]");

		try {
			if (this.client.checkExists().forPath(path) != null) {
				return this.client.getData().forPath(path);
			} else {
				LOGGER.debug(LOG_PREFIX + "readBytes - path is not available!");

				return null;
			}
		} catch (Exception e) {
			LOGGER.error(LOG_PREFIX + "readBytes - fails. ", e);

			throw new RuntimeException(LOG_PREFIX + "readBytes - fails. " + e.getMessage());
		}
	}

	public void deleteDataFromPath(final String path) {
		LOGGER.debug(LOG_PREFIX + "deleteDataFromPath [path=" + path + "]");

		try {
			// Check if the path exists.
			if (this.client.checkExists().forPath(path) != null) {
				this.client.delete().guaranteed().forPath(path);
			} else {
				LOGGER.debug(
						LOG_PREFIX + "deleteDataFromPath - Warn: Nothing to delete '" + path + "' does not exists!");
			}
		} catch (Exception e) {
			LOGGER.error(LOG_PREFIX + "deleteDataFromPath - fails. ", e);

			throw new RuntimeException(LOG_PREFIX + "deleteDataFromPath - fails. " + e.getMessage());
		}
	}

	public void deleteNodeWithChildrenIfNeeded(final String path) {
		LOGGER.debug(LOG_PREFIX + "deleteNodeWithChildrenIfNeeded [path=" + path + "]");

		try {
			// Check if the path exists.
			if (this.client.checkExists().forPath(path) != null) {
				this.client.delete().deletingChildrenIfNeeded().forPath(path);
			} else {
				LOGGER.debug(LOG_PREFIX + "deleteNodeWithChildrenIfNeeded - Warn: Nothing to delete '" + path
						+ "' does not exists!");
			}
		} catch (Exception e) {
			LOGGER.error(LOG_PREFIX + "deleteNodeWithChildrenIfNeeded - fails. ", e);

			throw new RuntimeException(LOG_PREFIX + "deleteNodeWithChildrenIfNeeded - fails. " + e.getMessage());
		}
	}
}