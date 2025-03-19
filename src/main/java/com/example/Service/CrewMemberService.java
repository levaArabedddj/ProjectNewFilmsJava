package com.example.Service;


import com.example.DTO.DtoCrewMemberProfile;
import com.example.Entity.CrewMemberProfiles;
import com.example.Entity.FilmCrewMembers;
import com.example.Repository.CrewMemberProfilesRepo;
import com.example.Repository.CrewMemberRepo;
import com.example.loger.Loggable;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public boolean updateCrewMemberProfile(Long crewMemberId, String fieldName, String newValue){

        Optional<CrewMemberProfiles> crewMemberProfiles = crewMemberProfilesRepo.findByCrewMemberId(crewMemberId);

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
        return false;
    }


    public String uploadProfilePhoto(Long crewMemberId, MultipartFile file) throws IOException {

        Optional<CrewMemberProfiles> crewMemberProfiles = crewMemberProfilesRepo.findByCrewMemberId(crewMemberId);

        if(crewMemberProfiles.isPresent()){
            CrewMemberProfiles crewMemberProfile = crewMemberProfiles.get();
            String fileUrl = uploadPhotoCrewMember(file);
            crewMemberProfile.setProfile_photo_url(fileUrl);
            crewMemberProfilesRepo.save(crewMemberProfile);
            return fileUrl;
        }

        throw new IllegalArgumentException("CrewMemberProfile not found");
    }


    private String uploadPhotoCrewMember(MultipartFile file) throws IOException {

        String fileName = "profile_photos/"+ UUID.randomUUID() + file.getOriginalFilename();

        Bucket bucket = storage.get(bucketName);
        Blob blob = bucket.create(fileName, file.getInputStream(), file.getContentType());

        return "https://storage.googleapis.com/"+bucketName+"/"+fileName;
    }


    public Optional<DtoCrewMemberProfile> getCrewMemberProfile(Long userId) {
        // Получаем FilmCrewMembers по userId
        Optional<FilmCrewMembers> crewMemberOpt = crewMemberRepo.findByUserUserId(userId);

        if (crewMemberOpt.isPresent()) {
            FilmCrewMembers crewMember = crewMemberOpt.get();
            System.out.println("Found crewMember: " + crewMember.getCrewMember_id());

            // Получаем профиль члена съемочной группы по crewMemberId
            Optional<CrewMemberProfiles> crewMemberProfilesOpt = crewMemberProfilesRepo.findByCrewMemberId(crewMember.getCrewMember_id());

            if (crewMemberProfilesOpt.isPresent()) {
                return Optional.of(convertToDto(crewMemberProfilesOpt.get(), crewMember));
            }
        }

        return Optional.empty();
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
