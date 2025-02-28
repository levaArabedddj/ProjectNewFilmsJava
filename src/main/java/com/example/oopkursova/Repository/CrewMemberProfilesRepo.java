package com.example.oopkursova.Repository;

import com.example.oopkursova.Entity.CrewMemberProfiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CrewMemberProfilesRepo extends JpaRepository<CrewMemberProfiles, Long> {

    @Query("SELECT ap FROM CrewMemberProfiles ap WHERE ap.crewMembers.crewMember_id = :crewMemberId")
    Optional<CrewMemberProfiles> findByCrewMemberId(@Param("crewMemberId") Long crewMemberId);



}