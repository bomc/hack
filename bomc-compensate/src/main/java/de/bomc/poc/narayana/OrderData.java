package de.bomc.poc.narayana;

import org.jboss.narayana.compensations.api.CompensationScoped;

import java.io.Serializable;

/**
 * @author paul.robinson@redhat.com 02/08/2013
 */
@CompensationScoped
public class OrderData implements Serializable {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = -1750583091117724600L;

	private String item;
	private String address;

	public String getItem() {

		return item;
	}

	public void setItem(String item) {

		this.item = item;
	}

	public String getAddress() {

		return address;
	}

	public void setAddress(String address) {

		this.address = address;
	}
}
