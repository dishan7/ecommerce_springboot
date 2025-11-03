package com.personal.ecommerce.repository;

import com.personal.ecommerce.dto.UserDto;
import com.personal.ecommerce.entity.Cart;
import com.personal.ecommerce.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository _userRepository;

    private User user;

    @BeforeEach
    public void setup(){
        user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("test");
        user.setRole("USER");
        Cart cart = new Cart();
        user.setCart(cart);
        user.setOrders(new ArrayList<>());
    }

    @Test
    public void testUserSuccessfully(){
        User savedUser = _userRepository.save(user);
        Assertions.assertNotNull(savedUser);
        Assertions.assertEquals("test@gmail.com", savedUser.getEmail());
        Assertions.assertEquals("USER", savedUser.getRole());
    }

//    @AfterEach
//    public void cleanup(){
//        _userRepository.delete(user);
//    }
}
