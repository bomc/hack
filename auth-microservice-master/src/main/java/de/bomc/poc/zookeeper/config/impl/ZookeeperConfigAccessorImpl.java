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
package de.bomc.poc.zookeeper.config.impl;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.validation.constraints.NotNull;

import de.bomc.poc.exception.app.AppZookeeperException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;

import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.zookeeper.config.ZookeeperConfigAccessor;
import de.bomc.poc.zookeeper.curator.qualifier.CuratorFrameworkQualifier;

/**
 * This class allows writing and reading data from/to zookeeper.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
 */
public class ZookeeperConfigAccessorImpl implements ZookeeperConfigAccessor {
	private static final String LOG_PREFIX = "ZookeeperConfigAccessorImpl#";
	@Inject
	@LoggerQualifier
	private Logger logger;
	@Inject
	@CuratorFrameworkQualifier
	private CuratorFramework client;

	public ZookeeperConfigAccessorImpl() {
		//
		// Used by the CDI provider to proxy this bean.
	}

	/**
	 * @return true for connected to zookeeper otherwise false.
	 */
	public boolean isConnected() {
		final boolean isConnected = this.client.getZookeeperClient().isConnected();

		this.logger.debug(LOG_PREFIX + "isConnected [isConnected=" + isConnected + "]");

		return isConnected;
	}

