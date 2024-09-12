package org.ttd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    // Fetch an order by ID
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    // Update an existing order
    public Order updateOrder(Long id, Order updatedOrder) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setCustomerName(updatedOrder.getCustomerName());
                    order.setOrderDate(updatedOrder.getOrderDate());
                    order.setShippingAddress(updatedOrder.getShippingAddress());
                    order.setTotal(updatedOrder.getTotal());
                    return orderRepository.save(order);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }


    // Delete an order by ID
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        orderRepository.delete(order);
    }

    // Fetch all orders (optional if needed)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}


