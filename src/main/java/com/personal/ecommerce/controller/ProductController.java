package com.personal.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.personal.ecommerce.entity.Product;
import com.personal.ecommerce.entity.User;
import com.personal.ecommerce.service.ProductService;
import com.personal.ecommerce.service.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ProductController {

    @Autowired
    private UserService _userService;

    @Autowired
    private ProductService _productService;

    @GetMapping("/products")
    public List<Product> fetchProducts(@RequestParam(name = "categoryId", required = false) Long categoryId){
        if(categoryId != null){
            return _productService.fetchProductsByCategoryId(categoryId);
        }
        return _productService.fetchAllProducts();
    }

    // @PostMapping("/addProductToCart")
    // @PreAuthorize("hasRole('USER')")
    // public String addProductToCart(@RequestParam(name = "productId") Long productId, 
    // @RequestParam(name = "cartId") Long cartId){
    //     return _productService.addProductToCart(productId, cartId);
    // }

    @PostMapping("/addProductToCart")
    @PreAuthorize("hasRole('USER')")
    public String addProductToCart(@RequestParam(name = "productId") Long productId,
                                   @RequestParam(name = "quantity") Integer quantity){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = _userService.fetchUserByEmail(email);
        if(quantity == null){
            quantity = 1;
        }
        return _productService.addProductToCart(productId, user, quantity);
    }

    @PostMapping("/removeProductFromCart")
    @PreAuthorize("hasRole('USER')")
    public String removeProductFromCart(@RequestParam(name = "productInCartId") Long productInCartId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = _userService.fetchUserByEmail(email);
        return _productService.removeProductFromCart(productInCartId, user);
    }

    @GetMapping("/productsAsync")
    public Mono<Product> getProductsAsync(){
        return _productService.getProductsAsync();
    }

    @GetMapping(value = "/products/fluxStream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Product> getProductsFluxStream(@RequestParam(name = "categoryId") Long categoryId){
        return _productService.getProductsFluxStream(categoryId);
    }

    @GetMapping("/product")
    public Product getProductById(@RequestParam(name = "productId") Long productId){
        return _productService.getProductById(productId);
    }
}
