package com.example.oopkursova.Service;

import com.example.oopkursova.DTO.*;
import com.example.oopkursova.Entity.Movies;
import com.example.oopkursova.Repository.MoviesRepo;
import com.example.oopkursova.Repository.UsersRepo;
import com.example.oopkursova.Entity.Users;
import com.example.oopkursova.loger.Loggable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class MovieService {

    @Autowired
    private final MoviesRepo moviesRepo;
    @Autowired
    private final UsersRepo usersRepo;

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
        Optional<Users> optionalUser = usersRepo.findByName(userEmail);

        if (optionalUser.isEmpty()) {
            log.warn("Пользователь с email {} не найден", userEmail);
            return Collections.emptyList(); // Возвращаем пустой список, если пользователь не найден
        }

        Users user = optionalUser.get();

        // Получаем список фильмов пользователя
        List<Movies> movies = moviesRepo.findByUser(user);

        if (movies == null || movies.isEmpty()) {
            log.info("Фильмы для пользователя {} не найдены", userEmail);
            return Collections.emptyList();
        }

        return movies.stream().
                map(this::convertToMovieDto).
                collect(Collectors.toList());
    }


    private DtoMovie convertToMovieDto(Movies movie){
        Set<DtoActor> dtoActors = movie.getActors().stream()
                .map(actors -> new DtoActor(actors.getId(), actors.getName(), actors.getSurName(),actors.getSalaryPerHour(), actors.getRating()))
                .collect(Collectors.toSet());

        Set<DtoCrewMember> dtoCrewMembers = movie.getFilmCrewMembers().stream()
                .map(crewMember -> new DtoCrewMember(crewMember.getCrewMember_id(), crewMember.getName(), crewMember.getSurName(), crewMember.getSalaryPerHours()))
                .collect(Collectors.toSet());

        Set<DtoShootingDay> dtoShootingDays = movie.getShootingDays().stream()
                .map(shootingDay -> new DtoShootingDay(shootingDay.getId(), shootingDay.getShootingDate(), shootingDay.getShootingTime(), shootingDay.getShootingLocation(), shootingDay.getEstimatedDurationHours()))
                .collect(Collectors.toSet());

        DtoScript dtoScript = new DtoScript();
        if(movie.getScript() != null) {
            dtoScript.setId(movie.getScript().getId());
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
                movie.getGenre(), dtoActors, dtoCrewMembers,
                movie.getDateTimeCreated(), dtoShootingDays, dtoScript, dtoFinance);
    }

}
