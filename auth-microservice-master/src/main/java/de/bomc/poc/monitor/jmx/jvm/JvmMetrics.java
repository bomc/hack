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
package de.bomc.poc.monitor.jmx.jvm;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.HashMap;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import de.bomc.poc.monitor.jmx.AuthMBean;

/**
 * The implementation for reporting JVM CPU usage statistics.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class JvmMetrics extends AuthMBean implements JvmMetricsMBean {
	private static final String LOG_PREFIX = "JvmMetrics#";
	private static final String OBJECT_NAME_OS = "java.lang:type=OperatingSystem";
	private static final String MBEAN_ATTRIBUTE_OS_SYSTEM_CPU_LOAD_KEY = "SystemCpuLoad";
	private static final String MBEAN_ATTRIBUTE_OS_SYSTEM_CPU_LOAD_VALUE = "cpu.system";
	private static final String MBEAN_ATTRIBUTE_OS_PROCESS_CPU_LOAD_KEY = "ProcessCpuLoad";
	private static final String MBEAN_ATTRIBUTE_OS_PROCESS_CPU_LOAD_VALUE = "cpu.jvm";
	private Map<String, String> attributesJvmMap = new HashMap<String, String>();
	private Map<String, String> attributesSystemMap = new HashMap<String, String>();
	private MBeanServer mbs;
	private ObjectName objectNameOS;

	/**
	 * Creates a instance of <code>JvmMetrics</code>.
	 * 
	 */
	public JvmMetrics() {
		super(JvmMetrics.class.getSimpleName());

		this.attributesJvmMap.put(MBEAN_ATTRIBUTE_OS_SYSTEM_CPU_LOAD_KEY, MBEAN_ATTRIBUTE_OS_SYSTEM_CPU_LOAD_VALUE);
		this.attributesSystemMap.put(MBEAN_ATTRIBUTE_OS_PROCESS_CPU_LOAD_KEY,
				MBEAN_ATTRIBUTE_OS_PROCESS_CPU_LOAD_VALUE);

		try {
			this.mbs = ManagementFactory.getPlatformMBeanServer();
			this.objectNameOS = ObjectName.getInstance(OBJECT_NAME_OS);
		} catch (MalformedObjectNameException e) {
			LOGGER.warn(LOG_PREFIX + "co - initialization failed! No recording will be performed!", e);
		}
	}

	@Override
	public void start() {
		// Nothing todo here.

	}

	@Override
	public void stop() {
		// Nothing todo here.

	}

	/**
	 * List cpu metrics by given attribute list.
	 * 
	 * @param attributeMap
	 *            the attribute map (SystemCpuLoad or ProcessCpuLoad)
	 * 
	 * @return cpu metrics depends on given attribute list or -1.0 if metric
	 *         could not determine.
	 */
	private double getCpuMetrics(final Map<String, String> attributeMap) {
		Double value = -1.0;

		try {
			final AttributeList list = mbs.getAttributes(this.objectNameOS,
					attributeMap.keySet().toArray(new String[attributeMap.size()]));

			if (list == null || list.isEmpty()) {
				LOGGER.warn(LOG_PREFIX + "recordStats - could not determine attributelist for os stats.");

				value = -1.0;
			}

			final Attribute attribute = (Attribute) list.get(0);
			value = (Double) attribute.getValue();

			if (value == null) {
				value = -1.0;
			}

			value = ((int) (value * 1000)) / 10.0d; // 0-100 with 1-decimal
													// precision
		} catch (InstanceNotFoundException | ReflectionException e) {
			LOGGER.error(LOG_PREFIX + "recordStats - failed! ", e);

			value = -1.0;
		}

		return value;
	}

	@Override
	public double getProcessCpuLoad() {
		return this.getCpuMetrics(this.attributesJvmMap);
	}

	@Override
	public double getSystemCpuLoad() {
		return this.getCpuMetrics(this.attributesSystemMap);
	}

	@Override
	public double getHeapMemoryUsage() {
		final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

		final Long max = memoryMXBean.getHeapMemoryUsage().getMax();
		final Long used = memoryMXBean.getHeapMemoryUsage().getUsed();

		final double usedPercentage = ((double) used / (double) max) * 100.0;

		return ((int) (usedPercentage * 10)) / 10.0d; // 0-100 with 1-decimal
														// precision
	}

	@Override
	public double getNonHeapMemoryUsage() {
		final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

		final Long max = memoryMXBean.getNonHeapMemoryUsage().getMax();
		final Long used = memoryMXBean.getNonHeapMemoryUsage().getUsed();

		double usedPercentage = ((double) used / (double) max) * 100.0;
		if (usedPercentage < 0) {
			usedPercentage = 0.0;
		}

		return ((int) (usedPercentage * 10)) / 10.0d; // 0-100 with 1-decimal
														// precision
	}
}
