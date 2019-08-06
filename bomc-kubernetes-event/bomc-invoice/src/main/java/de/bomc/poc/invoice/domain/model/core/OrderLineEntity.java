/**
 * Project: bomc-invoice
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
package de.bomc.poc.invoice.domain.model.core;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Min;

import de.bomc.poc.invoice.domain.model.basis.AbstractEntity;

/**
 * This entity represents a orderline in the shop context.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Entity
@Table(name = "T_ORDER_LINE")
@NamedQueries({
        @NamedQuery(name = OrderLineEntity.NQ_FIND_BY_LATEST_MODIFIED_DATE_TIME_ORDERLINE, query = "select max(o.modifyDateTime) from OrderLineEntity o") })
public class OrderLineEntity extends AbstractEntity<OrderLineEntity> implements Serializable {

    /**
     * The serial UID.
     */
    private static final long serialVersionUID = 7786102972192575991L;
    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(OrderLineEntity.class.getName());
    /**
     * A log prefix.
     */
    private static final String LOG_PREFIX = "OrderLineEntity#";

    /* --------------------- constants ------------------------------ */
    /**
     * The default prefix String for each created
     * <code>OrderLineEntity</code>-NamedQuery.
     */
    protected static final String NQ_PREFIX = "ORDERLINE.";
    /**
     * <pre>
     * Query to find latest modified date.
     * </pre>
     */
    public static final String NQ_FIND_BY_LATEST_MODIFIED_DATE_TIME_ORDERLINE = NQ_PREFIX
            + "findByLatestModifiedDateTime";

    /* --------------------- columns -------------------------------- */
    @Min(1)
    @Column(name = "C_QUANTITY", nullable = false)
    private Integer quantity;

    /* --------------------- associations --------------------------- */
    /**
     * The OrderLineEntity represents the many side of the relationship and
     * contains the foreign key of the ItemEntity.
     */
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "C_FK_ID", referencedColumnName = "C_ID", nullable = false)
    private ItemEntity item;

    /* --------------------- constructors --------------------------- */
    /**
     * Creates a new instance <code>OrderLineEntity</code> (default#co).
     */
    public OrderLineEntity() {
        LOGGER.log(Level.INFO, LOG_PREFIX + "co");

        // Used by Jpa-Provider.
    }

    /**
     * Creates a new instance of <code>OrderLineEntity</code>
     * 
     * @param quantity
     *            quantity of items.
     * @param item
     *            the given item.
     */
    public OrderLineEntity(final Integer quantity, final ItemEntity item) {
        this.quantity = quantity;
        this.item = item;
    }

    /* ----------------------------- methods ------------------------- */
    /**
     * @return the type of this entity.
     */
    @Override
    protected Class<OrderLineEntity> getEntityClass() {
        return OrderLineEntity.class;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(final Integer quantity) {
        this.quantity = quantity;
    }

    public ItemEntity getItem() {
        return this.item;
    }

    public void setItem(final ItemEntity item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "OrderLineEntity [id=" + super.getId() + ", quantity=" + quantity + ", item=" + item + "]";
    }

}
