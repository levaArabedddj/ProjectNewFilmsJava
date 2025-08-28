package com.example.Controllers;


import com.example.DTO.DtoMovie;
import com.example.DTO.MovieCreatedEvent;
import com.example.ElasticSearch.ClassDocuments.MovieDocument;

import com.example.ElasticSearch.Service.MovieElasticService;
import com.example.Entity.Director;
import com.example.Entity.Movies;
import com.example.Entity.Users;
import com.example.Enum.DevelopmentStage;
import com.example.Enum.Genre;
import com.example.Exception.ApiException;
import com.example.RabbitMQ.DtoRabbitMQ.MovieDtoRM;
import com.example.RabbitMQ.DtoRabbitMQ.MovieDtoUpdateRM;
import com.example.RabbitMQ.ElasticTask.ElasticConfigQueue;
import com.example.RabbitMQ.ElasticTask.UpdateFilmConfigQueue;
import com.example.Repository.DirectorRepo;
import com.example.Repository.MoviesRepo;
import com.example.Repository.UsersRepo;
import com.example.Service.MovieService;
import com.example.config.MyUserDetails;
import com.example.loger.Loggable;
import io.micrometer.core.annotation.Timed;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.springframework.web.client.ResourceAccessException;


import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/Film")
public class MovieControllers {

    private final MoviesRepo moviesRepo;
    private final MovieService movieService;

    private final DirectorRepo directorRepo;
    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(MovieControllers.class);
   // private final FilmEventPublisher publisher;

    private final RabbitTemplate rabbitTemplate;
    // Добавить в контролеры проверку что юзер может изменять текущие
    // фильм и проверять что авторизованный юзер именно тот за кого себя выдает


    private final ElasticsearchClient elasticsearchClient;

    private final MovieElasticService movieElasticService;



