/**
 * Project: bomc-onion-architecture
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
package de.bomc.poc.order.domain.model.item;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.log4j.Logger;

import de.bomc.poc.order.domain.model.basis.AbstractEntity;

/**
 * This entity represents an item in the shop context.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Entity
@Table(name = "T_ITEM")
@NamedQueries({
        @NamedQuery(name = ItemEntity.NQ_FIND_BY_LATEST_MODIFIED_DATE_TIME_ITEM, query = "select max(i.modifyDateTime) from ItemEntity i"),
        @NamedQuery(name = ItemEntity.NQ_FIND_BY_ITEM_NAME, query = "select i from ItemEntity i where i.name = :name") })
public class ItemEntity extends AbstractEntity<ItemEntity> implements Serializable {

    /**
     * The serial UID.
     */
    private static final long serialVersionUID = -6489159317065062138L;
    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ItemEntity.class);
    /**
     * A log prefix.
     */
    private static final String LOG_PREFIX = "ItemEntity#";

    /* --------------------- constants ------------------------------ */
    /**
     * The default prefix String for each created
     * <code>ItemEntity</code>-NamedQuery.
     */
    protected static final String NQ_PREFIX = "ITEM.";
    /**
     * <pre>
     * Query to find latest modified date.
     * </pre>
     */
    public static final String NQ_FIND_BY_LATEST_MODIFIED_DATE_TIME_ITEM = NQ_PREFIX + "findByLatestModifiedDateTime";
    /**
     * Query to find <strong>one</strong> <code>ItemEntity</code> by the name.
     * <li>Query parameter name <strong>name</strong> : The name of the
     * <code>ItemEntity</code> to search for.</li> Name is {@value} .
     */
    public static final String NQ_FIND_BY_ITEM_NAME = NQ_PREFIX + "findName";

    /* --------------------- columns -------------------------------- */
    @Column(name = "C_NAME", unique = true, length = 32, nullable = false)
    private String name;

    @Column(name = "C_PRICE", nullable = false)
    private Double price;

    /* --------------------- associations --------------------------- */

    /* --------------------- constructors --------------------------- */
    /**
     * Creates a new instance <code>ItemEntity</code> (Default#co).
     */
    public ItemEntity() {
        LOGGER.debug(LOG_PREFIX + "co");

        // Used by Jpa-Provider.
    }

    /**
     * Creates a new instance of <code>ItemEntity</code>
     * 
     * @param name
     *            the name of the item.
     * @param price
     *            the price of the item.
     */
    public ItemEntity(final String name, final Double price) {
        this.name = name;
        this.price = price;
    }

    /* ----------------------------- methods ------------------------- */
    /**
     * @return the type of this entity.
     */
    @Override
    protected Class<ItemEntity> getEntityClass() {
        return ItemEntity.class;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(final Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "ItemEntity [id=" + super.getId() + ", name=" + name + ", price=" + price + "]";
    }
}
