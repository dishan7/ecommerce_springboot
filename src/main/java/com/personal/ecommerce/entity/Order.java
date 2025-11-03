package com.personal.ecommerce.entity;

import com.personal.ecommerce.enums.ORDER_STATUS;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    private User user;

    @OneToMany
    private List<ProductInCart> products;

    private LocalDateTime timestamp;

    private Double orderValue;

    private ORDER_STATUS orderStatus;
}