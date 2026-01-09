package com.example.bookStore.demo.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    private Offers appliedOffer;



    private int quantity;

    private BigDecimal pricePerUnit;

    private BigDecimal totalPrice;

    private Double discountPercent;



   private BigDecimal finalPrice;


   ;



    @PrePersist
    @PreUpdate
    public void calculatePrices() {
        if (pricePerUnit != null && quantity > 0) {
            this.totalPrice = pricePerUnit.multiply(BigDecimal.valueOf(quantity));

            double discount = 0.0;

            // 1️⃣ Offer discount has priority if applied
            if (appliedOffer != null && appliedOffer.isActive()) {
                discount = appliedOffer.getDiscountPercentage();
            }
            // 2️⃣ Otherwise use item-specific discountPercent
            else if (discountPercent != null) {
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
