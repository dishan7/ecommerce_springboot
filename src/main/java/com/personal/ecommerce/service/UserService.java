package com.personal.ecommerce.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.personal.ecommerce.dto.UserDto;
import com.personal.ecommerce.entity.Cart;
import com.personal.ecommerce.entity.User;
import com.personal.ecommerce.entity.VerificationToken;
import com.personal.ecommerce.repository.CartRepository;
import com.personal.ecommerce.repository.UserRepository;
import com.personal.ecommerce.repository.VerificationTokenRepository;
import com.personal.ecommerce.util.TokenUtil;

import jakarta.transaction.Transactional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository _userRepository;

    @Autowired
    private VerificationTokenRepository _verificationTokenRepository;

    @Autowired
    private PasswordEncoder _passwordEncoder;

    @Autowired
    private CartRepository _cartRepository;

    @Transactional
    public User registerUser(UserDto userDto){
        User registerUser = new User();
        registerUser.setEmail(userDto.getEmail());
        registerUser.setPassword(_passwordEncoder.encode(userDto.getPassword()));
        registerUser.setRole(userDto.getRole());
        registerUser.setEnabled(false);
        Cart cart = new Cart();
        cart.setUser(registerUser);
        cart.setProducts(new ArrayList<>());
        _cartRepository.save(cart);
        registerUser.setCart(cart);
        return _userRepository.save(registerUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User fetchedUser = _userRepository.findByEmail(username);
        if(fetchedUser == null){
            throw new UsernameNotFoundException(username + " not found");
        }
        return org.springframework.security.core.userdetails.User
            .withUsername(fetchedUser.getEmail())
            .password(fetchedUser.getPassword())
            .roles(fetchedUser.getRole())
            .disabled(false)
            .build();
    }

    public void saveVerificationToken(User user, String verificationTokenString){
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(verificationTokenString);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(new Date(System.currentTimeMillis() + 100 * 60 * 60 * 24));
        _verificationTokenRepository.save(verificationToken);
    }

    public boolean verifyRegistrationToken(String token){
        VerificationToken storedToken = _verificationTokenRepository.findByToken(token);
        if(storedToken == null || storedToken.getExpiryDate().getTime() < System.currentTimeMillis()){
            return false;
        }
        return true;
    }

    public void enableUser(String token){
        VerificationToken storedToken = _verificationTokenRepository.findByToken(token);
        User fetchedUser = storedToken.getUser();
        fetchedUser.setEnabled(true);
        _userRepository.save(fetchedUser);
        _verificationTokenRepository.delete(storedToken);
    }

    public String loginUser(String email, String password){
        User fetchedUser = _userRepository.findByEmail(email);
        if(fetchedUser == null){
            return "User with email " + email + " not found";
        }
        if(fetchedUser.isEnabled() == false){
            return "User with email " + email + " is not enabled. Please verify your email.";
        }
        boolean isPasswordMatch = _passwordEncoder.matches(password, fetchedUser.getPassword());
        if(!isPasswordMatch){
            return "Invalid credentials. Please try again.";
        }
        return TokenUtil.generateToken(fetchedUser, fetchedUser.getRole());
    }

    public User fetchUserByEmail(String email){
        return _userRepository.findByEmail(email);
    }
    
}
