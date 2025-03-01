package com.example.oopkursova.config;

import com.example.oopkursova.Enum.Gender;
import com.example.oopkursova.Enum.UserRole;
import lombok.Data;

@Data
public class SignupRequest {
    private String userName;
    private String name;
    private String surName;
    private String gmail;
    private String password;
    private UserRole role;
    private Gender gender;
    private String phone;
}