    @Autowired
    public MovieControllers(MoviesRepo moviesRepo, MovieService movieService, DirectorRepo directorRepo, RabbitTemplate rabbitTemplate, ElasticsearchClient elasticsearchClient, MovieElasticService movieElasticService) {
        this.moviesRepo = moviesRepo;
        this.movieService = movieService;
        this.directorRepo = directorRepo;
//        this.publisher = publisher;
        this.rabbitTemplate = rabbitTemplate;
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
        try {
            // 1) Получаем userId
            Long userId = currentUser.getUser_id();

            // 2) Ищем профиль режиссёра
            Long directorId = directorRepo.findIdBuUserId(userId)
                    .orElseThrow(() -> new ApiException("You are not a director"));
            Director directorRef = entityManager.getReference(Director.class, directorId);

            // 3) Проверяем, не существует ли фильм с таким же названием

            if(moviesRepo.existsByTitle(movies.getTitle())) {
                throw new ApiException("Title already exists");
            }

            // 4) Создаём новую сущность фильма
            Movies newMovie = new Movies();
            newMovie.setTitle(movies.getTitle());
            newMovie.setDescription(movies.getDescription());
            newMovie.setGenre_film(movies.getGenre_film());
            newMovie.setDevelopmentStage(DevelopmentStage.CONCEPT);
            newMovie.setDirector(directorRef);

            // 5) Сохраняем в БД
            moviesRepo.save(newMovie);

            // 6) Индексируем в Elasticsearch, но сначала в RabbitMQ
            MovieCreatedEvent evt = new MovieCreatedEvent(
                    newMovie.getId(),
                    newMovie.getTitle(),
                    newMovie.getDescription(),
                    newMovie.getGenre_film().name()
            );
            rabbitTemplate.convertAndSend(
                    ElasticConfigQueue.INDEX_EXCHANGE,
                    "movies.created",
                    evt
            );
            logger.info(" опубликован  MovieCreatedEvent в RabbitMQ: {}", evt);


            // 8) Готовим DTO для ответа
            DtoMovie dtoMovie = new DtoMovie();
            dtoMovie.setTitle(newMovie.getTitle());
            dtoMovie.setDescription(newMovie.getDescription());
            dtoMovie.setGenre_film(Genre.valueOf(String.valueOf(newMovie.getGenre_film())));
            dtoMovie.setDateTimeCreated(newMovie.getDateTimeCreated());

            // 9) Возвращаем результат
            return ResponseEntity.ok("Фильм создан");

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
    @PostMapping("/create_movie_async")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> createFilmAsync(
            @Valid @RequestBody Movies movies,
            @AuthenticationPrincipal MyUserDetails currentUser
    ) {

        Long userId = currentUser.getUser_id();
        if(moviesRepo.existsByTitle(movies.getTitle())) {
            throw new ApiException("Title already exists");
        }

        CompletableFuture.runAsync(()-> AsyncCreateMovie(movies, userId));

            return ResponseEntity.
                    accepted().
                    body("Фильм ставится в очередь на создание");
    }

    @Transactional
    public void AsyncCreateMovie(Movies movies, Long userId) {
        // 2) Ищем профиль режиссёра
        Long directorId = directorRepo.findIdBuUserId(userId)
                .orElseThrow(() -> new ApiException("You are not a director"));
        Director directorRef = entityManager.getReference(Director.class, directorId);

        // 3) Проверяем, не существует ли фильм с таким же названием

        // 4) Создаём новую сущность фильма
        Movies newMovie = new Movies();
        newMovie.setTitle(movies.getTitle());
        newMovie.setDescription(movies.getDescription());
        newMovie.setGenre_film(movies.getGenre_film());
        newMovie.setDevelopmentStage(DevelopmentStage.CONCEPT);
        newMovie.setDirector(directorRef);

        // 5) Сохраняем в БД
        moviesRepo.save(newMovie);

        // 6) Индексируем в Elasticsearch, но сначала в RabbitMQ
        MovieCreatedEvent evt = new MovieCreatedEvent(
                newMovie.getId(),
                newMovie.getTitle(),
                newMovie.getDescription(),
                newMovie.getGenre_film().name()
        );
        rabbitTemplate.convertAndSend(
                ElasticConfigQueue.INDEX_EXCHANGE,
                "movies.created",
                evt
        );
        logger.info(" опубликован  MovieCreatedEvent в RabbitMQ: {}", evt);
    }
    @Loggable
    @PostMapping("/create_movie_mq")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> createMovieMq(@Valid @RequestBody Movies movies,
                                           @AuthenticationPrincipal MyUserDetails userDetails){
        Long userId = userDetails.getUser_id();
        String email = userDetails.getGmail();
        if(moviesRepo.existsByTitle(movies.getTitle())) {
            throw new ApiException("Title already exists");
        }

        MovieDtoRM dto = new MovieDtoRM(
        );
        dto.setTitle(movies.getTitle());
        dto.setDescription(movies.getDescription());
        dto.setGenreFilm(movies.getGenre_film());
        dto.setUserId(userId);
        dto.setEmail(email);


      CompletableFuture.runAsync( ()-> {rabbitTemplate.convertAndSend("filmCreateExchange","filmcreate.binding",dto);}); // пусть так разницы в производительности и так нету

        return ResponseEntity.accepted().body(Map.of("status","accepted",
                "message","Movie creation in progress"));

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
    @PostMapping("/update_movie_mq/{filmId}")
    public ResponseEntity<?> updateMovieMq(@PathVariable("filmId") Long filmId,
                                           @Valid @RequestBody Movies movies,
                                           @AuthenticationPrincipal MyUserDetails details) {
        try {
            String currentUserName = details.getUsername();
            Users users = usersRepo.findByUserName(currentUserName).
                    orElseThrow(()-> new ResourceAccessException("You are not a director"));

            System.out.println("зашли в метод");
            // Проверка, существует ли фильм и принадлежит ли он текущему пользователю
            Movies existingMovie = moviesRepo.findById(filmId)
                    .orElseThrow(() -> new ApiException("Film not found"));

            // Проверка, принадлежит ли фильм текущему пользователю
            if (existingMovie.getDirector().getUsers().getUser_id() != users.getUser_id()) {
                throw new ApiException("Ви не маєте дозволу редагувати цей фільм");
            }

            MovieDtoUpdateRM dto = new MovieDtoUpdateRM();
            dto.setMovieId(filmId);
            dto.setTitle(movies.getTitle());
            dto.setDescription(movies.getDescription());
            dto.setGenreFilm(movies.getGenre_film());
            dto.setUserId(users.getUser_id());
            dto.setEmail(details.getGmail());

            System.out.println("отправляем сообщение в кролика");

            rabbitTemplate.convertAndSend(
                    "filmUpdateConfigExchange",
                    "filmupdate.binding",
                    dto);

        }catch (Exception e) {
            e.getMessage();
        }
        return ResponseEntity.accepted().
                body("Movie has been updated.Please wait for a message in your Gmail");

    }
// подготовлен новый подход к обновлению фильмов
//    @Loggable
//    @PostMapping("/update_movie/{filmId}")
//    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
//    public ResponseEntity<?> updateMovie(
//            @PathVariable("filmId") Long filmId,
//            @Valid @RequestBody Movies updatedData,
//            Principal principal) {
//        try {
//            // Получение имени текущего пользователя
//            String username = principal.getName();
//
//            // Поиск пользователя по имени
//            Users user = usersRepo.findByUserName(username)
//                    .orElseThrow(() -> new ApiException("User not found"));
//
//            // Проверка, существует ли фильм и принадлежит ли он текущему пользователю
//            Movies existingMovie = moviesRepo.findById(filmId)
//                    .orElseThrow(() -> new ApiException("Film not found"));
//
//            // Проверка, принадлежит ли фильм текущему пользователю
//            if (existingMovie.getDirector().getUsers().getUser_id() != user.getUser_id()) {
//                throw new ApiException("Ви не маєте дозволу редагувати цей фільм");
//            }
//
//            // Обновление данных фильма
//            existingMovie.setTitle(updatedData.getTitle());
//            existingMovie.setDescription(updatedData.getDescription());
//            existingMovie.setGenre_film(updatedData.getGenre_film());
//
//            // Сохранение изменений
//            moviesRepo.save(existingMovie);
//
//            MovieCreatedEvent event = new MovieCreatedEvent(
//                    existingMovie.getId(),
//                    existingMovie.getTitle(),
//                    existingMovie.getDescription(),
//                    existingMovie.getGenre_film().name()
//            );
//            rabbitTemplate.convertAndSend(
//                    UpdateFilmConfigQueue.EXCHANGE_NAME,
//                    "filmupdate.elastic",
//                    event);
//
//
//            DtoMovie dtoMovie = new DtoMovie();
//            dtoMovie.setTitle(existingMovie.getTitle());
//            dtoMovie.setDescription(existingMovie.getDescription());
//            dtoMovie.setGenre_film(Genre.valueOf(String.valueOf(existingMovie.getGenre_film())));
//            dtoMovie.setDateTimeCreated(existingMovie.getDateTimeCreated());
//            // Возврат обновленного фильма
//            return ResponseEntity.ok(dtoMovie);
//
//        } catch (ApiException e) {
//            logger.error("Error updating movie: {}", e.getMessage());
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            logger.error("Unexpected error while updating movie", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some error while updating movie");
//        }
//    }
//

    @Loggable
    @DeleteMapping("/DeleteFilm/{filmId}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> deleteMovie(@PathVariable("filmId") Long filmId,
                                         Principal principal) {
        try {
            // Получение имени текущего пользователя
            String username = principal.getName();



            Movies movie = moviesRepo
                    .findByIdAndDirectorUserUserName(filmId, username)
                    .orElseThrow(() -> new ApiException(
                            "Film not found or you have no permission to delete it"));

            moviesRepo.delete(movie);

//            elasticsearchClient.delete( i -> i
//                    .index("movies")
//                    .id(String.valueOf(deleteMovie.getId())
//                    ));

            rabbitTemplate.convertAndSend(
                    UpdateFilmConfigQueue.EXCHANGE_NAME_DELETE,
                    "filmdelete.elastic",
                    movie.getId());


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

    @Loggable
    @DeleteMapping("/DeleteFilm_mq/{filmId}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> deleteMovieMq(@PathVariable("filmId") Long filmId,
                                         @AuthenticationPrincipal MyUserDetails principal) {
        try {
            // Получение имени текущего пользователя
            String username = principal.getUsername();


            rabbitTemplate.convertAndSend("filmDeleteConfigExchange","filmdelete.binding,");


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
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    @Timed(value = "film.getMovieById", description = "Time taken to get movie by id")
    public ResponseEntity<String>getMovieById(@PathVariable Long movieId) {

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Long userId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();

        String movies = movieService.getCachedMovie(movieId);
        return ResponseEntity.ok(movies);

    }

//    @GetMapping("/getMovieByIdd/{movieId}")
//    public ResponseEntity<String> getMovieByIdd(@PathVariable Long movieId) {
//        long startController = System.nanoTime();
//
//        // 1) Вызов сервиса
//        long beforeService = System.nanoTime();
//        String moviesJson = movieService.getCachedMovie(movieId);
//        long afterService = System.nanoTime();
//
//        // 2) Формирование ResponseEntity
//        ResponseEntity<String> response = ResponseEntity.ok(moviesJson);
//        long afterResponse = System.nanoTime();
//
//        // 3) Логируем результаты
//        logger.info("TIMING — controller total: {} ms; service: {} ms; responseBuild: {} ms",
//                (afterResponse - startController) / 1_000_000,
//                (afterService     - beforeService)   / 1_000_000,
//                (afterResponse    - afterService)    / 1_000_000
//        );
//
//        return response;
//    }





}
