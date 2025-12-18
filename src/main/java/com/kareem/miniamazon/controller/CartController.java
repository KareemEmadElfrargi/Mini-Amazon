package com.kareem.miniamazon.controller;

import com.kareem.miniamazon.dto.ApiResponse;
import com.kareem.miniamazon.dto.CartDTO;
import com.kareem.miniamazon.dto.CartItemRequest;
import com.kareem.miniamazon.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getCart(Principal principal) {
        return ResponseEntity.ok(
                ApiResponse.<CartDTO>builder()
                        .success(true)
                        .message("Cart retrieved successfully")
                        .data(cartService.getCart(principal.getName()))
                        .build()
        );
    }
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartDTO>> addToCart(@RequestBody CartItemRequest request, Principal principal) {
        return ResponseEntity.ok(
                ApiResponse.<CartDTO>builder()
                        .success(true)
                        .message("Item added to cart")
                        .data(cartService.addToCart(principal.getName(), request))
                        .build()
        );
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<ApiResponse<CartDTO>> removeFromCart(@PathVariable Long cartItemId, Principal principal) {
        return ResponseEntity.ok(
                ApiResponse.<CartDTO>builder()
                        .success(true)
                        .message("Item removed from cart")
                        .data(cartService.removeFromCart(principal.getName(), cartItemId))
                        .build()
        );
    }
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<String>> clearCart(Principal principal) {
        cartService.clearCart(principal.getName());
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Cart cleared successfully")
                        .data(null)
                        .build()
        );
    }

}
