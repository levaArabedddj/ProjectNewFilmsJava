package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.Users;
import com.example.oopkursova.loger.Loggable;
import com.example.oopkursova.Repository.UsersRepo;
import com.example.oopkursova.Service.UserrService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class UserControllers {

    private static final Logger logger = LoggerFactory.getLogger(UserControllers.class);
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserrService userService;
    private final UsersRepo usersRepo;

    public UserControllers(UserrService userService, UsersRepo usersRepo) {
        this.userService = userService;
        this.usersRepo = usersRepo;
    }
    @Loggable
    @GetMapping("/add_users")
    public String addStudent(Model model) {
        model.addAttribute("users", new Users());
        return "add_users";
    }

    @Loggable
    @PostMapping("/add_users")
    public String processForm(@Valid Users users, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            logger.error("Validation failed for user: {}", users);
            return "/add_users";
        }

        String rawPassword = users.getPassword();
        users.setRole("ROLE_USER");
        users.setPassword(passwordEncoder.encode(rawPassword));
        usersRepo.save(users);
        logger.info("User saved successfully: {}", users);
        return "Main";
    }


}
