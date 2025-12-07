package com.example.bookStore.demo.Service;

import com.example.bookStore.demo.Entity.Book;
import com.example.bookStore.demo.Entity.User;
import com.example.bookStore.demo.Entity.Wishlist;
import com.example.bookStore.demo.Repository.BookRepository;
import com.example.bookStore.demo.Repository.WishListRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishListRepository wishListRepository;
    private final BookRepository bookRepository;

    // Add book to wishlist
    @Transactional
    public String addBookToWishlist(User user, Long bookId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found for id: " + bookId));

        boolean exists = wishListRepository.existsByUserAndBook(user, book);
        if (exists) {
            throw new RuntimeException("Book already in wishlist: " + book.getTitle());
        }

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .book(book)
                .build();

        wishListRepository.save(wishlist);

        return "Book added to wishlist: " + book.getTitle();
    }

    // Delete book from wishlist
    @Transactional
    public String deleteFromWishlist(User user, Long bookId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found for id: " + bookId));

        boolean exists = wishListRepository.existsByUserAndBook(user, book);
        if (!exists) {
            throw new RuntimeException("Book not found in wishlist: " + book.getTitle());
        }

        wishListRepository.deleteByUserAndBook(user, book);

        return "Book removed from wishlist: " + book.getTitle();
    }

    // Get all books from user's wishlist
    public List<Book> getBooksFromWishlist(User user) {

        List<Wishlist> wishlistItems = wishListRepository.findByUser(user);

        return wishlistItems.stream()
                .map(Wishlist::getBook)
                .toList();
    }
}
