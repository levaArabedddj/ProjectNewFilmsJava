package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.Users;
import com.example.oopkursova.Repository.UsersRepo;
import com.example.oopkursova.Service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserControllers {

    private static final Logger logger = LoggerFactory.getLogger(UserControllers.class);

    private final UserService userService;
    private final UsersRepo usersRepo;

    public UserControllers(UserService userService, UsersRepo usersRepo) {
        this.userService = userService;
        this.usersRepo = usersRepo;
    }
    @GetMapping("/add_users")
    public String addStudent(Model model) {
        model.addAttribute("users", new Users());
        return "add_users";
    }

    @PostMapping("/")
    public String processForm(@Valid Users users, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Validation failed for user: {}", users);
            return "add_users";
        }
        usersRepo.save(users);
        logger.info("User saved successfully: {}", users);
        return "MenuDirectors";
    }


}
