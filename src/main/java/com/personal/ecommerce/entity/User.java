package com.personal.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.personal.ecommerce.enums.ROLES;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    private String password;

    private ROLES role;

    private boolean isEnabled;

    @OneToOne
    private Cart cart;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private List<Order> orders;
}
