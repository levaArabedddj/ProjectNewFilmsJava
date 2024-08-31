package com.example.oopkursova.Controllers;


import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class MainController {

    @GetMapping("/")
    public String home() {
        return "Main";
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @GetMapping("/Main")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String MainController() {
        return "MenuDirectors";
    }
}



