package com.example.bookStore.demo.Service;

import com.example.bookStore.demo.Entity.Offers;
import com.example.bookStore.demo.Repository.OffersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OffersService {

    private final OffersRepository offersRepository;


    public Offers createOffer(Offers offer) {

        if (offer.getStartDate().isAfter(offer.getEndDate())) {
            throw new RuntimeException("Start date cannot be after end date");
        }

        offer.setActive(true);
        offer.setFeatured(false);
        return offersRepository.save(offer);
    }


    public void deleteOffer(Long offerId) {

        Offers offer = offersRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        offersRepository.delete(offer);
    }


    public List<Offers> getAllOffers() {
        return offersRepository.findAll();
    }

    public List<Offers> getAllActiveOffers() {
        return offersRepository.findByIsActiveTrue();
    }


    public List<Offers> getAllValidActiveOffers() {

        LocalDate today = LocalDate.now();

        return offersRepository
                .findByIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        today, today
                );
    }


    public Offers getValidOfferByCoupon(String couponCode) {

        LocalDate today = LocalDate.now();

        Offers offer = offersRepository
                .findByCouponCodeIgnoreCaseAndIsActiveTrue(couponCode)
                .orElseThrow(() -> new RuntimeException("Invalid coupon code"));

        if (today.isBefore(offer.getStartDate()) || today.isAfter(offer.getEndDate())) {
            throw new RuntimeException("Offer expired");
        }

        return offer;
    }

    public Offers toggleFeatured(Long offerId) {
        Offers offer = offersRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        offer.setFeatured(!offer.isFeatured()); // ⭐ toggle
        return offersRepository.save(offer);
    }


    public List<Offers> getFeaturedOffers() {
        return offersRepository.findByIsFeaturedTrueAndIsActiveTrue();
    }


}
