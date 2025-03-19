package com.example.Repository;


import com.example.Entity.CrewMemberProfiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CrewMemberProfilesRepo extends JpaRepository<CrewMemberProfiles, Long> {

    @Query("SELECT ap FROM CrewMemberProfiles ap WHERE ap.crewMembers.crewMember_id = :crewMemberId")
    Optional<CrewMemberProfiles> findByCrewMemberId(@Param("crewMemberId") Long crewMemberId);


    @Query("SELECT cm.user.user_id FROM CrewMemberProfiles ap JOIN ap.crewMembers cm WHERE ap.crewMembers.crewMember_id = :crewMemberId")
    Optional<Long> findUserIdByCrewMemberId(@Param("crewMemberId") Long crewMemberId);


    @Query("SELECT cm FROM CrewMemberProfiles cm WHERE cm.crewMembers.user.user_id = :userId")
    Optional<CrewMemberProfiles> findByUserId(@Param("userId") Long userId);


}