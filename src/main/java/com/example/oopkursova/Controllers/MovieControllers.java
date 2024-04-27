package com.example.oopkursova.Controllers;


import com.example.oopkursova.Entity.Movies;
import com.example.oopkursova.Repository.MoviesRepo;
import com.example.oopkursova.Service.MovieService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MovieControllers {

    private final MoviesRepo moviesRepo;
    private final MovieService movieService;

    private static final Logger logger = LoggerFactory.getLogger(MovieControllers.class);


    public MovieControllers(MoviesRepo moviesRepo, MovieService movieService) {
        this.moviesRepo = moviesRepo;
        this.movieService = movieService;
    }

    @PostMapping("/create_movie")
    public String CreateFilm(@Valid Movies movies, BindingResult bindingResult){
        moviesRepo.save(movies);
        logger.info("New movie created: {}", movies);
        return "MenuDirectors";
    }
    @GetMapping("/create_movie")
    public String addMovie(Model model){
        model.addAttribute("movies",new Movies());
        return "create_movie";
    }


    @GetMapping("/movie_details")
    public String GetFilms(Model model) {
        List<Movies> films = moviesRepo.findAll();
        model.addAttribute("list", films);
        return "movie_details";
    }

    @GetMapping("/edit_movie_details")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        Movies movie = movieService.findById(id);
        model.addAttribute("id", id);
        model.addAttribute("movie", movie);
        return "edit_movie_details";
    }

    @PutMapping("/update_movie")
    public String updateMovie(@ModelAttribute("movie") Movies movie) {
        movieService.update(movie);
        logger.info("Movie updated: {}", movie);
        return "redirect:/menu"; // Перенаправляем пользователя на другую страницу после обновления фильма
    }

}
