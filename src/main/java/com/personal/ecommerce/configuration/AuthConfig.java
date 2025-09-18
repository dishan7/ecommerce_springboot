package com.personal.ecommerce.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AuthConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){
        try{
            httpSecurity.csrf(csrf -> csrf.disable())
                        .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                            .requestMatchers("/register", "/test", "/error", 
                            "/verifyRegistrationToken", "signin", "/loginTest", "/addCategory", "/addProduct", "/assignProductToCategory", "/products", "/assignAndCreateProductsToCategory", "/addProductToCart", "/fetchUserDetails", "/removeProductFromCart", "/categories", "/api/v1/products")
                            .permitAll()
                            .anyRequest()
                            .authenticated())
                        .formLogin(formLogin -> formLogin.defaultSuccessUrl("/test", true)
                        .permitAll());
            return httpSecurity.build();
        }
        catch(Exception e){
            throw new RuntimeException("Error configuring security filter chain", e);
        }
    }
    
}
