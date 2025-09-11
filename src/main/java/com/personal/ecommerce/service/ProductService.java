package com.personal.ecommerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import com.personal.ecommerce.entity.Cart;
import com.personal.ecommerce.entity.Category;
import com.personal.ecommerce.entity.Product;
import com.personal.ecommerce.entity.User;
import com.personal.ecommerce.repository.CartRepository;
import com.personal.ecommerce.repository.CategoryRepository;
import com.personal.ecommerce.repository.ProductRepository;
import com.personal.ecommerce.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductService {

    @Autowired
    private UserRepository _userRepository;

    @Autowired
    private CategoryRepository _categoryRepository;

    @Autowired
    private ProductRepository _productRepository;

    @Autowired
    private CartRepository _cartRepository;
    
    public List<Product> fetchAllProducts(){
        return _productRepository.findAll();
    }

    public Product addProduct(Product product){
        return _productRepository.save(product);
    }

    @Transactional
    public String assignProductToCategory(Long productId, Long categoryId){
        Product product = _productRepository.findById(productId).orElse(null);
        if(product == null){
            return "Product not found";
        }
        Category category = _categoryRepository.findById(categoryId).get();
        if(category == null){
            return "Category not found";
        }
        product.setCategory(category);
        category.getProductList().add(product);
        _categoryRepository.save(category);
        //don't need to save product again as cascade is set
        return "Product assigned to category successfully";
    }

    @Transactional
    public String assignAndCreateProductsToCategory(Long categoryId, List<Product> products){
        Category category = _categoryRepository.findById(categoryId).orElse(null);
        if(category == null){
            return "Category not found";
        }
        for(Product product : products){
            product.setCategory(category);
        }
        category.getProductList().addAll(products);
        _categoryRepository.save(category);
        //don't need to save category again as cascade is set
        //_productRepository.saveAll(products);
        return "Products assigned to category successfully";
    }

    public List<Product> fetchProductsByCategoryId(Long categoryId){
        return _productRepository.findByCategoryCategoryId(categoryId);
    }

    // public String addProductToCart(Long productId, Long cartId){
    //     Product product = _productRepository.findById(productId).orElse(null);
    //     if(product == null){
    //         return "Product not found";
    //     }
    //     Cart cart = _cartRepository.findById(cartId).orElse(null);
    //     if(cart == null){
    //         return "Cart not found";
    //     }
    //     cart.getProducts().add(product);
    //     _cartRepository.save(cart);
    //     return "Product added to cart successfully";
    // }

    public String addProductToCart(Long productId, User user){
        Product product = _productRepository.findById(productId).get();
        if(product == null){
            return "Product not found";
        }
        Cart cart = user.getCart();
        cart.getProducts().add(product);
        _cartRepository.save(cart);
        return "Product added to cart successfully";
    }

}
