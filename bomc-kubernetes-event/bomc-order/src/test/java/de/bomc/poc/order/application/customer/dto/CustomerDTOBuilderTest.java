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
package de.bomc.poc.order.application.customer.dto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;

import de.bomc.poc.order.CategoryFastUnitTest;
import de.bomc.poc.order.application.customer.dto.CustomerDTO;

/**
 * Tests the {@link CustomerDTO}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 13.02.2019
 */
@Category(CategoryFastUnitTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomerDTOBuilderTest {

    private static final String LOG_PREFIX = "CustomerDTOBuilderTest#";
    private static final Logger LOGGER = Logger.getLogger(CustomerDTOBuilderTest.class);
    // _______________________________________________
    // Test constants
    // -----------------------------------------------
    private static final String EMAIL = "bomc@bomc.org";
    private static final String NAME = "myName";
    private static final String FIRST_NAME = "myFirstName";

    @Test
    public void test010_build_pass() {
        LOGGER.debug(LOG_PREFIX + "test010_build_pass");
        
        final CustomerDTO dto = CustomerDTO.username(EMAIL).name(NAME).firstname(FIRST_NAME).build();
        
        assertThat(dto.getUsername(), equalTo(EMAIL));
        assertThat(dto.getName(), equalTo(NAME));
        assertThat(dto.getFirstname(), equalTo(FIRST_NAME));
        
        dto.setFirstname(null);
        dto.setName(null);
        dto.setUsername(null);
        
        assertThat(dto.getUsername(), nullValue());
        assertThat(dto.getName(), nullValue());
        assertThat(dto.getFirstname(), nullValue());
    }

    @Test
    @SuppressWarnings("unlikely-arg-type")
    public void test020_equalsHashcode() {
        LOGGER.debug(LOG_PREFIX + "test020_equalsHashcode");
        
        final CustomerDTO dto1 = CustomerDTO.username(EMAIL).name(NAME).firstname(FIRST_NAME).build();
        final CustomerDTO dto2 = new CustomerDTO();
        dto2.setFirstname(FIRST_NAME);
        dto2.setName(NAME);
        dto2.setUsername(EMAIL);
        final CustomerDTO dto3 = CustomerDTO.username(EMAIL + "_2").name(NAME + "_2").firstname(FIRST_NAME + "_2").build();
        
        final CustomerDTO dto4 = CustomerDTO.username(null).name(null).firstname(null).build();
        final CustomerDTO dto5 = null;
        
        assertThat(dto1.equals(dto1), equalTo(true));
        assertThat(dto1.equals(dto2), equalTo(true));
        assertThat(dto1.equals(null), equalTo(false));
        assertThat(dto1.equals(dto3), equalTo(false));
        assertThat(dto1.equals(dto4), equalTo(false));
        assertThat(dto1.equals(dto5), equalTo(false));
        assertThat(dto1.equals(new String()), equalTo(false));
        assertThat(dto1.equals(new CustomerDTO()), equalTo(false));
        assertThat(dto1.hashCode(), equalTo(dto2.hashCode()));
        assertThat(dto1.hashCode(), not(equalTo(dto3.hashCode())));
        assertThat(dto1.hashCode(), not(equalTo(dto4.hashCode())));
    }
}
