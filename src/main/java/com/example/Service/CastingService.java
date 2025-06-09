package com.example.Service;

import com.example.DTO.*;
import com.example.Entity.*;
import com.example.Entity.MoviesPackage.Movies;
import com.example.Enum.ApplicationStatus;
import com.example.Enum.TrialResult;
import com.example.Enum.UserRole;
import com.example.Exception.ApiException;
import com.example.Repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CastingService {

    private final MoviesRepo moviesRepo;
    private final ActorRepo actorRepo;
    private final UsersRepo usersRepo;
    private final CastingsRepo castingsRepo;
    private final DirectorRepo directorRepo;
    private final CastingApplicationsRepo castingApplicationsRepo;
    private final Trial_ShootingsRepo trialShootingsRepo;
    private final TrialParticipantsRepo trialParticipantsRepo;
    private final SenderService service;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public CastingService(MoviesRepo moviesRepo, ActorRepo actorRepo, UsersRepo usersRepo, CastingsRepo castingsRepo, DirectorRepo directorRepo, CastingApplicationsRepo castingApplicationsRepo, Trial_ShootingsRepo trialShootingsRepo, TrialParticipantsRepo trialParticipantsRepo, SenderService service) {
        this.moviesRepo = moviesRepo;
        this.actorRepo = actorRepo;
        this.usersRepo = usersRepo;
        this.castingsRepo = castingsRepo;
        this.directorRepo = directorRepo;
        this.castingApplicationsRepo = castingApplicationsRepo;
        this.trialShootingsRepo = trialShootingsRepo;
        this.trialParticipantsRepo = trialParticipantsRepo;

        this.service = service;
    }


    public void createCastings(Long userId, Long movieId, CastingDto castingsBody){
        Director director = directorRepo.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Director not found"));

        Movies movies = moviesRepo.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movies not found"));

        if(!movies.getDirector().equals(director)){
            throw new RuntimeException("Movie is not a director");
        }

        if (castingsBody.getRoleName() == null || castingsBody.getRoleName().isEmpty()) {
            throw new RuntimeException("Role names cannot be null or empty");
        }


        Castings castings = new Castings();
        castings.setMovie(movies);
        castings.setRoleName(castingsBody.getRoleName());
        castings.setDescription(castingsBody.getDescription());
        castings.setRequirements(castingsBody.getRequirements());
        castingsRepo.save(castings);
    }

    public CastingApplications applyForCasting(Long userId, int castingId, String message ){

        Users actors = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Actor not found"));

        if(actors.getRole()!= UserRole.ACTOR){
            throw new RuntimeException("Only actors can apply for casting");
        }

        Castings casting = castingsRepo.findById(castingId)
                .orElseThrow(()-> new EntityNotFoundException("casting not found"));


        if(castingApplicationsRepo.existsByActorAndCastings(actors, casting)){
            throw new RuntimeException("You have already applied for this casting");
        }


        CastingApplications applications = new CastingApplications();
        applications.setActor(actors);
        applications.setCastings(casting);
        applications.setStatus(ApplicationStatus.Pending);
        applications.setMessage(message);
        return castingApplicationsRepo.save(applications);
    }



    @SneakyThrows
    public List<CastingApplicationDto> getAllCastingApplications(int castingId,Long filmId, long directorId, Principal principal){

        try {
            String username = principal.getName();
            Users director = usersRepo.findByUserName(username)
                    .orElseThrow(() -> new ApiException("User not found"));

            if(director.getRole()!= UserRole.DIRECTOR){
                throw new RuntimeException("Only directors can get casting applications");
            }

            Movies movie = moviesRepo.findById(filmId)
                    .orElseThrow(() -> new ApiException("Movie not found"));

//            if (!(movie.getDirector().getId() == directorId)) {
//                throw new ApiException("Access denied: You are not the owner of this movie");
//            }

            if(!(movie.getDirector().getUsers().getUser_id()== directorId)){
                throw new ApiException("Access denied: You are not the owner of this movie");
            }
//
//
//            if(!moviesRepo.existsByIdAndUsername(filmId, username)){
//                throw new ApiException("Access denied: You are not the owner of thus movie");
//            }
            Castings castings = castingsRepo.findById(castingId).
                    orElseThrow(()->new ApiException("Castings not found"));

            if(!(castings.getMovie().getId() == filmId)){
                throw new ApiException("This casting does not belong to the specified movie");
            }

            List<CastingApplications> applications = castingApplicationsRepo.findByCastings(castings);


                // 5) Мапим их в DTO
                return applications.stream()
                        .map(application -> {
                            Users user = application.getActor();
                            Actors actor = user.getActor();
                            ActorProfileDto profileDto = null;
                            if (actor != null && actor.getActorProfile() != null) {
                                ActorProfiles p = actor.getActorProfile();
                                profileDto = new ActorProfileDto(
                                        p.getBiography(),
                                        p.getSkills(),
                                        p.getLanguages(),
                                        p.getExperience(),
                                        p.getProfile_photo_url()
                                );
                            }

                            DtoActor actorDto = null;
                            if (actor != null) {
                                actorDto = new DtoActor(
                                        actor.getId(),
                                        actor.getName(),
                                        actor.getSurName(),
                                        actor.getRating(),
                                        profileDto
                                );
                            }

                            return new CastingApplicationDto(
                                    application.getId(),
                                    application.getMessage(),
                                    application.getStatus(),
                                    castings.getRoleName(),
                                    actorDto
                            );
                        })
                        .collect(Collectors.toList());

        }catch (Exception e){
            e.printStackTrace();
        }

        return List.of();
    }

    @Transactional
    public CastingApplications reviewApplication(Long applicationId, ApplicationStatus status, String feedback){
        CastingApplications applications = castingApplicationsRepo.findById(applicationId)
                .orElseThrow(()-> new EntityNotFoundException("Application not found") );

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        Users director = applications.getCastings().getMovie().getDirector().getUsers();

        if(director == null || !director.getUserName().equals(currentUsername)){
            throw new AccessDeniedException("У вас нет прав изменять заявку в этом кастинге!");
        }
        if(status != ApplicationStatus.Approved && status != ApplicationStatus.Rejected){
            throw new RuntimeException("Application is not approved");
        }
        applications.setStatus(status);
        applications.setMessage(feedback);

        Actors actor = applications.getActor().getActor();
        if(actor == null || actor.getUser() == null){
            throw new RuntimeException("Actor not found");
        }
        String email = actor.getUser().getGmail();
        String username  = actor.getUser().getUserName();

        service.sendAssignApplication(email,username,status);
        return castingApplicationsRepo.save(applications);
    }




    public void assignToTrial(Long applicationId, Long trialId){

        CastingApplications castingApplications = castingApplicationsRepo.findById(applicationId)
                .orElseThrow(()-> new EntityNotFoundException("Application not found") );

        if(castingApplications.getStatus() != ApplicationStatus.Approved){
            throw new RuntimeException("Application is not approved");
        }

        Trial_Shootings trial = trialShootingsRepo.findById(trialId)
                .orElseThrow(()-> new EntityNotFoundException("Trial not found"));

        TrialParticipants participants = new TrialParticipants();
        participants.setActors(castingApplications.getActor());
        participants.setCastings(castingApplications.getCastings());
        participants.setShootings(trial);
        participants.setResult(TrialResult.Pending);
        participants.setCastingApplications(castingApplications);
        trialParticipantsRepo.save(participants);


        trialShootingsRepo.save(trial);
    }


// более детально расмотреть эти методы и при возможности упростить работу с ними
// как минимум , рассмотреть принятия актера после его заявки , создание пробных сьемочных дней
    // также упростить работу с принятием актера в команду , слишком сложно


    @Transactional
    public void approveActorForMovie(Long trialParticipantsId, Long movieId, String role){

        TrialParticipants participants =  trialParticipantsRepo.findById(trialParticipantsId)
                .orElseThrow(() -> new EntityNotFoundException("Trial participants not found"));

        if(participants.getResult() == TrialResult.Failed){
            throw new RuntimeException("Trial is not approved");
        }

        Movies movies = moviesRepo.findById(movieId)
                .orElseThrow(()-> new EntityNotFoundException("Movies not found"));

        Long count = (Long) entityManager.createQuery(
                "SELECT COUNT(ftu) FROM filmTeamUser ftu WHERE ftu.user = :user AND ftu.movies = :movie")
                .setParameter("user", participants.getActors())
                .setParameter("movie", movies)
                .getSingleResult();

        if(count > 0){
            throw new RuntimeException("Trial is already approved");
        }
        filmTeamUser filmTeamUser = new filmTeamUser();
        filmTeamUser.setUser(participants.getActors());
        filmTeamUser.setMovies(movies);
        filmTeamUser.setRole(role);

        entityManager.persist(filmTeamUser);
        service.sendMsgForActorInTeam(participants.getActors().getActor(), role);
    } // - создать новый метод , после того как актера приняли в комнаду сделать метод
    // где будет выносится вопрос по условиям контракта с актером
    // и его согласованости , типо режиссер кидает
    // свою версию контракта , но актера что то не устраивает и он
    // отправляет такой же контракт но со своими правками и такое может
    // просиходить сколько угодно раз пока актер и режиссер не будут решатся и
    // после этого , актер отправляет подпись и режиссер свое подпись и
    // после этого конракт создается

    public Trial_Shootings createTrialShooting(Long userId, Long movieId, TrialShootingDto trialDto) {

        Movies movie = moviesRepo.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movies not found"));

        if(!(movie.getDirector().getUsers().getUser_id() == userId)){
            throw new AccessDeniedException("Only the film's director can create a trial shooting day!");
        }

        // Конвертируем DTO в сущность
        Trial_Shootings trialShooting = new Trial_Shootings();
        trialShooting.setMovies(movie);
        trialShooting.setDate(trialDto.getDate());
        trialShooting.setStartTime(trialDto.getStartTime());
        trialShooting.setLocation(trialDto.getLocation());
        trialShooting.setDescription(trialDto.getDescription());

        return trialShootingsRepo.save(trialShooting);
    }

    @SneakyThrows
    public List<CastingDto> getAllCastingForMovieId(Long userId, long filmId, Principal principal) {


        String username = principal.getName();
        Users director = usersRepo.findByUserName(username)
                .orElseThrow(()-> new EntityNotFoundException("User not found"));

        if(!(director.getRole() == UserRole.DIRECTOR)){
            throw new RuntimeException("Only director can get casting");
        }

        Movies movie = moviesRepo.findById(filmId).orElseThrow();

        if(!(movie.getDirector().getUsers().getUser_id() == userId)){
            throw new AccessDeniedException("Only the film's director can get casting");
        }

        List<Castings> castings = castingsRepo.findByMovieId(filmId);

        return castings.stream().map(
                casting1 -> {
                    return new CastingDto(casting1.getId(),
                            casting1.getRoleName(),
                            casting1.getDescription(),
                            casting1.getRequirements());
                }
        ).collect(Collectors.toList());
    }
}
