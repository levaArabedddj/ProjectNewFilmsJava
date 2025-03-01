package com.example.oopkursova.Controllers;


import com.example.oopkursova.DTO.DtoMovie;
import com.example.oopkursova.Entity.Director;
import com.example.oopkursova.Entity.Movies;
import com.example.oopkursova.Entity.Users;
import com.example.oopkursova.Exception.ApiException;
import com.example.oopkursova.Repository.DirectorRepo;
import com.example.oopkursova.Repository.UsersRepo;
import com.example.oopkursova.loger.Loggable;
import com.example.oopkursova.Repository.MoviesRepo;
import com.example.oopkursova.Service.MovieService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/Film")
public class MovieControllers {

    private final MoviesRepo moviesRepo;
    private final MovieService movieService;

    private final DirectorRepo directorRepo;
    @Autowired
    private UsersRepo usersRepo;

    private static final Logger logger = LoggerFactory.getLogger(MovieControllers.class);

    @Autowired
    public MovieControllers(MoviesRepo moviesRepo, MovieService movieService, DirectorRepo directorRepo) {
        this.moviesRepo = moviesRepo;
        this.movieService = movieService;
        this.directorRepo = directorRepo;
    }

    @Loggable
    @PostMapping("/create_movie")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> createFilm(@Valid @RequestBody Movies movies, Principal principal) {
        try {
            // Извлечение текущего пользователя из контекста
            String username = principal.getName();
            Users user = usersRepo.findByUserName(username)
                    .orElseThrow(() -> new ApiException("User not found"));

            // Проверяем, есть ли профиль режиссёра у пользователя
            Director director = directorRepo.findByUsers(user)
                    .orElseThrow(() -> new ApiException("You are not a director"));


            // Проверка, существует ли фильм с таким названием
            Optional<Movies> existingMovie = moviesRepo.findByTitle(movies.getTitle());
            if (existingMovie.isPresent()) {
                throw new ApiException("Film is already exist");
            }

            // Создание нового фильма
            Movies newMovie = new Movies();
            newMovie.setTitle(movies.getTitle());
            newMovie.setDescription(movies.getDescription());
            newMovie.setGenre(movies.getGenre());
            newMovie.setDirector(director);

            moviesRepo.save(newMovie);
            logger.info("New movie created: {}", newMovie);

            DtoMovie dtoMovie = new DtoMovie();
            dtoMovie.setTitle(newMovie.getTitle());
            dtoMovie.setDescription(newMovie.getDescription());
            dtoMovie.setGenre(newMovie.getGenre());
            dtoMovie.setDateTimeCreated(newMovie.getDateTimeCreated());

            // Возврат DTO в ответе
            return ResponseEntity.ok(dtoMovie);
        } catch (ApiException e) {
            logger.error("Error creating movie: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error creating movie", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some error while creating film");
        }
    }




    @Loggable
    @GetMapping("/movie_details")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> getFilms(Principal principal) {
        // Проверка на наличие авторизованного пользователя
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Вы не авторизованы. Пожалуйста, выполните вход.");
        }

        // Получение имени пользователя (email) из Principal
        String currentUserName = principal.getName();

        // Проверка на корректность email
        if (currentUserName == null || currentUserName.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка получения информации о пользователе.");
        }

        // Вызов сервиса для получения фильмов
        List<DtoMovie> movies = movieService.getMoviesByUser(currentUserName);

        // Возвращаем список фильмов
        return ResponseEntity.ok(movies);
    } // метод написан




    @Loggable
    @PostMapping("/update_movie/{filmId}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> updateMovie(
            @PathVariable("filmId") Long filmId,
            @Valid @RequestBody Movies updatedData,
            Principal principal) {
        try {
            // Получение имени текущего пользователя
            String username = principal.getName();

            // Поиск пользователя по имени
            Users user = usersRepo.findByUserName(username)
                    .orElseThrow(() -> new ApiException("User not found"));

            // Проверка, существует ли фильм и принадлежит ли он текущему пользователю
            Movies existingMovie = moviesRepo.findById(filmId)
                    .orElseThrow(() -> new ApiException("Film not found"));

            // Проверка, принадлежит ли фильм текущему пользователю
            if (existingMovie.getDirector().getUsers().getUser_id() != user.getUser_id()) {
                throw new ApiException("Ви не маєте дозволу редагувати цей фільм");
            }

            // Обновление данных фильма
            existingMovie.setTitle(updatedData.getTitle());
            existingMovie.setDescription(updatedData.getDescription());
            existingMovie.setGenre(updatedData.getGenre());

            // Сохранение изменений
            moviesRepo.save(existingMovie);

            DtoMovie dtoMovie = new DtoMovie();
            dtoMovie.setTitle(existingMovie.getTitle());
            dtoMovie.setDescription(existingMovie.getDescription());
            dtoMovie.setGenre(existingMovie.getGenre());
            dtoMovie.setDateTimeCreated(existingMovie.getDateTimeCreated());
            // Возврат обновленного фильма
            return ResponseEntity.ok(dtoMovie);

        } catch (ApiException e) {
            logger.error("Error updating movie: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while updating movie", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some error while updating movie");
        }
    }






    @Loggable
    @DeleteMapping("/DeleteFilm/{filmId}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> deleteMovie(@PathVariable("filmId") Long filmId,
                                         Principal principal) {
        try {
            // Получение имени текущего пользователя
            String username = principal.getName();

            // Поиск пользователя по имени
            Users user = usersRepo.findByUserName(username)
                    .orElseThrow(() -> new ApiException("User not found"));

            // Проверка, существует ли фильм и принадлежит ли он текущему пользователю
            Movies deleteMovie = moviesRepo.findById(filmId)
                    .orElseThrow(() -> new ApiException("Film not found"));

            if (deleteMovie.getDirector().getUsers().getUser_id() != user.getUser_id()) {
                throw new ApiException("You don't have permission to edit this film");
            }
            moviesRepo.delete(deleteMovie);

            return ResponseEntity.status(HttpStatus.OK).body("Film deleted successfully");


        } catch (ApiException e) {
            logger.error("Error updating movie: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            logger.error("Unexpected error while updating movie", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some error while updating movie");
        }

    }




}
