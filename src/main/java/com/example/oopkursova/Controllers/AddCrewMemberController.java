package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.FilmCrewMembers;
import com.example.oopkursova.Entity.Movies;
import com.example.oopkursova.Repository.ActorRepo;
import com.example.oopkursova.Repository.MoviesRepo;
import com.example.oopkursova.loger.Loggable;
import com.example.oopkursova.Repository.CrewMemberRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/CrewMember")
public class AddCrewMemberController {


    private final CrewMemberRepo crewMemberRepo;


    private final MoviesRepo moviesRepo;

    @Autowired
    public AddCrewMemberController(CrewMemberRepo crewMemberRepo, MoviesRepo moviesRepo) {
        this.crewMemberRepo = crewMemberRepo;
        this.moviesRepo = moviesRepo;
    }

    @Loggable
    @PostMapping("/addCrewMemberToFilm/{filmId}")
    public ResponseEntity<?> addCrewMemberToFilm(@PathVariable int filmId,
                                      @RequestBody Map<String,Long> requestBody) {

        try{
            Long crewMemberId = requestBody.get("crewMemberId");

            if (crewMemberId == null) {
                return ResponseEntity.badRequest().body("Crew Member is required");
            }

            Movies film = moviesRepo.findById(filmId);

            FilmCrewMembers crewMembers = crewMemberRepo.findById(crewMemberId)
                    .orElseThrow(()-> new RuntimeException("Crew Member not found"));

            if(film.getFilmCrewMembers().contains(crewMembers)){
                return ResponseEntity.badRequest().body("Film already exists");
            }

            film.getFilmCrewMembers().add(crewMembers);
            crewMembers.getMovies().add(film);

            moviesRepo.save(film);
            crewMemberRepo.save(crewMembers);

            return ResponseEntity.ok("Crew Member added to film successfully");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Loggable
    @DeleteMapping("/deleteCrewMemberToFilm/{filmId}")
    public ResponseEntity<?>  removeCrewMemberFromFilm(@PathVariable Long filmId,
                                                       @RequestBody Map<String,Long> requestBody) {

        try {
            Long crewMemberId = requestBody.get("crewMemberId");
            if (crewMemberId == null) {
                return ResponseEntity.badRequest().body("Crew Member is required");
            }

            Movies film = moviesRepo.findById(filmId)
                    .orElseThrow(()-> new RuntimeException("Film not found"));

            FilmCrewMembers crewMembers = crewMemberRepo.findById(crewMemberId)
                    .orElseThrow(()-> new RuntimeException("Crew Member not found"));

            if(!film.getFilmCrewMembers().contains(crewMembers)){
                return ResponseEntity.badRequest().body("Crew Member is not in the film");
            }

            film.getFilmCrewMembers().remove(crewMembers);
            crewMembers.getMovies().remove(film);

            moviesRepo.save(film);
            crewMemberRepo.save(crewMembers);

            return ResponseEntity.ok("Crew Member removed from film successfully");
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }






}
