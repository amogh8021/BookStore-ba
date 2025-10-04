package com.example.bookStore.demo.Repository;


import com.example.bookStore.demo.Entity.Cart;
import com.example.bookStore.demo.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Find all items in a cart
    List<CartItem> findByCart(Cart cart);

    // Find a single item by user + book
    Optional<CartItem> findByCartUserIdAndBookId(Long userId, Long bookId);
}
