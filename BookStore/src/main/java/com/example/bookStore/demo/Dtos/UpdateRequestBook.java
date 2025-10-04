package com.example.bookStore.demo.Dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UpdateRequestBook {

    private String author;
    private String title;
    private String genre;
    private String description;
    private Double price;
    private Integer quantity;
    private LocalDate publishedDate;
    private String imageUrl;

}