package de.bomc.poc.model.mock;

import de.bomc.poc.model.test.AbstractUnitTest;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the object mother instances.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 10.08.2016
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SutObjectMotherTest extends AbstractUnitTest {

    private static final Logger LOGGER = Logger.getLogger(ObjectMother.class);
    private static final String LOG_PREFIX = "SutObjectMotherTest#";
    protected static final String ATTRIBUTE_A_VALUE = "attributeA";
    protected static final String ATTRIBUTE_B_VALUE = "attributeB";
    protected static final String ATTRIBUTE_C_VALUE = "attributeC";
    protected static final String ATTRIBUTE_NEW_VALUE = "attributeNew";

    @Test
    public void test010_createSimpleObjectInDb() {
        LOGGER.debug(LOG_PREFIX + "test010_createSimpleObjectInDb");

        final SutMother sutMother = new SutMother(this.emProvider, ATTRIBUTE_A_VALUE);
        final Sut sut = sutMother.instance();

        assertThat(sut, is(notNullValue()));
        assertThat(sut.getId(), is(notNullValue()));
        assertThat(sut.getAttributeA(), is(equalTo(ATTRIBUTE_A_VALUE)));
        assertThat(sut.getAttributeB(), is(equalTo(ATTRIBUTE_B_VALUE)));
        assertThat(sut.getAttributeC(), is(equalTo(ATTRIBUTE_C_VALUE)));
    }

    @Test
    public void test020_createMultipleObjectInDb() {
        LOGGER.debug(LOG_PREFIX + "test020_createMultipleObjectInDb");

        final SutMother sutMotherA = new SutMother(this.emProvider, ATTRIBUTE_A_VALUE);
        final Sut sutA = sutMotherA.instance();

        assertThat(sutA, is(notNullValue()));
        assertThat(sutA.getId(), is(notNullValue()));
        assertThat(sutA.getAttributeA(), is(equalTo(ATTRIBUTE_A_VALUE)));
        assertThat(sutA.getAttributeB(), is(equalTo(ATTRIBUTE_B_VALUE)));
        assertThat(sutA.getAttributeC(), is(equalTo(ATTRIBUTE_C_VALUE)));

        // Create an other one...
        final SutMother sutMotherNew = new SutMother(this.emProvider, ATTRIBUTE_NEW_VALUE);
        final Sut sutNew = sutMotherNew.instance();

        assertThat(sutNew, is(notNullValue()));
        assertThat(sutNew.getId(), is(notNullValue()));
        assertThat(sutNew.getAttributeA(), is(equalTo(ATTRIBUTE_NEW_VALUE)));
        assertThat(sutNew.getAttributeB(), is(equalTo(ATTRIBUTE_B_VALUE)));
        assertThat(sutNew.getAttributeC(), is(equalTo(ATTRIBUTE_C_VALUE)));

        assertThat(sutA.getId(), not(equalTo(sutNew.getId())));
    }

    @Test
    public void test030_createMultipleSameObjectInDb() {
        LOGGER.debug(LOG_PREFIX + "test030_createMultipleSameObjectInDb");

        final SutMother sutMotherA = new SutMother(this.emProvider, ATTRIBUTE_A_VALUE);
        final Sut sutA = sutMotherA.instance();

        assertThat(sutA, is(notNullValue()));
        assertThat(sutA.getId(), is(notNullValue()));
        assertThat(sutA.getAttributeA(), is(equalTo(ATTRIBUTE_A_VALUE)));
        assertThat(sutA.getAttributeB(), is(equalTo(ATTRIBUTE_B_VALUE)));
        assertThat(sutA.getAttributeC(), is(equalTo(ATTRIBUTE_C_VALUE)));

        // Create an other one...
        final SutMother sutMotherNew = new SutMother(this.emProvider, ATTRIBUTE_A_VALUE);
        final Sut sutNew = sutMotherNew.instance();

        assertThat(sutNew, is(notNullValue()));
        assertThat(sutNew.getId(), is(notNullValue()));
        assertThat(sutNew.getAttributeA(), is(equalTo(ATTRIBUTE_A_VALUE)));
        assertThat(sutNew.getAttributeB(), is(equalTo(ATTRIBUTE_B_VALUE)));
        assertThat(sutNew.getAttributeC(), is(equalTo(ATTRIBUTE_C_VALUE)));

        assertThat(sutA.getId(), is(equalTo(sutNew.getId())));
    }
}
