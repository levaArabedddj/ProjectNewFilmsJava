package com.example.oopkursova.Service;

import com.example.oopkursova.DTO.DtoActorProfile;
import com.example.oopkursova.Entity.ActorProfiles;
import com.example.oopkursova.Entity.Actors;
import com.example.oopkursova.Repository.ActorProfilesRepository;
import com.example.oopkursova.Repository.ActorRepo;
import com.example.oopkursova.loger.Loggable;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class ActorsService {

    public final ActorRepo actorRepo;
    public  final ActorProfilesRepository actorProfilRepo;
    private final Storage storage;

    @Autowired
    public ActorsService(ActorRepo actorRepo, ActorProfilesRepository actorProfilRepo, Storage storage) {
        this.actorRepo = actorRepo;
        this.actorProfilRepo = actorProfilRepo;
        this.storage = storage;
    }

    @Value("${google.cloud.storage.bucket.name1}")
    private String bucketName;

    @Transactional
    @Loggable
    public Actors CreatActors(Actors actors){
        return actorRepo.save(actors);
    }

    @Transactional
    public boolean updateActorProfile(Long actorId,String fieldName, String newValue){

        Optional<ActorProfiles> actorProfile = actorProfilRepo.findByActorId(actorId);

        if(actorProfile.isPresent() && newValue != null){
            ActorProfiles actorProfiles = actorProfile.get();

            switch (fieldName){
                case "biography"-> actorProfiles.setBiography(newValue);
                case "skills"-> actorProfiles.setSkills(newValue);
                case "experience"->actorProfiles.setExperience(newValue);
                case "numberPhone"->actorProfiles.setNumberPhone(newValue);
                case "languages" -> actorProfiles.setLanguages(newValue);
                default -> throw new IllegalStateException("Unexpected value: " + fieldName);

            }
            actorProfilRepo.save(actorProfiles);
            return true;
        }
        return false;
    }

    public String uploadProfilePhoto(Long actorId , MultipartFile file) throws IOException {
        Optional<ActorProfiles> actorProfileOpt = actorProfilRepo.findByActorId(actorId);
        if (actorProfileOpt.isPresent()) {
            ActorProfiles actorProfiles = actorProfileOpt.get();
            String fileUrl = uploadProfilePhoto(file);
            actorProfiles.setProfile_photo_url(fileUrl);
            actorProfilRepo.save(actorProfiles);
            return fileUrl;
        }
        throw new IllegalArgumentException("Actor profile not found!");
    }

    public String uploadProfilePhoto(MultipartFile file) throws IOException {

        String fileName = "profile_photos/"+ UUID.randomUUID() + file.getOriginalFilename();

        Bucket bucket = storage.get(bucketName);
        Blob blob = bucket.create(fileName,file.getInputStream(),file.getContentType());

        return "https://storage.googleapis.com/"+ bucketName+"/"+fileName;
    }

    public Optional<DtoActorProfile> getInformationActor(Long actorId){

        Optional<Actors> actors = actorRepo.findById(actorId);
        Optional<ActorProfiles> actorProfileOpt = actorProfilRepo.findByActorId(actorId);

        if(actors.isPresent() && actorProfileOpt.isPresent()){
            return Optional.of(convertToDto(actorProfileOpt.get(), actors.get()));
        }


        return Optional.empty();
    }

    private DtoActorProfile convertToDto(ActorProfiles actorProfiles , Actors actor) {
        return new DtoActorProfile(
                actor.getName(),
                actor.getSurName(),
                actor.getSalaryPerHour(),
                actorProfiles.getBiography(),
                actorProfiles.getSkills(),
                actorProfiles.getExperience(),
                actorProfiles.getProfile_photo_url(),
                actorProfiles.getGmail(),
                actorProfiles.getNumberPhone()
        );

    }


}
