package com.example.Service;

import com.example.DTO.DtoActorProfile;
import com.example.Entity.ActorProfiles;
import com.example.Entity.Actors;
import com.example.Repository.ActorProfilesRepository;
import com.example.Repository.ActorRepo;
import com.example.config.MyUserDetails;
import com.example.loger.Loggable;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class ActorsService {

    public final ActorRepo actorRepo;
    public  final ActorProfilesRepository actorProfilRepo;
 //   private  Storage storage;

    @Autowired
    public ActorsService(ActorRepo actorRepo, ActorProfilesRepository actorProfilRepo) {
        this.actorRepo = actorRepo;
        this.actorProfilRepo = actorProfilRepo;
        //this.storage = storage;
    }

    @Value("${google.cloud.storage.bucket.name1}")
    private String bucketName;

    @Transactional
    @Loggable
    public Actors CreatActors(Actors actors){
        return actorRepo.save(actors);
    }

    @Transactional
    public boolean updateActorProfile(Long userId,String fieldName, String newValue) throws AccessDeniedException {

        // Получаем текущего аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long authenticatedUserId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();

        // Проверяем, что переданный ID совпадает с ID аутентифицированного пользователя
        if (!authenticatedUserId.equals(userId)) {
            throw new AccessDeniedException("You are not authorized to update this profile photo");
        }

        Optional<Actors> actorsOpt = actorRepo.findByUserUserId(userId);

        if(actorsOpt.isPresent()) {

            Actors actors = actorsOpt.get();
            System.out.println("Found Actor: " + actors.getId());

            Optional<ActorProfiles> actorProfileOpt = actorProfilRepo.findByActorId(actors.getId());

            if (actorProfileOpt.isPresent() && newValue != null) {

                ActorProfiles actorProfiles = actorProfileOpt.get();

                switch (fieldName) {
                    case "biography" -> actorProfiles.setBiography(newValue);
                    case "skills" -> actorProfiles.setSkills(newValue);
                    case "experience" -> actorProfiles.setExperience(newValue);
                    case "numberPhone" -> actorProfiles.setNumberPhone(newValue);
                    case "languages" -> actorProfiles.setLanguages(newValue);
                    case "gmail" -> actorProfiles.setGmail(newValue);
                    default -> throw new IllegalStateException("Unexpected value: " + fieldName);

                }
                actorProfilRepo.save(actorProfiles);
                return true;
            }
        }
        return false;
    }

//    public String uploadProfilePhoto(Long userId , MultipartFile file) throws IOException {
//        // Получаем текущего аутентифицированного пользователя
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Long authenticatedUserId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();
//
//        // Проверяем, что переданный ID совпадает с ID аутентифицированного пользователя
//        if (!authenticatedUserId.equals(userId)) {
//            throw new AccessDeniedException("You are not authorized to update this profile photo");
//        }
//
//        Optional<Actors> actorsOpt = actorRepo.findByUserUserId(userId);
//
//        if(actorsOpt.isPresent()) {
//            Actors actors = actorsOpt.get();
//            System.out.println("Found Actor: " + actors.getId());
//            String fileUrl = uploadProfilePhoto(file);
//            Optional<ActorProfiles> actorProfileOpt = actorProfilRepo.findByActorId(actors.getId());
//
//            if (actorProfileOpt.isPresent()) {
//                ActorProfiles actorProfiles = actorProfileOpt.get();
//                actorProfiles.setProfile_photo_url(fileUrl);
//                actorProfilRepo.save(actorProfiles);
//                return fileUrl;
//            }
//        }
//        throw new IllegalArgumentException("Actor profile not found!");
//    }
//
//    public String uploadProfilePhoto(MultipartFile file) throws IOException {
//
//        String fileName = "profile_photos/"+ UUID.randomUUID() + file.getOriginalFilename();
//
//        Bucket bucket = storage.get(bucketName);
//        Blob blob = bucket.create(fileName,file.getInputStream(),file.getContentType());
//
//        return "https://storage.googleapis.com/"+ bucketName+"/"+fileName;
//    }

    public Optional<DtoActorProfile> getInformationActor(Long userId){
        // получаем актера по Id
        Optional<Actors> actorsOpt = actorRepo.findByUserUserId(userId);

        if (actorsOpt.isPresent()) {
            Actors actors = actorsOpt.get();
            System.out.println("Found Actor: " + actors.getId());


            Optional<ActorProfiles> actorProfileOpt = actorProfilRepo.findByActorId(actors.getId());

            if (actorProfileOpt.isPresent()) {
                return Optional.of(convertToDto(actorProfileOpt.get(), actors));
            }
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
