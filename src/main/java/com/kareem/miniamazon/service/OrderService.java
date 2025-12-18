package com.kareem.miniamazon.service;

import com.kareem.miniamazon.dto.OrderDTO;
import com.kareem.miniamazon.dto.OrderItemDTO;
import com.kareem.miniamazon.entity.*;
import com.kareem.miniamazon.enums.OrderStatus;
import com.kareem.miniamazon.exception.InsufficientStockException;
import com.kareem.miniamazon.exception.ResourceNotFoundException;
import com.kareem.miniamazon.repository.CartRepository;
import com.kareem.miniamazon.repository.OrderRepository;
import com.kareem.miniamazon.repository.ProductRepository;
import com.kareem.miniamazon.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CartService cartService;





    @Transactional
    public OrderDTO placeOrder(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty");
        }
        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalAmount(cart.getTotalAmount())
                .build();

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException("Not enough stock for product: " + product.getName());
            }
            // Deduct stock
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(product.getPrice())
                    .build();
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        // Clear the user's cart after successful order
        cartService.clearCart(email);


        return mapToDTO(savedOrder);
    }
    public List<OrderDTO> getUserOrders(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return orderRepository.findByUserId(user.getId()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, String status){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        try{
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(newStatus);
        }catch (IllegalArgumentException  e){
            throw new IllegalArgumentException("Invalid order status: " + status);
        }
        Order savedOrder = orderRepository.save(order);
        return mapToDTO(savedOrder);

    }







    private OrderDTO mapToDTO(Order order) {
        List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
                .map(item -> OrderItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .priceAtPurchase(item.getPriceAtPurchase())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return OrderDTO.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(itemDTOs)
                .build();
    }
}
