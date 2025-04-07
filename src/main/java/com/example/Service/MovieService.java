package com.example.Service;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.DTO.DtoFinance;
import com.example.DTO.DtoMovie;
import com.example.DTO.DtoScript;
import com.example.DTO.DtoShootingDay;
import com.example.ElasticSearch.ClassDocuments.MovieDocument;

import com.example.Entity.Director;
import com.example.Entity.Movies;
import com.example.Entity.Users;
import com.example.Enum.Genre;
import com.example.Repository.DirectorRepo;
import com.example.Repository.MoviesRepo;
import com.example.Repository.UsersRepo;
import com.example.loger.Loggable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import co.elastic.clients.elasticsearch.core.search.Hit;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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


    @Autowired
    public MovieService( MoviesRepo moviesRepo, UsersRepo usersRepo, DirectorRepo directorRepo) {
        this.moviesRepo = moviesRepo;
        this.usersRepo = usersRepo;
        this.directorRepo = directorRepo;
    }

    @Loggable
    public Movies findById(long id) {
        return moviesRepo.findById(id);
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
                .map(shootingDay -> new DtoShootingDay( shootingDay.getShootingDate(), shootingDay.getShootingTime(), shootingDay.getShootingLocation(), shootingDay.getEstimatedDurationHours()))
                .collect(Collectors.toSet());

        DtoScript dtoScript = new DtoScript();
        if(movie.getScript() != null) {
            dtoScript.setContent(movie.getScript().getContent());
        }
        DtoFinance dtoFinance = new DtoFinance();
        if(movie.getFilmFinance() != null) {
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



}
