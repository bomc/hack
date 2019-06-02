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
package de.bomc.poc.order.application.customer.mapping;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;

import de.bomc.poc.order.CategoryFastUnitTest;
import de.bomc.poc.order.application.customer.dto.CustomerDTO;
import de.bomc.poc.order.domain.model.customer.CustomerEntity;

/**
 * Tests mapping between {@link CustomerDTO} and {@link CustomerEntity}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Category(CategoryFastUnitTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DTOEntityCustomerMapperTest {

    private static final String LOG_PREFIX = "DTOEntityCustomerMapperTest#";
    private static final Logger LOGGER = Logger.getLogger(DTOEntityCustomerMapperTest.class);
    // _______________________________________________
    // Test constants
    // -----------------------------------------------
    private final String EMAIL = "bomc@bomc.org";
    private final String NAME = "myName";
    private final String FIRST_NAME = "myFirstName";

    /**
     * <pre>
     *  mvn clean install -Dtest=DTOEntityCustomerMapperTest#test010_mapEntityToDTO_pass
     *
     * <b><code>test010_mapping_pass</code>:</b><br>
     *  Tests the mapping from CustomerEntity to a CusotmerDTO.
     *
     * <b>Preconditions:</b><br>
     *  - 
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The mapping is invoked.
     *
     * <b>Postconditions:</b><br>
     *  All attributes of the CustomerEntity is mapped to CustomerDTO.
     * </pre>
     */
    @Test
    public void test010_mapEntityToDTO_pass() {
        LOGGER.debug(LOG_PREFIX + "test010_mapEntityToDTO_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        final CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setUsername(EMAIL);
        customerEntity.setName(NAME);
        customerEntity.setFirstname(FIRST_NAME);

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        final CustomerDTO customerDTO = DTOEntityCustomerMapper.INSTANCE.mapEntityToDTO(customerEntity);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        assertThat(customerDTO.getUsername(), equalTo(EMAIL));
        assertThat(customerDTO.getName(), equalTo(NAME));
        assertThat(customerDTO.getFirstname(), equalTo(FIRST_NAME));
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=DTOEntityCustomerMapperTest#test020_mappingList_pass
     *
     * <b><code>test020_mappingList_pass</code>:</b><br>
     *  Tests the mapping from a CustomerEntity list to a CustomerDTO list.
     *
     * <b>Preconditions:</b><br>
     *  - 
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The mapping is invoked.
     *
     * <b>Postconditions:</b><br>
     *  All elements of the CustomerEntity list are mapped to CustomerDTO list.
     * </pre>
     */
    @Test
    public void test020_mappingList_pass() {
        LOGGER.debug(LOG_PREFIX + "test020_mappingList_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        final List<CustomerEntity> customerEntityList = new ArrayList<CustomerEntity>();

        final CustomerEntity customerEntity1 = new CustomerEntity();
        customerEntity1.setUsername(EMAIL);
        customerEntity1.setName(NAME);
        customerEntity1.setFirstname(FIRST_NAME);

        customerEntityList.add(customerEntity1);

        final CustomerEntity customerEntity2 = new CustomerEntity();
        customerEntity2.setUsername(EMAIL);
        customerEntity2.setName(NAME);
        customerEntity2.setFirstname(FIRST_NAME);

        customerEntityList.add(customerEntity2);

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        final List<CustomerDTO> customerDTOList = DTOEntityCustomerMapper.INSTANCE.mapEntityToDTO(customerEntityList);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        assertThat(customerDTOList, notNullValue());
        assertThat(customerDTOList.size(), equalTo(2));

        customerDTOList.forEach(customerDTO -> {
            assertThat(customerDTO.getUsername(), equalTo(EMAIL));
            assertThat(customerDTO.getName(), equalTo(NAME));
            assertThat(customerDTO.getFirstname(), equalTo(FIRST_NAME));
        });
    }
    
    /**
     * <pre>
     *  mvn clean install -Dtest=DTOEntityCustomerMapperTest#test030_nullCheck
     *
     * <b><code>test030_nullCheck</code>:</b><br>
     *  Tests the null check of the mapping.
     *
     * <b>Preconditions:</b><br>
     *  - 
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The mapping is invoked.
     *
     * <b>Postconditions:</b><br>
     *  All invocations return null.
     * </pre>
     */
    @Test
    public void test030_nullCheck() {
        LOGGER.debug(LOG_PREFIX + "test030_nullCheck");
        
        final CustomerEntity customerEntity = null;
        final List<CustomerEntity> customerEntityList = null;
        assertThat(DTOEntityCustomerMapper.INSTANCE.mapEntityToDTO(customerEntity), equalTo(null));
        assertThat(DTOEntityCustomerMapper.INSTANCE.mapEntityToDTO(customerEntityList), equalTo(null));
        assertThat(DTOEntityCustomerMapper.INSTANCE.mapDTOToEntity(null), equalTo(null));
    }
}
