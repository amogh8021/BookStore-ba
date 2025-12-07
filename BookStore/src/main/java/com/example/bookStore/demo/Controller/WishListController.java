package com.example.bookStore.demo.Controller;

import com.example.bookStore.demo.Entity.Book;
import com.example.bookStore.demo.Entity.User;
import com.example.bookStore.demo.Service.UserService;
import com.example.bookStore.demo.Service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishListController {

    private final WishlistService service;
    private final UserService userService; // to get user from JWT

    @PostMapping("/add")
    public ResponseEntity<String> addToWishlist(@RequestParam Long bookId, Principal principal) {
        User user = userService.getUserFromPrincipal(principal);
        String msg = service.addBookToWishlist(user, bookId);
        return ResponseEntity.ok(msg);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> removeFromWishlist(@RequestParam Long bookId, Principal principal) {
        User user = userService.getUserFromPrincipal(principal);
        String msg = service.deleteFromWishlist(user, bookId);
        return ResponseEntity.ok(msg);
    }

    @GetMapping("/books")
    public ResponseEntity<List<Book>> getWishlist(Principal principal) {
        User user = userService.getUserFromPrincipal(principal);
        List<Book> books = service.getBooksFromWishlist(user);

        return ResponseEntity.ok(books);
    }
}
