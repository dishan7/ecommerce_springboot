package com.personal.ecommerce.dto;

import com.personal.ecommerce.enums.ROLES;

public class UserDto {

    private String email;

    private String password;

    private ROLES role;

    public UserDto() {
    }

    public UserDto(String email, String password, ROLES role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ROLES getRole() {
        return role;
    }

    public void setRole(ROLES role) {
        this.role = role;
    }
}
