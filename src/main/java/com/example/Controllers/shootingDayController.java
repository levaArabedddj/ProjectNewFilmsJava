package com.example.Controllers;


import com.example.DTO.DtoShootingDay;
import com.example.Entity.Director;
import com.example.Entity.Movies;
import com.example.Entity.ShootingDay;
import com.example.Entity.Users;
import com.example.Exception.ApiException;

import com.example.RabbitMQ.DtoRabbitMQ.DtoShootingDayMQ;
import com.example.RabbitMQ.FilmTask.ShootingDayEventPublisher;
import com.example.Repository.DirectorRepo;
import com.example.Repository.MoviesRepo;
import com.example.Repository.ShootingDayRepo;
import com.example.Repository.UsersRepo;
import com.example.loger.Loggable;
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
import java.util.stream.Collectors;


@RestController
@RequestMapping("/ShootingDay")
public class shootingDayController {

    @Autowired
    private ShootingDayRepo shootingDayRepo;
    private static final Logger logger = LoggerFactory.getLogger(shootingDayController.class);


    private final ShootingDayEventPublisher shootingDayEventPublisher;

    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private MoviesRepo moviesRepo;

    private final DirectorRepo directorRepo;

    // Добавить в контролеры проверку что юзер может изменять текущие
    // съемочные дни и проверять что авторизованный юзер именно тот за кого себя выдает


    @Autowired
    public shootingDayController(ShootingDayRepo shootingDayRepo, ShootingDayEventPublisher shootingDayEventPublisher, DirectorRepo directorRepo) {
        this.shootingDayRepo = shootingDayRepo;
        this.shootingDayEventPublisher = shootingDayEventPublisher;
        this.directorRepo = directorRepo;
    }

    @Loggable
    @PostMapping("/create_shootingDay/{filmId}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> CreateShootingDay(@PathVariable("filmId") Long filmId,
                                               @Valid @RequestBody DtoShootingDay shootingDay, Principal principal) {
        try {
            // Получаем имя текущего пользователя
            String username = principal.getName();
            Users users = usersRepo.findByUserName(username)
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
            DtoShootingDayMQ event = new DtoShootingDayMQ();
            event.setFilmId(filmId);
            event.setShootingDate(newShootingDay.getShootingDate());
            event.setShootingTime(newShootingDay.getShootingTime());
            event.setLocation(newShootingDay.getShootingLocation());
          shootingDayEventPublisher.publish(event,shootingDay.getShootingTime(), shootingDay.getShootingDate(), shootingDay.getShootingLocation()); // << отправка в RabbitMQ

            return ResponseEntity.ok(event);
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
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> GetShootingDays(@PathVariable("filmId") Long filmId,
                                             Principal principal) throws ApiException {

            if (principal == null || principal.getName() == null) {
                throw new ApiException("Unauthorized access: no user information found");
            }

            String username = principal.getName();
              Movies movies = moviesRepo.findById(filmId).
                    orElseThrow(() -> new ApiException("Movie not found"));

            //проверяет, существует ли фильм с указанным id, принадлежащий пользователю с заданным именем
            if (!moviesRepo.existsByIdAndUsername(filmId, username)) {
                throw new ApiException("Access denied: You are not the owner of this movie");
            }

        if (movies.getDirector() == null ||
                movies.getDirector().getUsers() == null ||
                !movies.getDirector().getUsers().getUserName().equals(username)) {
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


    }
    @Loggable
    @PutMapping("/update/shootingDay/{filmId}/{shootingDayId}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> UpdateShootingDayPartial(@PathVariable("filmId") Long filmId,
                                                      @PathVariable("shootingDayId") Long shootingDayId,
                                                      @RequestBody Map<String, Object> updates,
                                                      Principal principal) {
        try {
            // Получаем имя текущего пользователя
            String username = principal.getName();
            Users users = usersRepo.findByUserName(username)
                    .orElseThrow(() -> new ApiException("User not found"));

            // Проверка существования фильма
            Movies movie = moviesRepo.findById(filmId)
                    .orElseThrow(() -> new ApiException("Film not found"));

            // Проверяем, что фильм принадлежит пользователю
            if (movie.getDirector().getUsers().getUser_id() != users.getUser_id()) {
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
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> deleteShootingDay(@PathVariable("filmId") Long filmId,
                                               @PathVariable("shootingDayId") Long shootingDayId,
                                               Principal principal )throws ApiException {

        try{
            String username = principal.getName();
            Users users = usersRepo.findByUserName(username)
                    .orElseThrow(() -> new ApiException("User not found"));

            // Проверка существования фильма
            Movies movie = moviesRepo.findById(filmId)
                    .orElseThrow(() -> new ApiException("Film not found"));
            // Проверяем, что фильм принадлежит пользователю
            if (movie.getDirector().getUsers().getUser_id() != users.getUser_id()) {
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


    @Loggable
    @GetMapping("/getAllShootingDayDirector")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> getAllShootingDayMovie(Principal principal) throws ApiException {

        if (principal == null || principal.getName() == null) {
            throw new ApiException("Unauthorized access: no user information found");
        }

        String username = principal.getName();

        // Получаем текущего пользователя
        Users user = usersRepo.findByUserName(username)
                .orElseThrow(() -> new ApiException("User not found"));

        // Получаем директора, связанного с пользователем
        Director director = directorRepo.findByUsers(user)
                .orElseThrow(() -> new ApiException("Director profile not found"));

        // Получаем все фильмы, снятые этим директором
        List<Movies> movies = moviesRepo.findByDirector(director);

        // Из всех фильмов достаём съёмочные дни
        List<DtoShootingDay> dtoShootingDays = movies.stream()
                .flatMap(movie -> shootingDayRepo.findByMovieId(movie.getId()).stream())
                .map(day -> {
                    DtoShootingDay dto = new DtoShootingDay();
                    dto.setShootingDate(day.getShootingDate());
                    dto.setShootingTime(day.getShootingTime());
                    dto.setShootingLocation(day.getShootingLocation());
                    dto.setEstimatedDurationHours(day.getEstimatedDurationHours());
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoShootingDays);


    }
}
