package com.example.Service;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.DTO.DtoFinance;
import com.example.DTO.DtoMovie;
import com.example.DTO.DtoScript;
import com.example.DTO.DtoShootingDay;
import com.example.ElasticSearch.ClassDocuments.MovieDocument;

import com.example.ElasticSearch.Service.MovieElasticService;
import com.example.Entity.Director;
import com.example.Entity.Movies;
import com.example.Entity.Users;
import com.example.Enum.DevelopmentStage;
import com.example.Exception.ApiException;
import com.example.Repository.DirectorRepo;
import com.example.Repository.MoviesRepo;
import com.example.Repository.UsersRepo;
import com.example.config.MyUserDetails;
import com.example.loger.Loggable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import co.elastic.clients.elasticsearch.core.search.Hit;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MovieService {


//    private final MovieElasticRepo movieElasticRepo;
    private final MoviesRepo moviesRepo;

    private final UsersRepo usersRepo;

    private final DirectorRepo directorRepo;

    @Autowired
    private ElasticsearchClient elasticsearchClient;



    private final MovieElasticService movieElasticService;

    @Autowired
    public MovieService(MoviesRepo moviesRepo, UsersRepo usersRepo, DirectorRepo directorRepo, MovieElasticService movieElasticService) {
        this.moviesRepo = moviesRepo;
        this.usersRepo = usersRepo;
        this.directorRepo = directorRepo;
        this.movieElasticService = movieElasticService;
    }

    @Loggable
    public List<DtoMovie> findById(Long id, Long userId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long authenticatedUserId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();

        if(!authenticatedUserId.equals(userId)) {
            throw new AccessDeniedException("You are not authorized to access this resource");
        }

        return moviesRepo.findById(id).stream().
                map(this::convertToMovieDto)
                .collect(Collectors.toList());

    }

    @Loggable
    public void save(Movies movie) {
        moviesRepo.save(movie);
    }
    @Loggable
    public Movies createdMovies(Movies movies){
        return moviesRepo.save(movies);
    }

    @Loggable
    public void update(Movies movie) {
        moviesRepo.save(movie);
    }

    @Loggable
    public void deleteMovie(Long id ){
        moviesRepo.deleteById(id);
    }


    @Loggable
    public List<DtoMovie> getMoviesByUser(String userEmail) {
        // Проверяем, существует ли пользователь
        Optional<Users> optionalUser = usersRepo.findByUserName(userEmail);

        if (optionalUser.isEmpty()) {
            log.warn("Пользователь с email {} не найден", userEmail);
            return Collections.emptyList(); // Возвращаем пустой список, если пользователь не найден
        }

        Users user = optionalUser.get();

        // Проверяем, является ли пользователь режиссером
        Optional<Director> optionalDirector = directorRepo.findByUsers(user);

        if (optionalDirector.isEmpty()) {
            log.warn("Пользователь {} не является режиссером", userEmail);
            return Collections.emptyList();
        }

        Director director = optionalDirector.get();

        // Получаем список фильмов режиссера
        List<Movies> movies = moviesRepo.findByDirector(director);

        if (movies == null || movies.isEmpty()) {
            log.info("Фильмы для пользователя {} не найдены", userEmail);
            return Collections.emptyList();
        }

        return movies.stream().
                map(this::convertToMovieDto).
                collect(Collectors.toList());
    }


    private DtoMovie convertToMovieDto(Movies movie){

        Set<DtoShootingDay> dtoShootingDays = movie.getShootingDays().stream()
                .map(shootingDay -> new DtoShootingDay(shootingDay.getId(), shootingDay.getShootingDate(), shootingDay.getShootingTime(), shootingDay.getShootingLocation(), shootingDay.getEstimatedDurationHours()))
                .collect(Collectors.toSet());

        DtoScript dtoScript = new DtoScript();
        if(movie.getScript() != null) {
            dtoScript.setContent(movie.getScript().getContent());
        }
        DtoFinance dtoFinance = new DtoFinance();
        if(movie.getFilmFinance() != null) {
            dtoFinance.setId(movie.getFilmFinance().getId());
            dtoFinance.setBudget(movie.getFilmFinance().getBudget());
            dtoFinance.setActorsSalary(movie.getFilmFinance().getActorsSalary());
            dtoFinance.setCrewSalary(movie.getFilmFinance().getCrewSalary());
            dtoFinance.setAdvertisingCost(movie.getFilmFinance().getAdvertisingCost());
            dtoFinance.setEditingCost(movie.getFilmFinance().getEditingCost());
            dtoFinance.setEquipmentCost(movie.getFilmFinance().getEquipmentCost());
        }

        return new DtoMovie(movie.getId(), movie.getTitle(), movie.getDescription(),
                movie.getGenre_film(),
                movie.getDateTimeCreated(), dtoShootingDays, dtoScript, dtoFinance);
    }


    // для проверки
    @Loggable
    public List<DtoMovie> searchMovies(String keyword) {
        List<Movies> foundMovies = moviesRepo.searchByTitleOrDescription(keyword);

        if (foundMovies == null || foundMovies.isEmpty()) {
            log.info("По ключевому слову '{}' фильмы не найдены", keyword);
            return Collections.emptyList();
        }

        return foundMovies.stream()
                .map(this::convertToMovieDto)
                .collect(Collectors.toList());
    }


    public List<MovieDocument> searchByKeyword(String keyword) {
        try {
            SearchResponse<MovieDocument> response = elasticsearchClient.search(s -> s
                            .index("movies")
                            .query(q -> q
                                    .multiMatch(m -> m
                                            .fields("title^2", "description") // title важнее
                                            .query(keyword)
                                            .fuzziness("AUTO") // разрешаем опечатки
                                    )
                            ),
                    MovieDocument.class
            );

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при поиске в Elasticsearch", e);
        }
    }


    public List<DtoMovie> getAllMovie(Long userId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long authenticatedUserId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();


        if(!authenticatedUserId.equals(userId)) {
            throw new AccessDeniedException("You are not authorized to access this resource");
        }

        Optional<Director> director = directorRepo.findByUserUserId(userId);

            Director director1 = director.get();
            System.out.println("Found director " + director1.getId());

            List<Movies> movies = directorRepo.findMoviesByDirectorId(director1.getId());

            if (movies == null || movies.isEmpty()) {
                System.out.println("film not found");
                return Collections.emptyList();
            }

            return movies.stream()
                    .map(this::convertToDtoMovie)
                    .collect(Collectors.toList());

    }

    private DtoMovie convertToDtoMovie(Movies movies) {

        return new DtoMovie(movies.getId(),
                movies.getTitle(),movies.getDescription(),
                movies.getGenre_film());
    }


    public DtoMovie createMovieForUser(Movies movies, MyUserDetails currentUser) throws ApiException, IOException {
        Long userId = currentUser.getUser_id();

        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new ApiException("User not found"));

        Director director = directorRepo.findByUsers(user)
                .orElseThrow(() -> new ApiException("You are not a director"));

        Optional<Movies> existingMovie = moviesRepo.findByTitle(movies.getTitle());
        if (existingMovie.isPresent()) {
            throw new ApiException("Film already exists");
        }

        Movies newMovie = new Movies();
        newMovie.setTitle(movies.getTitle());
        newMovie.setDescription(movies.getDescription());
        newMovie.setGenre_film(movies.getGenre_film());
        newMovie.setDevelopmentStage(DevelopmentStage.CONCEPT);
        newMovie.setDirector(director);

        moviesRepo.save(newMovie);

        MovieDocument document = movieElasticService.mapToElastic(newMovie);
        elasticsearchClient.index(i -> i
                .index("movies")
                .id(String.valueOf(newMovie.getId()))
                .document(document)
        );

        DtoMovie dtoMovie = new DtoMovie();
        dtoMovie.setTitle(newMovie.getTitle());
        dtoMovie.setDescription(newMovie.getDescription());
        dtoMovie.setGenre_film(newMovie.getGenre_film());
        dtoMovie.setDateTimeCreated(newMovie.getDateTimeCreated());

        return dtoMovie;
    }

}
