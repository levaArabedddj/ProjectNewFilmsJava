package com.example.oopkursova.Repository;

import com.example.oopkursova.Entity.ActorProfiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActorProfilesRepository extends JpaRepository<ActorProfiles, Long> {
}