package com.example.Service;


import com.example.DTO.DtoCrewMemberProfile;
import com.example.Entity.CrewMemberProfiles;
import com.example.Entity.FilmCrewMembers;
import com.example.Repository.CrewMemberProfilesRepo;
import com.example.Repository.CrewMemberRepo;
import com.example.config.MyUserDetails;
import com.example.loger.Loggable;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j

public class CrewMemberService {

    private final CrewMemberRepo crewMemberRepo;
    private final CrewMemberProfilesRepo crewMemberProfilesRepo;
    private final Storage storage;

    @Autowired
    public CrewMemberService(CrewMemberRepo crewMemberRepo, CrewMemberProfilesRepo crewMemberProfilesRepo, Storage storage) {
        this.crewMemberRepo = crewMemberRepo;
        this.crewMemberProfilesRepo = crewMemberProfilesRepo;

        this.storage = storage;
    }


    @Value("${google.cloud.storage.bucket.name1}")
    private String bucketName;

//    public List<FilmCrewMembers> allGetCrewMember(){
//        return crewMemberRepo.findAllWithMovies();
//    }

    @Transactional
    @Loggable
    public FilmCrewMembers createdCrewMember(FilmCrewMembers filmCrewMembers){
        return crewMemberRepo.save(filmCrewMembers);
    }

    @Transactional
    public boolean updateCrewMemberProfile(Long userId, String fieldName, String newValue) throws AccessDeniedException {

        // Получаем текущего аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long authenticatedUserId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();

        // Проверяем, что переданный ID совпадает с ID аутентифицированного пользователя
        if (!authenticatedUserId.equals(userId)) {
            throw new AccessDeniedException("You are not authorized to update this profile photo");
        }
        Optional<FilmCrewMembers> crewMembersOpt = crewMemberRepo.findByUserUserId(userId);

        if(crewMembersOpt.isPresent()){
            FilmCrewMembers crewMembers = crewMembersOpt.get();
            System.out.println("Found crewMember: " + crewMembers.getCrewMember_id());

        Optional<CrewMemberProfiles> crewMemberProfiles = crewMemberProfilesRepo.findByCrewMemberId(crewMembers.getCrewMember_id());

        if(crewMemberProfiles.isPresent() && newValue != null){

            CrewMemberProfiles crewMemberProfile = crewMemberProfiles.get();

            switch (fieldName){

                case "languages" -> crewMemberProfile.setLanguages(newValue);
                case "equipmentList" -> crewMemberProfile.setEquipmentList(newValue);
                case "biography" -> crewMemberProfile.setBiography(newValue);
                case "position" -> crewMemberProfile.setPosition(newValue);
                case "expertise" -> crewMemberProfile.setExpertise(newValue);
                case "workingHoursPerWeek" -> crewMemberProfile.setWorkingHoursPerWeek(Integer.valueOf(newValue));
                case "experience" -> crewMemberProfile.setExperience(newValue);
                case "gmail" -> crewMemberProfile.setGmail(newValue);
                case "numberPhone" -> crewMemberProfile.setNumberPhone(newValue);
                case "portfolioUrl" -> crewMemberProfile.setPortfolioUrl(newValue);
                case "linkedinUrl" -> crewMemberProfile.setLinkedinUrl(newValue);
            }
            crewMemberProfilesRepo.save(crewMemberProfile);
            return true;
        }
    }
        return false;
    }


    public String uploadProfilePhoto(Long userId, MultipartFile file) throws IOException {

        // Получаем текущего аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long authenticatedUserId = ((MyUserDetails) authentication.getPrincipal()).getUser_id();

        // Проверяем, что переданный ID совпадает с ID аутентифицированного пользователя
        if (!authenticatedUserId.equals(userId)) {
            throw new AccessDeniedException("You are not authorized to update this profile photo");
        }
        Optional<FilmCrewMembers> crewMembersOpt = crewMemberRepo.findByUserUserId(userId);

        if(crewMembersOpt.isPresent()){
            FilmCrewMembers crewMembers = crewMembersOpt.get();
            System.out.println("Found crewMember: " + crewMembers.getCrewMember_id());
            String fileUrl = uploadPhotoCrewMember(file);
            Optional<CrewMemberProfiles> crewMemberProfile = crewMemberProfilesRepo.findByCrewMemberId(crewMembers.getCrewMember_id());

            if (crewMemberProfile.isPresent()){
                CrewMemberProfiles profiles = crewMemberProfile.get();
                profiles.setProfile_photo_url(fileUrl);
                crewMemberProfilesRepo.save(profiles);
                return fileUrl;
            }
            return null;

        }

        throw new IllegalArgumentException("CrewMemberProfile not found");
    }


    private String uploadPhotoCrewMember(MultipartFile file) throws IOException {

        String fileName = "profile_photos/"+ UUID.randomUUID() + file.getOriginalFilename();

        Bucket bucket = storage.get(bucketName);
        Blob blob = bucket.create(fileName, file.getInputStream(), file.getContentType());

        return "https://storage.googleapis.com/"+bucketName+"/"+fileName;
    }


    public CompletableFuture<Optional<DtoCrewMemberProfile>> getCrewMemberProfile(Long userId) {
        // Получаем FilmCrewMembers по userId
        Optional<FilmCrewMembers> crewMemberOpt = crewMemberRepo.findByUserUserId(userId);

        if (crewMemberOpt.isPresent()) {
            FilmCrewMembers crewMember = crewMemberOpt.get();
            System.out.println("Found crewMember: " + crewMember.getCrewMember_id());

            // Получаем профиль члена съемочной группы по crewMemberId
            Optional<CrewMemberProfiles> crewMemberProfilesOpt = crewMemberProfilesRepo.findByCrewMemberId(crewMember.getCrewMember_id());

            if (crewMemberProfilesOpt.isPresent()) {
                DtoCrewMemberProfile profile = convertToDto(crewMemberProfilesOpt.get(), crewMember);

                return CompletableFuture.completedFuture(Optional.of(profile));
            }
        }

        return CompletableFuture.completedFuture(Optional.empty());
    }





    private DtoCrewMemberProfile convertToDto(CrewMemberProfiles crewProfiles, FilmCrewMembers crewMembers) {
        return new DtoCrewMemberProfile(
                crewMembers.getName(),
                crewMembers.getSurName(),
                crewProfiles.getGender(),
                crewProfiles.getLanguages(),
                crewProfiles.getEquipmentList(),
                crewProfiles.getBiography(),
                crewProfiles.getPosition(),
                crewProfiles.getExpertise(),
                crewProfiles.getWorkingHoursPerWeek(),
                crewProfiles.getExperience(),
                crewProfiles.getProfile_photo_url(),
                crewProfiles.getGmail(),
                crewProfiles.getNumberPhone(),
                crewProfiles.getPortfolioUrl(),
                crewProfiles.getLinkedinUrl()
        );
    }


}
