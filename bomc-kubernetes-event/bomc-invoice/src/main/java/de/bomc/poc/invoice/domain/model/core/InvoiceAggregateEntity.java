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
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import de.bomc.poc.invoice.domain.model.basis.AbstractEntity;

/**
 * This aggregate entity represents the context of a invoice.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Entity
@Table(name = "T_INVOICE_AGGREGATE_ENTITY")
public class InvoiceAggregateEntity extends AbstractEntity<InvoiceAggregateEntity> implements Serializable {
	
	/**
     * The serial UID
     */
	private static final long serialVersionUID = -4622045178496072580L;
    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(InvoiceAggregateEntity.class.getName());
    /**
     * A log prefix.
     */
    private static final String LOG_PREFIX = "InvoiceAggrgateEntity#";

    /* --------------------- constants ------------------------------ */
    /**
     * The default prefix String for each created
     * <code>InvoiceAggrgateEntity</code>-NamedQuery.
     */
    protected static final String NQ_PREFIX = "INVOICE_AGGREGATE.";

    /* --------------------- columns -------------------------------- */

    @Column(name = "C_ORDER_ID", nullable = false)
    private Long orderId;

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

    /* --------------------- associations --------------------------- */
    @ManyToOne(cascade = { CascadeType.PERSIST })
    private CustomerEntity customer;
    
    @OrderColumn(name = "quantity")
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, orphanRemoval = true)
    private Set<OrderLineEntity> orderLineSet = new HashSet<OrderLineEntity>();

    /* --------------------- constructors --------------------------- */
    public InvoiceAggregateEntity() {
        LOGGER.log(Level.INFO, LOG_PREFIX + "co");

        // Used by Jpa-Provider.
    }

    /* ----------------------------- methods ------------------------- */
    /**
     * @return the type of this entity.
     */
    @Override
    protected Class<InvoiceAggregateEntity> getEntityClass() {
        return InvoiceAggregateEntity.class;
    }
    
    public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	
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
        return "InvoiceAggrgateEntity [shippingAddress=" + this.shippingAddress + ", billingAddress=" + this.billingAddress
                + ", customer=" + this.customer + ", orderLine.size=" + this.orderLineSet.size() + "]";
    }
}
