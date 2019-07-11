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
 * A data transfer object for items.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class ItemDTO implements Serializable {

    /**
     * The serial uid.
     */
    private static final long serialVersionUID = 8550193646664605709L;
    // The name of the item.
    private String name;
    // The price of the item.
    private Double price;
    
    /**
     * Creates a new instance <code>ItemDTO</code>.
     */
    public ItemDTO () {
        // Indicates a pojo.
    }

    // _______________________________________________
    // Getter, setter for attributes, should only be used during mapping.
    // -----------------------------------------------

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(final Double price) {
        this.price = price;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((price == null) ? 0 : price.hashCode());
        
        return result;
    }

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
        
        final ItemDTO other = (ItemDTO) obj;
        
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        
        if (price == null) {
            if (other.price != null) {
                return false;
            }
        } else if (!price.equals(other.price)) {
            return false;
        }
        
        return true;
    }

    @Override
    public String toString() {
        return "ItemDTO [name=" + name + ", price=" + price + "]";
    }
    
    // _______________________________________________
    // Implementation of the builder pattern as inner class.
    // -----------------------------------------------
    
    public static IName name(final String name) {
        return new ItemDTO.Builder(name);
    }
    
    public interface IName {
        IBuild price(Double price);
    }
    
    public interface IBuild {
        
        ItemDTO build();
    }   
    
    /**
     * The builder implementation for ItemDTO.
     */
    private static final class Builder
            implements IBuild, IName {

        private final ItemDTO instance = new ItemDTO();

        public Builder(final String name) {
            this.instance.name = name;
        }

        @Override
        public IBuild price(final Double price) {
            this.instance.price = price;
            return this;
        }
        
        @Override
        public ItemDTO build() {
            return this.instance;
        }
    }    
}
