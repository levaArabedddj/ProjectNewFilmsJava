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
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
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

            // Создание нового объекта ShootingDay
            ShootingDay newShootingDay = new ShootingDay();
            newShootingDay.setShootingDate(shootingDay.getShootingDate());
            newShootingDay.setShootingTime(shootingDay.getShootingTime());
            newShootingDay.setShootingLocation(shootingDay.getShootingLocation());
            newShootingDay.setEstimatedDurationHours(shootingDay.getEstimatedDurationHours());
            newShootingDay.setMovie(movie); // Устанавливаем фильм

            // Сохранение нового дня съемки в базу данных
            shootingDayRepo.save(newShootingDay);

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
    @PreAuthorize("hasAuthority('User_Role')")
    public ResponseEntity<?> GetShootingDays(@PathVariable("filmId") Long filmId,
                                             Principal principal) throws ApiException {
        try{
            String username = principal.getName();

            if (principal == null || principal.getName() == null) {
                throw new ApiException("Unauthorized access: no user information found");
            }
            Users users = usersRepo.findByName(username).
                    orElseThrow(() -> new ApiException("User not found"));
            Movies movies = moviesRepo.findById(filmId).
                    orElseThrow(() -> new ApiException("Movie not found"));
            //проверяет, существует ли фильм с указанным id, принадлежащий пользователю с заданным именем
            if (!moviesRepo.existsByIdAndUsername(filmId, username)) {
                throw new ApiException("Access denied: You are not the owner of this movie");
            }
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
    @Loggable
    @PutMapping("/update/shootingDay/{filmId}/{shootingDayId}")
    @PreAuthorize("hasAuthority('User_Role')")
    public ResponseEntity<?> UpdateShootingDayPartial(@PathVariable("filmId") Long filmId,
                                                      @PathVariable("shootingDayId") Long shootingDayId,
                                                      @RequestBody Map<String, Object> updates,
                                                      Principal principal) {
        try {
            // Получаем имя текущего пользователя
            String username = principal.getName();
            Users users = usersRepo.findByName(username)
                    .orElseThrow(() -> new ApiException("User not found"));

            // Проверка существования фильма
            Movies movie = moviesRepo.findById(filmId)
                    .orElseThrow(() -> new ApiException("Film not found"));

            // Проверяем, что фильм принадлежит пользователю
            if (movie.getUser().getUser_id() != users.getUser_id()) {
                throw new ApiException("You don't have permission to update shooting days for this film");
            }

            // Проверка существования дня съемок
            ShootingDay existingShootingDay = shootingDayRepo.findById(shootingDayId)
                    .orElseThrow(() -> new ApiException("Shooting day not found"));


            // Проверяем, что съемочный день принадлежит указанному фильму
            if (existingShootingDay.getMovie().getId() != filmId) {
                throw new ApiException("This shooting day does not belong to the specified film");
            }
            // Проверяем, существует ли фильм с указанным id, принадлежащий пользователю с заданным именем
            if (!moviesRepo.existsByIdAndUsername(filmId, username)) {
                throw new ApiException("Access denied: You are not the owner of this movie");
            }

            // Применяем изменения к существующему объекту
            updates.forEach((key, value) -> {
                switch (key) {
                    case "shootingDate":
                        existingShootingDay.setShootingDate(LocalDate.parse(value.toString()));
                        break;
                    case "shootingTime":
                        existingShootingDay.setShootingTime(LocalTime.parse(value.toString()));
                        break;
                    case "shootingLocation":
                        existingShootingDay.setShootingLocation(value.toString());
                        break;
                    case "estimatedDurationHours":
                        existingShootingDay.setEstimatedDurationHours(Integer.parseInt(value.toString()));
                        break;
                    default:
                        try {
                            throw new ApiException("Invalid field: " + key);
                        } catch (ApiException e) {
                            throw new RuntimeException(e);
                        }
                }
            });

            // Сохраняем изменения
            shootingDayRepo.save(existingShootingDay);

            return ResponseEntity.ok("Shooting day updated successfully");
        } catch (ApiException e) {
            logger.error("Error updating shooting day: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error updating shooting day", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some error while updating shooting day");
        }
    }

    @Loggable
    @DeleteMapping("/delete/shootingDay/{filmId}/{shootingDayId}")
    @PreAuthorize("hasAuthority('User_Role')")
    public ResponseEntity<?> deleteShootingDay(@PathVariable("filmId") Long filmId,
                                               @PathVariable("shootingDayId") Long shootingDayId,
                                               Principal principal )throws ApiException {

        try{
            String username = principal.getName();
            Users users = usersRepo.findByName(username)
                    .orElseThrow(() -> new ApiException("User not found"));

            // Проверка существования фильма
            Movies movie = moviesRepo.findById(filmId)
                    .orElseThrow(() -> new ApiException("Film not found"));
            // Проверяем, что фильм принадлежит пользователю
            if (movie.getUser().getUser_id() != users.getUser_id()) {
                throw new ApiException("You don't have permission to delete shooting days for this film");
            }
            // Проверка существования дня съемок
            ShootingDay existingShootingDay = shootingDayRepo.findById(shootingDayId)
                    .orElseThrow(() -> new ApiException("Shooting day not found"));

            // Проверяем, что съемочный день принадлежит указанному фильму
            if (existingShootingDay.getMovie().getId() != filmId) {
                throw new ApiException("This shooting day does not belong to the specified film");
            }

            shootingDayRepo.delete(existingShootingDay);
            return ResponseEntity.ok("Shooting Day deleted successfully");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
