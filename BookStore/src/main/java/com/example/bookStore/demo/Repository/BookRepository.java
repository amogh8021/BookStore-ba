package com.example.bookStore.demo.Repository;


import com.example.bookStore.demo.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> , JpaSpecificationExecutor<Book> {

    Optional<Book> findByTitleAndAuthor(String title, String author);




}
