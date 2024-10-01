package com.example.oopkursova.Controllers;


import com.example.oopkursova.Entity.Movies;
import com.example.oopkursova.Repository.UsersRepo;
import com.example.oopkursova.config.Users;
import com.example.oopkursova.loger.Loggable;
import com.example.oopkursova.Repository.MoviesRepo;
import com.example.oopkursova.Service.MovieService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/movie")
public class MovieControllers {

    private final MoviesRepo moviesRepo;
    private final MovieService movieService;

    @Autowired
    private UsersRepo usersRepo;

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
    public String GetFilms(Model model, Principal principal) {
        // Проверка на наличие авторизованного пользователя
        if (principal == null) {
            System.out.println("Вы не авторизованы. Пожалуйста, выполните вход.");
            return "redirect:/login";  // Перенаправление на страницу логина, если пользователь не авторизован
        }

        // Получение имени пользователя (email) из Principal
        String currentUserName = principal.getName();

        // Проверка на корректность email (например, пустая строка)
        if (currentUserName == null || currentUserName.isEmpty()) {
            System.out.println( "Ошибка получения информации о пользователе.");
            return "redirect:/login";  // Перенаправление на страницу ошибки, если email некорректный
        }

        // Поиск пользователя в базе данных по его email
        Optional<Users> optionalUser = usersRepo.findByName(currentUserName);

        // Проверка на существование пользователя
        if (optionalUser.isEmpty()) {
            System.out.println( "Пользователь не найден.");
            return "redirect:/login";  // Возвращаем страницу ошибки, если пользователь не найден
        }

        // Если пользователь найден, получаем его
        Users user = optionalUser.get();

        // Получение списка фильмов, которые были созданы этим пользователем
        List<Movies> films;
        try {
            films = moviesRepo.findByUser(user);

            // Проверка на наличие фильмов у пользователя
            if (films == null || films.isEmpty()) {
                model.addAttribute("message", "У вас пока нет фильмов.");
                films = new ArrayList<>();  // Инициализируем пустой список, чтобы избежать ошибок в представлении
            }
        } catch (Exception e) {
            // Ловим возможные исключения при получении фильмов и логируем ошибку
            System.out.println( "Ошибка при получении фильмов пользователя.");
            e.printStackTrace();  // Логирование ошибки (можно заменить на логгер)
            return "redirect:/login";  // Возвращаем страницу ошибки в случае исключения
        }

        // Добавление списка фильмов в модель для отображения на странице
        model.addAttribute("list", films);

        // Возвращаем страницу для отображения фильмов
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
