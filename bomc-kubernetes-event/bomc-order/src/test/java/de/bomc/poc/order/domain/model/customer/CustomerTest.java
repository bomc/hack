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
package de.bomc.poc.order.domain.model.customer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import de.bomc.poc.order.AbstractUnitTest;
import de.bomc.poc.order.CategoryFastUnitTest;

/**
 * Test the {@link CustomerEntity}.
 * 
 * <pre>
 *  mvn clean install -Dtest=CustomerTest
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 23.11.2018
 */
@Category(CategoryFastUnitTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomerTest extends AbstractUnitTest {

    private static final Logger LOGGER = Logger.getLogger(CustomerTest.class);
    private static final String LOG_PREFIX = "CustomerTest#";
    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    private EntityTransaction utx;
    // _______________________________________________
    // Constants
    // -----------------------------------------------
    private static final String EMAIL = "bomc@bomc.org";
    private static final String NAME = "Börner";
    private static final String FIRST_NAME = "Michael";
    private static final String CREATE_USER = "CreateUser";
    private static final String MODIFY_USER = "ModifyUser";

    @Before
    public void setup() {
        this.entityManager = this.emProvider.getEntityManager();
        assertThat(this.entityManager, notNullValue());

        this.utx = this.entityManager.getTransaction();
        assertThat(this.utx, notNullValue());
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=CustomerTest#test010_createCustomer_pass
     *
     * <b><code>test010_createCustomer_pass</code>:</b><br>
     *
     * <b>Preconditions:</b><br>
     *  - Create and persist Customer instance.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Create instance.
     *  - Persist instance in db
     *
     * <b>Postconditions:</b><br>
     *  - Technical db id is not null.
     * </pre>
     */
    @Test
    public void test010_createCustomer_pass() {
        LOGGER.debug(LOG_PREFIX + "test010_createCustomer_pass");

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        final CustomerEntity customer = new CustomerEntity();
        customer.setUsername(EMAIL);
        customer.setFirstname(FIRST_NAME);
        customer.setName(NAME);
        customer.setCreateUser(CREATE_USER);

        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        this.utx.begin();
        this.entityManager.persist(customer);
        this.utx.commit();

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(customer.getId(), notNullValue());
        assertThat(customer.getModifyDateTime(), notNullValue());
        assertThat(customer.getModifyUser(), nullValue());
        assertThat(customer.getCreateUser(), equalTo(CREATE_USER));
        assertThat(customer.getCreateDateTime(), notNullValue());

        LOGGER.debug(LOG_PREFIX + "test010_createCustomer_pass " + customer);
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=CustomerTest#test020_nQByLatestModifyDate_pass
     *
     * <b><code>test020_nQByLatestModifyDate_pass</code>:</b><br>
     *  Test the NQ for latest entry by modified date.
     *  
     * <b>Preconditions:</b><br>
     *  - Create 3 persist Customer instances, with three different modified dates.
     *  - The latest date is the current date.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Create instance.
     *  - Persist instance in db
     *  - Perform the NQ_FIND_BY_LATEST_MODIFIED_DATE-query.
     *
     * <b>Postconditions:</b><br>
     *  - Return the latest modified date, which is the current date.
     * </pre>
     */
    @Test
    public void test020_nQByLatestModifyDate_pass() {
        LOGGER.debug(LOG_PREFIX + "test020_nQByLatestModifyDate_pass");

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        this.createCustomerWithModifyDate();

        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        final TypedQuery<LocalDateTime> typedQuery = this.entityManager
                .createNamedQuery(CustomerEntity.NQ_FIND_BY_LATEST_MODIFIED_DATE_TIME_CUSTOMER, LocalDateTime.class);
        final List<LocalDateTime> customerModifyDateTimeList = typedQuery.getResultList();

        // Contains only one element, the latest date.
        final LocalDateTime latestModifiedDateTime = customerModifyDateTimeList.get(0);

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(latestModifiedDateTime.toLocalDate(), equalTo(LocalDateTime.now().toLocalDate()));
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=CustomerTest#test030_nQByLatestModifyDate_fail
     *
     * <b><code>test030_nQByLatestModifyDate_fail</code>:</b><br>
     *  Test the NQ for latest entry by modified date.
     *  
     * <b>Preconditions:</b><br>
     *  - No entity is stored in db.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Perform the NQ_FIND_BY_LATEST_MODIFIED_DATE-query.
     *
     * <b>Postconditions:</b><br>
     *  - Return a element that is null.
     * </pre>
     */
    @Test
    public void test030_nQByLatestModifyDate_fail() {
        LOGGER.debug(LOG_PREFIX + "test030_nQByLatestModifyDate_fail");

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------

        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        final TypedQuery<LocalDateTime> typedQuery = this.entityManager
                .createNamedQuery(CustomerEntity.NQ_FIND_BY_LATEST_MODIFIED_DATE_TIME_CUSTOMER, LocalDateTime.class);
        final List<LocalDateTime> customerModifyDateTimeList = typedQuery.getResultList();

        // Contains only one element, the latest date.
        final LocalDateTime latestModifiedDateTime = customerModifyDateTimeList.get(0);

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(latestModifiedDateTime, nullValue());
    }

    /**
     * <pre>
     *  mvn clean install -Dtest=CustomerTest#test040_nQFindByUsername
     *
     * <b><code>test040_nQFindByUsername</code>:</b><br>
     *  Test the NQ for latest entry by modified date.
     *  
     * <b>Preconditions:</b><br>
     *  - No entity is stored in db.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - Perform the NQ_FIND_BY_USERNAME-query.
     *
     * <b>Postconditions:</b><br>
     *  - Return a element that is null.
     * </pre>
     */
    @Test
    public void test040_nQFindByUsername() {
        LOGGER.debug(LOG_PREFIX + "test040_nQFindByUsername");

        // ___________________________________________
        // Do the test preparation.
        // -------------------------------------------
        final CustomerEntity customer = new CustomerEntity();
        customer.setUsername(EMAIL + "_040");
        customer.setFirstname(FIRST_NAME);
        customer.setName(NAME);
        customer.setCreateUser(CREATE_USER);

        this.utx.begin();
        this.entityManager.persist(customer);
        this.utx.commit();
        // ___________________________________________
        // Perform actual test.
        // -------------------------------------------
        final TypedQuery<CustomerEntity> typedQuery = this.entityManager
                .createNamedQuery(CustomerEntity.NQ_FIND_BY_USERNAME, CustomerEntity.class);
        typedQuery.setParameter("username", EMAIL + "_040");
        final List<CustomerEntity> customerEntityList = typedQuery.getResultList();

        // Contains only one element, because username is unique.
        final CustomerEntity customerEntity = customerEntityList.get(0);

        // ___________________________________________
        // Do asserts.
        // -------------------------------------------
        assertThat(customerEntity, notNullValue());
        assertThat(customerEntity.getUsername(), equalTo(EMAIL + "_040"));
    }
    
    private List<CustomerEntity> createCustomerWithModifyDate() {

        final List<CustomerEntity> customerEntitiesList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            final CustomerEntity customer = new CustomerEntity();
            customer.setUsername(EMAIL + "_" + i);
            customer.setFirstname(FIRST_NAME + "_" + i);
            customer.setName(NAME + "_" + i);
            customer.setCreateUser(CREATE_USER + "_" + i);
            customer.setModifyUser(MODIFY_USER + "_" + i);
            // 0 is the current date.
            customer.setModifyDateTime(LocalDateTime.now().plusSeconds(i));

            this.utx.begin();

            this.entityManager.persist(customer);

            this.utx.commit();

            customerEntitiesList.add(customer);
        }

        return customerEntitiesList;
    }
}