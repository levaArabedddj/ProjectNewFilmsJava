package com.example.oopkursova.Controllers;


import com.example.oopkursova.Entity.Movies;
import com.example.oopkursova.loger.Loggable;
import com.example.oopkursova.Repository.MoviesRepo;
import com.example.oopkursova.Service.MovieService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/movie")
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
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String CreateFilm(@Valid Movies movies){
        moviesRepo.save(movies);
        logger.info("New movie created: {}", movies);
        return "/MenuDirectors";
    }
    @Loggable
    @GetMapping("/create_movie")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String addMovie(Model model){
        model.addAttribute("movies",new Movies());
        return "/create_movie";
    }


    @Loggable
    @GetMapping("/movie_details")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String GetFilms(Model model) {
        List<Movies> films = moviesRepo.findAll();
        model.addAttribute("list", films);
        return "movie_details";
    }

    @Loggable
    @GetMapping("/edit_movie_details")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String showEditForm( Model model) {
        model.addAttribute("movie",new Movies());
        return "edit_movie_details";
    }

    @Loggable
    @PostMapping("/edit_movie_details")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String updateMovie(@RequestParam("id") Long id,  @Valid Movies updatedMovie) {
        Movies movie = movieService.findById(id);
        if (movie == null) {
            return "redirect:/error";
        }
        // Оновити дані фільму з отриманими значеннями
        movie.setTitle(updatedMovie.getTitle());
        movie.setDescription(updatedMovie.getDescription());
        movie.setGenre(updatedMovie.getGenre());
        movieService.update(movie);
        return "MenuDirectors";
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

        return "MenuDirectors"; // Перенаправление пользователя на другую страницу
    }




}
