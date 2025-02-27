package com.example.oopkursova.Repository;

import com.example.oopkursova.Entity.CrewMemberProfiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrewMemberProfilesRepo extends JpaRepository<CrewMemberProfiles, Long> {
}