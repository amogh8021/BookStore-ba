package com.example.bookStore.demo.Controller;

import com.example.bookStore.demo.Entity.Order;
import com.example.bookStore.demo.Entity.Status;
import com.example.bookStore.demo.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ✅ Create new order (for logged-in user)
    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(Principal principal) {
        String email = principal.getName();
        Order order = orderService.createOrder(email);
        return ResponseEntity.ok(order);
    }

    // ✅ Get all orders (ADMIN only) with pagination
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Order>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(orderService.getAllOrders(page, size));
    }

    // ✅ Get all orders for the logged-in user
    @GetMapping("/my-orders")
    public ResponseEntity<Page<Order>> getUserOrders(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String email = principal.getName();
        return ResponseEntity.ok(orderService.getOrdersForUser(email, page, size));
    }

    // ✅ Get a specific order by ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // ✅ Update order status (ADMIN only)
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam Status status
    ) {
        Order updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    // ✅ Delete order (ADMIN only)
    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok("Order deleted successfully");
    }

    // ✅ Get new orders (within last 24 hours) with pagination
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/neworders")
    public ResponseEntity<Page<Order>> getNewOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(orderService.getNewOrders(page, size));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<Page<Order>> getPendingOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(orderService.getPendingOrders(page, size));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/completed")
    public ResponseEntity<Page<Order>> getCompletedOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(orderService.getCompletedOrders(page, size));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/cancelled")
    public ResponseEntity<Page<Order>> getCancelledOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(orderService.getCancelledOrders(page, size));
    }
}
