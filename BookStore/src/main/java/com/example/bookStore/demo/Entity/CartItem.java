package com.example.bookStore.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    private int quantity;

    private BigDecimal pricePerUnit;       // Original book price

    private BigDecimal totalPrice;         // pricePerUnit * quantity (before discount)

    private Double discountPercent;        // discount in %, e.g., 10.0

    private BigDecimal finalPrice;         // price after discount


    @PrePersist
    @PreUpdate
    public void calculatePrices() {
        if (pricePerUnit != null && quantity > 0) {
            this.totalPrice = pricePerUnit.multiply(BigDecimal.valueOf(quantity));

            double discount = 0.0;
            if (discountPercent != null) {
                discount = discountPercent;
            }

            BigDecimal discountMultiplier = BigDecimal.valueOf((100.0 - discount) / 100.0);

            this.finalPrice = pricePerUnit
                    .multiply(BigDecimal.valueOf(quantity))
                    .multiply(discountMultiplier)
                    .setScale(2, RoundingMode.HALF_UP);
        }
    }


}
