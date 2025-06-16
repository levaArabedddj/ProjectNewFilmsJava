package com.example.Controllers;


import com.example.DTO.DtoFinance;
import com.example.Entity.Finance;
import com.example.Entity.Movies;
import com.example.Entity.Users;
import com.example.Exception.ApiException;
import com.example.Exception.FinanceException;
import com.example.Repository.FinanceRepo;
import com.example.Repository.MoviesRepo;
import com.example.Repository.UsersRepo;
import com.example.config.MyUserDetails;
import com.example.loger.Loggable;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Finance")
public class FinanceControllers {

    private final FinanceRepo financeRepo;



    private final UsersRepo usersRepo;

    private final MoviesRepo moviesRepo;

    @Autowired
    public FinanceControllers(FinanceRepo financeRepo, UsersRepo usersRepo, MoviesRepo moviesRepo) {
        this.financeRepo = financeRepo;
        this.usersRepo = usersRepo;
        this.moviesRepo = moviesRepo;
    }


    // Добавить в контролерры проверку что юзер может изменять текущие
    // финансы и проверять что авторизованный юзер именно тот за кого себя выдает


    @Loggable
    @PostMapping("/createFinance/{filmId}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> createFinance(@PathVariable("filmId") Long filmId, Principal principal,
                                           @Valid @RequestBody DtoFinance dtoFinance){
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();

            String username = principal.getName();
            Users users = usersRepo.findByUserName(username)
                    .orElseThrow(()-> new ApiException("User not found"));

            boolean isFinanceByFilm = financeRepo.existsByMovieId(filmId);
            if(isFinanceByFilm){
                throw new ApiException("Finance already exists");
            }

            Movies movies = moviesRepo.findById(filmId)
                    .orElseThrow(()-> new ApiException("Movie not found"));

            try {
                Finance newFinance = new Finance();
                newFinance.setBudget(dtoFinance.getBudget());
                newFinance.setActorsSalary(dtoFinance.getActorsSalary());
                newFinance.setCrewSalary(dtoFinance.getCrewSalary());
                newFinance.setEquipmentCost(dtoFinance.getEquipmentCost());
                newFinance.setEditingCost(dtoFinance.getEditingCost());
                newFinance.setAdvertisingCost(dtoFinance.getAdvertisingCost());
                newFinance.setMovie(movies);
                financeRepo.save(newFinance);

                DtoFinance dtoFinanceDto = new DtoFinance();
                dtoFinanceDto.setBudget(dtoFinance.getBudget());
                dtoFinanceDto.setActorsSalary(dtoFinance.getActorsSalary());
                dtoFinanceDto.setCrewSalary(dtoFinance.getCrewSalary());
                dtoFinanceDto.setEquipmentCost(dtoFinance.getEquipmentCost());
                dtoFinanceDto.setEditingCost(dtoFinance.getEditingCost());
                dtoFinanceDto.setAdvertisingCost(dtoFinance.getAdvertisingCost());

                return ResponseEntity.ok(dtoFinanceDto);
            } catch (Exception e){
                throw new FinanceException("Ошибка при сохранение финансов "+ e.getMessage(), true);
            }
        } catch (ApiException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @SneakyThrows
    @Loggable
    @GetMapping("/getFinance/{filmId}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> getFinance(@PathVariable("filmId") Long filmId,
                                        Principal principal){

        try {
            String username = principal.getName();
            Users user = usersRepo.findByUserName(username)
                    .orElseThrow(() -> new ApiException("User not found"));

            // Проверяем, что фильм существует и принадлежит текущему пользователю
            Movies movie = moviesRepo.findById(filmId)
                    .orElseThrow(() -> new ApiException("Movie not found"));
            // Проверка, что у нас есть доступ к информации
            if (!moviesRepo.existsByIdAndUsername(filmId, username)) {
                throw new ApiException("Access denied: You are not the owner of this movie");
            }

            List<Finance> finance = financeRepo.findByMovieId(filmId);
            List<DtoFinance> dtoFinances = finance.stream()
                    .map(finance1 -> {
                        DtoFinance dtoFinance = new DtoFinance();
                        dtoFinance.setBudget(finance1.getBudget());
                        dtoFinance.setActorsSalary(finance1.getActorsSalary());
                        dtoFinance.setCrewSalary(finance1.getCrewSalary());
                        dtoFinance.setEquipmentCost(finance1.getEquipmentCost());
                        dtoFinance.setEditingCost(finance1.getEditingCost());
                        dtoFinance.setAdvertisingCost(finance1.getAdvertisingCost());
                        return dtoFinance;
                    }).toList();

            return ResponseEntity.ok(dtoFinances);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Loggable
    @PutMapping("/updateFinance/{filmId}/{finID}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> updateFinance(@PathVariable("filmId") Long filmId,
                                           @PathVariable("finID") Long financeId,
                                           Principal principal,
                                           @RequestBody Map<String,Object> updates){
        try {
            String username = principal.getName();
            Users users = usersRepo.findByUserName(username)
                    .orElseThrow(()->new ApiException("User not found"));

            Movies movie = moviesRepo.findById(filmId)
                    .orElseThrow(() -> new ApiException("Film not found"));

            Finance existingFinance = financeRepo.findById(financeId)
                    .orElseThrow(() -> new ApiException("Finance not found"));

            // Проверяем, что фильм принадлежит пользователю
            if (movie.getDirector().getUsers().getUser_id() != users.getUser_id()) {
                throw new ApiException("You don't have permission to update shooting days for this film");
            }
            // Проверяем, существует ли фильм с указанным id, принадлежащий пользователю с заданным именем
            if (!moviesRepo.existsByIdAndUsername(filmId, username)) {
                throw new ApiException("Access denied: You are not the owner of this movie");
            }
            // Выводим перед обновлением, какие данные пришли
            updates.forEach((key, value) -> {
                System.out.println("Key: " + key + " | Value: " + value);
            });

            // Сначала обновляем поля расходов
            if (updates.containsKey("actorsSalary")) {
                existingFinance.setActorsSalary(BigDecimal.valueOf(Integer.parseInt(updates.get("actorsSalary").toString())));
            }
            if (updates.containsKey("crewSalary")) {
                existingFinance.setCrewSalary(BigDecimal.valueOf(Integer.parseInt(updates.get("crewSalary").toString())));
            }
            if (updates.containsKey("advertisingCost")) {
                existingFinance.setAdvertisingCost(BigDecimal.valueOf(Integer.parseInt(updates.get("advertisingCost").toString())));
            }
            if (updates.containsKey("editingCost")) {
                existingFinance.setEditingCost(BigDecimal.valueOf(Integer.parseInt(updates.get("editingCost").toString())));
            }
            if (updates.containsKey("equipmentCost")) {
                existingFinance.setEquipmentCost(BigDecimal.valueOf(Integer.parseInt(updates.get("equipmentCost").toString())));
            }

            // Затем обновляем бюджет — поле обновляем в последнюю очередь
            if (updates.containsKey("budget")) {
                existingFinance.setBudget(BigDecimal.valueOf(Integer.parseInt(updates.get("budget").toString())));
            }

            financeRepo.save(existingFinance);
            return ResponseEntity.ok("Finance updated successfully");
        } catch (ApiException | IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }


    @Loggable
    @DeleteMapping("/deleteFinance/{filmId}/{finID}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    @Transactional
    public ResponseEntity<?> deleteFinance(@PathVariable("filmId") Long filmId,
                                           @PathVariable("finID") Long financeId,
                                           Principal principal){
        try{
            String username = principal.getName();
            Users users = usersRepo.findByUserName(username)
                    .orElseThrow(()->new  ApiException("User not found "));

            Movies movie = moviesRepo.findById(filmId).
                    orElseThrow(()->new  ApiException("Movie not found"));

            Finance finance = financeRepo.findById(financeId).
                    orElseThrow(()->new  ApiException("Finance not found"));

            // Проверяем, что фильм принадлежит пользователю
            if (movie.getDirector().getUsers().getUser_id() != users.getUser_id()) {
                throw new ApiException("You don't have permission to update shooting days for this film");
            }

            // Проверяем, существует ли фильм с указанным id, принадлежащий пользователю с заданным именем
            if (!moviesRepo.existsByIdAndUsername(filmId, username)) {
                throw new ApiException("Access denied: You are not the owner of this movie");
            }

            // Удаление связи между фильмом и финансами
            movie.setFilmFinance(null);
            moviesRepo.save(movie);
            financeRepo.delete(finance);
            return ResponseEntity.ok("Finance deleted successfully");
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

    }
}
