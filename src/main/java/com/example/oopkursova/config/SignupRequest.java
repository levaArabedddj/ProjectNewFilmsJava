package com.example.oopkursova.config;

import com.example.oopkursova.Entity.UserRole;
import lombok.Data;

@Data
public class SignupRequest {
    private String name;
    private String gmail;
    private String password;
    private UserRole role;
}
