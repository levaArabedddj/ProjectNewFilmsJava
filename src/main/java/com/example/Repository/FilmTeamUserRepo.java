package com.example.Repository;

import com.example.Entity.filmTeamUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FilmTeamUserRepo extends JpaRepository<filmTeamUser, Long> {

    @Query("""
        SELECT ftu
        FROM filmTeamUser ftu
        WHERE ftu.movies.id = :movies
          AND ftu.role     = 'Actor'
        """)
    List<filmTeamUser> findActorsByMovieId(@Param("movies") Long movies);
}

