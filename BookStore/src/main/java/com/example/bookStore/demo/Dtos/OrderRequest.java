package com.example.bookStore.demo.Dtos;


import com.example.bookStore.demo.Entity.CartItem;
import com.example.bookStore.demo.Entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {

    private List<CartItem> item;

    private LocalDateTime orderDate;

    private Status status;
}

