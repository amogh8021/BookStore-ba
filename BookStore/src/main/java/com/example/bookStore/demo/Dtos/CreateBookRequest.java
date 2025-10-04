package com.example.bookStore.demo.Dtos;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;



@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateBookRequest {

    @NotBlank
    private String author;

    @NotBlank
    private String title;

    @NotBlank
    private String genre;

    @NotBlank
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "1.0", message = "Price must be at least ₹1")
    private Double price;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    private LocalDate publishedDate;

    private String imageUrl;
}
