package com.kareem.miniamazon.repository;


import com.kareem.miniamazon.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Retrieve all orders associated with a specific user ID
    List<Order> findByUserId(Long userId);
}
