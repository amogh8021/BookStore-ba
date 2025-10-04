package com.example.bookStore.demo.Service;

import com.example.bookStore.demo.Entity.Book;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class BookSpecification {

    public static Specification<Book> hasGenre(String genre) {
        return (root, query, cb) -> {
            if (genre == null || genre.trim().isEmpty()) return null;
            String likePattern = "%" + genre.trim().toLowerCase() + "%";
            return cb.like(cb.lower(cb.trim(root.get("genre"))), likePattern);
        };
    }

    public static Specification<Book> hasAuthor(String author) {
        return (root, query, cb) -> {
            if (author == null || author.trim().isEmpty()) return null;
            String likePattern = "%" + author.trim().toLowerCase() + "%";
            return cb.like(cb.lower(cb.trim(root.get("author"))), likePattern);
        };
    }

    public static Specification<Book> hasTitle(String title) {
        return (root, query, cb) -> {
            if (title == null || title.trim().isEmpty()) return null;
            String likePattern = "%" + title.trim().toLowerCase() + "%";
            return cb.like(cb.lower(cb.trim(root.get("title"))), likePattern);
        };
    }

    public static Specification<Book> priceBetween(Double minPrice, Double maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) return null;
            if (minPrice == null) return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
            if (maxPrice == null) return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
            return cb.between(root.get("price"), minPrice, maxPrice);
        };
    }

    public static Specification<Book> publishedDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate == null && endDate == null) return null;
            if (startDate == null) return cb.lessThanOrEqualTo(root.get("publishedDate"), endDate);
            if (endDate == null) return cb.greaterThanOrEqualTo(root.get("publishedDate"), startDate);
            return cb.between(root.get("publishedDate"), startDate, endDate);
        };
    }
}
