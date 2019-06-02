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
package de.bomc.poc.test.auth.arq.usermanagement.mock;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.bomc.poc.logger.qualifier.LoggerQualifier;
import de.bomc.poc.rest.client.RestClientBuilder;
import de.bomc.poc.zookeeper.config.ZookeeperConfigAccessor;

/**
 * A mock implementation for zookeeper access configurations.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class ZookeeperConfigAccessorMockImpl implements ZookeeperConfigAccessor {
	private static final String LOG_PREFIX = "ZookeeperConfigAccessorMockImpl#";
	@Inject
	@LoggerQualifier
	private Logger logger;
	
	@Override
	public boolean isConnected() {
		this.logger.debug(LOG_PREFIX + "isConnected [isConnected=true]");
		return true;
	}

	@Override
	public void writeJSON(String path, Map<Object, Object> data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Object, Object> readJSON(String path) {
		this.logger.debug(LOG_PREFIX + "readJSON [path=" + path + "]");
		
		final Map<Object, Object> dataMap = new HashMap<>();
		dataMap.put(RestClientBuilder.CONNECTION_TIMEOUT_KEY, "\"15000\"");
		dataMap.put(RestClientBuilder.SOCKET_TIMEOUT_KEY, "\"10000\"");
		
		return dataMap;
	}

	@Override
	public void deleteDataFromPath(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteNodeWithChildrenIfNeeded(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createPersistentZNodePath(String path) {
		// TODO Auto-generated method stub
		
	}

}
