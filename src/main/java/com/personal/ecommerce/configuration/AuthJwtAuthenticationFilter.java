package com.personal.ecommerce.configuration;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.personal.ecommerce.util.TokenUtil;

import io.jsonwebtoken.Claims;
import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthJwtAuthenticationFilter extends OncePerRequestFilter{

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authrizationOnHeader = request.getHeader("authorization").substring(7);
        if(authrizationOnHeader == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authorization header is missing");
            return;
        }
        Claims fetchedClaims = TokenUtil.validateSignedToken(authrizationOnHeader);
        String username = fetchedClaims.getSubject();
        String role = fetchedClaims.get("roles", String.class);
        List<SimpleGrantedAuthority> authorityList = List.of(new SimpleGrantedAuthority(role));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, authorityList);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/login") || path.equals("/register") || path.equals("/test") || path.equals("/error") || path.equals("/verifyRegistrationToken") || path.equals("/signin") || path.equals("/addCategory") || 
        path.equals("/addProduct") || path.equals("/assignProductToCategory")
        || path.equals("/products") || path.equals("/assignAndCreateProductsToCategory") || path.equals("/addProductToCart") || path.equals("/fetchUserDetails");
    }
}
