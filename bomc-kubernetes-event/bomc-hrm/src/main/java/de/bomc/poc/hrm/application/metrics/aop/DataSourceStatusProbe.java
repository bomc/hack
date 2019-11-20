/**
 * Project: hrm
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: micha
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 */
package de.bomc.poc.hrm.application.metrics.aop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

import javax.sql.DataSource;

import de.bomc.poc.hrm.application.exception.AppErrorCodeEnum;
import de.bomc.poc.hrm.application.exception.AppRuntimeException;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.extern.slf4j.Slf4j;

/**
 * @formatter:off
 * DataSourceStatusProbe is implementing specific logic for collecting
 * dataSource status metrics via MeterBinde bindTo() method and depending on
 * dataSource status () method. MeterBinders register one or more metrics to
 * provide informations about the state of the application.
 * 
 * see: https://www.atlantbh.com/blog/custom_metrics_micrometer_prometheus_spring_boot_actuator/
 * @formatter:on
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 20.11.2019
 */
@Slf4j
public class DataSourceStatusProbe implements MeterBinder {

	private static final String LOG_PREFIX = "DataSourceStatusProbe#";
	
	private final String name;
	private final String description;
	private final Iterable<Tag> tags;
	private static final String SELECT_1 = "SELECT 1;";
	private static final int QUERY_TIMEOUT_IN_SECONDS = 1;
	private static final double UP = 1.0;
	private static final double DOWN = 0.0;
	private final DataSource dataSource;

	/**
	 * Creates a new instance of <code>DataSourceStatusProbe</code>.
	 * 
	 * @param dataSource the dataSource to monitor.
	 */
	public DataSourceStatusProbe(final DataSource dataSource) {

		Objects.requireNonNull(dataSource, "dataSource cannot be null");

		this.dataSource = dataSource;
		this.name = "bomc_hrm_data_source";
		this.description = "DataSource status";
		this.tags = tags(dataSource);
	}

	private boolean status() {

		try (final Connection connection = dataSource.getConnection()) {
			final PreparedStatement statement = connection.prepareStatement(SELECT_1);
			statement.setQueryTimeout(QUERY_TIMEOUT_IN_SECONDS);
			statement.executeQuery();

			return true;
		} catch (final SQLException ignored) {
			return false;
		}
	}

	@Override
	public void bindTo(final MeterRegistry meterRegistry) {
		Gauge.builder(name, this, value -> value.status() ? UP : DOWN).description(description).tags(tags)
		        .baseUnit("status").register(meterRegistry);
	}

	protected static Iterable<Tag> tags(final DataSource dataSource) {

		Objects.requireNonNull(dataSource, "dataSource cannot be null");

		try {
			return Tags.of(Tag.of("url", dataSource.getConnection().getMetaData().getURL()));
		} catch (final SQLException sqlException) {
			String exceptionMessage = "";
			
			if(sqlException != null) {
				exceptionMessage = sqlException.getMessage();
			}
			
			final String errMsg = LOG_PREFIX + "#co#tags - [errMsg=" + exceptionMessage + "]"; 
			final AppRuntimeException appRuntimeException = new AppRuntimeException(errMsg, AppErrorCodeEnum.IO_DATASOURCE_CONNECTION_IS_BROKEN);
			
			log.error(appRuntimeException.stackTraceToString());
			
			throw appRuntimeException;
		}
	}
}
