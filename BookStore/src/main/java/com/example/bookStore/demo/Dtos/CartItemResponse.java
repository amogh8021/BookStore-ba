package com.example.bookStore.demo.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {


    private Long itemId;
    private Long bookId;
    private String bookTitle;
    private int quantity;
    private BigDecimal pricePerUnit;
    private Double discountPercent;
    private BigDecimal totalPrice;
    private BigDecimal finalPrice;
}
