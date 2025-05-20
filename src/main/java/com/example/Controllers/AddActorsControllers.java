package com.example.Controllers;


import com.example.Repository.ActorProfilesRepository;
import com.example.Repository.ActorRepo;
import com.example.Repository.MoviesRepo;
import com.example.config.MyUserDetails;
import org.springframework.http.ResponseEntity;
import com.example.Service.ActorsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Actors")
public class AddActorsControllers {

     /*
    Поправить что бы айди юзера передавался через токен,
    точно так же как и в класе кастинг контроллер
     */


    private final ActorRepo actorRepo;
    private final ActorProfilesRepository actorProfilesRepo;

    private static final Logger logger = LoggerFactory.getLogger(AddActorsControllers.class);

    @Autowired
    private MoviesRepo moviesRepo ;

    @Autowired
    private ActorsService actorsService;

    public AddActorsControllers(ActorRepo actorRepo, ActorProfilesRepository actorProfilesRepo) {
        this.actorRepo = actorRepo;
        this.actorProfilesRepo = actorProfilesRepo;
    }

//    @Loggable
//    @PostMapping("/addActorToFilm/{filmId}")
//    public ResponseEntity<?> addActorToFilm(@PathVariable Long filmId, @RequestBody Map<String, Long> requestBody) {
//        try {
//            Long actorId = requestBody.get("actorId");
//            if (actorId == null) {
//                return ResponseEntity.badRequest().body("Actor ID is required");
//            }
//
//            Movies film = moviesRepo.findById(filmId)
//                    .orElseThrow(() -> new RuntimeException("Film not found"));
//
//            Actors actor = actorRepo.findById(actorId)
//                    .orElseThrow(() -> new RuntimeException("Actor not found"));
//
//            // Проверяем, есть ли актёр уже в фильме, чтобы не добавлять дубликаты
//            if (film.getActors().contains(actor)) {
//                return ResponseEntity.badRequest().body("Actor is already in this film");
//            }
//
//            // Устанавливаем связь
//            film.getActors().add(actor);
//            actor.getMovies().add(film);
//
//            // Сохраняем изменения
//            moviesRepo.save(film);
//            actorRepo.save(actor);
//
//            return ResponseEntity.ok("Actor added to film successfully");
//        } catch (Exception e) {
//            logger.error("Error adding actor to film", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding actor to film");
//        }
//    }


//    @Loggable
//    @DeleteMapping("/deleteActorToFilm/{filmId}")
//    public ResponseEntity<?> removeActorFromFilm(@RequestBody Map<String, Long> requestBody,
//                                                 @PathVariable Long filmId) {
//
//        try {
//            Long actorId = requestBody.get("actorId");
//            if (actorId == null) {
//                return ResponseEntity.badRequest().body("Actor ID is required");
//            }
//
//            Movies film = moviesRepo.findById(filmId)
//                    .orElseThrow(() -> new RuntimeException("Film not found"));
//
//            Actors actor = actorRepo.findById(actorId)
//                    .orElseThrow(() -> new RuntimeException("Actor not found"));
//
//            if(!film.getActors().contains(actor)) {
//                return ResponseEntity.badRequest().body("Actor is not in this film");
//            }
//            film.getActors().remove(actor);
//            actor.getMovies().remove(film);
//
//            moviesRepo.save(film);
//            actorRepo.save(actor);
//
//            return ResponseEntity.ok("Actor removed from film successfully");
//        } catch (RuntimeException e) {
//            throw new RuntimeException(e);
//        }
//    }


    @PutMapping("/profile")
    @PreAuthorize("hasAuthority('ROLE_ACTOR')")
    public ResponseEntity<?> updateActorProfile(@RequestBody Map<String, String> request) throws AccessDeniedException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();
        if(!request.containsKey("fieldName") || !request.containsKey("newValue")) {
            return ResponseEntity.badRequest().body("FieldName and NewValue are required");
        }

        String fieldName = request.get("fieldName");
        String newValue = request.get("newValue");

        boolean update = actorsService.updateActorProfile(userId, fieldName, newValue);

        if(update) {
            return ResponseEntity.ok("Actor profile updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating actor profile");
        }
    }



//    @PostMapping("/profile/photo")
//    @PreAuthorize("hasAuthority('ROLE_ACTOR')")
//    public ResponseEntity<?> uploadPhotoActor(@RequestParam MultipartFile file) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Long userId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();
//        try {
//            String photoUrl = actorsService.uploadProfilePhoto(userId, file);
//            return ResponseEntity.ok(photoUrl);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//    }


    @GetMapping("/{userId}/profile")
    public ResponseEntity<?> getActorProfile(@PathVariable Long userId) {
        return actorsService.getInformationActor(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
