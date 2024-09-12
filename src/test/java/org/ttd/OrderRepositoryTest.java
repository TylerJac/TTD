package org.ttd;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void testSaveOrder() {
        Order order = new Order();
        order.setCustomerName("John Doe");
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress("123 Main St");
        order.setTotal(100.0);

        Order savedOrder = orderRepository.save(order);

        assertNotNull(savedOrder.getId());
    }
}