	/**
	 * Write the given data to zookeeper.
	 * 
	 * @param path
	 *            the data is written to.
	 * @param data
	 *            the data as map.
	 * @throws AppZookeeperException
	 *             when something goes wrong during the write process.
	 */
	public void writeJSON(final @NotNull String path, final @NotNull Map<Object, Object> data) {
		this.logger.debug(LOG_PREFIX + "writeJSON [path=" + path + ", data=" + data.toString() + "]");

		try {
			// Create json data from given data map.
			final JsonObjectBuilder dataBuilder = Json.createObjectBuilder();
			final Set<Map.Entry<Object, Object>> entries = data.entrySet();
			entries.stream().forEach(
					entry -> dataBuilder.add(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())));

			final JsonObject jsonToWrite = Json.createObjectBuilder().add(path, dataBuilder).build();

			this.logger.debug(LOG_PREFIX + "writeJSON [jsonToWrite=" + jsonToWrite.toString() + "]");

			// Write data to zookeeper.
			this.writeBytes(path, jsonToWrite.toString().getBytes(Charset.forName("UTF-8")));
		} catch (Exception e) {
			this.logger.error(LOG_PREFIX + "writeBytes - failed! ", e);

			throw new AppZookeeperException(LOG_PREFIX + "writeBytes - failed [message=" + e.getMessage() + "]");
		}
	}

	/**
	 * Write the given bytes to zookeeper.
	 * 
	 * @param path
	 *            the data is written to.
	 * @param bytes
	 *            the json data as bytes.
	 * @throws Exception
	 *             when something goes wrong during the write process.
	 */
	private void writeBytes(final String path, final byte[] bytes) throws Exception {
		this.logger.debug(LOG_PREFIX + "writeBytes [path=" + path + "]");

		if (this.client.checkExists().forPath(path) == null) {
			this.logger.debug(LOG_PREFIX
					+ "writeBytes - path is not available, create path first, than write data down to zookeeper [path="
					+ path + "]");
			// The znode will not be automatically deleted upon curator's
			// disconnect.
			this.client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, bytes);
		} else {
			this.logger.debug(
					LOG_PREFIX + "writeBytes - path is available, write data down to zookeeper [path=" + path + "]");
			// The znode will not be automatically deleted upon curator's
			// disconnect.
			this.client.setData().forPath(path, bytes);
		}
	}

	/**
	 * Read data from zookeeper by the given path.
	 * 
	 * @param path
	 *            the data is read from.
	 * @return the data as map.
	 * @throws AppZookeeperException
	 *             when something goes wrong during the read process.
	 */
	public Map<Object, Object> readJSON(final @NotNull String path) {
		this.logger.debug(LOG_PREFIX + "readJSON [path=" + path + "]");

		JsonReader reader = null;

		try {
			byte[] b = this.readBytes(path);

			if (b == null || b.length < 1) {
				return Collections.emptyMap();
			}

			reader = Json.createReader(new StringReader(new String(b, "UTF-8")));

			final JsonValue jsonValue = reader.readObject().get(path);

			this.logger.debug("ZookeeperConfigAccessorImpl#readJSON [jsonValue=" + jsonValue.toString() + "]");

			Map<Object, Object> returnMap = Collections.emptyMap();

			if (jsonValue.getValueType() == JsonValue.ValueType.OBJECT) {
				final JsonObject jsonObject = (JsonObject) jsonValue;

				returnMap = jsonObject.entrySet().stream()
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
			}

			return returnMap;
		} catch (Exception e) {
			this.logger.error(LOG_PREFIX + "readJSON - failed! ", e);

			throw new AppZookeeperException(
					LOG_PREFIX + "readJSON - failed! [path=" + path + ", message=" + e.getMessage() + "]");
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Read bytes from zookeeper by the given path.
	 * 
	 * @param path
	 *            the data is read from.
	 * @throws Exception
	 *             when something goes wrong during read process.
	 */
	private byte[] readBytes(String path) throws Exception {
		this.logger.debug(LOG_PREFIX + "readBytes [path=" + path + "]");

		if (this.client.checkExists().forPath(path) != null) {
			return this.client.getData().forPath(path);
		} else {
			this.logger.debug(LOG_PREFIX + "readBytes - path is not available!");

			return null;
		}

	}

	/**
	 * Delete data from given path. 'guaranteed' means: Curator will record
	 * failed node deletions and attempt to delete them in the background until
	 * successful.
	 * 
	 * @param path
	 *            the given path
	 * @throws AppZookeeperException
	 *             when something goes wrong during delete process.
	 */
	public void deleteDataFromPath(final @NotNull String path) {
		this.logger.debug(LOG_PREFIX + "deleteDataFromPath [path=" + path + "]");

		try {
			// Check if the path exists.
			if (this.client.checkExists().forPath(path) != null) {
				this.client.delete().guaranteed().forPath(path);
			} else {
				this.logger
						.debug(LOG_PREFIX + "deleteDataFromPath - Nothing to delete, '" + path + "' does not exists!");
			}
		} catch (Exception e) {
			this.logger.error(LOG_PREFIX + "deleteDataFromPath - failed! [path=" + path + "]", e);

			throw new AppZookeeperException(
					LOG_PREFIX + "deleteDataFromPath - failed! [path=" + path + ", message=" + e.getMessage() + "]");
		}
	}

	/**
	 * Deletes node with children if needed. The data under this not will be
	 * deleted too.
	 * 
	 * @param path
	 *            the given node.
	 * @throws AppZookeeperException
	 *             when something goes wrong during delete process.
	 */
	public void deleteNodeWithChildrenIfNeeded(final @NotNull String path) {
		this.logger.debug(LOG_PREFIX + "deleteNodeWithChildrenIfNeeded [path=" + path + "]");

		try {
			// Check if the path exists.
			if (this.client.checkExists().forPath(path) != null) {
				this.client.delete().deletingChildrenIfNeeded().forPath(path);
			} else {
				this.logger
						.debug(LOG_PREFIX + "deleteDataFromPath - Nothing to delete, '" + path + "' does not exists!");
			}
		} catch (Exception e) {
			this.logger.error(LOG_PREFIX + "deleteNodeWithChildrenIfNeeded - failed! [path=" + path + "]", e);

			throw new AppZookeeperException(LOG_PREFIX + "deleteNodeWithChildrenIfNeeded - failed! [path=" + path
					+ ", message=" + e.getMessage() + "]");
		}
	}

	/**
	 * Creates a PERSISTENT new path, inclusive the parents if needed.
	 * 
	 * @param zNodePath
	 *            the given zNodePath with the basepath.
	 * @throws AppZookeeperException
	 *             when something goes wrong during delete process.
	 */
	public void createPersistentZNodePath(final String path) {
		this.logger.debug(LOG_PREFIX + "createPersistentZNodePath [" + path + "]");

		try {
			// Check if path already exists.
			if (this.client.checkExists().forPath(path) == null) {
				this.client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
						.withACL(Ids.OPEN_ACL_UNSAFE).forPath(path, new byte[0]);
			}
		} catch (Exception e) {
			this.logger.error(LOG_PREFIX + "createPersistentZNodePath - failed! [path=" + path + "]", e);

			throw new AppZookeeperException(LOG_PREFIX + "deleteNodeWithChildrenIfNeeded - failed! [path=" + path
					+ ", message=" + e.getMessage() + "]");
		}
	}
}
