package de.bomc.poc.model.test;

import de.bomc.poc.model.account.Address;
import de.bomc.poc.model.mock.SingleAlternateKeyObjectMother;

/**
 * A pattern for creating test instances by the object mother pattern.
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 10.08.2016
 */
public class AddressMother extends SingleAlternateKeyObjectMother<Address, String, AddressMother> {

	private static final String ALTERNATE_ATTRIBUTE_VALUE = "city";
	
	/**
	 * Creates a new <code>AddressMother</code>.
	 * 
	 * @param entityManagerProvider
	 *            the given entityManagerProvider
	 * @param address
	 *            the given type.
	 * @param alternateValue
	 *            the alternate value.
	 */
	public AddressMother(final EntityManagerProvider entityManagerProvider, final Class<Address> address,
			final String alternateValue) {
		super(entityManagerProvider, Address.class, alternateValue, ALTERNATE_ATTRIBUTE_VALUE);
	}

	@Override
	protected void configureInstance(final Address address) {
		// Fill here instance with default data.
		address.setCountry(AbstractUnitTest.ADDRESS_COUNTRY);
		address.setStreet(AbstractUnitTest.ADDRESS_STREET);
		address.setZipCode(AbstractUnitTest.ADDRESS_ZIP_CODE);
	}

	@Override
    protected Address createInstance() {
        final Address address = new Address();
        address.setCity(super.getAlternateKey());

        return address;
    }
}