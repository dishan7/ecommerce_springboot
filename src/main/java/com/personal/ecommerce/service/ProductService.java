package com.personal.ecommerce.service;

import java.time.Duration;
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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

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

    @Autowired
    private WebClient webClient;
    
    public List<Product> fetchAllProducts(){
        return _productRepository.findAll();
    }

    public Product addProduct(Product product){
        if(product.getInventoryCount() == null) product.setInventoryCount(0);
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
            if(product.getInventoryCount() == null) product.setInventoryCount(0);
        }
        category.getProductList().addAll(products);
        _categoryRepository.save(category);
        //don't need to save category again as cascade is set
        //_productRepository.saveAll(products);
        return "Products assigned to category successfully";
    }

    public Product addInventoryToProduct(Long productId, Integer inventory) throws Exception {
        Product product = _productRepository.findById(productId).orElse(null);
        if(product == null){
            throw new Exception("product not found");
        }
        product.setInventoryCount(product.getInventoryCount() + inventory);
        return _productRepository.save(product);
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
        Product product = _productRepository.findById(productId).orElse(null);
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
        ProductInCart productInCart = _productInCartRepository.findById(productInCartId).orElse(null);
        if(productInCart == null){
            return "Product not found in cart";
        }
        Cart cart = user.getCart();
        cart.getProducts().remove(productInCart);
        _productInCartRepository.delete(productInCart);
        _cartRepository.save(cart);
        return "Product removed from the cart successfully";
    }

    public Product getProductById(Long productId){
        return _productRepository.findById(productId).orElse(null);
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

    public Mono<Product> getProductsAsync(){
        Mono<Product> apiResultMono = webClient.get()
                .uri("http://localhost:9070/products")
                .retrieve()
                .bodyToMono(Product.class)
                .doOnSuccess(apiResult -> {
                    System.out.println("Result thrown by thread: " + Thread.currentThread().getName());
                });

        return apiResultMono;
    }

    public Flux<Product> getProductsFluxStream(Long categoryId){
        return Flux.interval(Duration.ofSeconds(3))
                .take(20)
                .flatMap(i -> {
                    System.out.println("Generation product data for item " + i + " on thread " + Thread.currentThread().getName());
                    return webClient.get()
                            .uri(uriBuilder -> uriBuilder.path("/products")
                                    .queryParam("categoryId", categoryId)
                                    .build())
                            .retrieve()
                            .bodyToFlux(Product.class)
                            .doOnNext(productResult -> {
                                System.out.println("Product data fetched for item: " + i + "on thread: " + Thread.currentThread().getName());
                            });
                }).doOnComplete(() -> {
                    System.out.println("stream generation completed on thread:" + Thread.currentThread().getName());
                }).doOnError((error) -> {
                    System.out.println("stream generation completed on thread:" + Thread.currentThread().getName());
                });
    }
}
