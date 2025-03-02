package com.example.Repository;


import com.example.Entity.ActorProfiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActorProfilesRepository extends JpaRepository<ActorProfiles, Long> {

    @Query("SELECT ap FROM ActorProfiles ap where ap.actors.id = :actorId")
    Optional<ActorProfiles> findByActorId(Long actorId);
}