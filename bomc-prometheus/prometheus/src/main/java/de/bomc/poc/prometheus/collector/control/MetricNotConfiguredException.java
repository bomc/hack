package de.bomc.poc.prometheus.collector.control;

import javax.ejb.ApplicationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * This exception is thrown if metric is not configured.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 02.06.2017
 */
@ApplicationException(rollback = true)
public class MetricNotConfiguredException extends WebApplicationException {

	/**
	 * The serial UID
	 */
	private static final long serialVersionUID = 2453127987650418046L;

	/**
	 * Creates a new instance of <code>MetricNotConfiguredException</code> co.
	 * 
	 * @param metricName
	 *            the not configured metric name.
	 */
	public MetricNotConfiguredException(final String metricName) {
		super("MetricNotConfiguredException#co - Configuration for " + metricName + " not found!",
				Response.Status.INTERNAL_SERVER_ERROR);
	}

}
