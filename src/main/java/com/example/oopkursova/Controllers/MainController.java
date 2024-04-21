package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.Actors;
import com.example.oopkursova.Entity.Movies;
import com.example.oopkursova.Entity.ShootingDay;
import com.example.oopkursova.Entity.Users;
import com.example.oopkursova.Repository.ActorRepo;
import com.example.oopkursova.Repository.MoviesRepo;
import com.example.oopkursova.Repository.ShootingDayRepo;
import com.example.oopkursova.Repository.UsersRepo;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

@Controller
public class MainController {

    private final UsersRepo usersRepo;
    private final MoviesRepo moviesRepo;
    private final ActorRepo actorRepo;
    private final ShootingDayRepo shootingDayRepo;

    public MainController(UsersRepo usersRepo, MoviesRepo moviesRepo, ActorRepo actorRepo, ShootingDayRepo shootingDayRepo) {
        this.usersRepo = usersRepo;
        this.moviesRepo = moviesRepo;

        this.actorRepo = actorRepo;
        this.shootingDayRepo = shootingDayRepo;
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
    @PostMapping("/")
    public String processForm(@Valid Users users, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println("validation failed");
            return "add_users";
        }
        usersRepo.save(users);
        return "MenuDirectors";
    }

    //
    @GetMapping("/movie_details")
    public String GetFilms(Model model) {
        List<Movies> films = moviesRepo.findAll();
        model.addAttribute("list", films);
        return "movie_details";
    }


  //два метода вниз для работ с созданием  фильма
    @PostMapping("/create_movie")
    public String CreateFilm(@Valid Movies movies, BindingResult bindingResult){
        moviesRepo.save(movies);
        return "MenuDirectors";
    }
    @GetMapping("/create_movie")
    public String addMovie(Model model){
        List<Actors> actors = actorRepo.findAll();
        model.addAttribute("list", actors);
        model.addAttribute("movies",new Movies());
        return "create_movie";
    }
    @PostMapping("/create_shootingDay")
    public String CreateShootingDay(@Valid ShootingDay shootingDay, BindingResult bindingResult){
        shootingDayRepo.save(shootingDay);
        return "MenuDirectors";
    }


    @GetMapping("/create_shootingDay")
    public String CreateShootingDayGet(Model model){
       model.addAttribute("dayShooting",new ShootingDay());
        return "create_shootingDay";
    }



    @GetMapping("/add_users")
    public String addStudent(Model model) {
        model.addAttribute("users", new Users());
        return "add_users";
    }
}
