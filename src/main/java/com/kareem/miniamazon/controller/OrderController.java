package com.kareem.miniamazon.controller;

import com.kareem.miniamazon.dto.ApiResponse;
import com.kareem.miniamazon.dto.OrderDTO;
import com.kareem.miniamazon.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @PostMapping("/place")
    public ResponseEntity<ApiResponse<OrderDTO>> placeOrder(Principal principal){
        return ResponseEntity.ok(
                ApiResponse.<OrderDTO>builder()
                        .success(true)
                        .message("Order Placed successfully")
                        .data(orderService.placeOrder(principal.getName()))
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getUserOrders(Principal principal) {
        return ResponseEntity.ok(
                ApiResponse.<List<OrderDTO>>builder()
                        .success(true)
                        .message("Orders retrieved successfully")
                        .data(orderService.getUserOrders(principal.getName()))
                        .build()
        );
    }
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(
                ApiResponse.<OrderDTO>builder()
                        .success(true)
                        .message("Order status updated successfully")
                        .data(orderService.updateOrderStatus(orderId, status))
                        .build()
        );
    }
}
