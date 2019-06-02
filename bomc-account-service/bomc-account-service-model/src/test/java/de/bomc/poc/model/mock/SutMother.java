package de.bomc.poc.model.mock;

import de.bomc.poc.model.test.EntityManagerProvider;

/**
 * Subject under test mother.
 * @author <a href="mailto:bomc@bomc.org">Michael Börner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 10.08.2016
 */
public class SutMother extends SingleAlternateKeyObjectMother<Sut, String, SutMother> {

    /**
     * Creates a new <code>SutMother</code>.
     * @param entityManagerProvider the given entityManagerProvider
     */
    public SutMother(final EntityManagerProvider entityManagerProvider, final String attributeAValue) {
        super(entityManagerProvider, Sut.class, attributeAValue, "attributeA");
    }

    @Override
    protected void configureInstance(final Sut sut) {
        // Fill here instance with default data.
        sut.setAttributeB(SutObjectMotherTest.ATTRIBUTE_B_VALUE);
        sut.setAttributeC(SutObjectMotherTest.ATTRIBUTE_C_VALUE);
    }

    @Override
    protected Sut createInstance() {
        final Sut sut = new Sut(super.getAlternateKey());
        // or if no constructor is available.
        // ___________________________________________
        // final Sut sut = new Sut();
        // sut.setAttributeA(super.getAlternateKey());

        return sut;
    }
} // end SutMother
