package com.example.oopkursova.Repository;

import com.example.oopkursova.Entity.FilmCrewMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrewMemberRepo extends JpaRepository<FilmCrewMembers, Long> {
    @Query("SELECT fcm FROM FilmCrewMembers fcm JOIN FETCH fcm.movies")
    List<FilmCrewMembers> findAllWithMovies();
}
