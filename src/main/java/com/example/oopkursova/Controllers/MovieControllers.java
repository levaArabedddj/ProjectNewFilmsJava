package com.example.oopkursova.Controllers;


import com.example.oopkursova.Entity.Movies;
import com.example.oopkursova.Loggable;
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
    @Loggable
    @PostMapping("/create_movie")
    public String CreateFilm(@Valid Movies movies, BindingResult bindingResult){
        moviesRepo.save(movies);
        logger.info("New movie created: {}", movies);
        return "MenuDirectors";
    }
    @Loggable
    @GetMapping("/create_movie")
    public String addMovie(Model model){
        model.addAttribute("movies",new Movies());
        return "create_movie";
    }


    @Loggable
    @GetMapping("/movie_details")
    public String GetFilms(Model model) {
        List<Movies> films = moviesRepo.findAll();
        model.addAttribute("list", films);
        return "movie_details";
    }

    @Loggable
    @GetMapping("/EditDetailsMovie")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        Movies movie = movieService.findById(id);
        model.addAttribute("id", id);
        model.addAttribute("movie", movie);
        return "edit_movie_details";
    }

    @Loggable
    @PutMapping("/edit_movie_details/{id}")
    public String updateMovie(@PathVariable("id") Long id, @RequestBody Movies updatedMovie) {
        Movies movie = movieService.findById(id);
        if (movie == null) {
            return "redirect:/error";
        }
        // Оновити дані фільму з отриманими значеннями
        movie.setTitle(updatedMovie.getTitle());
        movie.setDescription(updatedMovie.getDescription());
        movie.setGenre(updatedMovie.getGenre());
        movieService.update(movie);
        logger.info("Movie updated: {}", movie);
        return "MenuDirectors"; // Перенаправити користувача на іншу сторінку після оновлення фільму
    }


    @GetMapping("/DeleteFilm")
    public String showDeleteFilmForm() {
        return "DeleteFilm";
    }

    @Loggable
    @PostMapping("/DeleteFilm")
    public String deleteMovie(@RequestParam("id") Long id) {
        Movies movie = movieService.findById(id);
        if (movie == null) {
            return "redirect:/error";
        }
        movieService.deleteMovie(id);
        logger.info("Movie deleted: {}", movie);
        return "MenuDirectors"; // Перенаправление пользователя на другую страницу
    }




}
