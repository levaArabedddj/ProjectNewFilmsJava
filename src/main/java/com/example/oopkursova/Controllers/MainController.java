package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.Users;
import com.example.oopkursova.Repository.UsersRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/Get")
    public String index(Model model) {
        List<Users> users = usersRepo.findAll();
        model.addAttribute("list", users);
        return "indexx";
    }

}
