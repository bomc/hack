package de.bomc.poc.narayana;

import org.jboss.narayana.compensations.api.TxCompensate;
import org.jboss.narayana.compensations.api.TxLogged;

import javax.inject.Inject;

/**
 * @author paul.robinson@redhat.com 02/08/2013
 */
public class PackageDispatcher {

	private static final String LOG_PREFIX = "PackageDispatcher#";

	@Inject
	OrderData orderData;

	@TxCompensate(RecallPackage.class)
//	@TxLogged(LoggedHandler.class)
	public void dispatch(final String item, final String address) {
		System.out.println(LOG_PREFIX + "dispatch");
		
		orderData.setAddress(address);
		orderData.setItem(item);
		// Dispatch the package
		System.out.println(LOG_PREFIX + "dispatch - Dispatching the package... " + this.orderData);
	}

}
