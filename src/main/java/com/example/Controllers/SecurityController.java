package com.example.Controllers;


import com.example.ElasticSearch.ClassDocuments.ActorDocument;
import com.example.ElasticSearch.ClassDocuments.CrewMemberDocument;
import com.example.ElasticSearch.Service.ActorService;
import com.example.ElasticSearch.Service.CrewMemberServiceElastic;
import com.example.Entity.*;
import com.example.Entity.VisitorPackage.Visitor;
import com.example.Enum.SubscriptionLevelVisitor;
import com.example.Repository.*;

import com.example.Service.CrewMemberService;
import com.example.Service.SenderService;
import com.example.config.JwtCore;
import com.example.config.SigninRequest;
import com.example.config.SignupRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class SecurityController {


    private UsersRepo usersRepo;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtCore jwtCore;
    private final MoviesRepo moviesRepo;

    private final ActorRepo actorRepo;
    private final ActorProfilesRepository actorProfilesRepository;
    private final CrewMemberRepo crewMemberRepo;
    private  final CrewMemberProfilesRepo crewMemberProfilesRepo;
    private final SenderService emailService;
    private final DirectorRepo directorRepo;
    private final DirectorProfilesRepo directorProfilesRepo;
    private final ActorService actorService;
    private final CrewMemberServiceElastic service;


    private final VisitorRepo visitorRepo;
    private final AdminRepo adminRepo;

    @Autowired
    public SecurityController(MoviesRepo moviesRepo, ActorRepo actorRepo, ActorProfilesRepository actorProfilesRepository, CrewMemberRepo crewMemberRepo, CrewMemberProfilesRepo crewMemberProfilesRepo, SenderService emailService, DirectorRepo directorRepo, DirectorProfilesRepo directorProfilesRepo, ActorService actorService, CrewMemberService service, CrewMemberServiceElastic service1, VisitorRepo visitorRepo, AdminRepo adminRepo) {
        this.moviesRepo = moviesRepo;
        this.actorRepo = actorRepo;
        this.actorProfilesRepository = actorProfilesRepository;
        this.crewMemberRepo = crewMemberRepo;
        this.crewMemberProfilesRepo = crewMemberProfilesRepo;
        this.emailService = emailService;
        this.directorRepo = directorRepo;
        this.directorProfilesRepo = directorProfilesRepo;
        this.actorService = actorService;

        this.service = service1;
        this.visitorRepo = visitorRepo;
        this.adminRepo = adminRepo;
    }

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
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signinRequest.getUserName(), signinRequest.getPassword())
            );

        } catch (BadCredentialsException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);
        return ResponseEntity.ok(jwt);
    }

    //Метод логина для андроид приложения , вместо строки возвращаем json
    @PostMapping("/signinn")
    public ResponseEntity<?> signinn(@RequestBody SigninRequest signinRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signinRequest.getUserName(),
                            signinRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"" + e.getMessage() + "\"}");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);

        // Оборачиваем токен в JSON-объект
        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);
        return ResponseEntity.ok(response);
    }


    @Transactional
    @PostMapping("/signup-Login")
    public ResponseEntity<?> signInAuth(@RequestBody SignupRequest signupRequest){
        if (usersRepo.existsUsersByUserName(signupRequest.getUserName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose different name");
        }
        if (usersRepo.existsUsersByGmail(signupRequest.getGmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose different email");
        }

        // Преобразование перечисления в строку и проверка на роль Admin
        String role = signupRequest.getRole().name().toUpperCase();

        //НИКТО НЕ МОЖЕТ СТАТЬ АДМИНОМ ЧЕРЕЗ РЕГИСТРАЦИЮ!!!
        if (role.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You cannot register as an Admin");
        }

        // Дополнительная валидация роли
        List<String> allowedRoles = Arrays.asList("ACTOR", "CREW_MEMBER", "DIRECTOR", "VISITOR");
        if (!allowedRoles.contains(role)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role selected");
        }

        // Створення користувача
        Users user = new Users();
        user.setUserName(signupRequest.getUserName());
        user.setGmail(signupRequest.getGmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRole(signupRequest.getRole());
        usersRepo.save(user);

        System.out.println(user);
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
                System.out.println(user);

                ActorDocument actorDocument = actorService.mapToElastic(actor,user,actorProfile);
                actorService.indexActor(actorDocument);

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
                CrewMemberDocument crewMemberDocument = service.mapToElastic(crewMembers,user,crewMemberProfiles);
                service.indexCrewMember(crewMemberDocument);

                break;
            case DIRECTOR:

                DirectorProfiles profiles = new DirectorProfiles();
                profiles.setFirstName(signupRequest.getName());
                profiles.setLastName(signupRequest.getSurName());
                profiles.setGender(signupRequest.getGender());
                profiles.setPhoneNumber(signupRequest.getPhone());
                profiles = directorProfilesRepo.save(profiles);


                Director director = new Director();
                director.setUsers(user);
                director.setName(signupRequest.getName());
                director.setSurName(signupRequest.getSurName());
                director.setDirectorProfiles(profiles);
                directorRepo.save(director);
                break;
                // прописать условие когда будет создаваться админ

            case VISITOR:

                Visitor visitor = new Visitor();
                visitor.setUser(user);
                visitor.setSubscriptionLevel(SubscriptionLevelVisitor.FREE);
                visitor.setBalance(BigDecimal.ZERO);

                visitorRepo.save(visitor);
                break;
        }

        System.out.println(user);
        emailService.sendRegistrationEmail(user.getGmail(), user.getUserName());

        // Автоматична авторизація
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signupRequest.getUserName(), signupRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);

        return ResponseEntity.ok(jwt);
    }


    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
            // возвращаем атрибуты, которые прислал Google (sub, email, name и т.п.)
            return ResponseEntity.ok(oauth2User.getAttributes());
        }
        // fallback — просто имя из токена
        return ResponseEntity.ok(Map.of("username", authentication.getName()));
    }



}
