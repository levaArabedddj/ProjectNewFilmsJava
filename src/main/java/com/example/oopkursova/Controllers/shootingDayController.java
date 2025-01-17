package com.example.oopkursova.Controllers;

import com.example.oopkursova.DTO.DtoShootingDay;
import com.example.oopkursova.Entity.Movies;
import com.example.oopkursova.Entity.ShootingDay;
import com.example.oopkursova.Entity.Users;
import com.example.oopkursova.Exception.ApiException;
import com.example.oopkursova.Repository.MoviesRepo;
import com.example.oopkursova.Repository.UsersRepo;
import com.example.oopkursova.loger.Loggable;
import com.example.oopkursova.Repository.ShootingDayRepo;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ShootingDay")
public class shootingDayController {

    @Autowired
    private ShootingDayRepo shootingDayRepo;
    private static final Logger logger = LoggerFactory.getLogger(shootingDayController.class);

    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private MoviesRepo moviesRepo;

    public shootingDayController(ShootingDayRepo shootingDayRepo) {
        this.shootingDayRepo = shootingDayRepo;
    }

    @Loggable
    @PostMapping("/create_shootingDay/{filmId}")
    @PreAuthorize("hasAuthority('User_Role')")
    public ResponseEntity<?> CreateShootingDay(@PathVariable("filmId") Long filmId,
                                               @Valid @RequestBody DtoShootingDay shootingDay, Principal principal) {
        try {
            // Получаем имя текущего пользователя
            String username = principal.getName();
            Users users = usersRepo.findByName(username)
                    .orElseThrow(() -> new ApiException("User not found"));

            // Проверка, существует ли уже съемка на указанную дату
            boolean isShootingDayExists = shootingDayRepo.existsByShootingDateAndMovieId(shootingDay.getShootingDate(), filmId);
            if (isShootingDayExists) {
                throw new ApiException("Shooting already exists on this day");
            }

            // Получаем фильм по ID
            Movies movie = moviesRepo.findById(filmId)
                    .orElseThrow(() -> new ApiException("Film not found"));
            logger.info("Id film :" +movie);

            // Создание нового объекта ShootingDay
            ShootingDay newShootingDay = new ShootingDay();
            newShootingDay.setShootingDate(shootingDay.getShootingDate());
            newShootingDay.setShootingTime(shootingDay.getShootingTime());
            newShootingDay.setShootingLocation(shootingDay.getShootingLocation());
            newShootingDay.setEstimatedDurationHours(shootingDay.getEstimatedDurationHours());
            newShootingDay.setMovie(movie); // Устанавливаем фильм

            // Сохранение нового дня съемки в базу данных
            shootingDayRepo.save(newShootingDay);
            logger.info("New ShootingDay created: {}, Movie ID: {}, Date: {}", newShootingDay, movie.getId(), newShootingDay.getShootingDate());

            // Создание DTO для возвращаемого объекта
            DtoShootingDay dtoShootingDay = new DtoShootingDay();
            dtoShootingDay.setShootingDate(newShootingDay.getShootingDate());
            dtoShootingDay.setShootingTime(newShootingDay.getShootingTime());
            dtoShootingDay.setShootingLocation(newShootingDay.getShootingLocation());
            dtoShootingDay.setEstimatedDurationHours(newShootingDay.getEstimatedDurationHours());


            return ResponseEntity.ok(dtoShootingDay);
        } catch (ApiException e) {
            logger.error("Error creating shooting day: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error creating shooting day", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some error while creating shooting day");
        }
    }


    @Loggable
    @GetMapping("/get_shootingDays/{filmId}")
    public ResponseEntity<?> GetShootingDays(@PathVariable("filmId") Long filmId,
                                             Principal principal) throws ApiException {

        try{
            String username = principal.getName();
            Users users = usersRepo.findByName(username).
                    orElseThrow(() -> new ApiException("User not found"));
            Movies movies = moviesRepo.findById(filmId).
                    orElseThrow(() -> new ApiException("Movie not found"));
            List<ShootingDay> shootingDays = shootingDayRepo.findByMovieId(filmId);

            List<DtoShootingDay> dtoShootingDays = shootingDays.stream()
                    .map(day ->{
                        DtoShootingDay dto = new DtoShootingDay();
                        dto.setShootingDate(day.getShootingDate());
                        dto.setShootingTime(day.getShootingTime());
                        dto.setShootingLocation(day.getShootingLocation());
                        dto.setEstimatedDurationHours(day.getEstimatedDurationHours());
                        return dto;

            }).toList();

            return ResponseEntity.ok(dtoShootingDays);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
