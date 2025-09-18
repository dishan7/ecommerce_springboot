package com.personal.ecommerce.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.personal.ecommerce.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    public List<Product> findByCategoryCategoryId(Long categoryId);

    public Page<Product> findByCategoryCategoryId(Long categoryId, Pageable pageable);

}
