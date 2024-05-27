package com.example.oopkursova.Controllers;

import com.example.oopkursova.config.Users;
import com.example.oopkursova.Repository.UsersRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@AllArgsConstructor
//@RequestMapping("/Main/")
public class MainController {

    private final UsersRepo usersRepo;

        @GetMapping("//")
    public String home(Model model) {
        List<Users> users = usersRepo.findAll();
        model.addAttribute("list", users);
        return "indexx";
    }

    @GetMapping("/")
    public String home() {
        return "Main";
    }


    @GetMapping("/login")
    public String login() {
        return "login"; // Укажите имя представления вашей страницы логина
    }

    @GetMapping("/Main")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String MainController() {
        return "MenuDirectors";
    }
}



