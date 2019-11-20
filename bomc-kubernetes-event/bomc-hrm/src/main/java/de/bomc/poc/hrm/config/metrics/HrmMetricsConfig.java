/**
 * Project: hrm
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.hrm.config.metrics;

import java.net.UnknownHostException;
//import java.time.Duration;
import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.bomc.poc.hrm.application.metrics.aop.DataSourceStatusProbe;
//import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.config.MeterFilter;
//import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;

/**
 * The {@code MeterRegistryCustomizer} interface that can expose to perform
 * customizations in Micrometer MeterRegistry. Additionally, MeterRegistry has
 * concepts of tags along with all the metrics and leverages the same concept to
 * add service information. However, to use common(global) tags to automatically
 * add service information to all the metrics.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 14.11.2019
 */
@Configuration
//@AutoConfigureAfter(CompositeMeterRegistryAutoConfiguration.class)
public class HrmMetricsConfig {

//	private static final Duration HISTOGRAM_EXPIRY = Duration.ofMinutes(10);
//	private static final Duration STEP = Duration.ofSeconds(5);

	// _______________________________________________
	// Member variables
	// -----------------------------------------------
	@Value("${spring.profiles.active:Unknown}")
	private String activeProfile;
	@Value("${spring.application.name}")
	private String applicationName;

	/**
	 * Register common tags application instead of job. This application tag is
	 * needed for Grafana dashboard.
	 *
	 * @return registry with registered tags.
	 * @throws UnknownHostException -> InetAddress.getLocalHost
	 */
	@Bean
	public MeterRegistryCustomizer<MeterRegistry> meterRegistryCustomizer() throws UnknownHostException {

		// Add service name, host and port to global tags.
		return registry -> registry.config()
		        // Tags that will be added to every metric.
		        .commonTags(
		                Arrays.asList(Tag.of("application", this.applicationName), Tag.of("stage", this.activeProfile)))
		        // Skipping not important endpoints, this will limit unwanted data.
		        .meterFilter(MeterFilter.deny(id -> {
			        String uri = id.getTag("uri");

			        return uri != null && uri.startsWith("/actuator");
		        })).meterFilter(MeterFilter.deny(id -> {
			        String uri = id.getTag("uri");

			        return uri != null && uri.startsWith("/swagger");
		        }))
		// (5) A list of percentiles that would be tracked.
		// (6) + (7) Histograms are calculated for some defined time window where more
		// recent values have bigger impact on final value. The bigger time window is
		// chosen, the more accurate statistics are, but the less sudden will be changes
		// of percentile value in case of very big or very small response time. It is
		// also very important to increase buffer length as you increase expiry time.
//		        .meterFilter(new MeterFilter() {
//			        @Override
//			        public DistributionStatisticConfig configure(final Meter.Id id,
//			                final DistributionStatisticConfig config) {
//				        return config.merge(DistributionStatisticConfig.builder().percentilesHistogram(true)
//				                .percentiles(0.5, 0.75, 0.95) // (5)
//				                .expiry(HISTOGRAM_EXPIRY) // (6)
//				                .bufferLength((int) (HISTOGRAM_EXPIRY.toMillis() / STEP.toMillis())) // (7)
//				                .build());
//			        }
//		        })
		;
	}

	@Bean
	public DataSourceStatusProbe dataSourceStatusProbe(@Autowired final DataSource dataSource) {
		return new DataSourceStatusProbe(dataSource);
	}

	/**
	 * Micrometer also provides {@code MeterFilter} which can be used to decide if
	 * one or multiple metrics will be added to MetricRegistry. So custom filters
	 * can be created with provided MeterFilter methods. The following MeterFilter
	 * bean implementation excludes all metrics starting with 'tomcat'.
	 * 
	 * @return a configured MeterFiler.
	 */
	@Bean
	public MeterFilter excludeTomcatFilter() {
		return MeterFilter.denyNameStartsWith("tomcat");
	}
}
