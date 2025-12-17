package com.kareem.miniamazon.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
        reCalculateTotal();
    }
    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
        reCalculateTotal();
    }
    public void clearItems() {
        if (this.items != null) {
            this.items.clear();
        }
        this.totalAmount = BigDecimal.ZERO;
    }

    public void reCalculateTotal(){
        this.totalAmount = items.stream()
                .map(item-> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
