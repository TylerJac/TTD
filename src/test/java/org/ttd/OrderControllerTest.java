package org.ttd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)  // This annotation sets up a test for the OrderController layer
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;  // MockMvc simulates HTTP requests to the controller

    @MockBean
    private OrderService orderService;  // Mock the OrderService to simulate service layer interactions

    @MockBean
    private OrderRepository orderRepository;  // Mock the OrderRepository

    private Order mockOrder;  // A mock Order object to use in tests

    @BeforeEach
    void setUp() {
        // This method runs before each test. Initializes the mockOrder object with sample data.
        mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setCustomerName("John Doe");
        mockOrder.setOrderDate(LocalDate.now());
        mockOrder.setShippingAddress("123 Main St");
        mockOrder.setTotal(100.00);
    }

    @Test
    public void testCreateOrder() throws Exception {
        // This test verifies that a valid order can be created using a POST request

        // Set up a sample order
        Order order = new Order();
        order.setCustomerName("Jane Doe");
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress("456 Elm St");
        order.setTotal(200.0);

        // Mock the behavior of the orderService to return the order when the createOrder method is called
        when(orderService.createOrder(any(Order.class))).thenReturn(order);

        // Use MockMvc to simulate a POST request to "/api/orders"
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(order)))
                .andExpect(status().isOk());  // Expecting an HTTP 200 OK response
    }

    @Test
    public void testCreateOrderWithInvalidName() throws Exception {
        // This test verifies that an invalid order (with an empty customer name) results in a 400 Bad Request

        // Set up an invalid order with an empty customer name
        Order order = new Order();
        order.setCustomerName(""); // Invalid name
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress("456 Elm St");
        order.setTotal(200.0);

        // Use MockMvc to simulate a POST request to "/api/orders" with the invalid order
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(order)))
                .andExpect(status().isBadRequest())  // Expecting an HTTP 400 Bad Request response due to validation error
                .andExpect(result -> assertInstanceOf(ValidationException.class, result.getResolvedException()))  // Verifying the exception type
                .andExpect(result -> assertEquals("Customer name is mandatory", Objects.requireNonNull(result.getResolvedException()).getMessage()));  // Verifying the error message
    }

    @Test
    public void testDeleteNonExistentOrder() throws Exception {
        // This test verifies that trying to delete a non-existent order returns a 404 Not Found

        // Mock the behavior of the orderService to throw a ResourceNotFoundException when trying to delete a non-existent order
        doThrow(new ResourceNotFoundException("Order not found"))
                .when(orderService).deleteOrder(999L);

        // Use MockMvc to simulate a DELETE request to "/api/orders/999"
        mockMvc.perform(delete("/api/orders/999"))
                .andExpect(status().isNotFound());  // Expecting an HTTP 404 Not Found response
    }

    @Test
    void testGetOrderById() {
        // This test verifies that an order can be fetched by its ID

        // Mock the repository's findById method to return the mock order when called
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        // Call the orderService to fetch the order
        Order order = orderService.getOrderById(1L);

        // This is just a test for fetching; assertions can be added to verify behavior
        assertNotNull(order);  // Ensure the fetched order is not null
    }

    @Test
    public void testUpdateOrder() throws Exception {
        // This test verifies that an existing order can be updated via a PUT request

        // Set up an updated order
        Order updatedOrder = new Order();
        updatedOrder.setCustomerName("Jane Doe");
        updatedOrder.setOrderDate(LocalDate.now());
        updatedOrder.setShippingAddress("456 Oak St");
        updatedOrder.setTotal(150.00);
        updatedOrder.setId(1L);  // Set the same ID as the order being updated

        // Use MockMvc to simulate a PUT request to "/api/orders/1" with the updated order
        mockMvc.perform(put("/api/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(updatedOrder)))
                .andExpect(status().isOk());  // Expecting an HTTP 200 OK response

        // Use ArgumentCaptor to capture the argument passed to the orderService's updateOrder method
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderService).updateOrder(eq(1L), orderCaptor.capture());

        // Get the captured order and verify that it has the updated details
        Order capturedOrder = orderCaptor.getValue();
        assertEquals("Jane Doe", capturedOrder.getCustomerName());  // Verify the updated customer name
        assertEquals("456 Oak St", capturedOrder.getShippingAddress());  // Verify the updated shipping address
        assertEquals(150.00, capturedOrder.getTotal());  // Verify the updated total amount
    }

}

