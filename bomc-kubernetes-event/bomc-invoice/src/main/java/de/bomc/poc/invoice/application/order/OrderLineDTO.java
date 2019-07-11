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
package de.bomc.poc.invoice.application.order;

import java.io.Serializable;

/**
 * A data transfer object for orderlines.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class OrderLineDTO implements Serializable {

    /**
     * The serial uid.
     */
    private static final long serialVersionUID = -4700914004808988939L;

    private Integer quantity;
    private ItemDTO item;

    /**
     * Creates a new instance <code>OrderLineDTO</code>.
     */
    public OrderLineDTO() {
        // Indicates a pojo.
    }

    // _______________________________________________
    // Getter, setter for attributes, should only be used during mapping.
    // -----------------------------------------------

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(final Integer quantity) {
        this.quantity = quantity;
    }

    public ItemDTO getItem() {
        return item;
    }

    public void setItem(final ItemDTO item) {
        this.item = item;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((item == null) ? 0 : item.hashCode());
        result = prime * result + ((quantity == null) ? 0 : quantity.hashCode());

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final OrderLineDTO other = (OrderLineDTO) obj;

        if (item == null) {
            if (other.item != null) {
                return false;
            }
        } else if (!item.equals(other.item)) {
            return false;
        }
        if (quantity == null) {
            if (other.quantity != null) {
                return false;
            }
        } else if (!quantity.equals(other.quantity)) {
            return false;
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "OrderLineDTO [quantity=" + quantity + ", item=" + item + "]";
    }

    // _______________________________________________
    // Implementation of the builder pattern as inner class.
    // -----------------------------------------------

    public static IQuantity quantity(final Integer quantity) {
        return new OrderLineDTO.Builder(quantity);
    }

    public interface IQuantity {
        IBuild item(ItemDTO item);
    }

    public interface IBuild {
        OrderLineDTO build();
    }

    /**
     * The builder implementation for OrderLineDTO.
     */
    private static final class Builder implements IBuild, IQuantity {

        private final OrderLineDTO instance = new OrderLineDTO();

        public Builder(final Integer quantity) {
            this.instance.quantity = quantity;
        }

        @Override
        public IBuild item(final ItemDTO item) {
            this.instance.item = item;

            return this;
        }

        @Override
        public OrderLineDTO build() {
            return this.instance;
        }

    }
}