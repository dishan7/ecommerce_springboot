package com.personal.ecommerce.service;

import com.personal.ecommerce.entity.*;
import com.personal.ecommerce.enums.ORDER_STATUS;
import com.personal.ecommerce.repository.CartRepository;
import com.personal.ecommerce.repository.OrderRepository;
import com.personal.ecommerce.repository.ProductRepository;
import com.personal.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private UserRepository _userRepository;

    @Autowired
    private CartRepository _cartRepository;

    @Autowired
    private OrderRepository _orderRepository;

    @Autowired
    private ProductRepository _productRepository;

    @Transactional
    public Order placeOrder(User user) throws Exception{
        Order order = new Order();
        if(user == null){
            throw new Exception("User not found");
        }
        order.setUser(user);
        Cart cart = user.getCart();
        order.setTimestamp(LocalDateTime.now());
        order.setOrderValue(0d);
        order.setProducts(new ArrayList<>());
        for(ProductInCart productInCart:cart.getProducts()){
            Product product = productInCart.getProduct();
            product.setInventoryCount(product.getInventoryCount() - productInCart.getQuantity());
            _productRepository.save(product);
            order.getProducts().add(productInCart);
            order.setOrderValue(order.getOrderValue()+productInCart.getQuantity()*productInCart.getProduct().getProductPrice());
        }
        order.setOrderStatus(ORDER_STATUS.PLACED);
//        _orderRepository.save(order);
        cart.setProducts(new ArrayList<>());
        _cartRepository.save(cart);
        user.getOrders().add(order);
        _userRepository.save(user);
        return order;
    }

    public List<Order> fetchOrdersByUserEmail(String userEmail){
        User user = _userRepository.findByEmail(userEmail);
        return user.getOrders();
    }

    public User fetchUserByOrderId(Long orderId) throws Exception{
        Order savedOrder = _orderRepository.findById(orderId).orElse(null);
        if(savedOrder == null){
            throw new Exception("order not found");
        }
        return savedOrder.getUser();
    }

    @Transactional
    public Order cancelOrder(Long orderId) throws Exception{
        Order savedOrder = _orderRepository.findById(orderId).orElse(null);
        if(savedOrder == null){
            throw new Exception("order not found");
        }
        savedOrder.setOrderStatus(ORDER_STATUS.CANCELLED);
        for(ProductInCart productInCart: savedOrder.getProducts()){
            Product product = productInCart.getProduct();
            product.setInventoryCount(product.getInventoryCount() + productInCart.getQuantity());
            _productRepository.save(product);
        }
        return _orderRepository.save(savedOrder);
    }

    public boolean isOrderValid(Long orderId, String userEmail) throws Exception{
        Order order = _orderRepository.findById(orderId).orElse(null);
        if(order == null){
            throw new Exception("order not found");
        }
        User savedUser = _userRepository.findByEmail(userEmail);
        return order.getUser().getId().equals(savedUser.getId());
    }
}
