package com.example.bookStore.demo.Controller;



import com.example.bookStore.demo.Entity.Offers;
import com.example.bookStore.demo.Service.OffersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OffersController {

    private final OffersService offersService;


    @PostMapping("/create")
    public ResponseEntity<Offers> createOffer(@RequestBody Offers offer) {
        Offers createdOffer = offersService.createOffer(offer);
        return ResponseEntity.ok(createdOffer);
    }


    @DeleteMapping("/delete/{offerId}")
    public ResponseEntity<String> deleteOffer(@PathVariable Long offerId) {
        offersService.deleteOffer(offerId);
        return ResponseEntity.ok("Offer deleted successfully");
    }


    @GetMapping("/get")
    public ResponseEntity<List<Offers>> getAllOffers() {
        return ResponseEntity.ok(offersService.getAllOffers());
    }


    @GetMapping("/active")
    public ResponseEntity<List<Offers>> getActiveOffers() {
        return ResponseEntity.ok(offersService.getAllActiveOffers());
    }


    @GetMapping("/valid")
    public ResponseEntity<List<Offers>> getValidActiveOffers() {
        return ResponseEntity.ok(offersService.getAllValidActiveOffers());
    }


    @PutMapping("/{offerId}/featured")
    public ResponseEntity<Offers> toggleFeatured(@PathVariable Long offerId) {
        Offers updatedOffer = offersService.toggleFeatured(offerId);
        return ResponseEntity.ok(updatedOffer);
    }




    @GetMapping("/validate")
    public ResponseEntity<Offers> validateCoupon(
            @RequestParam String couponCode
    ) {
        return ResponseEntity.ok(
                offersService.getValidOfferByCoupon(couponCode)
        );
    }
}

