package com.personal.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.ecommerce.dto.UserDto;
import com.personal.ecommerce.entity.Cart;
import com.personal.ecommerce.entity.User;
import com.personal.ecommerce.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockitoBean
    private UserService _userService;

    @Autowired
    private MockMvc _mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    public void testRegisterUser() throws Exception {
        UserDto userDto = new UserDto("test@gmail.com", "test", "USER");
        when(_userService.registerUser(Mockito.any(UserDto.class)))
                .thenReturn(user);
        String jsonBody = objectMapper.writeValueAsString(userDto);
        _mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType("application/json")
                        .content(jsonBody)
                        .with(csrf()))
                        .andExpect(status().isOk())
                        .andDo(print());
    }
}
