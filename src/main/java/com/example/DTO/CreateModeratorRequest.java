package com.example.DTO;

import com.example.Enum.AdminPermission;
import lombok.Data;

@Data
public class CreateModeratorRequest {
    private String userNameAdmin ;
    private String gmail;
    private String password;
    private AdminPermission permission;
    private Long movieId;
}
