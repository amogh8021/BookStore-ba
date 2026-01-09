package com.example.bookStore.demo.Repository;


import com.example.bookStore.demo.Entity.Offers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OffersRepository extends JpaRepository<Offers,Long> {

    Optional<Offers> findByCouponCodeIgnoreCaseAndIsActiveTrue(String couponCode);

    List<Offers> findByIsActiveTrue();


    List<Offers> findByIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            LocalDate today1,
            LocalDate today2
    );
    Optional<Offers> findByCouponCode(String couponCode);


    List<Offers> findByIsFeaturedTrueAndIsActiveTrue();
}
