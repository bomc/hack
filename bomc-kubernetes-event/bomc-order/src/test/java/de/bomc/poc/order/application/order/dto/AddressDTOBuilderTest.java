/**
 * Project: bomc-onion-architecture
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
package de.bomc.poc.order.application.order.dto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.not;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;

import de.bomc.poc.order.CategoryFastUnitTest;
import de.bomc.poc.order.application.order.dto.AddressDTO;

/**
 * Test the builder of {@link AddressDTO}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 01.03.2019
 */
@Category(CategoryFastUnitTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AddressDTOBuilderTest {

    private static final String LOG_PREFIX = AddressDTOBuilderTest.class.getSimpleName() + "#";
    private static final Logger LOGGER = Logger.getLogger(AddressDTOBuilderTest.class);
    // _______________________________________________
    // Constants for testing.
    // -----------------------------------------------
    private static final String ZIP = "4000";
    private static final String CITY = "Ninjago-City";
    private static final String STREET = "Downing Street";

    @Test
    public void test010_builder_pass() {
        LOGGER.debug(LOG_PREFIX + "test010_builder_pass");

        // ___________________________________________
        // Perform test.
        // -------------------------------------------
        final AddressDTO addressDTO = AddressDTO.zip(ZIP).street(STREET).city(CITY).build();

        LOGGER.debug(LOG_PREFIX + "test010_builder_pass [" + addressDTO + "]");

        // ___________________________________________
        // Do asserts
        // -------------------------------------------
        assertThat(addressDTO, notNullValue());
        assertThat(addressDTO.getCity(), equalTo(CITY));
        assertThat(addressDTO.getStreet(), equalTo(STREET));
        assertThat(addressDTO.getZip(), equalTo(ZIP));
    }

    @Test
    @SuppressWarnings("unlikely-arg-type")
    public void test020_equalHashcode_pass() {
        LOGGER.debug(LOG_PREFIX + "test020_equalHashcode_pass");

        // ___________________________________________
        // Perform test.
        // -------------------------------------------
        final AddressDTO addressDTO1 = AddressDTO.zip(ZIP).street(STREET).city(CITY).build();
        final AddressDTO addressDTO2 = new AddressDTO();
        addressDTO2.setCity(CITY);
        addressDTO2.setStreet(STREET);
        addressDTO2.setZip(ZIP);
        final AddressDTO addressDTO3 = AddressDTO.zip(ZIP + "_3").street(STREET + "_3").city(CITY + "_3").build();

        final Set<AddressDTO> set = new HashSet<AddressDTO>();
        set.add(addressDTO1);
        set.add(addressDTO2);
        set.add(addressDTO3);
        
        final AddressDTO addressDTO4 = AddressDTO.zip(null).street(null).city(null).build();
        final AddressDTO addressDTO5 = null;

        // ___________________________________________
        // Do asserts
        // -------------------------------------------
        // Only 2 elements are added to set, addressDTO1 are addressDTO2 equal.
        assertThat(set.size(), equalTo(2));
        
        assertThat(set.contains(addressDTO1), equalTo(true));
        assertThat(set.contains(new AddressDTO()), equalTo(false));
        assertThat(addressDTO1.equals(addressDTO1), equalTo(true));
        assertThat(addressDTO1.equals(addressDTO2), equalTo(true));
        assertThat(addressDTO1.equals(addressDTO3), equalTo(false));
        assertThat(addressDTO1.equals(addressDTO4), equalTo(false));
        assertThat(addressDTO1.equals(addressDTO5), equalTo(false));
        assertThat(addressDTO1.equals(null), equalTo(false));        
        assertThat(addressDTO1.equals(addressDTO3), equalTo(false));
        assertThat(addressDTO1.equals(new AddressDTO()), equalTo(false));
        assertThat(addressDTO1.equals(new String()), equalTo(false));
        assertThat(addressDTO1.hashCode(), equalTo(addressDTO1.hashCode()));
        assertThat(addressDTO1.hashCode(), not(equalTo(addressDTO3.hashCode())));
        assertThat(addressDTO1.hashCode(), not(equalTo(addressDTO4.hashCode())));
    }
}