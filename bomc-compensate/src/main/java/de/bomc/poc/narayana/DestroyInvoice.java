package de.bomc.poc.narayana;

import org.jboss.narayana.compensations.api.CompensationHandler;

import javax.inject.Inject;

/**
 * @author paul.robinson@redhat.com 02/08/2013
 */
public class DestroyInvoice implements CompensationHandler {

	private static final String LOG_PREFIX = "DestroyInvoice#";

	@Inject
	InvoiceData invoiceData;

	@Override
	public void compensate() {
		// Recall the package somehow
		System.out.println(LOG_PREFIX + "compensate - Hunt down invoice with id '" + invoiceData.getInvoiceId()
				+ "' and destroy it...");
	}
}
