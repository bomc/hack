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
package de.bomc.poc.order.application.order.mapping;

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
import de.bomc.poc.order.application.order.dto.ItemDTO;
import de.bomc.poc.order.domain.model.item.ItemEntity;

/**
 * Tests mapping between {@link ItemDTO} and {@link ItemEntity}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
@Category(CategoryFastUnitTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DTOEntityItemMapperTest {

    private static final String LOG_PREFIX = "DTOEntityItemMapperTest#";
    private static final Logger LOGGER = Logger.getLogger(DTOEntityItemMapperTest.class);
    // _______________________________________________
    // Test constants
    // -----------------------------------------------
    private final String NAME = "myName";
    private final Double PRICE = 2.99d;

    /**
     * <pre>
     *  mvn clean install -Dtest=DTOEntityItemMapperTest#test010_mapEntityToDTO_pass
     *
     * <b><code>test010_mapEntityToDTO_pass</code>:</b><br>
     *  Tests the mapping from ItemEntity to a ItemDTO.
     *
     * <b>Preconditions:</b><br>
     *  - Create a ItemEntity instance.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The mapping is invoked.
     *
     * <b>Postconditions:</b><br>
     *  All attributes of the ItemEntity is mapped to ItemDTO.
     * </pre>
     */
    @Test
    public void test010_mapEntityToDTO_pass() {
        LOGGER.debug(LOG_PREFIX + "test010_mapEntityToDTO_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        final ItemEntity itemEntity = new ItemEntity();
        itemEntity.setName(NAME);
        itemEntity.setPrice(PRICE);

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        final ItemDTO itemDTO = DTOEntityItemMapper.INSTANCE.mapEntityToDTO(itemEntity);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        assertThat(itemDTO.getPrice(), equalTo(PRICE));
        assertThat(itemDTO.getName(), equalTo(NAME));
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=DTOEntityItemMapperTest#test020_mappingList_pass
     *
     * <b><code>test020_mappingList_pass</code>:</b><br>
     *  Tests the mapping from a ItemEntity list to a ItemDTO list.
     *
     * <b>Preconditions:</b><br>
     *  - Create two Item instances and add them to a list.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The mapping is invoked.
     *
     * <b>Postconditions:</b><br>
     *  All elements of the ItemEntity list are mapped to ItemDTO list.
     * </pre>
     */
    @Test
    public void test020_mappingList_pass() {
        LOGGER.debug(LOG_PREFIX + "test020_mappingList_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        final List<ItemEntity> itemEntityList = new ArrayList<ItemEntity>();

        final ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setName(NAME);
        itemEntity1.setPrice(PRICE);

        itemEntityList.add(itemEntity1);

        final ItemEntity itemEntity2 = new ItemEntity();
        itemEntity2.setName(NAME);
        itemEntity2.setPrice(PRICE);

        itemEntityList.add(itemEntity2);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        final List<ItemDTO> itemDTOList = DTOEntityItemMapperImpl.INSTANCE.mapEntityListToDTOList(itemEntityList);

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        assertThat(itemDTOList, notNullValue());
        assertThat(itemDTOList.size(), equalTo(2));

        itemDTOList.forEach(itemDTO -> {
            assertThat(itemDTO.getName(), equalTo(NAME));
            assertThat(itemDTO.getPrice(), equalTo(PRICE));
        });
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=DTOEntityItemMapperTest#test030_mapDTOToEntity_pass
     *
     * <b><code>test030_mapDTOToEntity_pass</code>:</b><br>
     *  Tests the mapping from ItemDTO to a ItemEntity.
     *
     * <b>Preconditions:</b><br>
     *  - Create a ItemDTO instance.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The mapping is invoked.
     *
     * <b>Postconditions:</b><br>
     *  All attributes of the ItemDTO is mapped to ItemEntity.
     * </pre>
     */
    @Test
    public void test030_mapDTOToEntity_pass() {
        LOGGER.debug(LOG_PREFIX + "test030_mapDTOToEntity_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        final ItemDTO itemDTO = new ItemDTO();
        itemDTO.setName(NAME);
        itemDTO.setPrice(PRICE);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        final ItemEntity itemEntity = DTOEntityItemMapperImpl.INSTANCE.mapDTOToEntity(itemDTO);

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        assertThat(itemEntity, notNullValue());
        assertThat(itemEntity.getName(), equalTo(NAME));
        assertThat(itemEntity.getPrice(), equalTo(PRICE));
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=DTOEntityItemMapperTest#test040_mappingList_pass
     *
     * <b><code>test040_mappingList_pass</code>:</b><br>
     *  Tests the mapping from a ItemDTO list to a ItemEntity list.
     *
     * <b>Preconditions:</b><br>
     *  - Create two ItemDTO instances and add them to a list.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The mapping is invoked.
     *
     * <b>Postconditions:</b><br>
     *  All elements of the ItemDTO list are mapped to ItemEntity list.
     * </pre>
     */
    @Test
    public void test040_mappingList_pass() {
        LOGGER.debug(LOG_PREFIX + "test040_mappingList_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        final List<ItemDTO> itemDTOList = new ArrayList<ItemDTO>();

        final ItemDTO itemDTO1 = new ItemDTO();
        itemDTO1.setName(NAME);
        itemDTO1.setPrice(PRICE);

        itemDTOList.add(itemDTO1);

        final ItemDTO itemDTO2 = new ItemDTO();
        itemDTO2.setName(NAME);
        itemDTO2.setPrice(PRICE);

        itemDTOList.add(itemDTO2);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        final List<ItemEntity> itemEntityList = DTOEntityItemMapperImpl.INSTANCE.mapDTOListToEntityList(itemDTOList);

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        assertThat(itemEntityList, notNullValue());
        assertThat(itemEntityList.size(), equalTo(2));

        itemEntityList.forEach(itemEntity -> {
            assertThat(itemEntity.getName(), equalTo(NAME));
            assertThat(itemEntity.getPrice(), equalTo(PRICE));
        });
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=DTOEntityItemMapperTest#test050_nullCheck
     *
     * <b><code>test050_nullCheck</code>:</b><br>
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
    public void test050_nullCheck() {
        LOGGER.debug(LOG_PREFIX + "test050_nullCheck");

        assertThat(DTOEntityItemMapperImpl.INSTANCE.mapDTOListToEntityList(null), equalTo(null));
        assertThat(DTOEntityItemMapperImpl.INSTANCE.mapDTOToEntity(null), equalTo(null));
        assertThat(DTOEntityItemMapperImpl.INSTANCE.mapEntityListToDTOList(null), equalTo(null));
        assertThat(DTOEntityItemMapperImpl.INSTANCE.mapEntityToDTO(null), equalTo(null));
    }
}
