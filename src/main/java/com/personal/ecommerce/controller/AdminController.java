package com.personal.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.personal.ecommerce.entity.Product;
import com.personal.ecommerce.service.CategoryService;
import com.personal.ecommerce.service.ProductService;

@RestController
public class AdminController {

    @Autowired
    private CategoryService _categoryService;

    @Autowired
    private ProductService _productService;
    
    @PostMapping("/addCategory")
    @PreAuthorize("hasRole('ADMIN')")
    public String addCategory(@RequestParam(name = "categoryName") String categoryName){
        return _categoryService.addCategory(categoryName);
    }

    @PostMapping("/addProduct")
    @PreAuthorize("hasRole('ADMIN')")
    public Product addProduct(@RequestBody Product product){
        return _productService.addProduct(product);
    }

    @PostMapping("/assignProductToCategory")
    @PreAuthorize("hasRole('ADMIN')")
    public String assignProductToCategory(@RequestParam(name = "productId") Long productId, @RequestParam(name = "categoryId") Long categoryId){
        return _productService.assignProductToCategory(productId, categoryId);
    }

    @PostMapping("/assignAndCreateProductsToCategory")
    public String assignAndCreateProductsToCategory(@RequestParam(name = "categoryId") Long categoryId, @RequestBody List<Product> products){
        return _productService.assignAndCreateProductsToCategory(categoryId, products);
    }


}
