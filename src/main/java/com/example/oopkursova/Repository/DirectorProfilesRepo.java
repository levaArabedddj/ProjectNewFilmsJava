package com.example.oopkursova.Repository;

import com.example.oopkursova.Entity.DirectorProfiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectorProfilesRepo extends JpaRepository<DirectorProfiles, Long> {
}