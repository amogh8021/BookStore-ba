package com.example.bookStore.demo.Repository;


import com.example.bookStore.demo.Entity.Book;
import com.example.bookStore.demo.Entity.User;
import com.example.bookStore.demo.Entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishListRepository extends JpaRepository<Wishlist,Long> {

    List<Wishlist> findByUser(User user);
    boolean existsByUserAndBook(User user, Book book);
    void deleteByUserAndBook(User user, Book book);



}
