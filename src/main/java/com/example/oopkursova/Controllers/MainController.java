package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.Users;
import com.example.oopkursova.Repository.UsersRepo;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MainController {

    private final UsersRepo usersRepo;

    public MainController(UsersRepo usersRepo) {
        this.usersRepo = usersRepo;
    }

    @GetMapping("/")
    public String home(Model model){
        List<Users> users = usersRepo.findAll();
        model.addAttribute("list", users);
        return "indexx";
    }

    @GetMapping("/Get")
    public String index(Model model) {
        List<Users> users = usersRepo.findAll();
        model.addAttribute("list", users);
        return "indexx";
    }
    @PostMapping("/")
    public String processForm(@Valid Users users, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println("validation failed");
            return "add_users";
        }
        usersRepo.save(users);
        return "redirect:/";
    }

    @GetMapping("/add_users")
    public String addStudent(Model model) {
        model.addAttribute("users", new Users());
        return "add_users";
    }
}
