package com.example.Controllers;


import com.example.Repository.CrewMemberRepo;
import com.example.Repository.MoviesRepo;
import com.example.Service.CrewMemberService;
import com.example.config.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/CrewMember")
public class AddCrewMemberController {

    /*
        Поправить что бы айди юзера передавался через токен,
        точно так же как и в класе кастинг контроллер
         */

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



    @PutMapping("/profile")
    @PreAuthorize("hasAuthority('ROLE_CREW_MEMBER')")
    public ResponseEntity<?> updateCrewMemberProfile(@RequestBody Map<String,String> request) throws AccessDeniedException {


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();
        if (!request.containsKey("fieldName") || !request.containsKey("newValue")) {
            return ResponseEntity.badRequest().body("Missing required fields");
        }

        String fieldName = request.get("fieldName");
        String newValue = request.get("newValue");

        boolean update = crewMemberService.updateCrewMemberProfile(userId, fieldName, newValue);

        if (update) {
            return ResponseEntity.ok("Crew Member profile updated successfully");
        } else {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while updating profile");
        }
        return ResponseEntity.ok("Crew Member profile updated successfully");
    }


//    @PostMapping("/profile/photo")
//    @PreAuthorize("hasAuthority('ROLE_CREW_MEMBER')")
//    public ResponseEntity<?> uploadPhotoProfileCrewMember(
//            @RequestParam MultipartFile file) {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Long userId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();
//        try {
//            String url = crewMemberService.uploadProfilePhoto(userId,file);
//            return ResponseEntity.ok(url);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @GetMapping("/{userId}/profile")
    public CompletableFuture<ResponseEntity<?>> getCrewMemberProfile(@PathVariable Long userId) {
        return crewMemberService.getCrewMemberProfile(userId)
                .thenApply( profileOpt ->
                        profileOpt.map(ResponseEntity::ok)
                                .orElseThrow(() -> new RuntimeException("Crew Member profile not found")));
    }





}
