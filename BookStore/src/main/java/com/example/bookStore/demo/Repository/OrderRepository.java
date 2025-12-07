package com.example.bookStore.demo.Repository;


import com.example.bookStore.demo.Entity.Order;
import com.example.bookStore.demo.Entity.Status;
import com.example.bookStore.demo.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // For pagination
    Page<Order> findByStatus(Status status, Pageable pageable);

    Page<Order> findByUser(User user, Pageable pageable);

    Page<Order> findByOrderDateAfter(LocalDateTime dateTime, Pageable pageable);
}