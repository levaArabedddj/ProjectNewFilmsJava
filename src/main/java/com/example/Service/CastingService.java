package com.example.Service;

import com.example.DTO.ActorProfileDto;
import com.example.DTO.CastingApplicationDto;
import com.example.DTO.DtoActor;
import com.example.DTO.TrialShootingDto;
import com.example.Entity.*;
import com.example.Enum.ApplicationStatus;
import com.example.Enum.TrialResult;
import com.example.Enum.UserRole;
import com.example.Exception.ApiException;
import com.example.Repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

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


    public Castings createCastings(Long userId, Long movieId, Castings castingsBody){
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
        return castingsRepo.save(castings);
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



    public List<CastingApplicationDto> getAllCastingApplications(int castingId, Long filmId, long directorId, Principal principal){

        try {
            String username = principal.getName();
            Users director = usersRepo.findByUserName(username)
                    .orElseThrow(() -> new ApiException("User not found"));

            if(director.getRole()!= UserRole.DIRECTOR){
                throw new RuntimeException("Only directors can get casting applications");
            }

            Movies movie = moviesRepo.findById(filmId)
                    .orElseThrow(() -> new ApiException("Movie not found"));

            if (!(movie.getDirector().getId() ==directorId)) {
                throw new ApiException("Access denied: You are not the owner of this movie");
            }
//
//
//            if(!moviesRepo.existsByIdAndUsername(filmId, username)){
//                throw new ApiException("Access denied: You are not the owner of thus movie");
//            }
            List<Castings> castings = castingsRepo.findByMovieId(filmId);

            List<CastingApplicationDto> applicationDTOs = new ArrayList<>();

            for (Castings casting : castings) {
                List<CastingApplications> applications = castingApplicationsRepo.findByCastings(casting);

                for (CastingApplications application : applications) {
                    Users user = application.getActor();
                    Actors actor = user.getActor();  // Получаем актёра
                    ActorProfiles profile = actor != null ? actor.getActorProfile() : null;

                    // Преобразуем профиль актёра в DTO
                    ActorProfileDto profileDTO = (profile != null) ? new ActorProfileDto(
                            profile.getBiography(),
                            profile.getSkills(),
                            profile.getLanguages(),
                            profile.getExperience(),
                            profile.getProfile_photo_url()
                    ) : null;

                    // Преобразуем актёра в DTO
                    DtoActor actorDTO = (actor != null) ? new DtoActor(
                            actor.getId(),
                            actor.getName(),
                            actor.getSurName(),
                            actor.getRating(),
                            profileDTO
                    ) : null;

                    // Преобразуем заявку в DTO
                    applicationDTOs.add(new CastingApplicationDto(
                            application.getId(),
                            application.getMessage(),
                            application.getStatus(),
                            casting.getRoleName().toString(),
                            actorDTO
                    ));
                }
            }

            return applicationDTOs;
        }catch (Exception e){
            e.printStackTrace();
        } catch (ApiException e) {
            throw new RuntimeException(e);
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
    }

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
}
