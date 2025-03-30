package com.example.Controllers;

import com.example.Service.DirectorService;
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

@RestController()
@RequestMapping("/Director")
public class DirectorController {

    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
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


    @PostMapping("/profile/photo")
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<?> uploadPhotoProfileCrewMember(
            @RequestParam MultipartFile file) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();
        try {
            String url = directorService.uploadProfilePhoto(userId,file);
            return ResponseEntity.ok(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @GetMapping("/profile")
    public CompletableFuture<ResponseEntity<?>> getCrewMemberProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();

        return directorService.getDirectorProfile(userId)
                .thenApply( profileOpt ->
                        profileOpt.map(ResponseEntity::ok)
                                .orElseThrow(() -> new RuntimeException("Crew Member profile not found")));
    }


}
