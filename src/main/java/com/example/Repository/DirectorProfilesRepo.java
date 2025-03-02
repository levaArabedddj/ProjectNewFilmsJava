package com.example.Repository;


import com.example.Entity.DirectorProfiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectorProfilesRepo extends JpaRepository<DirectorProfiles, Long> {
}