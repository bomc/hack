/**
 * Project: bomc-order
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
package de.bomc.poc.order.interfaces.rest.v1.order;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.bomc.poc.order.CategoryFastUnitTest;
import de.bomc.poc.order.application.order.OrderController;
import de.bomc.poc.order.application.order.dto.ItemDTO;
import de.bomc.poc.order.application.order.dto.OrderDTO;
import de.bomc.poc.order.interfaces.rest.PortFinder;

/**
 * Tests the {@link OrderRestEndpoint}.
 * <p>
 * 
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=OrderRestEndpointTjwsTest
 * </pre>
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 19.03.2019
 */
@SuppressWarnings("deprecation")
@RunWith(MockitoJUnitRunner.class)
@Category(CategoryFastUnitTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrderRestEndpointTjwsTest {
    
    private static final String LOG_PREFIX = "OrderRestEndpointTjwsTest#";
    private static final Logger LOGGER = Logger.getLogger(OrderRestEndpointTjwsTest.class);
    private static int port;
    private static org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer server;
    @Mock
    private Logger logger;
    @Mock
    private OrderController orderControllerEJB;
    @InjectMocks
    private static OrderRestEndpointImpl sut = new OrderRestEndpointImpl();
    
    // _______________________________________________
    // Test data
    // -----------------------------------------------
    private static final String USER_ID = UUID.randomUUID().toString();
    private static final Long TECHNICAL_ID = 42L;
    private static final String ITEM_NAME = "iPhone";
    private static final Double ITEM_PRICE = 1399.99;
    private static final Long ORDER_ID = 42L;
    
    @Before
    public void init() {
        //
    }

    @BeforeClass
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void beforeClass() throws Exception {
        port = PortFinder.findPort();

        server = new org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer();
        server.setPort(port);
        server.getDeployment().setResources((List) Collections.singletonList(sut));

        server.start();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        if (server != null) {
            server.stop();
            server = null;
        }
    }
    
    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=OrderRestEndpointTjwsTest#test010_createOrder_pass
     *
     * <b><code>test010_createOrder_pass</code>:</b><br>
     *  Tests the behavior for creating a order.
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed to server.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The endpoint is invoked.
     *
     * <b>Postconditions:</b><br>
     *  Response status is 200, and a technical id 42 is returned.
     * </pre>
     */
    @Test
    public void test010_createOrder_pass() {
        LOGGER.debug(LOG_PREFIX + "test010_createOrder_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
       final OrderDTO orderDTO = new OrderDTO();
       final Long retId = TECHNICAL_ID;
       
       when(this.orderControllerEJB.createOrder(orderDTO, USER_ID)).thenReturn(retId);
        // ___________________________________________
        // WHEN
        // -------------------------------------------
        final Response response = sut.createOrder(orderDTO, USER_ID);
        
        // ___________________________________________
        // THEN
        // -------------------------------------------
        assertThat(response, notNullValue());
        assertThat(response.getStatus(), equalTo(Status.OK.getStatusCode()));
        assertThat(response.getMediaType().toString(), equalTo(OrderRestEndpoint.MEDIA_TYPE_JSON_V1));
        
        final JsonObject jsonObject = (JsonObject) response.getEntity();
        final String orderId = jsonObject.get("orderId").toString();
        assertThat(orderId, equalTo("42"));
    }
    
    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=OrderRestEndpointTjwsTest#test020_findAllItems_pass
     *
     * <b><code>test020_findAllItems_pass</code>:</b><br>
     *  Tests the behavior for find all items.
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed to server.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The endpoint is invoked.
     *
     * <b>Postconditions:</b><br>
     *  A List with one itemDTO is returned.
     * </pre>
     */
    @Test
    @SuppressWarnings("unchecked")
    public void test020_findAllItems_pass() {
        LOGGER.debug(LOG_PREFIX + "test020_findAllItems_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        final ItemDTO itemDTO = ItemDTO.name(ITEM_NAME).price(ITEM_PRICE).build();
        final List<ItemDTO> itemDTOList = new ArrayList<ItemDTO>();
        itemDTOList.add(itemDTO);
        
        when(this.orderControllerEJB.findAllItems(USER_ID)).thenReturn(itemDTOList);
        // ___________________________________________
        // WHEN
        // -------------------------------------------
        final Response response = sut.findAllItems(USER_ID);
        
        // ___________________________________________
        // THEN
        // -------------------------------------------
        assertThat(response, notNullValue());
        assertThat(response.getStatus(), equalTo(Status.OK.getStatusCode()));
        assertThat(response.getMediaType().toString(), equalTo(OrderRestEndpoint.MEDIA_TYPE_JSON_V1));
        
        final List<ItemDTO> retItemDTOList = (List<ItemDTO>) response.getEntity();
        
        assertThat(retItemDTOList, notNullValue());
        assertThat(retItemDTOList.size(), equalTo(1));
        assertThat(retItemDTOList.get(0).getName(), equalTo(ITEM_NAME));
        assertThat(retItemDTOList.get(0).getPrice(), equalTo(ITEM_PRICE));
    }
    
    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=OrderRestEndpointTjwsTest#test030_findAllOrder_pass
     *
     * <b><code>test030_findAllOrder_pass</code>:</b><br>
     *  Tests the behavior for find all orders.
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed to server.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The endpoint is invoked.
     *
     * <b>Postconditions:</b><br>
     *  A List with one orderDTO is returned.
     * </pre>
     */
    @Test
    @SuppressWarnings("unchecked")
    public void test030_findAllOrder_pass() {
        LOGGER.debug(LOG_PREFIX + "test030_findAllOrder_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        final OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(ORDER_ID);
        
        final List<OrderDTO> orderDTOList = new ArrayList<OrderDTO>();
        orderDTOList.add(orderDTO);
        
        when(this.orderControllerEJB.findAllOrder(USER_ID)).thenReturn(orderDTOList);
        
        // ___________________________________________
        // WHEN
        // -------------------------------------------
        final Response response = sut.findAllOrder(USER_ID);
        
        // ___________________________________________
        // THEN
        // -------------------------------------------
        assertThat(response, notNullValue());
        assertThat(response.getStatus(), equalTo(Status.OK.getStatusCode()));
        assertThat(response.getMediaType().toString(), equalTo(OrderRestEndpoint.MEDIA_TYPE_JSON_V1));
        
        final List<OrderDTO> retOrderDTOList = (List<OrderDTO>) response.getEntity();
        
        assertThat(retOrderDTOList, notNullValue());
        assertThat(retOrderDTOList.size(), equalTo(1));
        assertThat(retOrderDTOList.get(0).getOrderId(), equalTo(ORDER_ID));
    }
    
    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=OrderRestEndpointTjwsTest#test040_findOrderById_pass
     *
     * <b><code>test040_findOrderById_pass</code>:</b><br>
     *  Tests the behavior for find a order by id.
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed to server.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The endpoint is invoked.
     *
     * <b>Postconditions:</b><br>
     *  Return a orderDTO to the given id.
     * </pre>
     */
    @Test
    public void test040_findOrderById_pass() {
        LOGGER.debug(LOG_PREFIX + "test040_findOrderById_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        final OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(ORDER_ID);
        
        when(this.orderControllerEJB.findOrderById(ORDER_ID, USER_ID)).thenReturn(orderDTO);
        
        // ___________________________________________
        // WHEN
        // -------------------------------------------
        final Response response = sut.findOrderById(ORDER_ID, USER_ID);
        
        // ___________________________________________
        // THEN
        // -------------------------------------------
        assertThat(response, notNullValue());
        assertThat(response.getStatus(), equalTo(Status.OK.getStatusCode()));
        assertThat(response.getMediaType().toString(), equalTo(OrderRestEndpoint.MEDIA_TYPE_JSON_V1));
        
        final OrderDTO retOrderDTO = (OrderDTO) response.getEntity();
        
        assertThat(retOrderDTO, notNullValue());
        assertThat(retOrderDTO.getOrderId(), equalTo(ORDER_ID));
    }
    
    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=OrderRestEndpointTjwsTest#test040_addLine_pass
     *
     * <b><code>test040_addLine_pass</code>:</b><br>
     *  Tests the behavior for adding a order line.
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed to server.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The endpoint is invoked.
     *
     * <b>Postconditions:</b><br>
     *  Ensure the 'addLine' method is invoked in orderController is invoked.
     * </pre>
     */
    @Test
    public void test040_addLine_pass() {
        LOGGER.debug(LOG_PREFIX + "test040_addLine_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        final OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(ORDER_ID);
        
        // ___________________________________________
        // WHEN
        // -------------------------------------------
        final Response response = sut.addLine(orderDTO, USER_ID);
        
        // ___________________________________________
        // THEN
        // -------------------------------------------
        assertThat(response, notNullValue());
        assertThat(response.getStatus(), equalTo(Status.NO_CONTENT.getStatusCode()));
        assertThat(response.getMediaType().toString(), equalTo(OrderRestEndpoint.MEDIA_TYPE_JSON_V1));
        
        verify(orderControllerEJB).addLine(orderDTO, USER_ID);
    }
    
    /**
     * <pre>
     *  mvn clean install -Parq-wildfly-remote -Dtest=OrderRestEndpointTjwsTest#test050_deleteOrderById_pass
     *
     * <b><code>test050_deleteOrderById_pass</code>:</b><br>
     *  Tests the behavior for deleting a order.
     *
     * <b>Preconditions:</b><br>
     *  - Artifact must be successful deployed to server.
     *
     * <b>Scenario:</b><br>
     *  The following steps are executed:
     *  - The endpoint is invoked.
     *
     * <b>Postconditions:</b><br>
     *  
     * </pre>
     */
    @Test
    public void test050_deleteOrderById_pass() {
        LOGGER.debug(LOG_PREFIX + "test050_deleteOrderById_pass");

        // ___________________________________________
        // GIVEN
        // -------------------------------------------
        
        // ___________________________________________
        // WHEN
        // -------------------------------------------
        final Response response = sut.deleteOrderById(ORDER_ID, USER_ID);
        
        // ___________________________________________
        // THEN
        // -------------------------------------------
        assertThat(response, notNullValue());
        assertThat(response.getStatus(), equalTo(Status.NO_CONTENT.getStatusCode()));
        assertThat(response.getMediaType().toString(), equalTo(OrderRestEndpoint.MEDIA_TYPE_JSON_V1));
        
        verify(orderControllerEJB).deleteOrderById(ORDER_ID, USER_ID);
    }
}
