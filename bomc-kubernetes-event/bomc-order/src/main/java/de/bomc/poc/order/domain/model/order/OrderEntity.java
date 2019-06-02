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
package de.bomc.poc.order.domain.model.order;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.apache.log4j.Logger;

import de.bomc.poc.order.domain.model.basis.AbstractEntity;
import de.bomc.poc.order.domain.model.customer.CustomerEntity;
import de.bomc.poc.order.domain.model.item.ItemEntity;

/**
 * This entity represents a order in the shop context.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Entity
@Table(name = "T_ORDER")
@NamedQueries({
        @NamedQuery(name = OrderEntity.NQ_FIND_BY_LATEST_MODIFIED_DATE_TIME_ORDER, query = "select max(o.modifyDateTime) from OrderEntity o"),
        @NamedQuery(name = OrderEntity.NQ_FIND_ALL_OLDER_THAN_GIVEN_DATE, query = "select o from OrderEntity o where o.createDateTime < :createDateTime or o.modifyDateTime < :modifyDateTime") })
public class OrderEntity extends AbstractEntity<OrderEntity> implements Serializable {

    /**
     * The serial UID.
     */
    private static final long serialVersionUID = -972104553916007680L;
    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(OrderEntity.class);
    /**
     * A log prefix.
     */
    private static final String LOG_PREFIX = "OrderEntity#";

    /* --------------------- constants ------------------------------ */
    /**
     * The default prefix String for each created
     * <code>Customer</code>-NamedQuery.
     */
    protected static final String NQ_PREFIX = "ORDER.";
    /**
     * <pre>
     * Query to find latest modified date.
     * </pre>
     */
    public static final String NQ_FIND_BY_LATEST_MODIFIED_DATE_TIME_ORDER = NQ_PREFIX + "findByLatestModifiedDateTime";
    /**
     * <pre>
     * Query to find latest modified date.
     * </pre>
     */
    public static final String NQ_FIND_ALL_OLDER_THAN_GIVEN_DATE = NQ_PREFIX + "findByAllOlderThanGivenDate";
    
    /* --------------------- embeddables ---------------------------- */
    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "street", column = @Column(name = "T_SHIPPING_STREET")),
            @AttributeOverride(name = "zip", column = @Column(name = "T_SHIPPING_ZIP")),
            @AttributeOverride(name = "city", column = @Column(name = "T_SHIPPING_CITY")) })
    private AddressEntity shippingAddress = new AddressEntity();

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "street", column = @Column(name = "T_BILLING_STREET")),
            @AttributeOverride(name = "zip", column = @Column(name = "T_BILLING_ZIP")),
            @AttributeOverride(name = "city", column = @Column(name = "T_BILLING_CITY")) })
    private AddressEntity billingAddress = new AddressEntity();

    /* --------------------- columns -------------------------------- */

    // private String deliveryService;

    /* --------------------- associations --------------------------- */
    @ManyToOne(cascade = { CascadeType.PERSIST })
    private CustomerEntity customer;

    @OrderColumn(name = "quantity")
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, orphanRemoval = true)
    private Set<OrderLineEntity> orderLineSet = new HashSet<OrderLineEntity>();

    /* --------------------- constructors --------------------------- */
    /**
     * Creates a new instance of <code>OrderEntity</code> (default#co).
     */
    public OrderEntity() {
        LOGGER.debug(LOG_PREFIX + "co");

        // Used by Jpa-Provider.
    }

    /* --------------------- methods ------------------------- */
    /**
     * @return the type of this entity.
     */
    @Override
    protected Class<OrderEntity> getEntityClass() {
        return OrderEntity.class;
    }

    // public String getDeliveryService() {
    // return this.deliveryService;
    // }
    //
    // public void setDeliveryService(final String deliveryService) {
    // this.deliveryService = deliveryService;
    // }

    public AddressEntity getShippingAddress() {
        return this.shippingAddress;
    }

    public void setShippingAddress(final AddressEntity shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public AddressEntity getBillingAddress() {
        return this.billingAddress;
    }

    public void setBillingAddress(final AddressEntity billingAddress) {
        this.billingAddress = billingAddress;
    }

    public CustomerEntity getCustomer() {
        return this.customer;
    }

    public void setCustomer(final CustomerEntity customer) {
        this.customer = customer;
    }

    public Set<OrderLineEntity> getOrderLineSet() {
        return this.orderLineSet;
    }

    public void addLine(final int count, final ItemEntity item, final String createUser) {
        final OrderLineEntity orderLineEntity = new OrderLineEntity(count, item);
        orderLineEntity.setCreateUser(createUser);

        this.orderLineSet.add(orderLineEntity);
    }

    public int getNumberOfLines() {
        return this.orderLineSet.size();
    }

    /* ----------------------------- business methods --------------- */

    public double totalPrice() {
        return this.orderLineSet.stream().map((ol) -> ol.getQuantity() * ol.getItem().getPrice()).reduce(0.0,
                (d1, d2) -> d1 + d2);
    }

    @Override
    public String toString() {
        return "OrderEntity [shippingAddress=" + this.shippingAddress + ", billingAddress=" + this.billingAddress
                + ", customer=" + this.customer + ", orderLine.size=" + this.orderLineSet.size() + "]";
    }

}
