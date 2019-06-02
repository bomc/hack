package de.bomc.poc.hystrix.prometheus;

import io.prometheus.client.Collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

/**
 * Implementation of a Prometheus Collector for Hystrix metrics.
 */
public class HystrixMetricsCollector extends Collector {

	private static final Logger LOGGER = Logger.getLogger(HystrixMetricsCollector.class);

	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Map<Gauge, List<Value>> gauges = new HashMap<>();

	private final String namespace;

	public HystrixMetricsCollector(String namespace) {
		this.namespace = namespace;
	}

	public void addGauge(String subsystem, String metric, String helpDoc, Map<String, String> labels,
			Callable<Number> value) {

		lock.writeLock().lock();
		try {
			Gauge gauge = new Gauge(name(subsystem, metric), helpDoc);
			List<Value> values = gauges.get(gauge);
			if (values == null) {
				values = new ArrayList<>();
				gauges.put(gauge, values);
			}
			values.add(new Value(labels, value));
		} finally {
			lock.writeLock().unlock();
		}
	}

	private String name(String subsystem, String metric) {
		return (namespace != null) ? namespace + "_" + subsystem + "_" + metric : subsystem + "_" + metric;
	}

	@Override
	public List<MetricFamilySamples> collect() {
		lock.readLock().lock();
		try {
			return gauges.entrySet().stream().map(e -> e.getKey().toSamples(e.getValue())).collect(Collectors.toList());
		} finally {
			lock.readLock().unlock();
		}
	}

	private static class Gauge {

		private final String name;
		private final String helpDoc;

		public Gauge(String name, String helpDoc) {
			this.name = name;
			this.helpDoc = helpDoc;
		}

		public MetricFamilySamples toSamples(List<Value> values) {
			return new MetricFamilySamples(name, Type.GAUGE, helpDoc,
					values.stream().map(v -> v.toSample(name)).filter(Objects::nonNull).collect(Collectors.toList()));
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			} else if (obj instanceof Gauge) {
				Gauge other = (Gauge) obj;
				return this.name.equals(other.name);
			}
			return false;
		}
	}

	private static class Value {

		private final List<String> labelNames;
		private final List<String> labelValues;
		private final Callable<Number> value;

		public Value(Map<String, String> labels, Callable<Number> value) {
			this.labelNames = new ArrayList<>(labels.keySet());
			this.labelValues = new ArrayList<>(labels.values());
			this.value = value;
		}

		public MetricFamilySamples.Sample toSample(String name) {
			try {
				return new MetricFamilySamples.Sample(name, labelNames, labelValues, value.call().doubleValue());
			} catch (Exception e) {
				LOGGER.warn(String.format("Cannot export %s - caused by: %s", name, e), e);
				return null;
			}
		}
	}
}
