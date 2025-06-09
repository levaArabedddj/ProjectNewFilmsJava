package com.example.Controllers;

import com.example.DTO.CreateModeratorRequest;
import com.example.Entity.Admin;
import com.example.Entity.MoviesPackage.Movies;
import com.example.Entity.Users;
import com.example.Enum.AdminRole;
import com.example.Exception.ApiException;
import com.example.Repository.AdminRepo;
import com.example.Repository.DirectorRepo;
import com.example.Repository.MoviesRepo;
import com.example.Repository.UsersRepo;
import com.example.Service.DirectorService;
import com.example.config.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.example.Enum.UserRole.ADMIN;
import static com.example.Enum.UserRole.DIRECTOR;

@RestController()
@RequestMapping("/Director")
public class DirectorController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    private final DirectorService directorService;
    private final DirectorRepo directorRepo;
    private final AdminRepo adminRepo;
    private final UsersRepo usersRepo;
    private final MoviesRepo moviesRepo;

    @Autowired
    public DirectorController(DirectorService directorService, DirectorRepo directorRepo, AdminRepo adminRepo, UsersRepo usersRepo, MoviesRepo moviesRepo) {
        this.directorService = directorService;
        this.directorRepo = directorRepo;
        this.adminRepo = adminRepo;
        this.usersRepo = usersRepo;
        this.moviesRepo = moviesRepo;
    }



    @PutMapping("/profile")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> updateDirectorProfile(@RequestBody Map<String,String> request) throws AccessDeniedException {


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();
        if (!request.containsKey("fieldName") || !request.containsKey("newValue")) {
            return ResponseEntity.badRequest().body("Missing required fields");
        }

        String fieldName = request.get("fieldName");
        String newValue = request.get("newValue");

        boolean update = directorService.updateDirectorProfile(userId, fieldName, newValue);

        if (update) {
            return ResponseEntity.ok("Crew Member profile updated successfully");
        } else {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while updating profile");
        }
        return ResponseEntity.ok("Crew Member profile updated successfully");
    }


//    @PostMapping("/profile/photo")
//    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
//    public ResponseEntity<?> uploadPhotoProfileCrewMember(
//            @RequestParam MultipartFile file) {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Long userId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();
//        try {
//            String url = directorService.uploadProfilePhoto(userId,file);
//            return ResponseEntity.ok(url);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }


    @GetMapping("/profile")
    public CompletableFuture<ResponseEntity<?>> getCrewMemberProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();

        return directorService.getDirectorProfile(userId)
                .thenApply( profileOpt ->
                        profileOpt.map(ResponseEntity::ok)
                                .orElseThrow(() -> new RuntimeException("Crew Member profile not found")));
    }



    @PostMapping("/createAdmin")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> signInAdminForUser(
            @RequestBody CreateModeratorRequest req, Principal principal
    ) throws ApiException {

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null || !authentication.isAuthenticated()
//                || authentication instanceof AnonymousAuthenticationToken
//                || !(authentication.getPrincipal() instanceof MyUserDetails)) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
//        }
//
//        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();

        String username = principal.getName();


        Users user = usersRepo.findByUserName(username).orElseThrow(() -> new ApiException("User not found"));


        Movies movies = moviesRepo.findById(req.getMovieId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "not found"));

        if(!user.getRole().equals(DIRECTOR)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"you not director");
        }
        if( !(movies.getDirector().getUsers().getUser_id() == user.getUser_id()) ){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot a director for this movie");
        }

        if(usersRepo.existsUsersByUserName(req.getUserNameAdmin()) ||
                usersRepo.existsUsersByGmail(req.getGmail())){

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Username or email already taken");
        }

        Users moderatorUser = new Users();

        moderatorUser.setUserName(req.getUserNameAdmin());
        moderatorUser.setGmail(req.getGmail());
        moderatorUser.setPassword(passwordEncoder.encode(req.getPassword()));
        moderatorUser.setRole(ADMIN);
        usersRepo.save(moderatorUser);


        Admin admin = new Admin();
        admin.setUser(moderatorUser);
        admin.setRole(AdminRole.MODERATOR);
        admin.setAccessLevel(req.getPermission());
        admin.setMovie(movies);
        adminRepo.save(admin);

        Map<String, Object> response = new HashMap<>();
        response.put("adminId", admin.getId());
        response.put("userName", moderatorUser.getUserName());
        response.put("movieTitle", movies.getTitle());
        response.put("permission", admin.getAccessLevel());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
