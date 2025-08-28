package com.personal.ecommerce.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.personal.ecommerce.dto.UserDto;
import com.personal.ecommerce.entity.User;
import com.personal.ecommerce.service.UserService;

@RestController
public class UserController {
    
    @Autowired
    private UserService _userService;

    @GetMapping("/test")
    public String test(){
        return "Hello World!!";
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody UserDto userDto){
        User registeredUser = _userService.registerUser(userDto);
        String verificationToken = UUID.randomUUID().toString();
        String verificationUrl = "http://localhost:9070/verifyRegistrationToken?token=" + verificationToken;
        System.out.println("Please verify the user using this link " + verificationUrl);
        _userService.saveVerificationToken(registeredUser, verificationToken);
        return registeredUser;
    }

    @PostMapping("/verifyRegistrationToken")
    public String verifyRegistrationToken(@RequestParam String token){
        boolean isVerificationTokenValid = _userService.verifyRegistrationToken(token);
        if(!isVerificationTokenValid){
            return "Verification token is not valid. Please try again";
        }
        _userService.enableUser(token);
        return "Token verification is successful";
    }
}
