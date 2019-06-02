/**
 * Project: cdi-axon
 * <pre>
 *
 * Last change:
 *
 *  by:       $Author$
 *
 *  date:     $Date$
 *
 *  revision: $Revision$
 *
 *  © Bomc 2018
 *
 * </pre>
 */
package de.bomc.poc.axon.infrastructure.events.order;

import org.apache.log4j.Logger;

import de.bomc.poc.axon.domain.model.shared.ProductInfo;

import java.lang.invoke.MethodHandles;

/**
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 23.10.2018
 */
public class OrderCreateEvent {

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String LOG_PREFIX = "OrderCreateEvent#";
	private String orderId;
	private ProductInfo productInfo;

	public OrderCreateEvent() {
		// Used by CDI.
	}

	/**
	 * @param orderId
	 * @param productInfo
	 */
	public OrderCreateEvent(final String orderId, final ProductInfo productInfo) {
		LOGGER.debug(LOG_PREFIX + "co - [orderId=" + orderId + ", productInfo=" + productInfo + "]");

		this.orderId = orderId;
		this.productInfo = productInfo;
	}

	public String getOrderId() {
		return this.orderId;
	}

	public void setOrderId(final String orderId) {
		this.orderId = orderId;
	}

	public ProductInfo getProductInfo() {
		return this.productInfo;
	}

	public void setProductInfo(final ProductInfo productInfo) {
		this.productInfo = productInfo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((orderId == null) ? 0 : orderId.hashCode());
		result = prime * result + ((productInfo == null) ? 0 : productInfo.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		final OrderCreateEvent other = (OrderCreateEvent) obj;

		if (orderId == null) {
			if (other.orderId != null) {
				return false;
			}
		} else if (!orderId.equals(other.orderId)) {
			return false;
		}

		if (productInfo == null) {
			if (other.productInfo != null) {
				return false;
			}
		} else if (!productInfo.equals(other.productInfo)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "OrderCreateEvent [orderId=" + this.orderId + ", productInfo=" + this.productInfo + "]";
	}
}
