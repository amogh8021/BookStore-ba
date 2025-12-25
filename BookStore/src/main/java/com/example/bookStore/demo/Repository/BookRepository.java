package com.example.bookStore.demo.Repository;


import com.example.bookStore.demo.Entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> , JpaSpecificationExecutor<Book> {

    Optional<Book> findByTitleAndAuthor(String title, String author);
    @Query("select distinct b.genre from Book b ")
    List<String> getAllGenre();

    @Query("select distinct b.author from Book b ")
    List<String> getAllAuthor();


    Page<Book> findByFeaturedTrue(Pageable pageable);






}
