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

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;

    private Order mockOrder;

    @BeforeEach
    void setUp() {
        mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setCustomerName("John Doe");
        mockOrder.setOrderDate(LocalDate.now());
        mockOrder.setShippingAddress("123 Main St");
        mockOrder.setTotal(100.00);
    }

    @Test
    public void testCreateOrder() throws Exception {
        Order order = new Order();
        order.setCustomerName("Jane Doe");
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress("456 Elm St");
        order.setTotal(200.0);

        when(orderService.createOrder(any(Order.class))).thenReturn(order);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(order)))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateOrderWithInvalidName() throws Exception {
        Order order = new Order();
        order.setCustomerName(""); // Invalid name
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress("456 Elm St");
        order.setTotal(200.0);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(order)))
                .andExpect(status().isBadRequest()) // Expecting a 400 Bad Request due to validation error
                .andExpect(result -> assertInstanceOf(ValidationException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("Customer name is mandatory", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void testDeleteNonExistentOrder() throws Exception {
        doThrow(new ResourceNotFoundException("Order not found"))
                .when(orderService).deleteOrder(999L);

        mockMvc.perform(delete("/api/orders/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetOrderById() {
        // Mock repository's findById method to return a sample order
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        // Test fetching an order by its ID
        Order order = orderService.getOrderById(1L);
    }

    @Test
    public void testUpdateOrder() throws Exception {
        // Mock service layer's findById and save methods
        Order updatedOrder = new Order();
        updatedOrder.setCustomerName("Jane Doe");
        updatedOrder.setOrderDate(LocalDate.now());
        updatedOrder.setShippingAddress("456 Oak St");
        updatedOrder.setTotal(150.00);
        updatedOrder.setId(1L);  // Set the same ID as the order being updated

        // Perform the PUT request using MockMvc
        mockMvc.perform(put("/api/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(updatedOrder)))
                .andExpect(status().isOk());

        // Use ArgumentCaptor to capture the argument passed to updateOrder
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderService).updateOrder(eq(1L), orderCaptor.capture());

        Order capturedOrder = orderCaptor.getValue();

        // Verify that the captured order has the updated details
        assertEquals("Jane Doe", capturedOrder.getCustomerName());
        assertEquals("456 Oak St", capturedOrder.getShippingAddress());
        assertEquals(150.00, capturedOrder.getTotal());
    }

}


