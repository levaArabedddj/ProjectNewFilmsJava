package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.Actors;
import com.example.oopkursova.Entity.Movies;
import com.example.oopkursova.Repository.MoviesRepo;
import com.example.oopkursova.loger.Loggable;
import com.example.oopkursova.Repository.ActorRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Actors")
public class AddActorsControllers {

    private final ActorRepo actorRepo;
    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(AddActorsControllers.class);


    @Autowired
    private MoviesRepo moviesRepo ;

    public AddActorsControllers(ActorRepo actorRepo, EntityManager entityManager) {
        this.actorRepo = actorRepo;
        this.entityManager = entityManager;
    }



    @Loggable
    @PostMapping("/addActorToFilm/{filmId}")
    public ResponseEntity<?> addActorToFilm(@PathVariable Long filmId, @RequestBody Map<String, Long> requestBody) {
        try {
            Long actorId = requestBody.get("actorId");
            if (actorId == null) {
                return ResponseEntity.badRequest().body("Actor ID is required");
            }

            Movies film = moviesRepo.findById(filmId)
                    .orElseThrow(() -> new RuntimeException("Film not found"));

            Actors actor = actorRepo.findById(actorId)
                    .orElseThrow(() -> new RuntimeException("Actor not found"));

            // Проверяем, есть ли актёр уже в фильме, чтобы не добавлять дубликаты
            if (film.getActors().contains(actor)) {
                return ResponseEntity.badRequest().body("Actor is already in this film");
            }

            // Устанавливаем связь
            film.getActors().add(actor);
            actor.getMovies().add(film);

            // Сохраняем изменения
            moviesRepo.save(film);
            actorRepo.save(actor);

            return ResponseEntity.ok("Actor added to film successfully");
        } catch (Exception e) {
            logger.error("Error adding actor to film", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding actor to film");
        }
    }


    @Loggable
    @DeleteMapping("/deleteActorToFilm/{filmId}")
    public ResponseEntity<?> removeActorFromFilm(@RequestBody Map<String, Long> requestBody,
                                                 @PathVariable Long filmId) {

        try {
            Long actorId = requestBody.get("actorId");
            if (actorId == null) {
                return ResponseEntity.badRequest().body("Actor ID is required");
            }

            Movies film = moviesRepo.findById(filmId)
                    .orElseThrow(() -> new RuntimeException("Film not found"));

            Actors actor = actorRepo.findById(actorId)
                    .orElseThrow(() -> new RuntimeException("Actor not found"));

            if(!film.getActors().contains(actor)) {
                return ResponseEntity.badRequest().body("Actor is not in this film");
            }
            film.getActors().remove(actor);
            actor.getMovies().remove(film);

            moviesRepo.save(film);
            actorRepo.save(actor);

            return ResponseEntity.ok("Actor removed from film successfully");
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }







}
