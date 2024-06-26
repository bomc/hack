package de.bomc.poc.narayana;

import org.jboss.narayana.compensations.api.TxCompensate;

import javax.inject.Inject;

/**
 * @author paul.robinson@redhat.com 02/08/2013
 */
public class InvoicePrinter {

	private static final String LOG_PREFIX = "InvoicePrinter#";
	
	@Inject
	InvoiceData invoiceData;

	public static boolean hasInk = true;

	@TxCompensate(DestroyInvoice.class)
	public void print(Integer invoiceId, String invoiceBody) {
		System.out.println(LOG_PREFIX + "print [invoiceId=" + invoiceId + ", invoiceBody=" + invoiceBody + "]");
		
		if (!hasInk) {
			System.out.println(LOG_PREFIX + "print - Printer has run out of ink. Unable to print invoice, throw RuntimeException!");
			
			throw new RuntimeException("Printer has run out of ink. Unable to print invoice");
		}

		invoiceData.setInvoiceBody(invoiceBody);
		invoiceData.setInvoiceId(invoiceId);

		System.out.println(LOG_PREFIX + "print - Printing the invoice ready for posting... " + this.invoiceData);
	}

}
