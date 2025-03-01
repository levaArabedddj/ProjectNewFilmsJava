package com.example.oopkursova.Controllers;

import com.example.oopkursova.DTO.DtoFinance;
import com.example.oopkursova.Entity.Finance;
import com.example.oopkursova.Entity.Movies;
import com.example.oopkursova.Entity.Users;
import com.example.oopkursova.Exception.ApiException;
import com.example.oopkursova.Repository.MoviesRepo;
import com.example.oopkursova.Repository.UsersRepo;
import com.example.oopkursova.loger.Loggable;
import com.example.oopkursova.Repository.FinanceRepo;
import com.example.oopkursova.Service.FinanceService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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


    @Loggable
    @PostMapping("/createFinance/{filmId}")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> createFinance(@PathVariable("filmId") Long filmId, Principal principal,
                                           @Valid @RequestBody DtoFinance dtoFinance){
        try {

            String username = principal.getName();
            Users users = usersRepo.findByUserName(username)
                    .orElseThrow(()-> new ApiException("User not found"));

            boolean isFinanceByFilm = financeRepo.existsByMovieId(filmId);
            if(isFinanceByFilm){
                throw new ApiException("Finance already exists");
            }

            Movies movies = moviesRepo.findById(filmId)
                    .orElseThrow(()-> new ApiException("Movie not found"));

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

            updates.forEach((key, value) -> {

                switch (key) {
                    case "budget":
                        existingFinance.setBudget(BigDecimal.valueOf(Integer.parseInt(value.toString())));
                        break;
                    case "actorsSalary":
                        existingFinance.setActorsSalary(BigDecimal.valueOf(Integer.parseInt(value.toString())));
                        break;
                    case "crewSalary":
                        existingFinance.setCrewSalary(BigDecimal.valueOf(Integer.parseInt(value.toString())));
                        break;
                    case "equipmentCost":
                        existingFinance.setEquipmentCost(BigDecimal.valueOf(Integer.parseInt(value.toString())));
                        break;
                    case "editingCost":
                        existingFinance.setEditingCost(BigDecimal.valueOf(Integer.parseInt(value.toString())));
                        break;
                    case "advertisingCost":
                        existingFinance.setAdvertisingCost(BigDecimal.valueOf(Integer.parseInt(value.toString())));
                        break;
                    default:
                        try {
                            throw new ApiException("Invalid field: " + key);
                        }catch (ApiException e){
                            throw new RuntimeException(e);
                        }
                }



            });

            financeRepo.save(existingFinance);
            return ResponseEntity.ok("Finance updated successfully");
        }catch (ApiException e) {
            throw new RuntimeException(e);
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
