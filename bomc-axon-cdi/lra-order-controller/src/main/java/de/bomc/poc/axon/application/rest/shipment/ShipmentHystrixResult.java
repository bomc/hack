/**
 * Project: cdi-axon
 * <pre>
 *
 * Last change:
 *
 *  by:       $Author$
 *
 *  date:     $Date$
 *
 *  revision: $Revision$
 *
 *  © Bomc 2018
 *
 * </pre>
 */
package de.bomc.poc.axon.application.rest.shipment;

/**
 * The result for the invoice request.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 01.02.2018
 */
public class ShipmentHystrixResult {

	private String status;
	private boolean successful;

	/**
	 * Creates a new instance of <code>InvoiceHystrixResult</code>.
	 * 
	 * @param status
	 *            the given status.
	 * @param successful
	 */
	public ShipmentHystrixResult(final String status, final boolean successful) {
		this.status = status;
		this.successful = successful;
	}

	public String getStatus() {
		return this.status;
	}

	public boolean isSuccessful() {
		return this.successful;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + (successful ? 1231 : 1237);

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		final ShipmentHystrixResult other = (ShipmentHystrixResult) obj;

		if (this.status == null) {
			if (other.status != null) {
				return false;
			}
		} else if (!this.status.equals(other.status)) {
			return false;
		}

		if (successful != other.successful) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "InvoiceHystrixResult [status=" + status + ", successful=" + successful + "]";
	}
}