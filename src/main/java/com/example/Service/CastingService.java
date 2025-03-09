package com.example.Service;

import com.example.Entity.*;
import com.example.Enum.ApplicationStatus;
import com.example.Enum.FilmRole;
import com.example.Enum.TrialResult;
import com.example.Enum.UserRole;
import com.example.Repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public CastingService(MoviesRepo moviesRepo, ActorRepo actorRepo, UsersRepo usersRepo, CastingsRepo castingsRepo, DirectorRepo directorRepo, CastingApplicationsRepo castingApplicationsRepo, Trial_ShootingsRepo trialShootingsRepo, TrialParticipantsRepo trialParticipantsRepo) {
        this.moviesRepo = moviesRepo;
        this.actorRepo = actorRepo;
        this.usersRepo = usersRepo;
        this.castingsRepo = castingsRepo;
        this.directorRepo = directorRepo;
        this.castingApplicationsRepo = castingApplicationsRepo;
        this.trialShootingsRepo = trialShootingsRepo;
        this.trialParticipantsRepo = trialParticipantsRepo;

    }


    public Castings createCastings(Long directorId, Long movieId, Castings castingsBody){
        Director director = directorRepo.findById(directorId)
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

    public CastingApplications applyForCasting(Long actorId, int castingId, String message ){

        Users actors = usersRepo.findByActor_Id(actorId)
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

    public CastingApplications reviewApplication(Long applicationId, ApplicationStatus status, String feedback){
        CastingApplications applications = castingApplicationsRepo.findById(applicationId)
                .orElseThrow(()-> new EntityNotFoundException("Application not found") );

        if(status != ApplicationStatus.Approved && status != ApplicationStatus.Rejected){
            throw new RuntimeException("Application is not approved");
        }
        applications.setStatus(status);
        applications.setMessage(feedback);
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


    @Transactional
    public void approveActorForMovie(Long trialParticipantsId, Long movieId, FilmRole role){

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
    }

}
