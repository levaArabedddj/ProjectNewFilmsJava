package com.example.oopkursova.config;

import lombok.Data;

@Data
public class SignupRequest {
    private String name;
    private String gmail;
    private String password;
    private  String role;
}
