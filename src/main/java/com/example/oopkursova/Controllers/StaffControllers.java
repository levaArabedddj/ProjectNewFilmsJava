package com.example.oopkursova.Controllers;

import com.example.oopkursova.DTO.DtoActor;
import com.example.oopkursova.DTO.DtoCrewMember;
import com.example.oopkursova.DTO.DtoStaff;
import com.example.oopkursova.Entity.Actors;
import com.example.oopkursova.Entity.FilmCrewMembers;
import com.example.oopkursova.Entity.Users;
import com.example.oopkursova.Repository.ActorRepo;
import com.example.oopkursova.Repository.CrewMemberRepo;
import com.example.oopkursova.Repository.StaffRepo;
import com.example.oopkursova.Repository.UsersRepo;
import com.example.oopkursova.loger.Loggable;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Controller
@RequestMapping("/Staff")
public class StaffControllers {
    // здесь реализовать логику штаба для админа сервера:
    //1.создание актеров, изменения данных, удаление из бд
    //2.создание членов съемочной группы, изменения данных, удаление из бд
    // здесь реализовать логику получения данных об актерах и ЧСМ для пользователя
    //другая логика

    @Autowired
    private ActorRepo actorRepo;
    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private CrewMemberRepo crewMemberRepo;

    private final StaffRepo staffRepo;

    @Autowired
    public StaffControllers(StaffRepo staffRepo) {
        this.staffRepo = staffRepo;
    }

    @Loggable
    @PostMapping("/createActor")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> createActor(@Valid @RequestBody DtoActor dtoActor,
                                         Principal principal) {

        try {
            String username = principal.getName();
            Users users = usersRepo.findByName(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Actors newActor = new Actors();
            newActor.setName(dtoActor.getName());
            newActor.setSurName(dtoActor.getSurName());
            newActor.setSalaryPerHour(dtoActor.getSalaryPerHour());
            newActor.setRating(dtoActor.getRating());

            actorRepo.save(newActor);
            return ResponseEntity.ok("Actor created");
        } catch (Exception e){
            log.error("Error creating actor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some erroe while creating actor");
        }

    }

    @Loggable
    @PostMapping("/createCrewMember")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> createCrewMember(@Valid @RequestBody DtoCrewMember dtoCrewMember,
                                         Principal principal) {

        try {
            String username = principal.getName();
            Users users = usersRepo.findByName(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            FilmCrewMembers crewMembers = new FilmCrewMembers();
            crewMembers.setName(dtoCrewMember.getName());
            crewMembers.setSurName(dtoCrewMember.getSurName());
            crewMembers.setSalaryPerHours(dtoCrewMember.getSalaryPerHours());

            crewMemberRepo.save(crewMembers);
            return ResponseEntity.ok("Crew Member created");
        } catch (Exception e){
            log.error("Error creating crew member film", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some erroe while creating crew member film");
        }

    }


    @Loggable
    @GetMapping("/getAllCrewMember")
    public ResponseEntity<?> getAllCrewMember(Principal principal) {

        try {
            String username = principal.getName();
            Users users = usersRepo.findByName(username).
                    orElseThrow(() -> new RuntimeException("User not found"));

            List<FilmCrewMembers> filmCrewMembers = crewMemberRepo.findAll();

            List<DtoCrewMember> dtoCrewMembers = filmCrewMembers.stream()
                    .map(crewMember -> {

                        DtoCrewMember dto = new DtoCrewMember();
                        dto.setName(crewMember.getName());
                        dto.setSurName(crewMember.getSurName());
                        dto.setSalaryPerHours(crewMember.getSalaryPerHours());
                        return dto;
                    }).toList();
            return ResponseEntity.ok(dtoCrewMembers);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    @Loggable
    @GetMapping("/getAllActor")
    public ResponseEntity<?> getAllActor(Principal principal) {

        try {
            String username = principal.getName();
            Users users = usersRepo.findByName(username).
                    orElseThrow(() -> new RuntimeException("User not found"));

            List<Actors> allActors = actorRepo.findAll();

            List<DtoActor> actors = allActors.stream().
                    map(actors1 -> {
                        DtoActor dtoActor = new DtoActor();
                        dtoActor.setName(actors1.getName());
                        dtoActor.setSurName(actors1.getSurName());
                        dtoActor.setRating(actors1.getRating());
                        dtoActor.setSalaryPerHour(actors1.getSalaryPerHour());
                        return dtoActor;
                    }).toList();
            return ResponseEntity.ok(actors);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    //работает медленнее
    @Loggable
    @GetMapping("/GetAllStaff")
    public ResponseEntity<?> getAllStaff(Principal principal) {
        try {

            String username = principal.getName();
            Users users = usersRepo.findByName(username).
                    orElseThrow(() -> new RuntimeException("User not found"));

            CompletableFuture<List<Actors>> actors = CompletableFuture.supplyAsync(()-> actorRepo.findAll());
            CompletableFuture<List<FilmCrewMembers>> crewMembers = CompletableFuture.supplyAsync(()-> crewMemberRepo.findAll());

            CompletableFuture.allOf(actors, crewMembers).join();

            List<DtoActor> dtoActors = actors.get()
                    .stream().map(actors1 -> {
                        DtoActor dto = new DtoActor();
                        dto.setName(actors1.getName());
                        dto.setSurName(actors1.getSurName());
                        dto.setSalaryPerHour(actors1.getSalaryPerHour());
                        dto.setRating(actors1.getRating());
                        return dto;
                    }).toList();

            List<DtoCrewMember> dtoCrewMembers = crewMembers.get()
                    .stream().map(filmCrewMembers -> {
                        DtoCrewMember dto = new DtoCrewMember();
                        dto.setName(filmCrewMembers.getName());
                        dto.setSurName(filmCrewMembers.getSurName());
                        dto.setSalaryPerHours(filmCrewMembers.getSalaryPerHours());
                        return dto;
                    }).toList();

            Map<String,Object> response = new HashMap<>();
            response.put("actors", dtoActors);
            response.put("crewMember", dtoCrewMembers);
            return ResponseEntity.ok(response);

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // работает быстрее
    @GetMapping("/getAllStaff")
    public ResponseEntity<List<DtoStaff>> getAllStaff() {
        List<DtoStaff> allStaff = staffRepo.findAllStaff();
        return ResponseEntity.ok(allStaff);
    }

    @DeleteMapping("/deleteActor/{ActorId}")
    @Loggable
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteActor(@PathVariable Long ActorId) {
        try {
            Actors actor = actorRepo.findById(ActorId)
                    .orElseThrow(() -> new RuntimeException("Actor not found"));

            actorRepo.delete(actor);
            return ResponseEntity.ok("Actor deleted");
        } catch (Exception e) {
            log.error("Error deleting actor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting actor");
        }

    }

    @Loggable
    @DeleteMapping("/deleteCrewMember/{CrewMemberId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteCrewMember(@PathVariable Long CrewMemberId) {
        try {
            FilmCrewMembers crewMember = crewMemberRepo.findById(CrewMemberId)
                    .orElseThrow(() -> new RuntimeException("Crew member not found"));

            crewMemberRepo.delete(crewMember);
            return ResponseEntity.ok("Crew member deleted");
        } catch (Exception e) {
            log.error("Error deleting crew member", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting crew member");
        }
    }

}
