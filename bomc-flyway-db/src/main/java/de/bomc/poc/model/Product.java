/**
 * Project: bomc-flyway-db
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
 * Copyright (c): BOMC, 2017
 */
package de.bomc.poc.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.log4j.Logger;

/**
 * A representation for a product.
 *
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Entity
@Table(name = "T_PRODUCT")
public class Product implements Serializable {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = -5987526988948552439L;
	/**
	 * The logger.
	 */
	private static final String LOG_PREFIX = "Product#";
	private static final Logger LOGGER = Logger.getLogger(Product.class);
	/**
	 * Unique technical key.
	 */
	@Id
	@Column(name = "C_ID", unique = true, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * Version field.
	 */
	@Version
	@Column(name = "C_VERSION")
	private Long version;
	/**
	 * The name of the product.
	 */
	@Column(name = "C_PRODUCT_NAME")
	private String productName;

	/**
	 * Creates a new instance of <code>Product</code>.
	 * 
	 */
	public Product() {
		//
	}

	/**
	 * @return the technical id.
	 */
	public Long getId() {
		LOGGER.debug(LOG_PREFIX + "getId [id=" + this.id + "]");

		return this.id;
	}

	/**
	 * Set the technical id.
	 * 
	 * @param id
	 *            the given technical id.
	 */
	public void setId(final Long id) {
		LOGGER.debug(LOG_PREFIX + "setId [id=" + id + "]");

		this.id = id;
	}

	/**
	 * @return the version (optimistic locking).
	 */
	public Long getVersion() {
		LOGGER.debug(LOG_PREFIX + "getVersion [version=" + this.version + "]");

		return this.version;
	}

	/**
	 * @return the productName.
	 */
	public String getProductName() {
		LOGGER.debug(LOG_PREFIX + "getProductName [productName=" + productName + "]");

		return productName;
	}

	/**
	 * @param productName
	 *            the productName to set.
	 */
	public void setProductName(String productName) {
		LOGGER.debug(LOG_PREFIX + "setProductName [productName=" + this.productName + "]");

		this.productName = productName;
	}

	/**
	 * Compare the id property field.
	 * 
	 * @see java.lang.Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object o) {
		LOGGER.debug(LOG_PREFIX + "equals");

		return (o == this || (o instanceof Product && this.getId().equals(((Product) o).getId())));
	}

	/**
	 * Use the id to calculate the hashCode.
	 * 
	 * @see java.lang.String#hashCode()
	 */
	@Override
	public int hashCode() {
		LOGGER.debug(LOG_PREFIX + "hashCode");

		return this.getId().hashCode();
	}

}
