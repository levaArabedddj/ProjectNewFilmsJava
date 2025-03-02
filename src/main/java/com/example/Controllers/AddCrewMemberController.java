package com.example.Controllers;


import com.example.Repository.CrewMemberRepo;
import com.example.Repository.MoviesRepo;
import com.example.Service.CrewMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/CrewMember")
public class AddCrewMemberController {


    private final CrewMemberRepo crewMemberRepo;

    private final CrewMemberService crewMemberService;

    private final MoviesRepo moviesRepo;

    @Autowired
    public AddCrewMemberController(CrewMemberRepo crewMemberRepo, CrewMemberService crewMemberService, MoviesRepo moviesRepo) {
        this.crewMemberRepo = crewMemberRepo;
        this.crewMemberService = crewMemberService;
        this.moviesRepo = moviesRepo;
    }

//    @Loggable
//    @PostMapping("/addCrewMemberToFilm/{filmId}")
//    public ResponseEntity<?> addCrewMemberToFilm(@PathVariable int filmId,
//                                      @RequestBody Map<String,Long> requestBody) {
//
//        try{
//            Long crewMemberId = requestBody.get("crewMemberId");
//
//            if (crewMemberId == null) {
//                return ResponseEntity.badRequest().body("Crew Member is required");
//            }
//
//            Movies film = moviesRepo.findById(filmId);
//
//            FilmCrewMembers crewMembers = crewMemberRepo.findById(crewMemberId)
//                    .orElseThrow(()-> new RuntimeException("Crew Member not found"));
//
//            if(film.getFilmCrewMembers().contains(crewMembers)){
//                return ResponseEntity.badRequest().body("Film already exists");
//            }
//
//            film.getFilmCrewMembers().add(crewMembers);
//            crewMembers.getMovies().add(film);
//
//            moviesRepo.save(film);
//            crewMemberRepo.save(crewMembers);
//
//            return ResponseEntity.ok("Crew Member added to film successfully");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//    @Loggable
//    @DeleteMapping("/deleteCrewMemberToFilm/{filmId}")
//    public ResponseEntity<?>  removeCrewMemberFromFilm(@PathVariable Long filmId,
//                                                       @RequestBody Map<String,Long> requestBody) {
//
//        try {
//            Long crewMemberId = requestBody.get("crewMemberId");
//            if (crewMemberId == null) {
//                return ResponseEntity.badRequest().body("Crew Member is required");
//            }
//
//            Movies film = moviesRepo.findById(filmId)
//                    .orElseThrow(()-> new RuntimeException("Film not found"));
//
//            FilmCrewMembers crewMembers = crewMemberRepo.findById(crewMemberId)
//                    .orElseThrow(()-> new RuntimeException("Crew Member not found"));
//
//            if(!film.getFilmCrewMembers().contains(crewMembers)){
//                return ResponseEntity.badRequest().body("Crew Member is not in the film");
//            }
//
//            film.getFilmCrewMembers().remove(crewMembers);
//            crewMembers.getMovies().remove(film);
//
//            moviesRepo.save(film);
//            crewMemberRepo.save(crewMembers);
//
//            return ResponseEntity.ok("Crew Member removed from film successfully");
//        } catch (RuntimeException e) {
//            throw new RuntimeException(e);
//        }
//    }



    @PutMapping("/{crewMemberId}/profile")
    public ResponseEntity<?> updateCrewMemberProfile(@PathVariable Long crewMemberId,
                                                     @RequestBody Map<String,String> request) {

        if (!request.containsKey("fieldName") || !request.containsKey("newValue")) {
            return ResponseEntity.badRequest().body("Missing required fields");
        }

        String fieldName = request.get("fieldName");
        String newValue = request.get("newValue");

        boolean update = crewMemberService.updateCrewMemberProfile(crewMemberId, fieldName, newValue);

        if (update) {
            return ResponseEntity.ok("Crew Member profile updated successfully");
        } else {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while updating profile");
        }
        return ResponseEntity.ok("Crew Member profile updated successfully");
    }


    @PostMapping("/{crewMemberId}/profile/photo")
    public ResponseEntity<?> uploadPhotoProfileCrewMember(
            @PathVariable Long crewMemberId,
            @RequestParam MultipartFile file) {

        try {
            String url = crewMemberService.uploadProfilePhoto(crewMemberId,file);
            return ResponseEntity.ok(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{crewMemberId}/profile")
    public ResponseEntity<?> getCrewMemberProfile(@PathVariable Long crewMemberId) {

        return crewMemberService.getCrewMemberProfile(crewMemberId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }




}
