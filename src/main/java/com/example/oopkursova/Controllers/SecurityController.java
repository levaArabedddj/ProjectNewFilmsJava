package com.example.oopkursova.Controllers;

import com.example.oopkursova.Entity.*;
import com.example.oopkursova.Repository.*;
import com.example.oopkursova.config.JwtCore;
import com.example.oopkursova.config.SigninRequest;
import com.example.oopkursova.config.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class SecurityController {


    private UsersRepo usersRepo;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtCore jwtCore;
    @Autowired
    private ActorRepo actorRepo;
    @Autowired
    private ActorProfilesRepository actorProfilesRepository;
    @Autowired
    private CrewMemberRepo crewMemberRepo;
    @Autowired
    private CrewMemberProfilesRepo crewMemberProfilesRepo;

    @Autowired
    public void setUsersRepo(UsersRepo usersRepo) {
        this.usersRepo = usersRepo;
    }
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    @Autowired
    public void setJwtCore(JwtCore jwtCore) {
        this.jwtCore = jwtCore;
    }

//    @PostMapping("/signup")
//    public ResponseEntity<?> signin(@RequestBody SignupRequest signupRequest) {
//        if(usersRepo.existsUsersByName(signupRequest.getName())){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose defferent name");
//        }
//        if(usersRepo.existsUsersByGmail(signupRequest.getGmail())){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose defferent email");
//        }
//
//        // Створюємо користувача
//        Users user = new Users();
//        user.setName(signupRequest.getName());
//        user.setGmail(signupRequest.getGmail());
//        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
//        user.setRole(signupRequest.getRole());
//
//        usersRepo.save(user);
//
//
//            switch(signupRequest.getRole()){
//
//                case ACTOR:
//                    Actors actor = new Actors();
//                    actor.setUser(user);
//                    actor.setName(signupRequest.getName());
//                    actor.setSurName(signupRequest.getSurName());
//                    actorRepo.save(actor);
//
//                    ActorProfiles actorProfile = new ActorProfiles();
//                    actorProfile.setGender(signupRequest.getGender());
//                    actorProfile.setGmail(signupRequest.getGmail());
//                    actorProfile.setNumberPhone(signupRequest.getPhone());
//                    actorProfile.setActors(actor);
//
//                    actorProfilesRepository.save(actorProfile);
//                    break;
//                case CREW_MEMBER:
//                    FilmCrewMembers crewMembers = new FilmCrewMembers();
//                    crewMembers.setUser(user);
//                    crewMembers.setName(signupRequest.getName());
//                    crewMembers.setSurName(signupRequest.getSurName());
//                    crewMemberRepo.save(crewMembers);
//
//                    CrewMemberProfiles crewMemberProfiles = new CrewMemberProfiles();
//                    crewMemberProfiles.setGender(signupRequest.getGender());
//                    crewMemberProfiles.setGmail(signupRequest.getGmail());
//                    crewMemberProfiles.setNumberPhone(signupRequest.getPhone());
//                    crewMemberProfiles.setCrewMembers(crewMembers);
//                    crewMemberProfilesRepo.save(crewMemberProfiles);
//                    break;
//            }
//        return ResponseEntity.status(HttpStatus.CREATED).body("User created");
//    }


    @PostMapping("/signin")
    ResponseEntity<?> signup(@RequestBody SigninRequest signinRequest) {
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getName(), signinRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/signup-Login")
    public ResponseEntity<?> signInAuth(@RequestBody SignupRequest signupRequest){
        if (usersRepo.existsUsersByName(signupRequest.getName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose different name");
        }
        if (usersRepo.existsUsersByGmail(signupRequest.getGmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose different email");
        }

        // Створення користувача
        Users user = new Users();
        user.setName(signupRequest.getName());
        user.setGmail(signupRequest.getGmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRole(signupRequest.getRole());
        usersRepo.save(user);

        switch (signupRequest.getRole()) {
            case ACTOR:
                Actors actor = new Actors();
                actor.setUser(user);
                actor.setName(signupRequest.getName());
                actor.setSurName(signupRequest.getSurName());
                actorRepo.save(actor);

                ActorProfiles actorProfile = new ActorProfiles();
                actorProfile.setGender(signupRequest.getGender());
                actorProfile.setGmail(signupRequest.getGmail());
                actorProfile.setNumberPhone(signupRequest.getPhone());
                actorProfile.setActors(actor);
                actorProfilesRepository.save(actorProfile);
                break;
            case CREW_MEMBER:
                FilmCrewMembers crewMembers = new FilmCrewMembers();
                crewMembers.setUser(user);
                crewMembers.setName(signupRequest.getName());
                crewMembers.setSurName(signupRequest.getSurName());
                crewMemberRepo.save(crewMembers);

                CrewMemberProfiles crewMemberProfiles = new CrewMemberProfiles();
                crewMemberProfiles.setGender(signupRequest.getGender());
                crewMemberProfiles.setGmail(signupRequest.getGmail());
                crewMemberProfiles.setNumberPhone(signupRequest.getPhone());
                crewMemberProfiles.setCrewMembers(crewMembers);
                crewMemberProfilesRepo.save(crewMemberProfiles);
                break;
        }

        // Автоматична авторизація
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signupRequest.getName(), signupRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);

        return ResponseEntity.ok(jwt);
    }

}
