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
package de.bomc.poc.order.application.customer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import de.bomc.poc.order.CategoryFastUnitTest;
import de.bomc.poc.order.application.customer.dto.CustomerDTO;
import de.bomc.poc.order.domain.model.customer.CustomerEntity;
import de.bomc.poc.order.domain.model.customer.JpaCustomerDao;

/**
 * Tests the {@link CustomerControllerEJB}.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 03.02.2019
 */
@Category(CategoryFastUnitTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(MockitoJUnitRunner.class)
public class CustomerControllerEJBTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private static final String LOG_PREFIX = "CustomerControllerEJBTest#";
    private static final Logger LOGGER = Logger.getLogger(CustomerControllerEJBTest.class);
    // _______________________________________________
    // Test constants
    // -----------------------------------------------
    private static final String USER_ID = UUID.randomUUID().toString();
    private static final String FIRST_NAME = "firstName";
    private static final String NAME = "name";
    private static final String USER_NAME = "userName";
    // _______________________________________________
    // Set mocks
    // -----------------------------------------------
    @Mock
    private Logger logger;
    @Mock
    private CustomerEntity customerEntity;
    @Mock
    private JpaCustomerDao jpaCustomerDao;
    @InjectMocks
    private CustomerControllerEJB sutCustomerControllerEJB;

    /**
     * <pre>
     *  mvn clean install -Dtest=CustomerControllerEJBTest#test010_nullValue_pass
     *
     * <b><code>test010_nullValue_pass</code>:</b><br>
     *  Tests 
     *
     * <b>Preconditions:</b><br>
     *  -
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - 
     *
     * <b>Postconditions:</b><br>
     * 
     * </pre>
     */
    @Test
    public void test010_nullValue_pass() {
        LOGGER.debug(LOG_PREFIX + "test010_nullValue_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        when(this.jpaCustomerDao.findLatestModifiedDateTime(USER_ID)).thenReturn(null);

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        final LocalDateTime modifyDateTime = this.sutCustomerControllerEJB.findLatestModifiedDateTime(USER_ID);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        assertThat(modifyDateTime, nullValue());
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=CustomerControllerEJBTest#test020_modifyDate_pass
     *
     * <b><code>test020_modifyDate_pass</code>:</b><br>
     *  Tests the 'findLatestModifiedDateTime'-method in 'CustomerControllerEJB'.
     *
     * <b>Preconditions:</b><br>
     *  -
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - sut is invoked.
     *  - jpaCustomerDao returns current LocalDateTime instance.
     *
     * <b>Postconditions:</b><br>
     *  The sut returned date/time is equals the date/time from jpaCustomerDao.
     * </pre>
     */
    @Test
    public void test020_modifyDate_pass() {
        LOGGER.debug(LOG_PREFIX + "test020_modifyDate_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        final LocalDateTime localDateTime = LocalDateTime.now();
        when(this.jpaCustomerDao.findLatestModifiedDateTime(USER_ID)).thenReturn(localDateTime);

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        final LocalDateTime modifyDateTime = this.sutCustomerControllerEJB.findLatestModifiedDateTime(USER_ID);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        assertThat(modifyDateTime, equalTo(localDateTime));
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=CustomerControllerEJBTest#test030_createCustomer_pass
     *
     * <b><code>test030_createCustomer_pass</code>:</b><br>
     *  Tests the 'createCustomer'-method in 'CustomerControllerEJB'.
     *
     * <b>Preconditions:</b><br>
     *  -
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - sut is invoked.
     *  - jpaCustomerDao persists the new customer.
     *
     * <b>Postconditions:</b><br>
     *  The sut returned the technical id.
     * </pre>
     */
    @Test
    public void test030_createCustomer_pass() {
        LOGGER.debug(LOG_PREFIX + "test030_createCustomer_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        final Long ID = 42L;
        final String EXECUTED_USER = UUID.randomUUID().toString();
        final CustomerDTO customerDTO = new CustomerDTO();

        doAnswer(new Answer<Long>() {
            public Long answer(final InvocationOnMock invocation) {
                // Get method parameter and manipulate the CustomerEntity.
                final Object[] args = invocation.getArguments();
                // Set id to allow assertion.
                final CustomerEntity customerEntity = (CustomerEntity) args[0];
                customerEntity.setId(ID);

                return null;
            }
        }).when(this.jpaCustomerDao).persist(any(CustomerEntity.class), anyString());

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        final Long id = this.sutCustomerControllerEJB.createCustomer(customerDTO, EXECUTED_USER);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        assertThat(id, equalTo(ID));
        verify(this.jpaCustomerDao).persist(any(CustomerEntity.class), anyString());
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=CustomerControllerEJBTest#test040_findAll_pass
     *
     * <b><code>test040_findAll_pass</code>:</b><br>
     *  Tests the 'findAllCustomers'-method in 'CustomerControllerEJB'.
     *
     * <b>Preconditions:</b><br>
     *  - Create 3 instances of CustomerEntitys.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - sut is invoked.
     *
     * <b>Postconditions:</b><br>
     *  The sut returned a lust of 3 CustomerDTOs.
     * </pre>
     */
    @Test
    public void test040_findAll_pass() {
        LOGGER.debug(LOG_PREFIX + "test040_findAll_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        final int CUSTOMER_COUNT = 3;
        final String userId = UUID.randomUUID().toString();
        final List<CustomerEntity> customerEntityList = new ArrayList<CustomerEntity>();
        for (int i = 0; i < CUSTOMER_COUNT; i++) {
            customerEntityList
                    .add(this.createCustomerEntity(USER_NAME + "_" + i, NAME + "_" + i, FIRST_NAME + "_" + i));
        }

        when(this.jpaCustomerDao.findAll()).thenReturn(customerEntityList);

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        final List<CustomerDTO> customerDTOList = this.sutCustomerControllerEJB.findAllCustomers(userId);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        assertThat(customerDTOList.size(), equalTo(CUSTOMER_COUNT));
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=CustomerControllerEJBTest#test050_findCustomerByUsername_pass
     *
     * <b><code>test050_findCustomerByUsername_pass</code>:</b><br>
     *  Tests the 'findCustomerByUsername'-method in 'CustomerControllerEJB'.
     *
     * <b>Preconditions:</b><br>
     *  - Create 1 instance of CustomerEntitys.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - sut is invoked.
     *
     * <b>Postconditions:</b><br>
     *  The sut returned a lust of 1 CustomerDTO.
     * </pre>
     */
    @Test
    public void test050_findCustomerByUsername_pass() {
        LOGGER.debug(LOG_PREFIX + "test050_findCustomerByUsername_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        final String userId = UUID.randomUUID().toString();
        final CustomerEntity customerEntity = this.createCustomerEntity(USER_NAME, NAME, FIRST_NAME);

        when(this.jpaCustomerDao.findByUsername(USER_NAME, userId)).thenReturn(customerEntity);

        // ___________________________________________
        // WHEN
        // -------------------------------------------
        final CustomerDTO customerDTO = this.sutCustomerControllerEJB.findCustomerByUsername(USER_NAME, userId);

        // ___________________________________________
        // THEN
        // -------------------------------------------
        assertThat(customerDTO, notNullValue());
        assertThat(customerDTO.getUsername(), equalTo(USER_NAME));
    }

    private CustomerEntity createCustomerEntity(final String userName, final String name, final String firstName) {
        final CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setFirstname(firstName);
        customerEntity.setName(name);
        customerEntity.setUsername(userName);

        return customerEntity;

    }
}
