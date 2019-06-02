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
package de.bomc.poc.monitor.controller;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;

import de.bomc.poc.exception.app.AppMonitorException;
import de.bomc.poc.monitor.jmx.AuthMBean;
import de.bomc.poc.monitor.jmx.MBeanInfo;

/**
 * A controller handles a connection to the platform mbeanserver. Further the
 * controller supports the creation of path and <code>ObjectNames</code>, also
 * registering and unregistering of mbeans.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@ApplicationScoped
public class MBeanController {
	private static final Logger LOGGER = Logger.getLogger(MBeanController.class);
	private static final String LOG_PREFIX = "MBeanController#";

//	public static final String DOMAIN_NAME = "de.bomc.auth.microservice";

	private final Map<MBeanInfo, String> mapBean2Path = new HashMap<MBeanInfo, String>();
	private final Map<String, MBeanInfo> mapName2Bean = new HashMap<String, MBeanInfo>();

	private MBeanServer mBeanServer;

	/**
	 * Creates a new instance of <code>MBeanController</code>.
	 */
	public MBeanController() {
		LOGGER.debug(LOG_PREFIX + "co");

	}

	public MBeanController(final MBeanServer mBeanServer) {
		LOGGER.debug(LOG_PREFIX + "co [mBeanServer=" + mBeanServer + "]");

		this.mBeanServer = mBeanServer;
	}

	/**
	 * Initialize the mBeanServer instance.
	 * 
	 * @throws AppMonitorException
	 *             if getting the MBeanServer failed.
	 */
	@PostConstruct
	public void init() {
		LOGGER.debug(LOG_PREFIX + "init");

		try {
			if (this.mBeanServer == null) {
				this.mBeanServer = ManagementFactory.getPlatformMBeanServer();
			} else {
				LOGGER.info(LOG_PREFIX + "init - A MBeanServer is already initialized.");
			}
		} catch (Exception ex) {
			LOGGER.error(LOG_PREFIX + "init - getting MBeanServer failed! ", ex);

			throw new AppMonitorException(LOG_PREFIX + "init - getting MBeanServer failed! ", true);
		}
	}

	/**
	 * Cleanup resources, unregister MBeans.
	 */
	@PreDestroy
	public void cleanup() {
		LOGGER.debug(LOG_PREFIX + "cleanup");

		this.unregisterAll();
	}

	/**
	 * Registers a new MBean with the platform MBean server.
	 * 
	 * @param bean
	 *            the bean being registered.
	 * @param parent
	 *            if not null, the new bean will be registered as a child node
	 *            of this parent.
	 * @throws AppMonitorException
	 *             if parent path is null, or registering failed.
	 */
	public void register(@NotNull final MBeanInfo mBeanInfo, final MBeanInfo parentMXBeanInfo) throws JMException {
		LOGGER.debug(LOG_PREFIX + "register [mBean=" + mBeanInfo.getName() + "]");

		String path = null;

		if (parentMXBeanInfo != null) {
			path = this.mapBean2Path.get(parentMXBeanInfo);

			if (path == null) {
				// This should never happen.
				LOGGER.error(LOG_PREFIX + "register - failed! Parent path is null!");

				throw new AppMonitorException(LOG_PREFIX + "register - failed! Parent path is null!");
			}
		}

		path = makeFullPath(path, parentMXBeanInfo);

		final ObjectName objectName = makeObjectName(path, mBeanInfo);

		try {
			this.mBeanServer.registerMBean(mBeanInfo, objectName);
			this.mapBean2Path.put(mBeanInfo, path);
			this.mapName2Bean.put(mBeanInfo.getName(), mBeanInfo);
		} catch (JMException e) {
			LOGGER.warn(LOG_PREFIX + "register - failed to register MBean [" + mBeanInfo.getName() + "] ", e);

			throw new AppMonitorException(
					LOG_PREFIX + "register - failed to register MBean [" + mBeanInfo.getName() + "]", true);
		}
	}

	/**
	 * Unregister the MBean identified by the path.
	 * 
	 * @param path
	 *            where to find the MBean.
	 * @param bean
	 *            the Mbean to unregister.
	 */
	private void unregister(final String path, final MBeanInfo mBeanInfo) throws JMException {
		LOGGER.debug(LOG_PREFIX + "unregister [path=" + path + ", mBean=" + mBeanInfo.getName() + "]");

		if (path == null) {
			return;
		}

		try {
			this.mBeanServer.unregisterMBean(makeObjectName(path, mBeanInfo));
		} catch (JMException e) {
			LOGGER.warn(LOG_PREFIX + "unregister - failed to unregister MBean [" + mBeanInfo.getName() + "]");

			throw new AppMonitorException(
					LOG_PREFIX + "unregister - failed to unregister MBean [" + mBeanInfo.getName() + "]", true);
		}
	}

	/**
	 * Unregister MBean.
	 * 
	 * @param bean
	 *            the bean to unregister.
	 */
	public void unregister(@NotNull final MBeanInfo mBeanInfo) {
		final String path = this.mapBean2Path.get(mBeanInfo);

		LOGGER.debug(LOG_PREFIX + "unregister [mBean=" + mBeanInfo.getName() + ", path=" + path + "]");

		try {
			this.unregister(path, mBeanInfo);
		} catch (JMException e) {
			LOGGER.warn(LOG_PREFIX + "unregister - error during unregister", e);
		}

		this.mapBean2Path.remove(mBeanInfo);
		this.mapName2Bean.remove(mBeanInfo.getName());
	}

	/**
	 * Unregister all currently registered MBeans
	 */
	public void unregisterAll() {
		LOGGER.debug(LOG_PREFIX + "unregisterAll");

		for (Map.Entry<MBeanInfo, String> e : mapBean2Path.entrySet()) {
			try {
				this.unregister(e.getValue(), e.getKey());
			} catch (JMException ex) {
				LOGGER.warn(LOG_PREFIX + "unregisterAll - error during unregister [mbean=" + e.getKey().getName() + "]",
						ex);
			}
		}

		this.mapBean2Path.clear();
		this.mapName2Bean.clear();
	}

	/**
	 * Gets the value of a specific attribute of a named MBean. The MBean is
	 * identified by its object name.
	 *
	 * @param name
	 *            The object name of the MBean from which the attribute is to be
	 *            retrieved.
	 * @param attribute
	 *            A String specifying the name of the attribute to be retrieved.
	 *
	 * @return The value of the retrieved attribute.
	 */
	public Object getAttribute(final ObjectName objectName, final String attributeName) {
		LOGGER.debug(LOG_PREFIX + "getAttribute [objectName=" + objectName.toString() + ", attributeName="
				+ attributeName + "]");

		try {
			return this.mBeanServer.getAttribute(objectName, attributeName);
		} catch (Exception ex) {
			LOGGER.error(LOG_PREFIX + "getAttribute - failed! ", ex);

			throw new AppMonitorException(LOG_PREFIX + "getAttribute failed [message=" + ex.getMessage() + "]", true);
		}
	}

	/**
	 * Returns the mBean to which the specified beanName is mapped, or
	 * {@code null} if this map contains no mapping to the given beanName.
	 * 
	 * @param beanName
	 *            the given beanName.
	 * @return the mBean or null if no mBean mapped to the beanName.
	 */
	public Object getMBeanBySimpleName(final String beanName) {
		LOGGER.debug(LOG_PREFIX + "getBeanByName [beanName=" + beanName + "]");

		return mapName2Bean.get(beanName);
	}

	/**
	 * Returns the beanPath to the given MBean, or {@code null} if this map
	 * contains no mapping to the given beanPath.
	 * 
	 * @param beanName
	 *            the given beanName.
	 * @return the mBean or null if no mBean mapped to the beanName.
	 */
	public String getBeanPathByMBean(MBeanInfo mBean) {
		LOGGER.debug(LOG_PREFIX + "getBeanPathByMBean [mBean=" + mBean.getName() + "]");

		return mapBean2Path.get(mBean);
	}

	/**
	 * Generate a filesystem-like path.
	 * 
	 * @param prefix
	 *            path prefix
	 * @param name
	 *            path elements
	 * @return absolute path
	 */
	private String makeFullPath(final String prefix, final String... name) {
		LOGGER.debug(LOG_PREFIX + "makeFullPath [prefix=" + prefix + ", names]");

		final StringBuilder sb = new StringBuilder(prefix == null ? "/" : (prefix.equals("/") ? prefix : prefix + "/"));

		boolean first = true;

		for (String s : name) {
			if (s == null) {
				continue;
			}

			if (!first) {
				sb.append("/");
			} else {
				first = false;
			}

			sb.append(s);
		}

		return sb.toString();
	}

	/**
	 * Generate a filesystem-like path.
	 * 
	 * @param prefix
	 *            path prefix
	 * @param mBeanInfo
	 *            bean
	 * @return absolute path
	 */
	public String makeFullPath(final String prefix, final MBeanInfo mBeanInfo) {
		LOGGER.debug(LOG_PREFIX + "makeFullPath [prefix=" + prefix + ", mXBeanInfo]");

		return this.makeFullPath(prefix, mBeanInfo == null ? null : mBeanInfo.getName());
	}

	/**
	 * This takes a path, such as /a/b/c, and converts it to
	 * name0=a,name1=b,name2=c
	 */
	private int tokenize(final StringBuilder sb, final String path, int index) {
		LOGGER.debug(LOG_PREFIX + "tokenize [sb=" + sb.toString() + " path=" + path + ", index=" + index + "]");

		final String[] tokens = path.split("/");

		for (String s : tokens) {

			if (s.length() == 0) {
				continue;
			}

			sb.append("name").append(index++).append("=").append(s).append(",");
		}

		return index;
	}

	/**
	 * Builds an MBean path and creates an ObjectName instance using the path.
	 * 
	 * @param path
	 *            MBean path
	 * @param bean
	 *            the MBean instance
	 * @return ObjectName to be registered with the platform MBean server
	 */
	public ObjectName makeObjectName(String path, MBeanInfo mBeanInfo) throws MalformedObjectNameException {
		LOGGER.debug(LOG_PREFIX + "makeObjectName [path=" + path + ", mXBeanInfo=" + mBeanInfo + "]");

		if (path == null) {
			return null;
		}

		final StringBuilder beanName = new StringBuilder(AuthMBean.DOMAIN_NAME + ":");
		int counter = 0;
		counter = tokenize(beanName, path, counter);
		tokenize(beanName, mBeanInfo.getName(), counter);
		beanName.deleteCharAt(beanName.length() - 1);

		try {
			return new ObjectName(beanName.toString());
		} catch (MalformedObjectNameException e) {
			LOGGER.warn(LOG_PREFIX + "makeObjectName - invalid name \"" + beanName.toString() + "\" for class "
					+ mBeanInfo.getClass().toString(), e);

			throw new AppMonitorException(LOG_PREFIX + "makeObjectName - invalid name \"" + beanName.toString()
					+ "\" for class " + mBeanInfo.getClass().toString(), true);
		}
	}

	/**
	 * Adds a listener to a registered MBean.
	 * 
	 * @param name
	 *            The name of the MBean on which the listener should be added.
	 * @param listener
	 *            The listener object which will handle the notifications
	 *            emitted by the registered MBean.
	 * @param filter
	 *            The filter object. If filter is null, no filtering will be
	 *            performed before handling notifications.
	 * @param handback
	 *            The context to be sent to the listener when a notification is
	 *            emitted.
	 *
	 * @exception InstanceNotFoundException
	 *                The MBean name provided does not match any of the
	 *                registered MBeans.
	 */
	public void addNotificationListener(final ObjectName name, final NotificationListener listener,
			final NotificationFilter filter, final Object handback) {
		try {
			this.mBeanServer.addNotificationListener(name, listener, filter, handback);
		} catch (InstanceNotFoundException ex) {
			LOGGER.error(LOG_PREFIX + "addNotificationListener - there is no mbean registered to this ojectName!", ex);

			throw new AppMonitorException(LOG_PREFIX + "addNotificationListener - there is no mbean registered to this ojectName! " + ex.getMessage(), true);
		}
	}
}
