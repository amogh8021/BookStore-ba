package com.example.bookStore.demo.Service;

import com.example.bookStore.demo.Entity.*;
import com.example.bookStore.demo.Repository.CartItemRepository;
import com.example.bookStore.demo.Repository.CartRepository;
import com.example.bookStore.demo.Repository.OrderRepository;
import com.example.bookStore.demo.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    // ✅ Create new order
    public Order createOrder(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("user not found " + email));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("cart not found for user " + email));

        if (cart.getCartItemList().isEmpty()) {
            throw new RuntimeException("Cannot create order: cart is empty");
        }

        // Create order items (deep copy from cart items)
        List<CartItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItemList()) {
            CartItem newItem = CartItem.builder()
                    .book(cartItem.getBook())
                    .quantity(cartItem.getQuantity())
                    .pricePerUnit(cartItem.getPricePerUnit())
                    .totalPrice(cartItem.getTotalPrice())
                    .discountPercent(cartItem.getDiscountPercent())
                    .finalPrice(cartItem.getFinalPrice())
                    .appliedOffer(cartItem.getAppliedOffer())
                    .build();
            orderItems.add(newItem);
        }

        // Calculate order totals
        BigDecimal subTotal = cart.getCartItemList().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPayable = cart.getCartItemList().stream()
                .map(CartItem::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountAmount = subTotal.subtract(totalPayable);

        // Collect applied coupon codes (if any)
        String appliedCoupon = cart.getCartItemList().stream()
                .filter(item -> item.getAppliedOffer() != null && item.getAppliedOffer().getCouponCode() != null)
                .map(item -> item.getAppliedOffer().getCouponCode())
                .distinct()
                .reduce((a, b) -> a + ", " + b)
                .orElse(null);

        // Create order
        Order order = Order.builder()
                .user(user)
                .item(orderItems)
                .orderDate(LocalDateTime.now())
                .status(Status.PENDING)
                .subTotal(subTotal)
                .discountAmount(discountAmount)
                .totalPayable(totalPayable)
                .appliedCoupon(appliedCoupon)
                .build();

        Order savedOrder = orderRepository.save(order);

        // Clear cart
        cartItemRepository.deleteAll(cart.getCartItemList());
        cart.getCartItemList().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setTotalItems(0);
        cartRepository.save(cart);

        return savedOrder;
    }

    // ✅ Get all orders for a user (paginated)
    public Page<Order> getOrdersForUser(String email, int page, int size) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("user not found for " + email));

        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        return orderRepository.findByUser(user, pageable);
    }

    // ✅ Get order by ID
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("order not found for id " + id));
    }

    // ✅ Update order status
    public Order updateOrderStatus(Long orderId, Status orderStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("order not found for id " + orderId));

        order.setStatus(orderStatus);
        return orderRepository.save(order);
    }

    // ✅ Delete order
    public void deleteOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new RuntimeException("order not found for id " + orderId);
        }
        orderRepository.deleteById(orderId);
    }

    // ✅ Get new orders (last 24 hours, paginated)
    public Page<Order> getNewOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        return orderRepository.findByOrderDateAfter(twentyFourHoursAgo, pageable);
    }

    // ✅ Get all orders (paginated)
    public Page<Order> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        return orderRepository.findAll(pageable);
    }

    // ✅ Get orders by status (paginated)
    public Page<Order> getOrderByStatus(Status status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        return orderRepository.findByStatus(status, pageable);
    }

    public Page<Order> getPendingOrders(int page, int size) {
        return getOrderByStatus(Status.PENDING, page, size);
    }

    public Page<Order> getCompletedOrders(int page, int size) {
        return getOrderByStatus(Status.COMPLETED, page, size);
    }

    public Page<Order> getCancelledOrders(int page, int size) {
        return getOrderByStatus(Status.CANCELLED, page, size);
    }
}
