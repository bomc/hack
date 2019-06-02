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
package de.bomc.poc.axon.domain.model.shared;

import java.lang.invoke.MethodHandles;

import org.apache.log4j.Logger;

/**
 * This class represents a product.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 23.10.2018
 */
public class ProductInfo {

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String LOG_PREFIX = "ProductInfo#";
	private String productId;
	private String productName;
	private Integer price;

	/**
	 * Creates a new instance of <code>ProductInfo</code>.
	 * 
	 * @param productId
	 *            the given productId.
	 * @param productName
	 *            the given productName.
	 * @param price
	 *            the given price.
	 */
	public ProductInfo(final String productId, final String productName, final Integer price) {
		LOGGER.debug(LOG_PREFIX + "co [productId=" + productId + ", comment=" + productName + ", price=" + price + "]");

		this.productId = productId;
		this.productName = productName;
		this.price = price;
	}

	public String getProductId() {
		return this.productId;
	}

	public void setProductId(final String productId) {
		this.productId = productId;
	}

	public String getComment() {
		return this.productName;
	}

	public void setComment(final String comment) {
		this.productName = comment;
	}

	public Integer getPrice() {
		return this.price;
	}

	public void setPrice(final Integer price) {
		this.price = price;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((productName == null) ? 0 : productName.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((productId == null) ? 0 : productId.hashCode());

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

		final ProductInfo other = (ProductInfo) obj;

		if (productName == null) {
			if (other.productName != null) {
				return false;
			}
		} else if (!productName.equals(other.productName)) {
			return false;
		}

		if (price == null) {
			if (other.price != null) {
				return false;
			}
		} else if (!price.equals(other.price)) {
			return false;
		}

		if (productId == null) {
			if (other.productId != null) {
				return false;
			}
		} else if (!productId.equals(other.productId)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "ProductInfo [productId=" + productId + ", comment=" + productName + ", price=" + price + "]";
	}
}
