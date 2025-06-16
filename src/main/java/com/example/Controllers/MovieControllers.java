package com.example.Controllers;


import com.example.DTO.DtoMovie;
import com.example.ElasticSearch.ClassDocuments.MovieDocument;

import com.example.ElasticSearch.Service.MovieElasticService;
import com.example.Entity.Director;
import com.example.Entity.Movies;
import com.example.Entity.Users;
import com.example.Enum.DevelopmentStage;
import com.example.Enum.Genre;
import com.example.Exception.ApiException;
import com.example.Repository.DirectorRepo;
import com.example.Repository.MoviesRepo;
import com.example.Repository.UsersRepo;
import com.example.Service.MovieService;
import com.example.config.MyUserDetails;
import com.example.loger.Loggable;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import co.elastic.clients.elasticsearch.ElasticsearchClient;


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


    // Добавить в контролеры проверку что юзер может изменять текущие
    // фильм и проверять что авторизованный юзер именно тот за кого себя выдает


    private final ElasticsearchClient elasticsearchClient;

    private final MovieElasticService movieElasticService;

    @Autowired
    public MovieControllers(MoviesRepo moviesRepo, MovieService movieService, DirectorRepo directorRepo,  ElasticsearchClient elasticsearchClient, MovieElasticService movieElasticService) {
        this.moviesRepo = moviesRepo;
        this.movieService = movieService;
        this.directorRepo = directorRepo;
        this.elasticsearchClient = elasticsearchClient;


        this.movieElasticService = movieElasticService;
    }

    @Loggable
    @PostMapping("/create_movie")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> createFilm(
            @Valid @RequestBody Movies movies,
            @AuthenticationPrincipal MyUserDetails currentUser
    ) {
        System.out.println("+++ ENTERED createFilm()");
        logger.debug(">> enter createFilm, payload movies={}, currentUser={}", movies, currentUser);

        try {
            // 1) Получаем userId
            Long userId = currentUser.getUser_id();
            logger.debug("1) extracted userId from token: {}", userId);

            // 2) Загружаем юзера из БД
            Users user = usersRepo.findById(userId)
                    .orElseThrow(() -> new ApiException("User not found"));
            logger.debug("2) loaded Users entity: {}", user);

            // 3) Ищем профиль режиссёра
            Director director = directorRepo.findByUsers(user)
                    .orElseThrow(() -> new ApiException("You are not a director"));
            logger.debug("3) found Director profile: {}", director);

            // 4) Проверяем, не существует ли фильм с таким же названием
            Optional<Movies> existingMovie = moviesRepo.findByTitle(movies.getTitle());
            logger.debug("4) check existingMovie by title '{}': present={}", movies.getTitle(), existingMovie.isPresent());
            if (existingMovie.isPresent()) {
                throw new ApiException("Film already exists");
            }

            // 5) Создаём новую сущность фильма
            Movies newMovie = new Movies();
            newMovie.setTitle(movies.getTitle());
            newMovie.setDescription(movies.getDescription());
            newMovie.setGenre_film(movies.getGenre_film());
            newMovie.setDevelopmentStage(DevelopmentStage.CONCEPT);
            newMovie.setDirector(director);
            logger.debug("5) new Movie object prepared: {}", newMovie);

            // 6) Сохраняем в БД
            moviesRepo.save(newMovie);
            logger.debug("6) Movie saved to DB, id={}", newMovie.getId());

            // 7) Индексируем в Elasticsearch
            MovieDocument document = movieElasticService.mapToElastic(newMovie);
            logger.debug("7) mapped Movie to MovieDocument: {}", document);
            elasticsearchClient.index(i -> i
                    .index("movies")
                    .id(String.valueOf(newMovie.getId()))
                    .document(document)
            );
            logger.debug("7) indexed MovieDocument in Elasticsearch, id={}", newMovie.getId());

            // 8) Готовим DTO для ответа
            DtoMovie dtoMovie = new DtoMovie();
            dtoMovie.setTitle(newMovie.getTitle());
            dtoMovie.setDescription(newMovie.getDescription());
            dtoMovie.setGenre_film(Genre.valueOf(String.valueOf(newMovie.getGenre_film())));
            dtoMovie.setDateTimeCreated(newMovie.getDateTimeCreated());
            logger.debug("8) prepared DtoMovie: {}", dtoMovie);

            // 9) Возвращаем результат
            logger.debug("<< exit createFilm successfully");
            return ResponseEntity.ok(dtoMovie);

        } catch (ApiException e) {
            logger.error("API error in createFilm: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            logger.error("Unexpected error in createFilm", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Some error while creating film");
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
    @GetMapping("/getAllFilms")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> getAllFilmsDirector(
            Principal principal) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();

        List<DtoMovie> movies = movieService.getAllMovie(userId);
        return ResponseEntity.ok(movies);
    }


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
            existingMovie.setGenre_film(updatedData.getGenre_film());

            // Сохранение изменений
            moviesRepo.save(existingMovie);

            MovieDocument document = movieElasticService.mapToElastic(existingMovie);

            elasticsearchClient.index(i -> i
                    .index("movies")
                    .id(String.valueOf(existingMovie.getId()))
                    .document(document)
            );

            DtoMovie dtoMovie = new DtoMovie();
            dtoMovie.setTitle(existingMovie.getTitle());
            dtoMovie.setDescription(existingMovie.getDescription());
            dtoMovie.setGenre_film(Genre.valueOf(String.valueOf(existingMovie.getGenre_film())));
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
            elasticsearchClient.delete( i -> i
                    .index("movies")
                    .id(String.valueOf(deleteMovie.getId())
                    ));


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




    @GetMapping("/search")
    public ResponseEntity<List<DtoMovie>> searchMovies(@RequestParam String keyword) {
        List<DtoMovie> movies = movieService.searchMovies(keyword);
        return ResponseEntity.ok(movies);
    }


    @GetMapping("/elastic-search")
    public ResponseEntity<List<MovieDocument>> searchMoviesElastic(@RequestParam String keyword) {
        List<MovieDocument> movies = movieService.searchByKeyword(keyword);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/getMovieById/{movieId}")
    public ResponseEntity<?>getMovieById(@PathVariable Long movieId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();

        List<DtoMovie> movies = movieService.findById(movieId,userId);
        return ResponseEntity.ok(movies);

    }





}
