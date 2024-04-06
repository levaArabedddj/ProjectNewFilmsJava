package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.Users;
import com.example.oopkursova.Service.UserService;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
public class UserControllers {
    private final UserService userService;

    public UserControllers(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/create")
    public ResponseEntity<Users> createUsers(@RequestBody Users user, UriComponentsBuilder uriBuilder) {
        Users createdUser = userService.usersCreate(user);
        return ResponseEntity.created(uriBuilder.path("/user/{id}").buildAndExpand(createdUser.getUser_id()).toUri()).body(createdUser);
    }

}
