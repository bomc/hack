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
package de.bomc.poc.invoice.application.controller;

import java.util.List;

import de.bomc.poc.invoice.application.order.OrderDTO;

/**
 * This controller handles the invoice domain.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public interface InvoiceComposition {

	/**
	 * Create the invoices to the transfered order list.
	 *  
	 * @param oderDtoList the given order list.
	 * @param userId the 
	 */
	void createInvoice(List<OrderDTO> oderDtoList, String userId);
}
