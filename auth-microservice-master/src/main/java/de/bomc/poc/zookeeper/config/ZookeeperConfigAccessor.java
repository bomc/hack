/**
 * Project: MY_POC_MICROSERVICE
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
package de.bomc.poc.zookeeper.config;

import java.util.Map;

import javax.validation.constraints.NotNull;

import de.bomc.poc.exception.app.AppZookeeperException;

/**
 * This class defines methods for writing and reading data from/to zookeeper.
 * 
 * @author <a href="mailto:bomc@myHome.org">bomc</a>
 *
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
	 * @throws AppZookeeperException
	 *             when something goes wrong during the write process.
	 */
	void writeJSON(final @NotNull String path, final @NotNull Map<Object, Object> data);

	/**
	 * Read data from zookeeper by the given path.
	 * 
	 * @param path
	 *            the data is read from.
	 * @return the data as map.
	 * @throws AppZookeeperException
	 *             when something goes wrong during the read process.
	 */
	Map<Object, Object> readJSON(final @NotNull String path);

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
	void deleteDataFromPath(final @NotNull String path);

	/**
	 * Deletes node with children if needed. The data under this not will be
	 * deleted too.
	 * 
	 * @param path
	 *            the given node.
	 * @throws AppZookeeperException
	 *             when something goes wrong during delete process.
	 */
	void deleteNodeWithChildrenIfNeeded(final @NotNull String path);

	/**
	 * Creates a PERSISTENT new path, inclusive the parents if needed.
	 * 
	 * @param zNodePath
	 *            the given zNodePath with the basepath.
	 * @throws AppZookeeperException
	 *             when something goes wrong during delete process.
	 */
	void createPersistentZNodePath(final String path);
}
