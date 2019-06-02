package de.bomc.poc.zk.config.accessor.impl;

import de.bomc.poc.zk.config.accessor.ZookeeperConfigAccessor;
import de.bomc.poc.zk.curator.qualifier.CuratorFrameworkQualifier;
import de.bomc.poc.zk.exception.AppZookeeperException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class allows writing and reading data from/to zookeeper.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $$
 * @since 22.07.2016
 */
public class ZookeeperConfigAccessorImpl implements ZookeeperConfigAccessor {

	private static final Logger LOGGER = Logger.getLogger(ZookeeperConfigAccessorImpl.class);
	private static final String LOG_PREFIX = "ZookeeperConfigAccessorImpl#";
	private final CuratorFramework client;

	/**
	 * This constructor should be used vor tests without a cdi environment.
	 * 
	 * @param client
	 *            the curator client tht wraps the zookeeper client.
	 */
	@Inject
	public ZookeeperConfigAccessorImpl(@CuratorFrameworkQualifier final CuratorFramework client) {
		this.client = client;
	}

	/**
	 * @return true for connected to zookeeper otherwise false.
	 */
	@Override
	public boolean isConnected() {
		final boolean isConnected = this.client.getZookeeperClient().isConnected();

		LOGGER.debug(LOG_PREFIX + "isConnected [isConnected=" + isConnected + "]");

		return isConnected;
	}

	/**
	 * Write the given data to zookeeper.
	 * 
	 * @param path
	 *            the data is written to.
	 * @param data
	 *            the data as map.
	 * @throws RuntimeException
	 *             when something goes wrong during the write process.
	 */
	@Override
	public void writeJSON(final String path, final Map<Object, Object> data) {
		LOGGER.debug(LOG_PREFIX + "writeJSON [path=" + path + ", data=" + data + "]");

		try {
			// Create json data from given data map.
			final JsonObjectBuilder dataBuilder = Json.createObjectBuilder();
			final Set<Map.Entry<Object, Object>> entries = data.entrySet();
			entries.stream().forEach(
					entry -> dataBuilder.add(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())));

			final JsonObject jsonToWrite = Json.createObjectBuilder().add(path, dataBuilder).build();

			LOGGER.debug(LOG_PREFIX + "writeJSON [jsonToWrite=" + jsonToWrite + "]");

			// Write data to zookeeper.
			this.writeBytes(path, jsonToWrite.toString().getBytes(Charset.forName("UTF-8")));
		} catch (final Exception ex) {
			LOGGER.error(LOG_PREFIX + "writeBytes - failed! ", ex);

			throw new AppZookeeperException(LOG_PREFIX + "writeBytes - failed! [message=" + ex.getMessage() + "]", ex);
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
	 *             is thrown during processing the curatorClient.
	 */
	private void writeBytes(final String path, final byte[] bytes) throws Exception {
		LOGGER.debug(LOG_PREFIX + "writeBytes [path=" + path + "]");

		if (this.client.checkExists().forPath(path) == null) {
			LOGGER.debug(LOG_PREFIX
					+ "writeBytes - path is not available, create path first, than write data down to zookeeper [path="
					+ path + "]");
			// The znode will not be automatically deleted upon curator's
			// disconnect.
			this.client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, bytes);
		} else {
			LOGGER.debug(
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
	 * @throws RuntimeException
	 *             when something goes wrong during the read process.
	 */
	@Override
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

			LOGGER.debug(LOG_PREFIX + "readJSON [jsonValue=" + jsonValue + "]");

			Map<Object, Object> returnMap = Collections.emptyMap();

			if (jsonValue.getValueType() == JsonValue.ValueType.OBJECT) {
				final JsonObject jsonObject = (JsonObject) jsonValue;

				returnMap = jsonObject.entrySet().stream()
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
			}

			return returnMap;
		} catch (final Exception ex) {
			LOGGER.error(LOG_PREFIX + "readJSON - failed! ", ex);

			throw new AppZookeeperException(
					LOG_PREFIX + "readJSON - failed! [path=" + path + ", message=" + ex.getMessage() + "]", ex);
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
	private byte[] readBytes(final String path) throws Exception {
		LOGGER.debug(LOG_PREFIX + "readBytes [path=" + path + "]");

		if (this.client.checkExists().forPath(path) != null) {
			return this.client.getData().forPath(path);
		} else {
			LOGGER.debug(LOG_PREFIX + "readBytes - path is not available!");

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
	 * @throws RuntimeException
	 *             when something goes wrong during delete process.
	 */
	@Override
	public void deleteDataFromPath(final String path) {
		LOGGER.debug(LOG_PREFIX + "deleteDataFromPath [path=" + path + "]");

		try {
			// Check if the path exists.
			if (this.client.checkExists().forPath(path) != null) {
				this.client.delete().guaranteed().forPath(path);
			} else {
				LOGGER.debug(LOG_PREFIX + "deleteDataFromPath - Nothing to delete, '" + path + "' does not exists!");
			}
		} catch (final Exception ex) {
			LOGGER.error(LOG_PREFIX + "deleteDataFromPath - failed! [path=" + path + "]", ex);

			throw new AppZookeeperException(
					LOG_PREFIX + "deleteDataFromPath - failed! [path=" + path + ", message=" + ex.getMessage() + "]",
					ex);
		}
	}

	/**
	 * Deletes node with children if needed. The data under this not will be
	 * deleted too.
	 * 
	 * @param path
	 *            the given node.
	 * @throws RuntimeException
	 *             when something goes wrong during delete process.
	 */
	public void deleteNodeWithChildrenIfNeeded(final String path) {
		LOGGER.debug(LOG_PREFIX + "deleteNodeWithChildrenIfNeeded [path=" + path + "]");

		try {
			// Check if the path exists.
			if (this.client.checkExists().forPath(path) != null) {
				this.client.delete().deletingChildrenIfNeeded().forPath(path);
			} else {
				LOGGER.debug(LOG_PREFIX + "deleteNodeWithChildrenIfNeeded - Nothing to delete, '" + path
						+ "' does not exists!");
			}
		} catch (final Exception ex) {
			LOGGER.error(LOG_PREFIX + "deleteNodeWithChildrenIfNeeded - failed! [path=" + path + "]", ex);

			throw new AppZookeeperException(LOG_PREFIX + "deleteNodeWithChildrenIfNeeded - failed! [path=" + path
					+ ", message=" + ex.getMessage() + "]", ex);
		}
	}

	/**
	 * Creates a PERSISTENT new path, inclusive the parents if needed.
	 * 
	 * @param zNodePath
	 *            the given zNodePath with the basepath.
	 * @throws RuntimeException
	 *             when something goes wrong during delete process.
	 */
	public void createPersistentZNodePath(final String zNodePath) {
		LOGGER.debug(LOG_PREFIX + "createPersistentZNodePath [" + zNodePath + "]");

		try {
			// Check if path already exists.
			if (this.client.checkExists().forPath(zNodePath) == null) {
				this.client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
						.withACL(Ids.OPEN_ACL_UNSAFE).forPath(zNodePath, new byte[0]);
			}
		} catch (final Exception e) {
			LOGGER.error(LOG_PREFIX + "createPersistentZNodePath - failed! [path=" + zNodePath + "]", e);

			throw new AppZookeeperException(LOG_PREFIX + "deleteNodeWithChildrenIfNeeded - failed! [path=" + zNodePath
					+ ", message=" + e.getMessage() + "]", e);
		}
	}
}
