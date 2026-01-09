package com.example.bookStore.demo.Controller;

import com.example.bookStore.demo.Dtos.AddToCartRequest;
import com.example.bookStore.demo.Dtos.CartResponse;
import com.example.bookStore.demo.Entity.User;
import com.example.bookStore.demo.Repository.UserRepository;
import com.example.bookStore.demo.Service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;



    // ✅ Add item to cart
    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(@RequestBody AddToCartRequest request,
                                                  Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("user not found: " + email));

        return ResponseEntity.ok(cartService.addToCart(request, user.getId()));
    }

    // ✅ Remove item from cart
    @DeleteMapping("/remove/{bookId}")
    public ResponseEntity<CartResponse> removeFromCart(@PathVariable Long bookId,
                                                       Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("user not found: " + email));

        return ResponseEntity.ok(cartService.removeFromCart(user.getId(), bookId));
    }

    // ✅ Update quantity
    @PutMapping("/update/{bookId}")
    public ResponseEntity<CartResponse> updateQuantity(@PathVariable Long bookId,
                                                       @RequestParam int quantity,
                                                       Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("user not found: " + email));

        return ResponseEntity.ok(cartService.updateQuantity(user.getId(), bookId, quantity));
    }

    // ✅ Get cart
    @GetMapping
    public ResponseEntity<CartResponse> getCart(Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("user not found: " + email));

        return ResponseEntity.ok(cartService.getCart(user.getId()));
    }

    // ✅ Clear cart
    @DeleteMapping("/clear")
    public ResponseEntity<CartResponse> clearCart(Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("user not found: " + email));

        return ResponseEntity.ok(cartService.clearCart(user.getId()));
    }



    @PostMapping("/apply-coupon")
    public ResponseEntity<CartResponse> applyCoupon(@RequestParam String couponCode,
                                                    Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("user not found: " + email));

        return ResponseEntity.ok(cartService.applyCoupon(user.getId(), couponCode));
    }

}
