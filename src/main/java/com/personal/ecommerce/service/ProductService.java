package com.personal.ecommerce.service;

import java.util.List;

import com.personal.ecommerce.entity.*;
import com.personal.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

    @Autowired
    private ProductInCartRepository _productInCartRepository;
    
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

    @Transactional
    public String addProductToCart(Long productId, User user, Integer quantity){
        Product product = _productRepository.findById(productId).get();
        if(product == null){
            return "Product not found";
        }

        Cart cart = user.getCart();
        ProductInCart productInCart = new ProductInCart();
        productInCart.setProduct(product);
        productInCart.setQuantity(quantity);
        productInCart.setCart(cart);
        _productInCartRepository.save(productInCart);
        cart.getProducts().add(productInCart);
        _cartRepository.save(cart);
        return "Product added to cart successfully";
    }

    @Transactional
    public String removeProductFromCart(Long productInCartId, User user){
        ProductInCart productInCart = _productInCartRepository.findById(productInCartId).get();
        if(productInCart == null){
            return "Product not found in cart";
        }
        Cart cart = user.getCart();
        cart.getProducts().remove(productInCart);
        _productInCartRepository.delete(productInCart);
        _cartRepository.save(cart);
        return "Product removed from the cart successfully";
    }

    public PagedResponse<Product> getPaginatedProducts(int pageSize, String sortBy, int pageNumber, String sortDir, Long categoryId){
        Pageable pageable;
        if(sortDir.equals("asc")){
            pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
        }
        else{
            pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
        }

        Page<Product> pagedProduct = _productRepository.findByCategoryCategoryId(categoryId, pageable);

        return new PagedResponse<>(
                pagedProduct.getContent(),
                pagedProduct.getNumber(),
                pagedProduct.getSize(),
                pagedProduct.getTotalElements(),
                pagedProduct.getTotalPages(),
                pagedProduct.isFirst(),
                pagedProduct.isLast(),
                pagedProduct.hasNext(),
                pagedProduct.hasPrevious()
        );
    }

}
