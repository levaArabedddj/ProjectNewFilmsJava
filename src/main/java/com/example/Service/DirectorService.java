package com.example.Service;

import com.example.DTO.DtoDirectorProfiles;
import com.example.Entity.Director;
import com.example.Entity.DirectorProfiles;
import com.example.Repository.DirectorProfilesRepo;
import com.example.Repository.DirectorRepo;
import com.example.config.MyUserDetails;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class DirectorService {


    // прописать логику для получения данных о профиле режиссера
    private final DirectorRepo directorRepo;
   // private  final Storage storage;
    private final DirectorProfilesRepo directorProfilesRepo;

    @Value("${google.cloud.storage.bucket.name1}")
    private String bucketName;

    @Autowired
    public DirectorService(DirectorRepo directorRepo/* Storage storage*/, DirectorProfilesRepo directorProfilesRepo) {
        this.directorRepo = directorRepo;
        //this.storage = storage;
        this.directorProfilesRepo = directorProfilesRepo;
    }

    @Transactional
    public boolean updateDirectorProfile(
            Long userId,
            String fieldName,
            String newValue) throws AccessDeniedException {

        // Получаем текущего аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long authenticatedUserId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();

        // Проверяем, что переданный ID совпадает с ID аутентифицированного пользователя
        if (!authenticatedUserId.equals(userId)) {
            throw new AccessDeniedException("You are not authorized to update this profile photo");
        }
        Optional<Director> directorOpt = directorRepo.findByUserUserId(userId);

        if (directorOpt.isPresent()) {
            Director director = directorOpt.get();
            System.out.println("Found director: " + director.getId());
            Optional<DirectorProfiles> directorProfiles1 = directorProfilesRepo.findByDirectorId(director.getId());

            if (directorProfiles1.isPresent() && newValue != null) {

                DirectorProfiles directorProfiles = directorProfiles1.get();

                switch (fieldName){
                    case "firstName" -> directorProfiles.setFirstName(newValue);
                    case "lastName" -> directorProfiles.setLastName(newValue);
                    case "experienceYears" -> directorProfiles.setExperienceYears(Integer.parseInt(newValue));
                    case "biography" -> {
                        System.out.println("Updating biography with value: " + newValue); // Логируем значение
                        directorProfiles.setBiography(newValue);
                    }
                    case "profilePhotoUrl" -> directorProfiles.setProfilePhotoUrl(newValue);
                    case "portfolioUrl" -> directorProfiles.setPortfolioUrl(newValue);
                    case "language" -> directorProfiles.setLanguage(newValue);
                    case "imdbProfileUrl" -> directorProfiles.setImdbProfileUrl(newValue);
                    case "linkedinUrl" -> directorProfiles.setLinkedinUrl(newValue);
                    case "awards" -> directorProfiles.setAwards(newValue);
                    case "education" -> directorProfiles.setEducation(newValue);
                    case "famousWorks" -> directorProfiles.setFamousWorks(newValue);
                    case "mainGenre" -> directorProfiles.setMainGenre(newValue);
                    default -> throw new IllegalArgumentException("Invalid field name: " + fieldName);
                }
                directorProfilesRepo.save(directorProfiles);
                return true;
            }
        }

        return false;
    }
//
//    public String uploadProfilePhoto(Long userId ,
//                                     MultipartFile file)
//            throws IOException {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Long authenticatedUserId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();
//
//        // Проверяем, что переданный ID совпадает с ID аутентифицированного пользователя
//        if (!authenticatedUserId.equals(userId)) {
//            throw new AccessDeniedException("You are not authorized to update this profile photo");
//        }
//
//        Optional<Director> directorOpt = directorRepo.findByUserUserId(userId);
//
//        if (directorOpt.isPresent()) {
//            Director director = directorOpt.get();
//            System.out.println("Found director: " + director.getId());
//           String fileUrl = uploadPhotoDirector(file);
//            Optional<DirectorProfiles> directorProfiles1 = directorProfilesRepo.findByDirectorId(director.getId());
//
//            if (directorProfiles1.isPresent()) {
//
//            DirectorProfiles directorProfiles = directorProfiles1.get();
//            directorProfiles.setProfilePhotoUrl(fileUrl);
//            directorProfilesRepo.save(directorProfiles);
//            return fileUrl;
//            }
//        }
//
//        return null;
//    }
//
//
//    private String uploadPhotoDirector(MultipartFile file) throws IOException {
//
//        String fileName ="profile_photos/" + UUID.randomUUID() + file.getOriginalFilename();
//
//        Bucket bucket = storage.get(bucketName);
//        Blob blob = bucket.create(fileName,file.getInputStream(),file.getContentType());
//
//        return "https://storage.googleapis.com/"+bucketName+"/"+fileName;
//    }


    public CompletableFuture<Optional<DtoDirectorProfiles>> getDirectorProfile(Long userId){

        Optional<Director> directorOpt = directorRepo.findByUserUserId(userId);
        if (directorOpt.isPresent()) {
            Director director = directorOpt.get();
            System.out.println("Found director: " + director.getId());

            Optional<DirectorProfiles> directorProfiles1 = directorProfilesRepo.findByDirectorId(director.getId());

            if (directorProfiles1.isPresent()) {
                DtoDirectorProfiles profiles = convertToDto(directorProfiles1.get(), director);
                return CompletableFuture.completedFuture(Optional.of(profiles));
            }
        }
        return CompletableFuture.completedFuture(Optional.empty());
    }

    private DtoDirectorProfiles convertToDto(DirectorProfiles directorProfiles, Director director) {

        return new DtoDirectorProfiles(
                director.getName(),
                director.getSurName(),
                directorProfiles.getExperienceYears(),
                directorProfiles.getBiography(),
                directorProfiles.getProfilePhotoUrl(),
                directorProfiles.getPortfolioUrl(),
                directorProfiles.getLanguage(),
                directorProfiles.getImdbProfileUrl(),
                directorProfiles.getLinkedinUrl(),
                directorProfiles.getAwards(),
                directorProfiles.getEducation(),
                directorProfiles.getFamousWorks(),
                directorProfiles.getMainGenre()
        );
    }
}
