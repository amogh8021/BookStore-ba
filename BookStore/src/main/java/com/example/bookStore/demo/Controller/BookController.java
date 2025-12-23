package com.example.bookStore.demo.Controller;


import com.example.bookStore.demo.Dtos.CreateBookRequest;
import com.example.bookStore.demo.Dtos.UpdateRequestBook;
import com.example.bookStore.demo.Entity.Book;
import com.example.bookStore.demo.Service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.context.annotation.Role;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;




@RequiredArgsConstructor
@RestController
@RequestMapping("/book")
public class BookController {

    private final BookService bookService;

    @PostMapping("/create-book")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<String> createBook(@Valid @RequestBody CreateBookRequest request){

        bookService.createBookRequest(request);
        return ResponseEntity.ok("book is successfully added");

    }






    @GetMapping("/info")
    public ResponseEntity<Book> getBookInfoById(@RequestParam Long id) {
        Book book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }


    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")  // restrict to admins
    public ResponseEntity<Book> updateBook(
            @PathVariable Long id,
            @RequestBody UpdateRequestBook request) {

        Book updatedBook = bookService.updateBook(id, request);
        return ResponseEntity.ok(updatedBook);
    }





    @GetMapping("/search")
    public Page<Book> searchBooks(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return bookService.searchBooksAdvanced(
                genre, author, title, minPrice, maxPrice,
                startDate, endDate, sortBy, direction, page, size
        );



    }



    @GetMapping("/list")
    public Page<Book> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return bookService.getAllBooksPaginated(page, size);
    }


    @DeleteMapping("/delete/{bookId}")

  public ResponseEntity<String>  deleteBook(@PathVariable Long bookid){

       bookService.deleteBook(bookid);
       return ResponseEntity.ok("the book is successfully deleted");

    }



    @GetMapping("/genres")
    public ResponseEntity<List<String>> getGenres() {
        return ResponseEntity.ok(bookService.getAllGenres());
    }

    @GetMapping("/authors")
    public ResponseEntity<List<String>> getAuthors() {
        return ResponseEntity.ok(bookService.getAllAuthor());
    }


    @GetMapping("/featured")
    public Page<Book> getFeaturedBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return bookService.getFeaturedBooks(page, size);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/featured")
    public Book setFeatured(
            @PathVariable Long id,
            @RequestParam boolean featured
    ) {
        return bookService.setFeatured(id, featured);
    }


    @GetMapping("/best-sellers")
    public List<Book> getBestSellers(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return bookService.getBestSellerBooks(limit);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book deleted successfully");
    }



}
