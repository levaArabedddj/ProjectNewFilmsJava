package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.Users;
import com.example.oopkursova.Repository.UsersRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MainController {

    private final UsersRepo usersRepo;

    public MainController(UsersRepo usersRepo) {
        this.usersRepo = usersRepo;
    }

    @GetMapping("//")
    public String home(Model model){
        List<Users> users = usersRepo.findAll();
        model.addAttribute("list", users);
        return "indexx";
    }
    @GetMapping("/")
    public String home(){
        return "Main";
    }

//    @GetMapping("/Get")
//    public String index(Model model) {
//        List<Users> users = usersRepo.findAll();
//        model.addAttribute("list", users);
//        return "indexx";
//    }
//    @GetMapping("/login")
//    public String login(){
//        return "/login";
//    }

    @PostMapping("/login")
    public String loginPost(){
        return "/login"; // Убедитесь, что это имя представления правильное
    }


//    @PostMapping("/logins")
//    public String loginPosts(@RequestParam String username, @RequestParam String password) {
//
//
//    }
}


