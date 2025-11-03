package com.personal.ecommerce.service;

import com.personal.ecommerce.dto.UserDto;
import com.personal.ecommerce.entity.Cart;
import com.personal.ecommerce.entity.User;
import com.personal.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService _userService;

    @Mock
    private UserRepository _userRepository;

    @Test
    public void testRegisterUser_Successfully() throws NullPointerException{
        User user = new User(1L, "test@gmail.com", "test", "USER", false, new Cart(), new ArrayList<>());
        when(_userRepository.save(user)).thenReturn(user);
        UserDto userDto = new UserDto("test@gmail.com", "test", "USER");
        User expectedUser = _userService.registerUser(userDto);
        Assertions.assertEquals("test@gmail.com", expectedUser.getEmail());
        Assertions.assertEquals("USER", expectedUser.getRole());
        Assertions.assertFalse(expectedUser.isEnabled());
    }
}
