package de.bomc.poc.prometheus.collector.entity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.bomc.poc.prometheus.collector.entity.Metric;

/**
 * Tests the metric entity, that is transfered to Prometheus.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 14.03.2016
 */
public class MetricTest {

	private static final String LOG_PREFIX = "MetricTest#";
	private static final Logger LOGGER = Logger.getLogger(MetricTest.class);

	@Test
	public void test010_serialization_Pass() {
		LOGGER.debug(LOG_PREFIX + "test010_serialization_Pass");

		final Metric metric = new Metric("wildfly", "tx", "commit", "success", "42");
		metric.addLabel("type", "local");
		metric.addLabel("state", "no_timeout");
		final String expected = "wildfly_tx_commit_success{state=no_timeout,type=local} 42\n";
		final String actual = metric.toMetric();

		assertThat(actual, is(expected));
	}

}
