package com.kareem.miniamazon.service;

import com.kareem.miniamazon.dto.CartDTO;
import com.kareem.miniamazon.dto.CartItemDTO;
import com.kareem.miniamazon.dto.CartItemRequest;
import com.kareem.miniamazon.entity.Cart;
import com.kareem.miniamazon.entity.CartItem;
import com.kareem.miniamazon.entity.Product;
import com.kareem.miniamazon.entity.User;
import com.kareem.miniamazon.exception.InsufficientStockException;
import com.kareem.miniamazon.exception.ResourceNotFoundException;
import com.kareem.miniamazon.repository.CartItemRepository;
import com.kareem.miniamazon.repository.CartRepository;
import com.kareem.miniamazon.repository.ProductRepository;
import com.kareem.miniamazon.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;




    public CartDTO addToCart(String email, CartItemRequest cartItemRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElse(Cart.builder().user(user).build());

        // Save cart if new (though auth service creates one usually)
        if (cart.getId() == null) cart = cartRepository.save(cart);

        Product product = productRepository.findById(cartItemRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStockQuantity() < cartItemRequest.getQuantity()) {
            throw new InsufficientStockException("Requested quantity exceeds stock");
        }
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item-> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        // Check if item already exists in the cart to update quantity
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + cartItemRequest.getQuantity();
            if (product.getStockQuantity() < newQuantity) {
                throw new InsufficientStockException("Requested quantity exceeds stock");
            }
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        }else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(cartItemRequest.getQuantity())
                    .build();
            cart.addItem(newItem);
            cartItemRepository.save(newItem);
        }
        // Add to in-memory cart list so the response (DTO) includes the new item immediately
        cart.reCalculateTotal();
        cartRepository.save(cart);

        return mapToDTO(cart);
    }


    private CartDTO mapToDTO(Cart cart) {
        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(item -> CartItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .price(item.getProduct().getPrice())
                        .quantity(item.getQuantity())
                        .subTotal(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .collect(Collectors.toList());

        return CartDTO.builder()
                .id(cart.getId())
                .totalAmount(cart.getTotalAmount())
                .items(itemDTOs)
                .build();
    }
}
