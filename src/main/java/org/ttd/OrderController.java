package org.ttd;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        return ResponseEntity.ok(orderService.createOrder(order));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @Valid @RequestBody Order order) {
        return ResponseEntity.ok(orderService.updateOrder(id, order));
    }

}

