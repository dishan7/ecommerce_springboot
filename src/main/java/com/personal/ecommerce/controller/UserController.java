package com.personal.ecommerce.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.personal.ecommerce.dto.UserDto;
import com.personal.ecommerce.entity.User;
import com.personal.ecommerce.service.UserService;

import jakarta.validation.Valid;

@RestController
public class UserController {
    
    @Autowired
    private UserService _userService;

    @GetMapping("/test")
    public String test(){
        return "Hello World!!";
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody @Valid UserDto userDto){
        User registeredUser = _userService.registerUser(userDto);
        String verificationToken = UUID.randomUUID().toString();
        String verificationUrl = "http://localhost:9070/verifyRegistrationToken?token=" + verificationToken;
        System.out.println("Please verify the user using this link " + verificationUrl);
        _userService.saveVerificationToken(registeredUser, verificationToken);
        return registeredUser;
    }

    @GetMapping("/verifyRegistrationToken")
    public String verifyRegistrationToken(@RequestParam(name = "token") String token){
        boolean isVerificationTokenValid = _userService.verifyRegistrationToken(token);
        if(!isVerificationTokenValid){
            return "Verification token is not valid. Please try again";
        }
        _userService.enableUser(token);
        return "Token verification is successful";
    }

    @PostMapping("/signin")
    public String loginUser(@RequestParam(name = "email") String email,
    @RequestParam(name = "password") String password){
        return _userService.loginUser(email, password);
    }

    @PostMapping("/loginTest")
    @PreAuthorize("hasRole('USER')")
    public String loginTest(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("Authenticated user: " + username);
        authentication.getAuthorities().forEach(authority -> {
            System.out.println("Authority: " + authority.getAuthority());
        });
        if(authentication.getAuthorities().isEmpty()){
            System.out.println("No authorities found for the user.");
            return "No authorities found for the user.";
        }
        System.out.println("User has ROLE_USER authority");
        return "Test successful!";
    }

    @PostMapping("/fetchUserDetails")
    @PreAuthorize("hasRole('USER')")
    public String fetchUserDetails(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println(authentication.isAuthenticated());
        System.out.println("Authenticated user: " + username);
        // User user = _userService.fetchUserByEmail(email);
        authentication.getAuthorities().forEach(authority -> {
            System.out.println("Authority: " + authority.getAuthority());
        });
        if(authentication.getAuthorities().isEmpty()){
            System.out.println("No authorities found for the user.");
            return "No authorities found for the user.";
        }
        return "User details fetched successfully";
    }
}
