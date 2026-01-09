package com.example.bookStore.demo.Entity;


import jakarta.persistence.*;
import jdk.jshell.Snippet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    private User user;

    @OneToMany(cascade = CascadeType.ALL)
    private List<CartItem> item;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    private BigDecimal subTotal;
    private BigDecimal discountAmount;
    private BigDecimal totalPayable;

    private String appliedCoupon;
}
