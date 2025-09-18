package com.personal.ecommerce.repository;

import com.personal.ecommerce.entity.ProductInCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInCartRepository extends JpaRepository<ProductInCart, Long> {

}
