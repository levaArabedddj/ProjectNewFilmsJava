package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCacheDTO implements Serializable {
    private Long userId;
    private String username;
    private String role;
    private String gmail;
    private String password;
}

