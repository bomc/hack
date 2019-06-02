package de.bomc.poc.prometheus.collector.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.json.JsonObject;

import org.apache.log4j.Logger;

/**
 * Entity for data transfer to prometheus.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 02.06.2017
 */
public class Metric {

	private static final String LOG_PREFIX = "Metric#";
	private static final Logger LOGGER = Logger.getLogger(Metric.class);

	public static final String SUFFIX = "suffix";
	public static final String UNITS = "units";
	public static final String COMPONENT = "component";
	public static final String APPLICATION = "application";

	private final List<String> metricParts;
	private final Map<String, String> labels;
	private final String value;

	/**
	 * Creates a new instance of <code>Metric</code> co.
	 * 
	 * @param application
	 *            aka namespace
	 * @param component
	 *            further describes a metric within an application.
	 * @param units
	 *            e.g. seconds
	 * @param unitDescriptionSuffix
	 *            e.g. total
	 */
	public Metric(final String application, final String component, final String units,
			final String unitDescriptionSuffix, final String value) {
		this.labels = new HashMap<>();
		this.value = value;
		this.metricParts = Arrays.asList(application, component, units, unitDescriptionSuffix);
	}

	/**
	 * Creates a new instance of <code>Metric</code> co.
	 * 
	 * @param configuredMetaData
	 *            metaData for prometheus configuration.
	 * @param applicationData
	 *            the application data.
	 */
	public Metric(final JsonObject configuredMetaData, final JsonObject applicationData) {
		this(configuredMetaData.getString(APPLICATION, applicationData.getString(APPLICATION, null)),
				configuredMetaData.getString(COMPONENT, applicationData.getString(COMPONENT, null)),
				configuredMetaData.getString(UNITS, applicationData.getString(UNITS, null)),
				configuredMetaData.getString(SUFFIX, applicationData.getString(SUFFIX, null)),
				applicationData.getString("value"));
	}

	/**
	 * Add a label as name to a value.
	 * 
	 * @param name
	 *            the given name.
	 * @param value
	 *            the given value.
	 */
	public void addLabel(String name, String value) {
		LOGGER.debug(LOG_PREFIX + "addLabel [name=" + name + ", value=" + value + "]");

		this.labels.put(name, value);
	}

	/**
	 * @return the metric with labels and the value.
	 */
	public String toMetric() {
		final String metric = this.metricParts.stream().filter(s -> s != null).collect(Collectors.joining("_"));

		LOGGER.debug(LOG_PREFIX + "toMetric [metric=" + metric + "]");

		if (this.labels.isEmpty()) {
			return addValue(metric, this.value);
		}

		final String metricWithLabels = metric + "{" + this.getLabels() + "}";

		return addValue(metricWithLabels, this.value);
	}

	/**
	 * Add a value to a metric.
	 * 
	 * @param metric
	 *            the metric for adding the givan value.
	 * @param value
	 *            the given value.
	 * @return the metric with a value as string with a CR at the end.
	 */
	String addValue(String metric, String value) {
		LOGGER.debug(LOG_PREFIX + "addValue [metric=" + metric + ", value=" + value + "]");

		return metric + " " + value + "\n";
	}

	/**
	 * @return all labels as string, splitted by a ','.
	 */
	String getLabels() {
		LOGGER.debug(LOG_PREFIX + "getLabels");

		return this.labels.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
				.collect(Collectors.joining(","));
	}
}
