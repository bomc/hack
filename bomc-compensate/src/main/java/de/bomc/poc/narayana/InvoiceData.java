package de.bomc.poc.narayana;

import org.jboss.narayana.compensations.api.CompensationScoped;

import java.io.Serializable;

/**
 * @author paul.robinson@redhat.com 02/08/2013
 */
@CompensationScoped
public class InvoiceData implements Serializable {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 3331486873814228948L;
	
	private Integer invoiceId;
	private String invoiceBody;

	public Integer getInvoiceId() {

		return invoiceId;
	}

	public void setInvoiceId(Integer invoiceId) {

		this.invoiceId = invoiceId;
	}

	public String getInvoiceBody() {

		return invoiceBody;
	}

	public void setInvoiceBody(String invoiceBody) {

		this.invoiceBody = invoiceBody;
	}

	@Override
	public String toString() {
		return "InvoiceData [invoiceId=" + invoiceId + ", invoiceBody=" + invoiceBody + "]";
	}
	
}
