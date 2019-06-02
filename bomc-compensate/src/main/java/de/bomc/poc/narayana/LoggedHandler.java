package de.bomc.poc.narayana;

import org.jboss.narayana.compensations.api.TransactionLoggedHandler;

public class LoggedHandler implements TransactionLoggedHandler {

	private static final String LOG_PREFIX = "LoggedHandler#";
	
	@Override
	public void transactionLogged(boolean success) {
		System.out.println(LOG_PREFIX + "transactionLogged [success=" + success + "]");
		
	}

}
