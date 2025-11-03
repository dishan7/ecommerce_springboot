package com.personal.ecommerce.controller;

import com.personal.ecommerce.entity.Order;
import com.personal.ecommerce.entity.User;
import com.personal.ecommerce.service.OrderService;
import com.personal.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    @Autowired
    private UserService _userService;

    @Autowired
    private OrderService _orderService;

    @PostMapping("/placeOrder")
    @PreAuthorize("hasRole('USER')")
    public Order placeOrder() throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        if(userEmail == null){
            throw new Exception("User not found");
        }
        User user = _userService.fetchUserByEmail(userEmail);
        return _orderService.placeOrder(user);
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('USER')")
    public List<Order> fetchOrdersByUserEmail(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return _orderService.fetchOrdersByUserEmail(userEmail);
    }

    @PutMapping("/cancelOrder")
    @PreAuthorize("hasRole('USER')")
    public Order cancelOrder(@RequestParam(name = "orderId") Long orderId) throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        if(userEmail == null){
            throw new Exception("User not found");
        }
        if(!_orderService.isOrderValid(orderId, userEmail)){
            throw new Exception("invalid request");
        }
        return _orderService.cancelOrder(orderId);
    }

//    @PutMapping("/replaceOrder")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity replaceOrder(@RequestParam(name = "order_id") Long orderId) throws Exception{
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String userEmail = authentication.getName();
//        if(!_orderService.isOrderValid(orderId, userEmail)){
//            return ResponseEntity.status(400).body("User is invalid");
//        }
//        Order replaced = _orderService.replaceOrder(orderId);
//        return ResponseEntity.status(200).body(replaced);
//    }
}
