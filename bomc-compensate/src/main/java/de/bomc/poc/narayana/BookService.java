package de.bomc.poc.narayana;

import org.jboss.narayana.compensations.api.Compensatable;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author paul.robinson@redhat.com 02/08/2013
 */
public class BookService {

	private static final String LOG_PREFIX = "BookService#";

	@Inject
	PackageDispatcher packageDispatcher;

	@Inject
	InvoicePrinter invoicePrinter;

	private static AtomicInteger orderId = new AtomicInteger(0);

	@Compensatable
	public void buyBook(final String item, final String address) {
		System.out.println(LOG_PREFIX + "buyBook [item=" + item + ", address=" + address + "]");

		packageDispatcher.dispatch(item, address);
		invoicePrinter.print(orderId.getAndIncrement(), "Invoice body would go here, blah blah blah");

	}

}
