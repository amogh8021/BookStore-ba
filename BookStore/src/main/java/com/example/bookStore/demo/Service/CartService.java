package com.example.bookStore.demo.Service;


import com.example.bookStore.demo.Dtos.AddToCartRequest;
import com.example.bookStore.demo.Dtos.CartItemResponse;
import com.example.bookStore.demo.Dtos.CartResponse;
import com.example.bookStore.demo.Entity.*;
import com.example.bookStore.demo.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;
    private final OffersRepository offersRepository;

    //logic to add the items in the cart

   public CartResponse addToCart(AddToCartRequest request, Long userId) {

       User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found for this id"));

       //1. Get a Cart or create a new cart

       //check whether the cart is already created for this user or not

       Cart cart = cartRepository.findByUser(user).orElseGet(() -> {

           Cart newCart = Cart.builder()
                   .user(user)
                   .cartItemList(null)
                   .totalPrice(BigDecimal.valueOf(0))
                   .updatedAt(LocalDateTime.now())
                   .createdAt(LocalDateTime.now())
                   .totalItems(0)
                   .build();


           return cartRepository.save(newCart);

       });


       //get the books or items

       Book book = bookRepository.findById(request.getBook_id()).orElseThrow(() -> new RuntimeException("book is not available"));

       //check if the book is already exists in the cart

       List<CartItem> cartItems = cart.getCartItemList();

       CartItem matchedItem = null;

       if (cartItems != null) {
           matchedItem = cartItems.stream()
                   .filter(item -> item.getBook().getId().equals(book.getId()))
                   .findFirst()
                   .orElse(null);
       }

       if (matchedItem != null) {
           // Book already in cart → increase quantity
           matchedItem.setQuantity(matchedItem.getQuantity() + request.getItems());
           matchedItem.calculatePrices(); // Update total price
       } else {
           // Book not in cart → create a new CartItem
           CartItem newItem = CartItem.builder()
                   .cart(cart)
                   .book(book)
                   .quantity(request.getItems())
                   .pricePerUnit(BigDecimal.valueOf(book.getPrice()))



                   .build();


           newItem.calculatePrices();

           if (cartItems == null) {
               cartItems = new ArrayList<>();
               cart.setCartItemList(cartItems);
           }

           cartItems.add(newItem);
       }

       //update cart totals

       int totalItems = cart.getCartItemList().stream()
               .mapToInt(CartItem::getQuantity)
               .sum();

       BigDecimal totalPrice = cart.getCartItemList().stream()
               .map(item -> item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO)
               .reduce(BigDecimal.ZERO, BigDecimal::add);
       ;



 cart.setTotalItems(totalItems);
 cart.setTotalPrice(totalPrice);
       cart.setUpdatedAt(LocalDateTime.now());

       cartRepository.save(cart);

       return convertToCartResponse(cart);

   }

    private CartResponse convertToCartResponse(Cart cart) {
        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getId())
                .totalPrice(cart.getTotalPrice())
                .finalPrice(cart.getCartItemList().stream()
                        .map(item -> item.getFinalPrice() != null ? item.getFinalPrice() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .totalItems(cart.getTotalItems())
                .items(cart.getCartItemList().stream().map(item -> CartItemResponse.builder()
                        .itemId(item.getId())
                        .bookId(item.getBook().getId())
                        .bookTitle(item.getBook().getTitle())
                        .quantity(item.getQuantity())
                        .pricePerUnit(item.getPricePerUnit())
                        .discountPercent(item.getDiscountPercent())
                        .totalPrice(item.getTotalPrice())
                        .finalPrice(item.getFinalPrice())
                        .build()
                ).toList())
                .build();
    }




    //logic to remove the item from the cart

    public CartResponse removeFromCart(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));

        List<CartItem> items = cart.getCartItemList();

        if (items == null || items.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        CartItem toRemove = items.stream()
                .filter(item -> item.getBook().getId().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Book not found in cart"));

        items.remove(toRemove);
        cartItemRepository.delete(toRemove);

        // Update totals
        int totalItems = items.stream().mapToInt(CartItem::getQuantity).sum();
        BigDecimal totalPrice = items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalItems(totalItems);
        cart.setTotalPrice(totalPrice);
        cart.setUpdatedAt(LocalDateTime.now());

        cartRepository.save(cart);

        return convertToCartResponse(cart);
    }


    //update qunatity

    public CartResponse updateQuantity(Long userId, Long bookId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));

        List<CartItem> items = cart.getCartItemList();
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        CartItem item = items.stream()
                .filter(ci -> ci.getBook().getId().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Book not found in cart"));

        if (quantity <= 0) {
            // remove item if quantity = 0
            items.remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            item.calculatePrices(); // recalc totalPrice per item
        }

        // update totals
        int totalItems = items.stream().mapToInt(CartItem::getQuantity).sum();
        BigDecimal totalPrice = items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalItems(totalItems);
        cart.setTotalPrice(totalPrice);
        cart.setUpdatedAt(LocalDateTime.now());

        cartRepository.save(cart);

        return convertToCartResponse(cart);
    }
  //get cart

    public CartResponse getCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));

        return convertToCartResponse(cart);
    }


    //clear cart

    public CartResponse clearCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));

        if (cart.getCartItemList() != null) {
            cartItemRepository.deleteAll(cart.getCartItemList());
            cart.getCartItemList().clear();
        }

        cart.setTotalItems(0);
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setUpdatedAt(LocalDateTime.now());

        cartRepository.save(cart);

        return convertToCartResponse(cart);
    }





    // add this field


    // ================= Apply Coupon =================
    public CartResponse applyCoupon(Long userId, String couponCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));

        Offers offer = offersRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new RuntimeException("Invalid coupon"));

        if (!offer.isActive()) {
            throw new RuntimeException("Coupon expired or inactive");
        }

        // Apply the offer to all cart items
        cart.getCartItemList().forEach(item -> {
            item.setAppliedOffer(offer);
            item.calculatePrices(); // recalc finalPrice
        });

        // Update cart totals
        updateCartTotals(cart);

        cartRepository.save(cart);

        return convertToCartResponse(cart);
    }

    // ================= Helper: update totalPrice and totalItems =================

    private void updateCartTotals(Cart cart) {
        BigDecimal subTotal = BigDecimal.ZERO;
        int totalItems = 0;

        for (CartItem item : cart.getCartItemList()) {
            subTotal = subTotal.add(
                    item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO
            );
            totalItems += item.getQuantity();
        }

        cart.setTotalPrice(subTotal);
        cart.setTotalItems(totalItems);
        cart.setUpdatedAt(LocalDateTime.now());
    }










}
