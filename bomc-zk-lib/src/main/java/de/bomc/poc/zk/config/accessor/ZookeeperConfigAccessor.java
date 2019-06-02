package de.bomc.poc.zk.config.accessor;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * This class defines methods for writing and reading data from/to zookeeper.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 22.07.2016
 */
public interface ZookeeperConfigAccessor {
	/**
	 * @return true for connected to zookeeper otherwise false.
	 */
	boolean isConnected();

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
	void writeJSON(@NotNull final String path, final Map<Object, Object> data);

	/**
	 * Read data from zookeeper by the given path.
	 * 
	 * @param path
	 *            the data is read from.
	 * @return the data as map or a empty map if no data available.
	 * @throws RuntimeException
	 *             when something goes wrong during the read process.
	 */
	Map<Object, Object> readJSON(@NotNull final String path);

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
	void deleteDataFromPath(@NotNull final String path);

	/**
	 * Deletes node with children if needed. The data under this not will be
	 * deleted too.
	 * 
	 * @param path
	 *            the given node.
	 * @throws RuntimeException
	 *             when something goes wrong during delete process.
	 */
	void deleteNodeWithChildrenIfNeeded(@NotNull final String path);

	/**
	 * Creates a PERSISTENT new path, inclusive the parents if needed.
	 * 
	 * @param zNodePath
	 *            the given zNodePath with the basepath.
	 * @throws RuntimeException
	 *             when something goes wrong during delete process.
	 */
	void createPersistentZNodePath(@NotNull final String zNodePath);
}
